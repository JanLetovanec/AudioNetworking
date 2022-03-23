package uk.ac.jl2119.partII.utils;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StreamUtils {
    public static List<List<Byte>> partitionData(Byte[] input, int batchSize) {
        UnmodifiableIterator<List<Byte>> batchedIterator = Iterators
                .partition(Arrays.stream(input).iterator(), batchSize);
        return Lists.newArrayList(batchedIterator);
    }

    public static Byte[] padData(Byte[] input, int targetBlockSize) {
        int padSize = (int)Math.ceil(input.length*1.0 / targetBlockSize) * targetBlockSize;
        Byte[] newInput = new Byte[padSize];
        copyBytesIn(newInput, input, 0, input.length);
        initializeFromOffset(newInput, input.length);

        return  newInput;
    }

    public static Byte[] padData(int targetBlockSize) {
        return padData(new Byte[0], targetBlockSize);
    }

    public static void copyBytesIn(Byte[] destination, Byte[] source, int start, int length) {
        if (length >= 0) System.arraycopy(source, start, destination, 0, length);
    }

    private static void initializeFromOffset(Byte[] destination, int offset) {
        for (int i = offset; i < destination.length; i++) {
            destination[i] = (byte) 0;
        }
    }

    public static Double[] addSignals(Double[] signal, Double[] noise) {
        Stream<Double> signalStream = Arrays.stream(signal);
        Stream<Double> noiseStream = Arrays.stream(noise);
        return Streams
                .zip(signalStream, noiseStream,
                        (signalFrame, noiseFrame) -> clamp(signalFrame + noiseFrame))
                .toArray(Double[]::new);
    }

    private static double clamp(double input) {
        return Math.min(1, Math.max(-1, input));
    }
}
