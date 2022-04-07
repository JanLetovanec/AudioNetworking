package uk.ac.cam.jl2119.partII.CodingSchemes.UEF;

import uk.ac.cam.jl2119.partII.Framework.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Filters.LowPassFilter;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;

public class UEFDemodulator extends ComposedTransformer<Double, Byte> {
    private static final double BASE_FREQUENCY = 1200;

    public UEFDemodulator(boolean originalMode, long sampleRate) {
        super(getLPF(BASE_FREQUENCY*2, sampleRate),
                getComposite(BASE_FREQUENCY, originalMode, sampleRate));
    }

    public UEFDemodulator(double baseFrequency, int cyclesPerBit, long sampleRate) {
        super(getLPF(baseFrequency*2, sampleRate),
                getComposite(baseFrequency, cyclesPerBit, sampleRate));
    }

    private static LowPassFilter getLPF(double baseFrequency, long sampleRate) {
        return new LowPassFilter(sampleRate, baseFrequency);
    }

    private static ITransformer<Double, Byte> getComposite(double baseFrequency,
                                                           boolean originalMode,
                                                           long sampleRate) {
        int cyclesPerBit = originalMode ? 1 : 4;
        return getComposite(baseFrequency, cyclesPerBit, sampleRate);
    }

    private static ITransformer<Double, Byte> getComposite(double baseFrequency, int cyclesPerBit, long sampleRate) {
        ITransformer<Double, Byte> fskDemod = getFSKDemod(baseFrequency, cyclesPerBit, sampleRate);
        ITransformer<Byte, Byte> startStop = new StartStopRemover();
        return new ComposedTransformer<>(fskDemod, startStop);
    }

    private static FSKDemodulator getFSKDemod(double baseFrequency, int cyclesPerBit, long sampleRate) {
        double secondsPerBit = cyclesPerBit / baseFrequency;
        return new FSKDemodulator(baseFrequency, secondsPerBit, sampleRate);
    }
}
