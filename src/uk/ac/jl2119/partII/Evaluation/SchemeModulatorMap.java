package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Filters.LowPassFilterTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.PSK.PSKDemodulator;
import uk.ac.jl2119.partII.PSK.PSKModulator;
import uk.ac.jl2119.partII.QAM.QAMDemodulator;
import uk.ac.jl2119.partII.QAM.QAMModulator;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.UEF.*;

public class SchemeModulatorMap {
    public static long DEFAULT_SAMPLE_RATE = 44100;
    public static double DEFAULT_BASE_FREQUENCY = 1200;

    public enum CodingScheme {
        PSK,
        UEF,
        FSK,
        QAM,
        SYNC_UEF
    }

    public enum Enrichment {
        RS
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
            case QAM:
                modem = new QAMModulator(DEFAULT_SAMPLE_RATE);
                demodem = new QAMDemodulator(DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            case SYNC_UEF:
                modem = new UEFModulator(true, DEFAULT_SAMPLE_RATE);
                demodem = new UEFSyncDemodulator(DEFAULT_BASE_FREQUENCY, true, DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            default: return null;
        }
    }

    public static SchemePair enrichScheme(SchemePair scheme, Enrichment enrichment) {
        switch (enrichment){
            case RS:
                ITransformer<Byte, Double> modem = new ComposedTransformer<>(
                        new RSEncoder(),
                        scheme.modem);
                ITransformer<Double, Byte> demodem = new ComposedTransformer<>(
                        scheme.demodem,
                        new RSDecoder());
                return new SchemePair(modem, demodem);
            default:
                return scheme;
        }
    }
}
