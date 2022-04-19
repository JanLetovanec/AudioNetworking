package uk.ac.cam.jl2119.partII.CodingSchemes.PSK;

import uk.ac.cam.jl2119.partII.Framework.FixedBatchModulator;
import uk.ac.cam.jl2119.partII.WavManipulation.BufferWavWriter;

public class PSKModulator extends FixedBatchModulator {
    private final double frequency;

    public PSKModulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit), sampleRate);
        this.frequency = frequency;
    }

    private static double getBatchSize(double frequency, int cyclesPerBit) {
        return ((double) cyclesPerBit/ frequency);
    }

    @Override
    protected void transformBits(Boolean[] bits, BufferWavWriter writer) {
        Boolean bit = bits[0];
        writeBit(writer, bit);
    }

    private void writeBit(BufferWavWriter writer, Boolean bit) {
        double phase = bit ? 0 : Math.PI;
        writer.writeFrequency(frequency, batchDuration, phase);
    }
}
