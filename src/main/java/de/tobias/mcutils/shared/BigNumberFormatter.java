package de.tobias.mcutils.shared;

import java.math.BigDecimal;

public class BigNumberFormatter {

    public String THOUSAND_SHORT = "k";
    public String MILLION_SHORT = "M";
    public String BILLION_SHORT = "B";
    public String TRILLION_SHORT = "T";
    public String QUADRILLION_SHORT = "QD";

    public int THOUSAND_DECIMALS = 2;
    public int MILLION_DECIMALS = 3;
    public int BILLION_DECIMALS = 3;
    public int TRILLION_DECIMALS = 3;
    public int QUADRILLION_DECIMALS = 3;
    public int NORMAL_DECIMALS = 4;

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
        return NumberUtils.stringRoundDouble(number/1.0, NORMAL_DECIMALS);
    }

    public String formatInteger(Integer number) {
        if (number == Integer.MAX_VALUE) return "MAX";
        if (number >= 1000 * 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000 * 1000), BILLION_DECIMALS) + BILLION_SHORT;
        if (number >= 1000 * 1000)
            return NumberUtils.stringRoundDouble(number / (1000.0 * 1000), MILLION_DECIMALS) + MILLION_SHORT;
        if (number >= 1000) return NumberUtils.stringRoundDouble(number / (1000.0), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return NumberUtils.stringRoundDouble(number/1.0, NORMAL_DECIMALS);
    }

    public String formatBigDecimal(BigDecimal number) {
        if (number.divide(new BigDecimal(1000*1000*1000)).divide(new BigDecimal(1000)).longValue() >= 1000) {
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000*1000*1000).multiply(new BigDecimal(1000*1000))).doubleValue(), QUADRILLION_DECIMALS) + QUADRILLION_SHORT;
        }

        if (number.divide(new BigDecimal(1000*1000*1000)).longValue() >= 1000) {
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000*1000*1000).multiply(new BigDecimal(1000))).doubleValue(), TRILLION_DECIMALS) + TRILLION_SHORT;
        }

        if (number.divide(new BigDecimal(1000*1000)).longValue() >= 1000) {
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000*1000*1000)).doubleValue(), BILLION_DECIMALS) + BILLION_SHORT;
        }

        if (number.divide(new BigDecimal(1000)).longValue() >= 1000) {
            return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000*1000)).doubleValue(), MILLION_DECIMALS) + MILLION_SHORT;
        }

        if (number.longValue() >= 1000) return NumberUtils.stringRoundDouble(number.divide(new BigDecimal(1000)).doubleValue(), THOUSAND_DECIMALS) + THOUSAND_SHORT;
        return NumberUtils.stringRoundDouble(number.doubleValue(), NORMAL_DECIMALS);
    }
}
