package org.bitbuckets.lib.util;

import java.lang.reflect.Method;

public class DebugName {


   /* @Deprecated
    public static String getDebugNameOf(Initializer setter) {
        for (Class<?> cl = setter.getClass(); cl != null; cl = cl.getSuperclass()) {

            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(setter);

                if (!(replacement instanceof SerializedLambda)) break;

                SerializedLambda lambda = (SerializedLambda) replacement;
                return lambda.getImplClass() + "::" + lambda.getImplMethodName();
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }

        Class<?> ownClass = setter.getClass();
        if (ownClass == null) return "[ADDITIONAL ERROR: NULL CLASS]";
        String name = ownClass.getCanonicalName();
        if (name == null) return "[ADDITIONAL ERROR: NULL NAME]";
        return name;
    }*/

}
