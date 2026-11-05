package io.github.s4ytv4n.utils;

public final class Prices {

    private Prices() {
    }

    /** "Rs. 1,500" -> 1500 */
    public static int parse(String label) {
        String digits = label.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse price from '" + label + "'");
        }
        return Integer.parseInt(digits);
    }
}
