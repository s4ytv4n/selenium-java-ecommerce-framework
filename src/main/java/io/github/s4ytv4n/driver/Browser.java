package io.github.s4ytv4n.driver;

import java.util.Arrays;

public enum Browser {
    CHROME, FIREFOX, EDGE;

    public static Browser from(String name) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported browser '" + name + "'. Use one of: " + Arrays.toString(values())));
    }
}
