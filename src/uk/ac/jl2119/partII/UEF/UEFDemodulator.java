package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Filters.LowPassFilterTransformer;

public class UEFDemodulator extends ComposedTransformer<Double, Byte> {
    public <T1> UEFDemodulator(double baseFrequency,boolean originalMode, long sampleRate) {
        super(getLPF(baseFrequency, sampleRate),
                getComposite(baseFrequency, originalMode, sampleRate));
    }

    private static LowPassFilterTransformer getLPF(double baseFrequency, long sampleRate) {
        return new LowPassFilterTransformer(sampleRate, baseFrequency);
    }

    private static FSKDemodulator getFSKDemod(double baseFrequency,boolean originalMode, long sampleRate) {
        int cyclesPerZero = originalMode ? 1 : 4;
        long framesPerCycle = (Math.round(Math.floor(sampleRate / baseFrequency)));
        long framesPerZero = framesPerCycle * cyclesPerZero;

        return new FSKDemodulator(baseFrequency, (int) framesPerZero,sampleRate);
    }

    private static ITransformer<Double, Byte> getComposite(double baseFrequency,boolean originalMode, long sampleRate) {
        ITransformer<Double, Byte> fskDemod = getFSKDemod(baseFrequency, originalMode, sampleRate);
        ITransformer<Byte, Byte> startStop = new StartStopRemover();
        return new ComposedTransformer<Double, Byte>(fskDemod, startStop);
    }
}
