package org.bitbuckets.lib.core;

import java.util.Optional;

/**
 * Utility class
 */
public class Path {

    final String[] pathComposition;

    public Path(String[] pathComposition) {
        this.pathComposition = pathComposition;
    }

    public String getTail() {
        return pathComposition[pathComposition.length - 1];
    }

    public String getAsTablePath() {
        StringBuilder builder = new StringBuilder();

        for (String string : pathComposition) {
            builder.append(string).append("/");
        }

        return builder.toString();
    }

    public String getAsFlatTablePath() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < pathComposition.length; i++) {
            if (i == pathComposition.length - 1) {
                builder.append(pathComposition[i]);
            } else {
                builder.append(pathComposition[i]).append("|");
            }
        }

        return builder.toString();
    }

    public Optional<String> getAsLastTwoPath() {
        if (pathComposition.length < 2) {
            return Optional.empty();
        }

        int lastIndex = pathComposition.length - 1;
        int secondLastIndex = lastIndex - 1;

        String toReturn = pathComposition[secondLastIndex] + "/" + pathComposition[lastIndex];
        return Optional.of(toReturn);

    }


    public Optional<String> getAsLastTwoPathFlat() {
        if (pathComposition.length < 2) {
            return Optional.empty();
        }

        int lastIndex = pathComposition.length - 1;
        int secondLastIndex = lastIndex - 1;

        String toReturn = pathComposition[secondLastIndex] + "|" + pathComposition[lastIndex];
        return Optional.of(toReturn);
    }

    public Path append(String next) {
        if (next == null) throw new IllegalStateException("what");

        if (pathComposition.length == 0) {
            return new Path(new String[] {next} );
        }

        String[] toReturnArray = new String[pathComposition.length + 1];
        for (int i = 0; i < pathComposition.length; i++) {
            toReturnArray[i] = pathComposition[i];
        }

        toReturnArray[pathComposition.length] = next;

        return new Path(toReturnArray);
    }

    public int length() {
        return pathComposition.length;
    }


}
