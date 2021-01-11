package ru.timeconqueror.timecore.animation;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static net.minecraft.util.math.MathHelper.*;

/**
 * Easings from https://easings.net/.
 */
public class Ease {
    public static float inSine(float x) {
        return 1 - cos((float) ((x * PI) / 2));
    }

    public static float outSine(float x) {
        return sin((float) ((x * PI) / 2));
    }

    public static float inOutSine(float x) {
        return -(cos((float) (PI * x)) - 1) / 2;
    }

    public static float inCubic(float x) {
        return x * x * x;
    }

    public static float outCubic(float x) {
        return (float) (1 - pow(1 - x, 3));
    }

    public static float inOutCubic(float x) {
        return (float) (x < 0.5 ? 4 * x * x * x : 1 - pow(-2 * x + 2, 3) / 2);
    }

    public static float inQuint(float x) {
        return x * x * x * x * x;
    }

    public static float outQuint(float x) {
        return (float) (1 - pow(1 - x, 5));
    }

    public static float inOutQuint(float x) {
        return (float) (x < 0.5 ? 16 * x * x * x * x * x : 1 - pow(-2 * x + 2, 5) / 2);
    }

    public static float inCirc(float x) {
        return 1 - sqrt(1 - pow(x, 2));
    }

    public static float outCirc(float x) {
        return sqrt(1 - pow(x - 1, 2));
    }

    public static float inOutCirc(float x) {
        return x < 0.5
                ? (1 - sqrt(1 - pow(2 * x, 2))) / 2
                : (sqrt(1 - pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public static float inElastic(float x) {
        final float c4 = (float) ((2 * Math.PI) / 3);

        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : -pow(2, 10 * x - 10) * sin((x * 10 - 10.75F) * c4));
    }

    public static float outElastic(float x) {
        final float c4 = (float) ((2 * Math.PI) / 3);

        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : pow(2, -10 * x) * sin((x * 10 - 0.75F) * c4) + 1);
    }

    public static float inOutElastic(float x) {
        final float c5 = (float) ((2 * Math.PI) / 4.5F);

        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? -(pow(2, 20 * x - 10) * sin((20 * x - 11.125F) * c5)) / 2
                : (pow(2, -20 * x + 10) * sin((20 * x - 11.125F) * c5)) / 2 + 1);
    }

    public static float inQuad(float x) {
        return x * x;
    }

    public static float outQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static float inOutQuad(float x) {
        return (float) (x < 0.5 ? 2 * x * x : 1 - pow(-2 * x + 2, 2) / 2);
    }

    public static float inQuart(float x) {
        return x * x * x * x;
    }

    public static float outQuart(float x) {
        return (float) (1 - pow(1 - x, 4));
    }

    public static float inOutQuart(float x) {
        return (float) (x < 0.5 ? 8 * x * x * x * x : 1 - pow(-2 * x + 2, 4) / 2);
    }

    public static float inExpo(float x) {
        return (float) (x == 0 ? 0 : pow(2, 10 * x - 10));
    }

    public static float outExpo(float x) {
        return (float) (x == 1 ? 1 : 1 - pow(2, -10 * x));
    }

    public static float inOutExpo(float x) {
        return (float) (x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5 ? pow(2, 20 * x - 10) / 2
                : (2 - pow(2, -20 * x + 10)) / 2);
    }

    public static float inBack(float x) {
        final float c1 = 1.70158F;
        final float c3 = c1 + 1;

        return c3 * x * x * x - c1 * x * x;
    }

    public static float outBack(float x) {
        final float c1 = 1.70158F;
        final float c3 = c1 + 1;

        return (float) (1 + c3 * pow(x - 1, 3) + c1 * pow(x - 1, 2));
    }

    public static float inBounce(float x) {
        return 1 - outBounce(1 - x);
    }

    public static float outBounce(float x) {
        final float n1 = 7.5625F;
        final float d1 = 2.75F;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5F / d1) * x + 0.75F;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25F / d1) * x + 0.9375F;
        } else {
            return n1 * (x -= 2.625F / d1) * x + 0.984375F;
        }
    }

    public static float inOutBounce(float x) {
        return x < 0.5
                ? (1 - outBounce(1 - 2 * x)) / 2
                : (1 + outBounce(2 * x - 1)) / 2;
    }

}
