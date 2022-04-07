package uk.ac.cam.jl2119.partII.CodingSchemes.PSK;

import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Framework.FixedBatchModulator;
import uk.ac.cam.jl2119.partII.WavManipulation.BufferWavWriter;

public class PSKModulator extends FixedBatchModulator {

    private static final double DEFAULT_FREQUENCY = SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    private double currentPhase;
    private final double frequency;

    public PSKModulator(long sampleRate) {
        super(getBatchSize(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT), sampleRate);
        this.frequency = DEFAULT_FREQUENCY;
    }

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
        currentPhase = getUpdatedPhase(bit);
        writeBit(writer);
    }

    private void writeBit(BufferWavWriter writer) {
        writer.writeFrequency(frequency, batchDuration, currentPhase);
    }

    private double getUpdatedPhase(Boolean newBit) {
        if (!newBit) {
            return currentPhase;
        }

        return flipPhase();
    }

    private double flipPhase() {
        // Rather than adding/subbing Pi set phase to constants,
        // Because floating point will make things off-sync eventually
        final double BASE_PHASE = 0;
        final double OFF_PHASE = Math.PI;
        final double EPSILON = 0.0001;

        boolean isInBasePhase = Math.abs(currentPhase - BASE_PHASE) < EPSILON;
        if (isInBasePhase) {
            return OFF_PHASE;
        }
        return BASE_PHASE;
    }
}
