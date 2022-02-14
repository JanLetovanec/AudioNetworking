package uk.ac.jl2119.partII.utils;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
import java.util.List;

public class StreamUtils {
    public static List<List<Byte>> partitionData(Byte[] input, int batchSize) {
        UnmodifiableIterator<List<Byte>> batchedIterator = Iterators
                .partition(Arrays.stream(input).iterator(), batchSize);
        return Lists.newArrayList(batchedIterator);
    }

    public static Byte[] padData(Byte[] input, int targetBlockSize) {
        int padSize = input.length % targetBlockSize;
        Byte[] newInput = new Byte[input.length + padSize];
        copyBytesIn(newInput, input);
        initializeFromOffset(newInput, input.length, (byte) 0);

        return  newInput;
    }

    private static void copyBytesIn(Byte[] destination, Byte[] source) {
        for (int i = 0; i < source.length; i++) {
            destination[i] = source[i];
        }
    }
    private static void initializeFromOffset(Byte[] destination, int offset, byte value) {
        for (int i = offset; i < destination.length; i++) {
            destination[i] = value;
        }
    }
}
