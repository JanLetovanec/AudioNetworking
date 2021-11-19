package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

import java.util.Arrays;

public class FSKModulator implements ITransformer<Byte, Double> {
    private final int symbolDurationInFrames;
    private final double baseFrequency;
    protected long sampleRate;

    /**
     * Binary Frequency shift keying
     */
    public FSKModulator(double baseFrequency, int symbolDurationInFrames, long sampleRate) {
        this.sampleRate = sampleRate;
        this.baseFrequency = baseFrequency;
        this.symbolDurationInFrames = symbolDurationInFrames;
    }

    @Override
    public Double[] transform(Byte[] input) {
        int numOfSamples = input.length * symbolDurationInFrames * 8; // Byte is 8 bits
        BufferWavWriter writer = new BufferWavWriter(numOfSamples, sampleRate);

        Arrays.stream(input).forEach(dataByte -> transformByte(writer, dataByte));

        return writer.getBuffer();
    }

    private void transformByte(BufferWavWriter writer, byte dataByte) {
        for (int maskBit = 0x80; maskBit > 0; maskBit = maskBit >> 1) {
            Boolean currentBit = (dataByte & maskBit) > 0;
            transformBit(currentBit, writer);
        }
    }

    private void transformBit(Boolean bit, BufferWavWriter writer){
        try {
            double frequency = bit ? (baseFrequency * 2) : baseFrequency;
            writer.writeFrequency((float)frequency, symbolDurationInFrames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
