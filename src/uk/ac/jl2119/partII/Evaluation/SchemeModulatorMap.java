package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Filters.LowPassFilterTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.PSK.PSKDemodulator;
import uk.ac.jl2119.partII.PSK.PSKModulator;
import uk.ac.jl2119.partII.UEF.FSKDemodulator;
import uk.ac.jl2119.partII.UEF.FSKModulator;
import uk.ac.jl2119.partII.UEF.UEFDemodulator;
import uk.ac.jl2119.partII.UEF.UEFModulator;

public class SchemeModulatorMap {
    public static long DEFAULT_SAMPLE_RATE = 44100;
    public static double DEFAULT_BASE_FREQUENCY = 1200;

    public enum CodingScheme {
        PSK,
        UEF,
        FSK
    }

    public static class SchemePair {
        public ITransformer<Byte, Double> modem;
        public ITransformer<Double, Byte> demodem;

        public SchemePair(ITransformer<Byte, Double> modem, ITransformer<Double, Byte> demodem) {
            this.modem = modem;
            this.demodem = demodem;
        }
    }

    public static SchemePair getDefaultScheme(CodingScheme scheme) {
        ITransformer<Byte, Double> modem;
        ITransformer<Double, Byte> demodem;
        switch (scheme){
            case FSK:
                long samplesPerCycle = Math.round(Math.floor(DEFAULT_SAMPLE_RATE / DEFAULT_BASE_FREQUENCY));
                modem = new FSKModulator(DEFAULT_BASE_FREQUENCY, (int)samplesPerCycle, DEFAULT_SAMPLE_RATE);
                demodem = new ComposedTransformer<>(
                        new LowPassFilterTransformer(DEFAULT_SAMPLE_RATE, DEFAULT_BASE_FREQUENCY * 2),
                        new FSKDemodulator(DEFAULT_BASE_FREQUENCY, (int)samplesPerCycle, DEFAULT_SAMPLE_RATE)
                );
                return new SchemePair(modem, demodem);
            case PSK:
                modem = new PSKModulator(DEFAULT_SAMPLE_RATE);
                demodem = new PSKDemodulator(DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            case UEF:
                modem = new UEFModulator(true, DEFAULT_SAMPLE_RATE);
                demodem = new UEFDemodulator(true, DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            default: return null;
        }
    }
}
