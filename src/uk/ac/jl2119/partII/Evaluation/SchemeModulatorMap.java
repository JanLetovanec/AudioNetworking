package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.UEF.UEFDemodulator;
import uk.ac.jl2119.partII.UEF.UEFModulator;

public class SchemeModulatorMap {
    public static long DEFAULT_SAMPLE_RATE = 44100;

    public enum CodingScheme {
        PSK,
        UEF,
        FSK,
        RS_PSK,
        RS_UEF
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
        switch (scheme){
            case FSK:
            case PSK:
            case UEF:
                ITransformer<Byte, Double> modem = new UEFModulator(true, DEFAULT_SAMPLE_RATE);
                ITransformer<Double, Byte> demodem = new UEFDemodulator(true, DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            case RS_PSK:
            case RS_UEF:
            default: return null;
        }
    }
}
