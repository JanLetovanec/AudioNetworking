package uk.ac.cam.jl2119.partII.UEF;

import uk.ac.cam.jl2119.partII.FixedBatchModulator;
import uk.ac.cam.jl2119.partII.WavManipulation.BufferWavWriter;

public class FSKModulator extends FixedBatchModulator {
    private final double baseFrequency;

    /**
     * Binary Frequency shift keying
     */
    public FSKModulator(double baseFrequency, double symbolDurationInSeconds, long sampleRate) {
        super(symbolDurationInSeconds, sampleRate);
        this.baseFrequency = baseFrequency;
    }

    @Override
    protected void transformBits(Boolean[] bits, BufferWavWriter writer){
        Boolean bit = bits[0];
        double frequency = bit ? (baseFrequency * 2) : baseFrequency;
        writer.writeFrequency(frequency, batchDuration, 0);
    }
}
