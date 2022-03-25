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
        super(getBatchSize(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT, sampleRate), 2, sampleRate);
        this.frequency = DEFAULT_FREQUENCY;
    }

    public QAMModulator(double frequency, int cyclesPerBit, long sampleRate) {
        super(getBatchSize(frequency, cyclesPerBit, sampleRate), 2, sampleRate);
        this.frequency = frequency;
    }

    private static int getBatchSize(double frequency, int cyclesPerBit, long sampleRate) {
        long samplesPerCycle = Math.round(Math.floor(sampleRate / frequency));
        long samplesPerBit = cyclesPerBit * samplesPerCycle;
        return (int) samplesPerBit;
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
        try {
            writer.writeFrames(Boxer.unBox(signal), signal.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double[] getComponent(double phase, boolean shouldInvert) {
        double phaseOffset = shouldInvert
                ? 0
                : Math.PI;
        BufferWavWriter writer = new BufferWavWriter(batchSize, sampleRate);
        try {
            writer.writeFrequency(frequency, batchSize, phase + phaseOffset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.getBuffer();
    }
}
