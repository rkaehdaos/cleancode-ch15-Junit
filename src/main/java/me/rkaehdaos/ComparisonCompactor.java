package me.rkaehdaos;

import junit.framework.Assert;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private int contextLength;
    private String expected;
    private String actual;
    private int prefixIndex;
    private int suffixLength;


    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    @SuppressWarnings("deprecation")
    public String formatCompactedComparison(String message) {
        String compactExpected=expected;
        String compactActual=actual;
        if (canBeCompacted()) {
            findCommonPrefixAndSuffix();
            compactExpected = compactString(expected);
            compactActual = compactString(actual);
        }
        return Assert.format(message, compactExpected, compactActual);
    }

    private boolean canBeCompacted() {
        return !ShouldNotBeCompacted();
    }

    private boolean ShouldNotBeCompacted() {
        return expected == null ||
                actual == null ||
                expected.equals(actual);
    }

    private void findCommonPrefixAndSuffix() {
        findCommonPrefix();
        suffixLength = 0;
        for (; !(suffixOverlapsPrefix(suffixLength)); suffixLength++) {
            if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)) {
                break;
            }
        }
    }

    private String compactString(String s) {
        return new StringBuilder()
                .append(startingEllipsis())
                .append(startingContext())
                .append(DELTA_START)
                .append(delta(s))
                .append(DELTA_END)
                .append(endingContext())
                .append(endingEllipsis())
                .toString();
    }

    private String endingContext() {
        int contextStart = expected.length() - suffixLength;
        int contextEnd = Math.min(contextStart + contextLength, expected.length());
        return expected.substring(contextStart, contextEnd);
    }


    private String startingEllipsis() {
        return prefixIndex > contextLength ? ELLIPSIS : "";
    }

    private String startingContext() {
        return expected.substring(Math.max(0, prefixIndex - contextLength), prefixIndex);
    }

    private String delta(String s) {
        int deltaStart = prefixIndex;
        int deltaEnd = s.length() - suffixLength;
        return s.substring(deltaStart, deltaEnd);
    }

    private String endingEllipsis() {
        return suffixLength > contextLength ? ELLIPSIS : "";
    }



    private char charFromEnd(String s, int i) {
        return s.charAt(s.length() - i - 1);
    }

    private boolean suffixOverlapsPrefix(int suffixLength) {
        return actual.length() - suffixLength <= prefixIndex || expected.length() - suffixLength <= prefixIndex;
    }

    private void findCommonPrefix() {
        prefixIndex = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixIndex < end; prefixIndex++) {
            if (expected.charAt(prefixIndex) != actual.charAt(prefixIndex)) {
                break;
            }
        }
    }

}