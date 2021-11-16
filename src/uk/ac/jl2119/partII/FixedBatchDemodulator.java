package uk.ac.jl2119.partII;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
import java.util.List;

public abstract class FixedBatchDemodulator implements ITransformer<Double, Byte> {
    int samplesPerBatch;

    protected FixedBatchDemodulator(int samplesPerBatch) {
        this.samplesPerBatch = samplesPerBatch;
    }

    @Override
    public Byte[] transform(Double[] input) {
        int byteBatchSize = 8 * samplesPerBatch;
        List<List<Double>> batchedInput = partitionData(input, byteBatchSize);

        Byte[] output = batchedInput.stream()
                .map(byteBatch -> transformByte(byteBatch.toArray(Double[]::new)))
                .toArray(Byte[]::new);

        return output;
    }

    private Byte transformByte(Double[] byteBatch) {
        List<List<Double>> batchedInput = partitionData(byteBatch, samplesPerBatch);
        Boolean[] transformedBits = batchedInput.stream()
                .map(batch -> transformBit(batch.toArray(Double[]::new)))
                .toArray(Boolean[]::new);
        byte result = 0;
        for (int bit = 0; bit < 8; bit++){
            if (!transformedBits[bit]) {
                continue;
            }

            int bitShift = 7 - bit;
            int shiftedMask = 0x1 << bitShift;
            result = (byte) (result | shiftedMask);
        }
        return result;
    }

    private List<List<Double>> partitionData(Double[] input, int batchSize) {
        UnmodifiableIterator<List<Double>> batchedIterator = Iterators
                .partition(Arrays.stream(input).iterator(), batchSize);
        List<List<Double>> batchedInput = Lists.newArrayList(batchedIterator);
        return batchedInput;
    }


    protected abstract Boolean transformBit(Double[] batch);
}
