package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

import java.util.Arrays;


/**
 * Often the time / bit is constant,
 * so it is way simpler to specify how to translate a single bit
 * rather than the whole signal
 *
 * FixedBatchModulator is dual to the FixedBatchDemodulator
 */
public abstract class FixedBatchModulator implements ITransformer<Byte, Double> {
    protected final double batchDuration;
    protected final long sampleRate;
    protected final int bitsPerBatch;

    public FixedBatchModulator(double batchDurationInSeconds, long sampleRate) {
        this.batchDuration = batchDurationInSeconds;
        this.sampleRate = sampleRate;
        this.bitsPerBatch = 1;
    }

    public FixedBatchModulator(double batchDurationInSeconds, int bitsPerBatch, long sampleRate) {
        this.batchDuration = batchDurationInSeconds;
        this.sampleRate = sampleRate;
        this.bitsPerBatch = bitsPerBatch;
    }

    @Override
    public Double[] transform(Byte[] input) {
        double totalTime = input.length  * batchDuration * 8 / bitsPerBatch;
        BufferWavWriter writer = new BufferWavWriter(totalTime, sampleRate);

        Arrays.stream(input).forEach(dataByte -> transformByte(writer, dataByte));

        return writer.getBuffer();
    }

    private void transformByte(BufferWavWriter writer, byte dataByte) {
        Boolean[] currentBits = new Boolean[bitsPerBatch];
        int bitCount = 0;
        for (int maskBit = 0x80; maskBit > 0; maskBit = maskBit >>> 1) {
            boolean currentBit = (dataByte & maskBit) > 0;
            currentBits[bitCount] = currentBit;
            bitCount++;

            if (bitCount == bitsPerBatch) {
                transformBits(currentBits, writer);
                bitCount = 0;
            }
        }
    }

    /**
     * @param bits - bit values to transform (T = 1, F = 0)
     * @param writer - BufferWriter to use to write the bytes
     */
    protected abstract void transformBits(Boolean[] bits, BufferWavWriter writer);
}
