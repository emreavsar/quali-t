package controllers;

import com.google.common.base.Function;

/**
 * Created by corina on 23.04.2015.
 */
public class Helper {
    public static Function<String, Long> parseLongFunction() {
        return ParseLongFunction.INSTANCE;
    }

    private enum ParseLongFunction implements Function<String, Long> {
        INSTANCE;

        public Long apply(String input) {
            return Long.valueOf(input);
        }
    }
}
