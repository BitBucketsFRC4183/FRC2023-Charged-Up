package org.bitbuckets;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BucketAnnotationProcessor extends AbstractProcessor {
    private static String getPackageName(Element e) {
        while (e != null) {
            if (e.getKind().equals(ElementKind.PACKAGE)) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
            e = e.getEnclosingElement();
        }

        return null;
    }

    static final TypeName DIFF_DATA_TYPE = ClassName.get("org.bitbuckets.lib.log", "IDiffableData");
    static final TypeName LOG_DRIVER_TYPE = ClassName.get("org.bitbuckets.lib.core", "LogDriver");

    private static final Map<String, String> LOGGABLE_TYPES_LOOKUP = new HashMap<>();
    private static final Map<String, String> UNLOGGABLE_TYPES_SUGGESTIONS = new HashMap<>();

    static {
        LOGGABLE_TYPES_LOOKUP.put("byte[]", "Raw");
        LOGGABLE_TYPES_LOOKUP.put("boolean", "Boolean");
        LOGGABLE_TYPES_LOOKUP.put("long", "Integer");
        LOGGABLE_TYPES_LOOKUP.put("float", "Float");
        LOGGABLE_TYPES_LOOKUP.put("double", "Double");
        LOGGABLE_TYPES_LOOKUP.put("java.lang.String", "String");
        LOGGABLE_TYPES_LOOKUP.put("boolean[]", "BooleanArray");
        LOGGABLE_TYPES_LOOKUP.put("long[]", "IntegerArray");
        LOGGABLE_TYPES_LOOKUP.put("float[]", "FloatArray");
        LOGGABLE_TYPES_LOOKUP.put("double[]", "DoubleArray");
        LOGGABLE_TYPES_LOOKUP.put("java.lang.String[]", "StringArray");
        LOGGABLE_TYPES_LOOKUP.put("edu.wpi.first.math.kinematics.SwerveModuleState[]", "SwerveModuleState[]");

        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Byte[]", "byte[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Boolean", "boolean");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Long", "long");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("int", "long");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Integer", "long");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Float", "float");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Double", "double");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Boolean[]", "boolean[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Long[]", "long[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("int[]", "long[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Integer[]", "long[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Float[]", "float[]");
        UNLOGGABLE_TYPES_SUGGESTIONS.put("java.lang.Double[]", "double[]");
    }

    public String deriveLastName(String original) {
        return "last" + original.substring(0,1).toUpperCase() + original.substring(1);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Optional<? extends TypeElement> annotationOptional = annotations.stream()
                .filter((te) -> te.getSimpleName().toString().equals("Loggable")).findFirst();
        if (annotationOptional.isEmpty()) {
            return false;
        }

        System.out.println("IT HAS BEEN FIRED");


        TypeElement annotation = annotationOptional.get();
        var modSet = roundEnv.getElementsAnnotatedWith(annotation);

        for (Element e : modSet) {
            TypeElement classElement = (TypeElement) e;
            String autologgedClassName = classElement.getSimpleName() + "AutoGen";
            String autologgedPackage = getPackageName(classElement);
            TypeSpec.Builder builder = TypeSpec
                    .classBuilder(autologgedClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(DIFF_DATA_TYPE)
                    .superclass(ClassName.get(e.asType()));

            var childElements = classElement.getEnclosedElements();

            //add lastFields
            for (Element element : childElements) {
                if (!element.getKind().equals(ElementKind.FIELD)) continue;
                String simpleName = element.getSimpleName().toString();
                builder.addField(ClassName.get(element.asType()), deriveLastName(simpleName));
            }

            MethodSpec.Builder startDiff = MethodSpec.methodBuilder("startDiff")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec.Builder modCheck = MethodSpec.methodBuilder("completeDiff")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(int.class, "id")
                    .addParameter(LOG_DRIVER_TYPE, "driver");


            StringBuilder problems = new StringBuilder();
            CodeBlock.Builder builder1 = CodeBlock.builder();

            for (Element element : childElements) {
                if (!element.getKind().equals(ElementKind.FIELD)) continue;

                String simpleName = element.getSimpleName().toString();
                String lastName = deriveLastName(simpleName);

                String fieldType = element.asType().toString();
                String logType = LOGGABLE_TYPES_LOOKUP.get(fieldType);
                if (logType == null) {
                    String typeSuggestion = UNLOGGABLE_TYPES_SUGGESTIONS.get(fieldType);
                    String extraText = "";
                    if (typeSuggestion != null) {
                        extraText = "Did you mean to use \"" + typeSuggestion + "\" instead?";
                    } else {
                        extraText = "\"" + fieldType + "\" is not supported";
                    }
                    System.err.println(
                            "\n[AutoLog] Unknown type for \"" + simpleName + "\" from \"" + classElement.getSimpleName()
                                    + "\" (" + extraText + ")");
                } else {


                    startDiff.addCode("this.$L = $L;\n", lastName, simpleName);


                    //TODO why isn't my diffing code working? probably double comparisons, check data type first.
                    builder1
                            .beginControlFlow("if (this.$L != $L)", lastName, simpleName)
                            .add("driver.report($L, $S, $L);", "id", simpleName, simpleName)
                            .endControlFlow();

                }

            }

            modCheck.addCode(builder1.build());

            TypeSpec finalSpec = builder
                    .addJavadoc("automatically generated by mattlib")
                    .addMethod(startDiff.build())
                    .addMethod(modCheck.build())
                    .build();


            JavaFile file = JavaFile.builder(autologgedPackage, finalSpec).build();
            try {
                file.writeTo(processingEnv.getFiler());
            } catch (IOException ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write class", classElement);
                ex.printStackTrace();
            }

        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("org.bitbuckets.lib.log.Loggable");
    }
}
