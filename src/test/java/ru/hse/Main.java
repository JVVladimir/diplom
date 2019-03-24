package ru.hse;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class Main {

    @Test
    public void testMethod() {
        System.out.println(fib(10));
    }

    @Test
    public void testMethod2() {
        System.out.println(fib2(3));
    }

    private int fib2(int n) {
        new Scanner(System.in).nextInt();
        if(n <= 1)
            return 1;
        int res = 2, n1 = 1, n2 = 1;
        for(int i = 3; i < n; i++) {
            res = n1 + n2;
            int a = n1;
            n1 = res;
            n2 = a;
        }
        return res;
    }

    public int fib(int n) {
        if(n == 1)
            return 0;
        else if (n == 2 || n == 3)
            return 1;
        return fib(n-1) + fib(n-2);
    }

}
