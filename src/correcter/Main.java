package correcter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String SEND_FILE = "send.txt";
    private static final String ENCODED_FILE = "encoded.txt";
    private static final String RECEIVED_FILE = "received.txt";
    private static final String DECODED_FILE = "decoded.txt";
    private static final String HEX_FORMAT = "%02X";
    private static final String BIN_FORMAT = "%8s";

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Write a mode: ");
        String action = s.nextLine();
        switch (action) {
            case "encode":
                encode();
                break;
            case "send":
                send();
                break;
            case "decode":
                decode();
                break;
            default:
                break;
        }
    }

    private static void decode() {
        try {
            byte[] data = readFile(RECEIVED_FILE);
            byte[] correctedData = Hamming.correct(data);
            byte[] decodedData = Hamming.decode(correctedData);

            writeFile(DECODED_FILE, decodedData);

            System.out.println(RECEIVED_FILE);
            printBytes("hex view: ",HEX_FORMAT, data);
            printBytes("bin view: ", BIN_FORMAT, data);
            System.out.printf("\n%s\n", DECODED_FILE);
            printBytes("correct: ", BIN_FORMAT, correctedData);
            printBytes("hex view: ",HEX_FORMAT, correctedData);
            printBytes("decode: ", BIN_FORMAT, decodedData);
            printBytes("hex view: ",HEX_FORMAT, decodedData);
            System.out.printf("text view: %s", new String(decodedData));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void send() {
        try {
            byte[] data = readFile(ENCODED_FILE);
            byte[] corruptedData = corruptData(data);

            writeFile(RECEIVED_FILE, corruptedData);

            System.out.println(ENCODED_FILE);
            printBytes("hex view: ",HEX_FORMAT, data);
            printBytes("bin view: ", BIN_FORMAT, data);
            System.out.printf("\n%s\n", RECEIVED_FILE);
            printBytes("bin view: ", BIN_FORMAT, corruptedData);
            printBytes("hex view: ",HEX_FORMAT, corruptedData);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void encode() {
        try {
            byte[] data = readFile(SEND_FILE);
            byte[] encodedData = Hamming.encode(data);

            writeFile(ENCODED_FILE, encodedData);

            System.out.println(SEND_FILE);
            System.out.printf("text view: %s\n", new String(data));
            printBytes("hex view: ",HEX_FORMAT, data);
            printBytes("bin view: ", BIN_FORMAT, data);
            System.out.printf("\n%s\n", ENCODED_FILE);
            printBytes("expand: ", BIN_FORMAT, encodedData, "..(.).(...).", "..$1.$2.");
            printBytes("parity: ", BIN_FORMAT, encodedData);
            printBytes("hex view: ", HEX_FORMAT, encodedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printBytes(String label, String format, byte[] data) {
        printBytes(label, format, data, null, null);
    }

    private static void printBytes(String label, String format, byte[] data, String regexReplace, String replacement) {
        StringBuilder sb = new StringBuilder(label);
        String s;
        boolean toBinary = BIN_FORMAT.equals(format);
        for (byte b: data) {
            if (toBinary) {
                s = String.format(format, Integer.toBinaryString(b & 255)).replace(" ", "0");
            } else {
                s = String.format(format, b);
            }
            if (regexReplace != null && replacement != null) {
                s = s.replaceAll(regexReplace, replacement);
            }
            sb.append(s).append(" ");
        }
        System.out.println(sb);
    }

    private static byte[] readFile(String fileName) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        byte[] data = fileInputStream.readAllBytes();
        fileInputStream.close();
        return data;
    }

    private static void writeFile(String fileName, byte[] data) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static byte[] corruptData(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Random random = new Random();
        for (byte b: data) {
            baos.write(b ^ 1 << random.nextInt(8));
        }
        return baos.toByteArray();
    }
}
