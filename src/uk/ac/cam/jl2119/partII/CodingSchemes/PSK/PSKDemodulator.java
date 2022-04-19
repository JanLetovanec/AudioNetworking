package uk.ac.cam.jl2119.partII.CodingSchemes.PSK;

import uk.ac.cam.jl2119.partII.Framework.FixedBatchDemodulator;
import uk.ac.cam.jl2119.partII.WavManipulation.BufferWavWriter;

public class PSKDemodulator extends FixedBatchDemodulator {
    private final double frequency;
    private final long sampleRate;

    public PSKDemodulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit), sampleRate);
        this.frequency = frequency;
        this.sampleRate = sampleRate;
    }

    private static double getBatchSize(double frequency, int cyclesPerBit) {
        return ((double) cyclesPerBit/ frequency);
    }

    @Override
    protected Boolean[] transformBits(Double[] batch) {
        boolean resultBit = getBitFromBatch(batch);
        return new Boolean[] {resultBit};
    }

    private boolean getBitFromBatch(Double[] batch) {
        Double[] baseSignal = getBaseSignal(batch.length);
        double inPhaseComponent = dotProductSignals(batch, baseSignal);
        return inPhaseComponent > 0;
    }

    private Double[] getBaseSignal(int buffSize) {
        BufferWavWriter writer = new BufferWavWriter(buffSize, sampleRate);
        writer.writeFrequency(frequency, 0);
        return writer.getBuffer();
    }

    private double dotProductSignals(Double[] signalOne, Double[] signalTwo) {
        double result = 0d;
        for (int i = 0; i < signalOne.length; i++) {
            result += signalOne[i] * signalTwo[i];
        }
        return result;
    }
}
