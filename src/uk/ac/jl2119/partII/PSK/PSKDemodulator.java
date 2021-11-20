package uk.ac.jl2119.partII.PSK;

import uk.ac.jl2119.partII.FixedBatchDemodulator;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

public class PSKDemodulator extends FixedBatchDemodulator {
    private double currentPhase;
    private final double frequency;
    private final long sampleRate;

    protected PSKDemodulator(double frequency, int samplesPerBatch, long sampleRate) {
        super(samplesPerBatch);
        this.frequency = frequency;
        this.sampleRate = sampleRate;
    }

    @Override
    protected Boolean transformBit(Double[] batch) {
        boolean resultBit = getBitFromBatch(batch);

        if (resultBit) {
            currentPhase = flipPhase();
        }

        return  resultBit;
    }

    private boolean getBitFromBatch(Double[] batch) {
        Double[] baseSignal = getBaseSignal(samplesPerBatch);
        double inPhaseComponent = dotProductSignals(batch, baseSignal);
        return inPhaseComponent > 0;
    }

    private Double[] getBaseSignal(int length) {
        try {
            BufferWavWriter writer = new BufferWavWriter(length, sampleRate);
            writer.writeFrequency(frequency, length, currentPhase);
            return writer.getBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }

    private double dotProductSignals(Double[] signalOne, Double[] signalTwo) {
        double result = 0d;
        for (int i = 0; i < signalOne.length; i++) {
            result += signalOne[i] * signalTwo[i];
        }
        return result;
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
