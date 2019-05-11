package ru.hse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.utils.Encrypter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class EncrypterTest {


    @Test
    void testShifrShortString() throws UnsupportedEncodingException {
        String message = "Vova - good!";
        short[] key = new short[]{1, -2, 2, 0, 1, -2, -2, -2,1,2,-2,1,1,2,2,0,0,1,0,-2,0,1,-2};
        byte[] keyBytes = Encrypter.toBytes(key);
        byte[] encryptmessage = Encrypter.encrypt(message.getBytes(),keyBytes);
        byte[] decryptmessage = Encrypter.decrypt(encryptmessage,keyBytes);

        System.out.println("Исходное сообщение в байтах: " +Arrays.toString(message.getBytes()));
        System.out.println("Исходное сообщение: " + new String(message.getBytes(), "utf-8"));
        System.out.println("Зашифрованное сообщение в байтах: " + Arrays.toString(encryptmessage));
        System.out.println("Зашифрованное сообщение: " + new String(encryptmessage, "utf-8"));
        System.out.println("Расшифровнаное сообщение в байтах: " + Arrays.toString(decryptmessage));
        System.out.println("Расшифровнаное сообщение: " + new String (decryptmessage, "utf-8"));

        Assertions.assertEquals(message, new String(decryptmessage, "utf-8"));
    }

    @Test
    void testShifrLongString() throws UnsupportedEncodingException {
        String message = "Сюжет детективного триллера «Подмена» основан на реальных событиях, произошедших в Лос-Анджелесе в конце 20-х годов прошлого столетия. Главная героиня фильма, молодая женщина Кристин Коллинс, обращается в полицию с заявление о пропаже ее маленького сына. В скором времени полиция находит ребенка, и возвращает его матери, но женщина заявляет, что это не ее сын. Однако полиция и городские власти не хотят признавать этот факт, и тогда Кристин принимает решение обратиться за помощью к журналистам. Чтобы не дать женщине скомпрометировать руководство города, ее объявляют сумасшедшей, и отправляют в клинику для душевно больных. Но даже такие испытания и преграды, не способны остановить настоящую любящую мать. Кристин решает сама докопаться до правды, и начинает собственное расследование.";
        short[] key = new short[]{1, -2, 2, 0, 1, -2, -2, -2};
        byte[] keyBytes = Encrypter.toBytes(key);
        byte[] encryptmessage = Encrypter.encrypt(message.getBytes(),keyBytes);
        byte[] decryptmessage = Encrypter.decrypt(encryptmessage,keyBytes);

        System.out.println("Исходное сообщение в байтах: " +Arrays.toString(message.getBytes()));
        System.out.println("Исходное сообщение: " + new String(message.getBytes(), "utf-8"));
        System.out.println("Зашифрованное сообщение в байтах: " + Arrays.toString(encryptmessage));
        System.out.println("Зашифрованное сообщение: " + new String(encryptmessage, "utf-8"));
        System.out.println("Расшифровнаное сообщение в байтах: " + Arrays.toString(decryptmessage));
        System.out.println("Расшифровнаное сообщение: " + new String (decryptmessage, "utf-8"));

        Assertions.assertEquals(message, new String(decryptmessage, "utf-8"));
    }

    @Test
    void testShifrFile() throws IOException {
        Path testFilePath = Paths.get("src/main/resources/orange.jpg");
        System.out.println("Path exists? " + testFilePath.toFile().exists());

        short[] key = new short[]{1, -2, 2, 0, 1, -2, -2, -2};
        byte[] keyBytes = Encrypter.toBytes(key);

        byte[] encryptfile = Encrypter.encrypt(getBytesFromFile(testFilePath.toFile()),keyBytes);
        writeBytesToFileNio(encryptfile, "src/main/resources/orange2.jpg");
        byte[] decryptfile = Encrypter.decrypt(encryptfile,keyBytes);
        writeBytesToFileNio(decryptfile, "src/main/resources/orange3.jpg");

        Assertions.assertArrayEquals(getBytesFromFile(testFilePath.toFile()), decryptfile);
    }


    static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;

        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    static void writeBytesToFileNio(byte[] bFile, String fileDest) {
        try {
            Path path = Paths.get(fileDest);
            Files.write(path, bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
