package uk.ac.jl2119.partII.utils;

import java.util.Arrays;

public class Boxer {
    public static Byte[] box(byte[] input) {
        Byte[] outputBuffer = new Byte[input.length];
        for (int i = 0; i < input.length; i++) {outputBuffer[i] = input[i];}
        return outputBuffer;
    }

    public static Double[] box(double[] input) {
        return Arrays.stream(input).boxed().toArray(Double[]::new);
    }

    public static byte[] unBox(Byte[] input) {
        byte[] outputBuffer = new byte[input.length];
        for (int i = 0; i < input.length; i++) {outputBuffer[i] = input[i];}
        return outputBuffer;
    }

    public static double[] unBox(Double[] input) {
        return Arrays.stream(input).mapToDouble(Double::doubleValue).toArray();
    }

    public static Byte[] convert(long[] input) {
        Byte[] outputBuffer = new Byte[input.length];
        for (int i = 0; i < input.length; i++) {outputBuffer[i] = (byte) (input[i] %0XFF);}
        return outputBuffer;
    }
}
