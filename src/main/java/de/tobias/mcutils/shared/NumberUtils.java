package de.tobias.mcutils.shared;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {

    public static String stringRoundLong(Long value, int places) {
        DecimalFormat df = new DecimalFormat("#." + ("#".repeat(places)));
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }

    public static String stringRoundDouble(Double value, int places) {
        DecimalFormat df = new DecimalFormat("#." + ("#".repeat(places)));
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }

    public static String stringRoundFloat(Float value, int places) {
        DecimalFormat df = new DecimalFormat("#." + ("#".repeat(places)));
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }

    public static Integer getRandomSign() {
        return Math.random() >= 0.5 ? 1 : -1;
    }
}
