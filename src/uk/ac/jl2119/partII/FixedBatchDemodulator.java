package uk.ac.jl2119.partII;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Often the samples / bit is constant,
 * so it is way simpler to specify how to translate a single *batch*
 * rather than the whole signal
 *
 * FixedBatchDemodulator simply feeds its child batches of the signal,
 * that correspond to a single bit
 */
public abstract class FixedBatchDemodulator implements ITransformer<Double, Byte> {
    protected final double batchDuration;
    protected final int bitsPerSample;
    protected final long sampleRate;

    protected FixedBatchDemodulator(double batchDurationInSeconds, long sampleRate) {
        this.batchDuration = batchDurationInSeconds;
        this.bitsPerSample = 1;
        this.sampleRate = sampleRate;
    }

    protected FixedBatchDemodulator(double batchDurationInSeconds, int bitsPerSample, long sampleRate) {
        this.batchDuration = batchDurationInSeconds;
        this.bitsPerSample = bitsPerSample;
        this.sampleRate = sampleRate;
    }

    @Override
    public Byte[] transform(Double[] input) {
        List<Boolean> bits = splitDataIntoBatches(input).stream()
                .flatMap(x -> Arrays.stream(transformBits(x)))
                .toList();

        return partitionBits(bits).stream()
                .map(this::collectBits)
                .toArray(Byte[]::new);
    }

    private Byte collectBits(List<Boolean> inputBooleans) {
        byte result = 0;
        for (int bit = 0; bit < inputBooleans.size(); bit++){
            if (!inputBooleans.get(bit)) {
                continue;
            }

            int bitShift = 7 - bit;
            int shiftedMask = 0x1 << bitShift;
            result = (byte) (result | shiftedMask);
        }
        return result;
    }

    private List<Double[]> splitDataIntoBatches(Double[] input) {
        List<Double[]> result = new ArrayList<>();
        double elapsedTime = 0;
        while(hasDataRemaining(input, elapsedTime)) {
            Double[] batch = getBatchAtTime(input, elapsedTime);
            result.add(batch);
            elapsedTime += batchDuration;
        }
        return result;
    }

    private boolean hasDataRemaining(Double[] input, double time) {
        int offset = getOffsetFromTime(time);
        return offset < input.length - 1;
    }

    private Double[] getBatchAtTime(Double[] input, double time) {
        int startIndex = getOffsetFromTime(time);
        time += batchDuration;
        int finishIndex = Math.min(getOffsetFromTime(time), input.length - 1);
        int length = finishIndex - startIndex;

        return StreamUtils.slice(input, startIndex, length);
    }

    private int getOffsetFromTime(double time) {
        return (int) Math.floor(time * sampleRate);
    }

    private List<List<Boolean>> partitionBits(List<Boolean> input) {
        UnmodifiableIterator<List<Boolean>> batchedIterator = Iterators
                .partition(input.iterator(), 8);
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
