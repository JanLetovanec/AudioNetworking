package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.FixedBatchModulator;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

public class FSKModulator extends FixedBatchModulator {
    private final double baseFrequency;

    /**
     * Binary Frequency shift keying
     */
    public FSKModulator(double baseFrequency, int symbolDurationInFrames, long sampleRate) {
        super(symbolDurationInFrames, sampleRate);
        this.baseFrequency = baseFrequency;
    }

    @Override
    protected void transformBits(Boolean[] bits, BufferWavWriter writer){
        Boolean bit = bits[0];
        try {
            double frequency = bit ? (baseFrequency * 2) : baseFrequency;
            writer.writeFrequency((float)frequency, batchSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
