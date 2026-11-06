package com.azerstar.util;

/** Hesablanan dəyərlər bir yerdə. İstifadəçi düsturu sonra dəyişəcək. */
public final class Formulas {

    private Formulas() {
    }

    /** Müvəqqəti: ləpə faizi = təmiz ləpə çəkisi / götürülmüş nümunə çəkisi * 100. */
    public static String kernelPercentSql(String analysisAlias) {
        return "ROUND(" + analysisAlias + ".kernel_weight_g / " + analysisAlias + ".sample_weight_g * 100, 2)";
    }

    public static double kernelPercent(double kernelG, double sampleG) {
        return sampleG <= 0 ? 0 : Math.round(kernelG / sampleG * 10000.0) / 100.0;
    }
}
