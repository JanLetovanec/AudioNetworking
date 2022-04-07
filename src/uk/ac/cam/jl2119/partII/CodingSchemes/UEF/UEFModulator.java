package uk.ac.cam.jl2119.partII.CodingSchemes.UEF;

import uk.ac.cam.jl2119.partII.Framework.ComposedTransformer;

/**
 * Transformer for UEF encoding
 * Simply composes StartStop D2D transformer
 * with FSK D2A transformer
 */
public class UEFModulator extends ComposedTransformer<Byte, Double> {
    private static final double BASE_FREQUENCY = 1200;

    public UEFModulator(boolean originalMode, long sampleRate) {
        super(new StartStopAdder(), getFSKTransformer(BASE_FREQUENCY, originalMode, sampleRate));
    }

    public UEFModulator(double baseFrequency, int cyclesPerBit, long sampleRate) {
        super(new StartStopAdder(), getFSKTransformer(baseFrequency, cyclesPerBit, sampleRate));
    }

    private static FSKModulator getFSKTransformer(double baseFrequency, boolean originalMode, long sampleRate) {
        int cyclesPerZero = originalMode ? 1 : 4;
        return getFSKTransformer(baseFrequency, cyclesPerZero, sampleRate);
    }

    private static FSKModulator getFSKTransformer(double baseFrequency, int cyclesPerBit, long sampleRate) {
        double secondsPerBit = cyclesPerBit / baseFrequency;
        return new FSKModulator(baseFrequency, secondsPerBit, sampleRate);
    }
}
