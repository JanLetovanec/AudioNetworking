package uk.ac.jl2119.partII.QAM;

import uk.ac.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.jl2119.partII.FixedBatchModulator;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;
import uk.ac.jl2119.partII.utils.Boxer;
import uk.ac.jl2119.partII.utils.StreamUtils;

public class QAMModulator extends FixedBatchModulator {
    private static final double DEFAULT_FREQUENCY = SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    private final double frequency;

    public QAMModulator(long sampleRate) {
        super(getBatchSize(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT), 2, sampleRate);
        this.frequency = DEFAULT_FREQUENCY;
    }

    public QAMModulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit), 2, sampleRate);
        this.frequency = frequency;
    }

    private static double getBatchSize(double frequency, int cyclesPerBit) {
        return ((double) cyclesPerBit/ frequency);
    }

    @Override
    protected void transformBits(Boolean[] bits, BufferWavWriter writer) {
        double cosPhase = Math.PI/2;

        Double[] inPhase = getComponent(0, bits[0]);
        Double[] quadPhase = getComponent(cosPhase, bits[1]);
        Double[] signal = StreamUtils.addSignals(inPhase, quadPhase);
        writeSignal(signal, writer);
    }

    private void writeSignal(Double[] signal, BufferWavWriter writer) {
        double[] buffer = Boxer.unBox(signal);
        writer.writeSignal(buffer, batchDuration);
    }

    private Double[] getComponent(double phase, boolean shouldInvert) {
        double phaseOffset = shouldInvert
                ? 0
                : Math.PI;
        BufferWavWriter writer = new BufferWavWriter(batchDuration, sampleRate);
        writer.writeFrequency(frequency, batchDuration, phase + phaseOffset);
        return writer.getBuffer();
    }
}
