package uk.ac.cam.jl2119.partII.CodingSchemes.QAM;

import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Framework.FixedBatchDemodulator;
import uk.ac.cam.jl2119.partII.WavManipulation.BufferWavWriter;

public class QAMDemodulator extends FixedBatchDemodulator{
    private static final double DEFAULT_FREQUENCY = SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    private final double frequency;

    public QAMDemodulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit), 2, sampleRate);
        this.frequency = frequency;
    }

    public QAMDemodulator(long sampleRate) {
        super(getBatchSize(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT), 2, sampleRate);
        this.frequency = DEFAULT_FREQUENCY;
    }

    private static double getBatchSize(double frequency, int cyclesPerBit) {
        return ((double) cyclesPerBit/ frequency);
    }

    @Override
    protected Boolean[] transformBits(Double[] batch) {
        boolean inPhaseBit = getBitFromBatch(batch, 0);
        boolean quadPhaseBit = getBitFromBatch(batch, Math.PI/2);

        return new Boolean[] {inPhaseBit, quadPhaseBit};
    }

    private boolean getBitFromBatch(Double[] batch, double phase) {
        Double[] baseSignal = getBaseSignal(batch.length, phase);
        double phaseComponent = dotProductSignals(batch, baseSignal);
        return phaseComponent > 0;
    }

    private Double[] getBaseSignal(int buffSize, double phase) {
        BufferWavWriter writer = new BufferWavWriter(buffSize, sampleRate);
        writer.writeFrequency(frequency, phase);
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
