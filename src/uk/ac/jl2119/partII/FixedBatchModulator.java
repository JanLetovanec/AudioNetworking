package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

import java.util.Arrays;


/**
 * Often the samples / bit is constant
 * and it is way simpler to specify how to translate a single bit
 * rather than the whole signal
 *
 * FixedBatchModulator is dual to the FixedBatchDemodulator
 */
public abstract class FixedBatchModulator implements ITransformer<Byte, Double> {
    protected final int batchSize;
    protected final long sampleRate;

    public FixedBatchModulator(int batchSize, long sampleRate) {
        this.batchSize = batchSize;
        this.sampleRate = sampleRate;
    }

    @Override
    public Double[] transform(Byte[] input) {
        int numOfSamples = input.length * batchSize * 8; // Byte is 8 bits
        BufferWavWriter writer = new BufferWavWriter(numOfSamples, sampleRate);

        Arrays.stream(input).forEach(dataByte -> transformByte(writer, dataByte));

        return writer.getBuffer();
    }

    private void transformByte(BufferWavWriter writer, byte dataByte) {
        for (int maskBit = 0x80; maskBit > 0; maskBit = maskBit >>> 1) {
            Boolean currentBit = (dataByte & maskBit) > 0;
            transformBit(currentBit, writer);
        }
    }

    /**
     * @param bit - bit value to transform (T = 1, F = 0)
     * @param writer - BufferWriter to use to write the bytes
     */
    protected abstract void transformBit(Boolean bit, BufferWavWriter writer);
}
