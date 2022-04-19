package uk.ac.cam.jl2119.partII.Evaluation;

import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.QAM.QAMDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.QAM.QAMModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.*;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSDecoder;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSEncoder;
import uk.ac.cam.jl2119.partII.Filters.LowPassFilter;
import uk.ac.cam.jl2119.partII.Framework.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;

public class SchemeModulatorMap {
    public static long DEFAULT_SAMPLE_RATE = 48000;
    public static double DEFAULT_BASE_FREQUENCY = 1200;

    public enum CodingScheme {
        DPSK,
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
                double secondsPerCycle = 1.0 / DEFAULT_BASE_FREQUENCY;
                modem = new FSKModulator(DEFAULT_BASE_FREQUENCY, secondsPerCycle, DEFAULT_SAMPLE_RATE);
                demodem = new ComposedTransformer<>(
                        new LowPassFilter(DEFAULT_SAMPLE_RATE, DEFAULT_BASE_FREQUENCY * 2),
                        new FSKDemodulator(DEFAULT_BASE_FREQUENCY, secondsPerCycle, DEFAULT_SAMPLE_RATE)
                );
                return new SchemePair(modem, demodem);
            case DPSK:
                modem = new DPSKModulator(DEFAULT_SAMPLE_RATE);
                demodem = new DPSKDemodulator(DEFAULT_SAMPLE_RATE);
                return new SchemePair(modem, demodem);
            case PSK:
                modem = new PSKModulator(DEFAULT_BASE_FREQUENCY, 1, DEFAULT_SAMPLE_RATE);
                demodem = new PSKDemodulator(DEFAULT_BASE_FREQUENCY, 1, DEFAULT_SAMPLE_RATE);
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
                double stepSize = 5.0 / DEFAULT_SAMPLE_RATE;
                modem = new UEFModulator(true, DEFAULT_SAMPLE_RATE);
                demodem = new UEFSyncDemodulator(DEFAULT_BASE_FREQUENCY, true, DEFAULT_SAMPLE_RATE, stepSize);
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
