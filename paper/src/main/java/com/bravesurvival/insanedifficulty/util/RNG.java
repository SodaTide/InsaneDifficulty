package com.bravesurvival.insanedifficulty.util;

import java.util.Random;

public final class RNG {

    private static final Random RANDOM = new Random();

    private RNG() {}

    public static boolean chance(double probability) {
        return RANDOM.nextDouble() < probability;
    }

    public static int range(int min, int max) {
        if (min >= max) return min;
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static double range(double min, double max) {
        return min + RANDOM.nextDouble() * (max - min);
    }

    public static Random getRandom() {
        return RANDOM;
    }
}
