package de.tobias.mcutils.shared;

import java.math.BigDecimal;

public class BigNumberFormatter {

    public String THOUSAND_SHORT = "k";
    public String MILLION_SHORT = "M";
    public String BILLION_SHORT = "B";
    public String TRILLION_SHORT = "T";
    public String QUADRILLION_SHORT = "QD";
    public String QUINTILLION_SHORT = "QT";
    public String SEXTILLION_SHORT = "SEXT";
    public String SEPTILLION_SHORT = "SEPT";
    public String OCTILLION_SHORT = "OCT";
    public String NONILLION_SHORT = "OCT";


    public int OTHER_DECIMALS = 3;
    public int THOUSAND_DECIMALS = 2;
    public int MILLION_DECIMALS = 3;
    public int BILLION_DECIMALS = 3;
    public int TRILLION_DECIMALS = 3;
    public int QUADRILLION_DECIMALS = 3;
    public int NORMAL_DECIMALS = 4;

    public BigNumberFormatter() {}

    public String formatLong(Long number) {
        if (number == Long.MAX_VALUE) return "MAX";
        return formatBigDecimal(new BigDecimal(number));
    }

    public String formatDouble(Double number) {
        if (number == Double.MAX_VALUE) return "MAX";
        return formatBigDecimal(new BigDecimal(number));
    }

    public String formatInteger(Integer number) {
        if (number == Integer.MAX_VALUE) return "MAX";
        return formatBigDecimal(new BigDecimal(number));
    }

    /* Thanks to: https://www.turito.com/blog/one-on-one-online-tutoring/what-comes-after-trillion :) */
    public String getShortByExponentialCount(Integer exponent) {
        if(exponent >= 45) return "QTT";
        if(exponent >= 42) return "TRE";
        if(exponent >= 39) return "DUO";
        if(exponent >= 36) return "UND";
        if(exponent >= 33) return "DEC";
        if(exponent >= 30) return "NO";
        if(exponent >= 27) return "OC";
        if(exponent >= 24) return "SE";
        if(exponent >= 21) return "SX";
        if(exponent >= 18) return "QI";
        if(exponent >= 15) return "QD";
        if(exponent >= 12) return "T";
        if(exponent >= 9) return "B";
        if(exponent >= 6) return "M";
        if(exponent >= 3) return "k";
        return "";
    }

    public String formatBigDecimal(BigDecimal number) {
        Integer numbersAfterZero = 0;
        BigDecimal temp = number;
        while(temp.compareTo(new BigDecimal(1000)) > 0 && !getShortByExponentialCount(numbersAfterZero+3).equalsIgnoreCase("")) {
            temp = temp.divide(new BigDecimal(1000));
            numbersAfterZero+=3;
        }
        return NumberUtils.stringRoundDouble(temp.doubleValue(), OTHER_DECIMALS) + getShortByExponentialCount(numbersAfterZero);
    }
}
