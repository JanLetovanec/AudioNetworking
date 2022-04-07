package uk.ac.cam.jl2119.partII.Evaluation.sims;

import uk.ac.cam.jl2119.partII.Framework.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.cam.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSDecoder;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RS_CorrectionRateSimulator implements ISimulatorGenerator<Integer> {
    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;
    private final int min;
    private final int max;
    private final double noiseLevel;

    public RS_CorrectionRateSimulator(ITransformer<Byte, Double> modulator,
                                      ITransformer<Double, Byte> demodulator,
                                      double noiseLevel,
                                      int min, int max) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(0.5));
        this.demodulator = demodulator;
        this.min = min;
        this.max = max;
        this.noiseLevel = noiseLevel;
    }

    public RS_CorrectionRateSimulator(ITransformer<Byte, Double> modulator,
                                      ITransformer<Double, Byte> demodulator,
                                      double noiseLevel, double attenuationFactor,
                                      int min, int max) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(attenuationFactor));
        this.demodulator = demodulator;
        this.min = min;
        this.max = max;
        this.noiseLevel = noiseLevel;
    }

    @Override
    public Map<Integer, Simulator<Integer>> getSimulators() {
        Map<Integer, Simulator<Integer>> result = new HashMap<>();
        Integer[] parameters = getValues();
        Arrays.stream(parameters)
                .forEach(param -> result.put(param, new RSSim(param)));
        return result;
    }

    private Integer[] getValues() {
        Integer[] values = new Integer[max - min];
        for (int i = 0; i < max - min; i++) {
            values[i] = min + i;
        }
        return values;
    }

    private class RSSim extends Simulator<Integer> {
        private final ITransformer<Byte, Double> modem;
        private final ITransformer<Double, Byte> demodem;
        private final
        ITransformer<Double, Double> noise;

        protected RSSim(Integer correctionSymbolCount) {
            super(correctionSymbolCount);
            double stdDeviation = Math.sqrt(noiseLevel);
            noise = new AWGNTransformer(stdDeviation);
            modem = new ComposedTransformer<>(new RSEncoder(correctionSymbolCount), modulator);
            demodem = new ComposedTransformer<>(demodulator, new RSDecoder(correctionSymbolCount));
        }

        @Override
        public Byte[] getReceivedData(Byte[] input) {
            return (new ComposedTransformer<>(modem, noise, demodem)).transform(input);
        }

        @Override
        public Double[] getTransmittedSignal(Byte[] input) {
            return modem.transform(input);
        }

        @Override
        public Double[] getReceivedSignal(Byte[] input) {
            return (new ComposedTransformer<>(modem, noise)).transform(input);
        }
    }
}
