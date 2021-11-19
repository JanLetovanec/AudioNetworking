package uk.ac.jl2119.partII.PSK;

import uk.ac.jl2119.partII.FixedBatchModulator;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

public class PSKModulator extends FixedBatchModulator {
    private double currentPhase;

    // Rather than adding/subbing Pi set phase to constants,
    // Because floating point will make things off-sync eventually
    private final double BASE_PHASE = 0;
    private final double OFF_PHASE = Math.PI;
    private final double EPSILON = 0.0001;

    private final double frequency;

    public PSKModulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit, sampleRate), sampleRate);
        currentPhase = BASE_PHASE;
        this.frequency = frequency;
    }

    private static int getBatchSize(double frequency, int cyclesPerBit, long sampleRate) {
        long samplesPerCycle = Math.round(Math.floor(sampleRate / frequency));
        long samplesPerBit = cyclesPerBit * samplesPerCycle;
        return (int) samplesPerBit;
    }

    @Override
    protected void transformBit(Boolean bit, BufferWavWriter writer) {
        updatePhase(bit);
        writeBit(writer);
    }

    private void writeBit(BufferWavWriter writer) {
        try {
            writer.writeFrequency(frequency, batchSize, currentPhase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double updatePhase(Boolean newBit) {
        if (!newBit) {
            return currentPhase;
        }

        return flipPhase();
    }

    private double flipPhase() {
        boolean isInBasePhase = (currentPhase - BASE_PHASE) < EPSILON;
        if (isInBasePhase) {
            return OFF_PHASE;
        }
        return BASE_PHASE;
    }
}
