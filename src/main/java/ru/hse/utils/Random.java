package ru.hse.utils;

/**
 * Класс генерирует целые или вещественные числа в заданном диапазоне, а также массивы
 * Если границы равны 0, то генерируется любое число
 * Если границы равны, но не 0, то возвращается левая граница
 * В остальных случаях генерируется число в заданном диапазоне
 */
public class Random {

    private static long seed;
    private static double left = 0, right = 0;

    private Random() {
    }

    public static double getDouble() {
        if (left == 0 && right == 0)
            return new java.util.Random().nextDouble();
        else if (left == right)
            return left;
        else
            return left + new java.util.Random().nextDouble() * (right - left);
    }

    public static int getInt() {
        if (left == 0 && right == 0)
            return new java.util.Random().nextInt();
        else if (left == right)
            return (int) left;
        else
            return (int) (left + Math.round(new java.util.Random().nextDouble() * (right - left)));
    }

    public static int[] getInts(int n) {
        int[] mas = new int[n];
        for (int i = 0; i < n; i++)
            mas[i] = Random.getInt();
        return mas;
    }

    public static double[] getIntsCastedToDouble(int n) {
        double[] mas = new double[n];
        for (int i = 0; i < n; i++) {
            if (new java.util.Random().nextDouble() >= 0.5)
                mas[i] = -1;
            else
                mas[i] = 1;
        }
        return mas;
    }

    public static double[] getDoubles(int n) {
        double[] mas = new double[n];
        for (int i = 0; i < n; i++)
            mas[i] = new java.util.Random().nextDouble();
        return mas;
    }

    public static void setSeed(long seed) {
        Random.seed = seed;
    }

    public static long getSeed() {
        return seed;
    }

    public static void setBounds(double leftBound, double rightBound) {
        left = leftBound;
        right = rightBound;
    }
}
