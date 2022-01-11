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

    private String compactString(String source) {
        return new StringBuilder()
                .append(computeCommonPrefix())
                .append(DELTA_START)
                .append(source.substring(prefixIndex, source.length() - suffixLength))
                .append(DELTA_END)
                .append(computeCommonSuffix())
                .toString();
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

    private String computeCommonPrefix() {
        return (prefixIndex > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefixIndex - contextLength), prefixIndex);
    }

    private String computeCommonSuffix() {
        int end = Math.min(expected.length() - suffixLength + contextLength, expected.length());
        return expected.substring(expected.length() - suffixLength, end) + (expected.length() - suffixLength < expected.length() - contextLength ? ELLIPSIS : "");
    }

}