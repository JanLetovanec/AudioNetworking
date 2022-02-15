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
        int padSize = (int)Math.ceil(input.length*1.0 / targetBlockSize) * targetBlockSize;
        Byte[] newInput = new Byte[padSize];
        copyBytesIn(newInput, input, 0, input.length);
        initializeFromOffset(newInput, input.length);

        return  newInput;
    }

    public static void copyBytesIn(Byte[] destination, Byte[] source, int start, int length) {
        if (length >= 0) System.arraycopy(source, start, destination, 0, length);
    }

    private static void initializeFromOffset(Byte[] destination, int offset) {
        for (int i = offset; i < destination.length; i++) {
            destination[i] = (byte) 0;
        }
    }
}
