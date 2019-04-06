package ru.hse.utils;

/**
 * Класс генерирует целые в заданном диапазоне.
 * Правая граница всегда включена в генерацию.
 */
public class Random {

    private static long seed;

    private Random() {
    }

    public static int getInt(int leftBound, int rightBound) {
        return new java.util.Random().ints(1, leftBound, rightBound + 1).findFirst().getAsInt();
    }

    public static int[] getInts(int n, int leftBound, int rightBound) {
        return new java.util.Random().ints(n, leftBound, rightBound + 1).toArray();
    }

    public static void setSeed(long seed) {
        Random.seed = seed;
    }

    public static long getSeed() {
        return seed;
    }
}
