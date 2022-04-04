package uk.ac.cam.jl2119.partII.Evaluation.sims.Scheme;

import uk.ac.cam.jl2119.partII.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;
import uk.ac.cam.jl2119.partII.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.cam.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.cam.jl2119.partII.Noises.RayleighFadingTransformer;
import uk.ac.cam.jl2119.partII.UEF.FSKDemodulator;
import uk.ac.cam.jl2119.partII.UEF.FSKModulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FSKSymbolTimeSim implements ISimulatorGenerator<Double> {
    private final double stdDev;
    private final int rayleighLength;
    private final double rayleighStdDev;
    private final double factor;

    private final int samples;
    private final double min;
    private final double max;

    public  FSKSymbolTimeSim(double stdDev, int rayleightLength, double min, double max, int samples, double attenuationFactor) {
        this.factor = attenuationFactor;
        this.stdDev = stdDev;
        this.rayleighLength = rayleightLength;
        this.rayleighStdDev = attenuationFactor;
        this.min = min;
        this.max = max;
        this.samples = samples;
    }

    public  FSKSymbolTimeSim(double stdDev, int rayleightLength, double min, double max, int samples) {
        this.factor = 0.5;
        this.stdDev = stdDev;
        this.rayleighLength = rayleightLength;
        this.rayleighStdDev = factor;
        this.min = min;
        this.max = max;
        this.samples = samples;
    }

    @Override
    public Map<Double, Simulator<Double>> getSimulators() {
        Map<Double, Simulator<Double>> result = new HashMap<>();
        Double[] params = getValues();
        Arrays.stream(params)
                .forEach(param -> result.put(param, new FSKSim(param)));
        return result;
    }

    private Double[] getValues() {
        Double[] values = new Double[samples];
        for (int i = 0; i < samples; i++) {
            Double value = min + (((max - min) * i) / samples);
            values[i] = value;
        }
        return values;
    }

    private class FSKSim extends Simulator<Double> {
        private final ITransformer<Byte, Double> modulator;
        private final ITransformer<Double, Byte> demodulator;
        private final ITransformer<Double, Double> noise;

        protected FSKSim(Double secondsPerSymbol) {
            super(secondsPerSymbol);
            modulator = new FSKModulator(SchemeModulatorMap.DEFAULT_BASE_FREQUENCY,
                    secondsPerSymbol,
                    SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
            demodulator = new FSKDemodulator(SchemeModulatorMap.DEFAULT_BASE_FREQUENCY,
                    secondsPerSymbol,
                    SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
            noise = new ComposedTransformer<>(
                    new AttenuatorTransformer(factor),
                    new AWGNTransformer(stdDev),
                    new RayleighFadingTransformer(rayleighLength, rayleighStdDev)
            );
        }

        @Override
        public Byte[] getReceivedData(Byte[] input) {
            return (new ComposedTransformer<>(modulator, noise, demodulator)).transform(input);
        }

        @Override
        public Double[] getTransmittedSignal(Byte[] input) {
            return modulator.transform(input);
        }

        @Override
        public Double[] getReceivedSignal(Byte[] input) {
            return (new ComposedTransformer<>(modulator, noise)).transform(input);
        }
    }
}
