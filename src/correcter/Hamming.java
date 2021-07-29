package correcter;

import java.io.ByteArrayOutputStream;

public class Hamming {

    private static final int[][] DATA_BITS_POS = {
            {7, 6, 4},
            {7, 5, 4},
            {6, 5, 4}
    };

    private static final int[][] PARITY_BITS_POS = {
            {5, 3, 1},
            {5, 2, 1},
            {3, 2, 1}
    };

    private static final int[] PARITY_VALUES = {1, 2, 4, 8};

    public static byte[] encode(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte b: data) {
            baos.write(encodedByteOf(b));
            baos.write(encodedByteOf((byte) (b << 4)));
        }
        return baos.toByteArray();
    }

    public static byte[] correct(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte b: data) {
            baos.write(b ^ (1 << bitWithErrorPosition(b)));
        }
        return baos.toByteArray();
    }

    public static byte[] decode(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < data.length; i += 2) {
            baos.write(decodedByteOf(data[i], data[i + 1]));
        }
        return baos.toByteArray();
    }

    private static int bitWithErrorPosition(byte b) {
        byte[] currentParity = parityBitsOf(b);
        byte[] calculatedParity = calculateParityBits(b, PARITY_BITS_POS);
        int pos = 0;
        for (int i = 0; i < currentParity.length; i++) {
            if(currentParity[i] != calculatedParity[i]) {
                pos += PARITY_VALUES[i];
            }
        }
        return 8 - pos;
    }

    private static byte[] parityBitsOf(byte b) {
        byte[] currentParity = new byte[4];
        currentParity[0] = (byte) ((b >> 7) & 1);
        currentParity[1] = (byte) ((b >> 6) & 1);
        currentParity[2] = (byte) ((b >> 4) & 1);
        currentParity[3] = (byte) (b & 1);
        return currentParity;
    }

    private static byte[] calculateParityBits(byte b, int[][] positions) {
        byte[] parity = new byte[4];
        int oneCounter;
        for (int i = 0; i < 3; i++) {
            oneCounter = 0;
            for (int j = 0; j < 3; j++) {
                oneCounter += (b >> positions[i][j]) & 1;
            }
            parity[i] = (byte) (oneCounter % 2 == 0 ? 0 : 1);
        }
        parity[3] = 0;
        return parity;
    }

    private static byte encodedByteOf(byte b) {
        byte output = 0;
        byte[] parityBits = calculateParityBits(b, DATA_BITS_POS);
        output ^= parityBits[0] << 7;
        output ^= parityBits[1] << 6;
        output ^= ((b >> 2) & 32);
        output ^= parityBits[2] << 4;
        output ^= ((b >> 3) & 8);
        output ^= ((b >> 3) & 4);
        output ^= ((b >> 3) & 2);
        return output;
    }

    private static byte decodedByteOf(byte dataHigh, byte dataLow) {
        byte output = 0;
        output ^= (dataHigh << 2) & 128;
        output ^= (dataHigh << 3) & 64;
        output ^= (dataHigh << 3) & 32;
        output ^= (dataHigh << 3) & 16;
        output ^= (dataLow >> 2) & 8;
        output ^= (dataLow >> 1) & 4;
        output ^= (dataLow >> 1) & 2;
        output ^= (dataLow >> 1) & 1;
        return output;
    }

}
