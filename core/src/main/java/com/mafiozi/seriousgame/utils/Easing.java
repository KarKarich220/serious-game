package com.mafiozi.seriousgame.utils;

/**
 * @see <a href="https://easings.net/">Easing Functions Cheat Sheet</a>
 */
public final class Easing {

    private Easing() { }

    // ----- Linear -----

    public static float linear(float x) {
        return x;
    }

    // ----- Quad -----

    public static float easeInQuad(float x) {
        return x * x;
    }

    public static float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static float easeInOutQuad(float x) {
        return x < 0.5f ? 2 * x * x : 1 - (float) Math.pow(-2 * x + 2, 2) / 2;
    }

    // ----- Cubic -----

    public static float easeInCubic(float x) {
        return x * x * x;
    }

    public static float easeOutCubic(float x) {
        return 1 - (float) Math.pow(1 - x, 3);
    }

    public static float easeInOutCubic(float x) {
        return x < 0.5f ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2;
    }

    // ----- Quart -----

    public static float easeInQuart(float x) {
        return x * x * x * x;
    }

    public static float easeOutQuart(float x) {
        return 1 - (float) Math.pow(1 - x, 4);
    }

    public static float easeInOutQuart(float x) {
        return x < 0.5f ? 8 * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 4) / 2;
    }

    // ----- Quint -----

    public static float easeInQuint(float x) {
        return x * x * x * x * x;
    }

    public static float easeOutQuint(float x) {
        return 1 - (float) Math.pow(1 - x, 5);
    }

    public static float easeInOutQuint(float x) {
        return x < 0.5f ? 16 * x * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 5) / 2;
    }

    // ----- Sine -----

    public static float easeInSine(float x) {
        return 1 - (float) Math.cos(x * Math.PI / 2);
    }

    public static float easeOutSine(float x) {
        return (float) Math.sin(x * Math.PI / 2);
    }

    public static float easeInOutSine(float x) {
        return -( (float) Math.cos(Math.PI * x) - 1) / 2;
    }

    // ----- Expo -----

    public static float easeInExpo(float x) {
        return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : 1 - (float) Math.pow(2, -10 * x);
    }

    public static float easeInOutExpo(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return x < 0.5f
            ? (float) Math.pow(2, 20 * x - 10) / 2
            : (2 - (float) Math.pow(2, -20 * x + 10)) / 2;
    }

    // ----- Circ -----

    public static float easeInCirc(float x) {
        return 1 - (float) Math.sqrt(1 - x * x);
    }

    public static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - (x - 1) * (x - 1));
    }

    public static float easeInOutCirc(float x) {
        return x < 0.5f
            ? (1 - (float) Math.sqrt(1 - (2 * x) * (2 * x))) / 2
            : ((float) Math.sqrt(1 - (-2 * x + 2) * (-2 * x + 2)) + 1) / 2;
    }

    // ----- Back -----

    private static final float C1 = 1.70158f;
    private static final float C2 = C1 * 1.525f;
    private static final float C3 = C1 + 1;

    public static float easeInBack(float x) {
        return C3 * x * x * x - C1 * x * x;
    }

    public static float easeOutBack(float x) {
        return 1 + C3 * (float) Math.pow(x - 1, 3) + C1 * (x - 1) * (x - 1);
    }

    public static float easeInOutBack(float x) {
        return x < 0.5f
            ? ( (float) Math.pow(2 * x, 2) * ((C2 + 1) * 2 * x - C2) ) / 2
            : ( (float) Math.pow(2 * x - 2, 2) * ((C2 + 1) * (x * 2 - 2) + C2) + 2 ) / 2;
    }

    // ----- Elastic -----

    private static final float ELASTIC_C4 = (float) (2 * Math.PI) / 3;

    public static float easeInElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return -(float) Math.pow(2, 10 * x - 10) * (float) Math.sin((x * 10 - 10.75f) * ELASTIC_C4);
    }

    public static float easeOutElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return (float) Math.pow(2, -10 * x) * (float) Math.sin((x * 10 - 0.75f) * ELASTIC_C4) + 1;
    }

    public static float easeInOutElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        float c5 = (float) (2 * Math.PI) / 4.5f;
        return x < 0.5f
            ? -(float) Math.pow(2, 20 * x - 10) * (float) Math.sin((20 * x - 11.125f) * c5) / 2
            : (float) Math.pow(2, -20 * x + 10) * (float) Math.sin((20 * x - 11.125f) * c5) / 2 + 1;
    }

    // ----- Bounce -----

    public static float easeInBounce(float x) {
        return 1 - easeOutBounce(1 - x);
    }

    public static float easeOutBounce(float x) {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x - 1.5f / d1) * (x - 1.5f / d1) + 0.75f;
        } else if (x < 2.5f / d1) {
            return n1 * (x - 2.25f / d1) * (x - 2.25f / d1) + 0.9375f;
        } else {
            return n1 * (x - 2.625f / d1) * (x - 2.625f / d1) + 0.984375f;
        }
    }

    public static float easeInOutBounce(float x) {
        return x < 0.5f
            ? (1 - easeOutBounce(1 - 2 * x)) / 2
            : (1 + easeOutBounce(2 * x - 1)) / 2;
    }
}