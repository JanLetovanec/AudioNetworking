package uk.ac.jl2119.partII.QAM;

import uk.ac.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.jl2119.partII.FixedBatchDemodulator;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;

public class QAMDemodulator extends FixedBatchDemodulator{
    private static final double DEFAULT_FREQUENCY = SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    private final double frequency;
    private final long sampleRate;

    public QAMDemodulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit, sampleRate), 2);
        this.frequency = frequency;
        this.sampleRate = sampleRate;
    }

    public QAMDemodulator(long sampleRate) {
        super(getBatchSize(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT, sampleRate), 2);
        this.frequency = DEFAULT_FREQUENCY;
        this.sampleRate = sampleRate;
    }

    private static int getBatchSize(double frequency, int cyclesPerBit, long sampleRate) {
        long samplesPerCycle = Math.round(Math.floor(sampleRate / frequency));
        long samplesPerBit = cyclesPerBit * samplesPerCycle;
        return (int) samplesPerBit;
    }

    @Override
    protected Boolean[] transformBits(Double[] batch) {
        boolean inPhaseBit = getBitFromBatch(batch, 0);
        boolean quadPhaseBit = getBitFromBatch(batch, Math.PI/2);

        return new Boolean[] {inPhaseBit, quadPhaseBit};
    }

    private boolean getBitFromBatch(Double[] batch, double phase) {
        Double[] baseSignal = getBaseSignal(samplesPerBatch, phase);
        double phaseComponent = dotProductSignals(batch, baseSignal);
        return phaseComponent > 0;
    }

    private Double[] getBaseSignal(int length, double phase) {
        try {
            BufferWavWriter writer = new BufferWavWriter(length, sampleRate);
            writer.writeFrequency(frequency, length, phase);
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
}
