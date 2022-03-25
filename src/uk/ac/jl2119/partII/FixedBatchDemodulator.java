package uk.ac.jl2119.partII;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
import java.util.List;


/**
 * Often the samples / bit is constant
 * and it is way simpler to specify how to translate a single *batch*
 * rather than the whole signal
 *
 * FixedBatchDemodulator simply feeds its child batches of the signal,
 * that correspond to a single bit
 */
public abstract class FixedBatchDemodulator implements ITransformer<Double, Byte> {
    protected final int samplesPerBatch;
    protected final int bitsPerSample;

    protected FixedBatchDemodulator(int samplesPerBatch) {
        this.samplesPerBatch = samplesPerBatch;
        this.bitsPerSample = 1;
    }

    protected FixedBatchDemodulator(int samplesPerBatch, int bitsPerSample) {
        this.samplesPerBatch = samplesPerBatch;
        this.bitsPerSample = bitsPerSample;
    }

    @Override
    public Byte[] transform(Double[] input) {
        int byteBatchSize = (8/bitsPerSample) * samplesPerBatch;
        List<List<Double>> batchedInput = partitionData(input, byteBatchSize);

        return batchedInput.stream()
                .map(byteBatch -> transformByte(byteBatch.toArray(Double[]::new)))
                .toArray(Byte[]::new);
    }

    private Byte transformByte(Double[] byteBatch) {
        Boolean[] transformedBits = getTransformedBits(byteBatch);
        return collectBits(transformedBits);
    }

    private Boolean[] getTransformedBits(Double[] byteBatch) {
        List<List<Double>> batchedInput = partitionData(byteBatch, samplesPerBatch);
        return batchedInput.stream()
                .flatMap(batch -> Arrays.stream(transformBits(batch.toArray(Double[]::new))))
                .toArray(Boolean[]::new);
    }

    private Byte collectBits(Boolean[] inputBooleans) {
        byte result = 0;
        for (int bit = 0; bit < inputBooleans.length; bit++){
            if (!inputBooleans[bit]) {
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
        return Lists.newArrayList(batchedIterator);
    }

    /**
     *
     * @param batch part of signal, that corresponds to a single bit,
     *              is always of size `samplesPerBatch`
     * @return bit (TRUE = 1 / FALSE = 0), this batch represents
     */
    protected abstract Boolean[] transformBits(Double[] batch);
}
