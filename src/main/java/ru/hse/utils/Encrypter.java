package ru.hse.utils;

public class Encrypter {

    public static byte[] encrypt(byte input[], byte key[]) {
        byte[] res = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            res[i] = (byte) (input[i] ^ key[i % key.length]);
        }
        return res;
    }

    public static byte[] decrypt(byte input[], byte key[]) {
       return encrypt(input, key);
    }

    public static byte[] toBytes(short[] key) {
        int len = key.length;
        int newlen = len%4 == 0? len/4: len/4+1;
        byte[] mas = new byte[newlen];
        int limit = 6;
        int j = 0;
        for(int i = 0; i < len; i++){
            key[i] = (short) (key[i]<<limit);
            mas[j] |= key[i];
            if (limit != 0)
                limit -= 2;
            else {
                limit = 6;
                j++;
            }
        }
        return mas;
    }


}
