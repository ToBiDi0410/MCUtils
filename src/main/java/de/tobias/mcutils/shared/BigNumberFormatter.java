package de.tobias.mcutils.shared;

import java.math.BigDecimal;

public class BigNumberFormatter {

    public String THOUSAND_SHORT = "k";
    public String MILLION_SHORT = "M";
    public String BILLION_SHORT = "M";

    public int THOUSAND_DECIMALS = 2;
    public int MILLION_DECIMALS = 3;
    public int BILLION_DECIMALS = 5;

    public BigNumberFormatter() {}

    public String formatLong(Long number) {
        if (number == Long.MAX_VALUE) return "MAX";
        if (number >= 1000 * 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000 * 1000), BILLION_DECIMALS) + BILLION_SHORT;
        if (number >= 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000), MILLION_DECIMALS) + MILLION_SHORT;
        if (number >= 1000) return NumberUtils.stringRoundDouble(number / (1000.0), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return number.toString();
    }

    public String formatDouble(Double number) {
        if (number == Double.MAX_VALUE) return "MAX";
        if (number >= 1000 * 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000 * 1000), BILLION_DECIMALS) + BILLION_SHORT;
        if (number >= 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000), MILLION_DECIMALS) + MILLION_SHORT;
        if (number >= 1000) return NumberUtils.stringRoundDouble(number / (1000.0), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return number.toString();
    }

    public String formatInteger(Integer number) {
        if (number == Integer.MAX_VALUE) return "MAX";
        if (number >= 1000 * 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000 * 1000), BILLION_DECIMALS) + BILLION_SHORT;
        if (number >= 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000), MILLION_DECIMALS) + MILLION_SHORT;
        if (number >= 1000) return NumberUtils.stringRoundDouble(number / (1000.0), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return number.toString();
    }

    public String formatBigDecimal(BigDecimal number) {
        if (number.longValue() >= 1000 * 1000 * 1000)
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal("1000000000")).doubleValue(), BILLION_DECIMALS) + BILLION_SHORT;
        if (number.longValue() >= 1000 * 1000)
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000000)).doubleValue(), MILLION_DECIMALS) + MILLION_SHORT;
        if (number.longValue() >= 1000) return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000)).doubleValue(), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return number.toString();
    }
}
