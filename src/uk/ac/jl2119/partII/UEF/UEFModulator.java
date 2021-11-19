package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ComposedTransformer;

/**
 * Transformer for UEF encoding
 * Simply composes StartStop D2D transformer
 * with FSK D2A transformer
 */
public class UEFModulator extends ComposedTransformer<Byte, Double> {

    public UEFModulator(double baseFrequency, boolean originalMode, long sampleRate) {
        super(new StartStopAdder(), getFSKTransformer(baseFrequency, originalMode, sampleRate));
    }

    private static FSKModulator getFSKTransformer(double baseFrequency, boolean originalMode, long sampleRate) {
        int cyclesPerZero = originalMode ? 1 : 4;
        long framesPerCycle = (Math.round(Math.floor(sampleRate / baseFrequency)));
        long framesPerZero = framesPerCycle * cyclesPerZero;

        return new FSKModulator(baseFrequency, (int)framesPerZero, sampleRate);
    }
}
