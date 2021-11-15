package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ComposedTransformer;

/**
 * Transformer for UEF encoding
 * Simply composes StartStop D2D transformer
 * with FSK D2A transformer
 */
public class UEFTransformer extends ComposedTransformer<Byte, Double> {

    public UEFTransformer(double baseFrequency,boolean originalMode, long sampleRate) {
        super(new StartStopTransformer(), getFSKTransformer(baseFrequency, originalMode, sampleRate));
    }

    private static FSKTransformer getFSKTransformer(double baseFrequency,boolean originalMode, long sampleRate) {
        int cyclesPerZero = originalMode ? 1 : 4;
        long framesPerCycle = (Math.round(Math.floor(sampleRate / baseFrequency)));
        long framesPerZero = framesPerCycle * cyclesPerZero;

        return new FSKTransformer(baseFrequency, (int)framesPerZero, sampleRate);
    }
}
