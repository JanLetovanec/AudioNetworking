package uk.ac.jl2119.partII.Evaluation.sims.Scheme;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.jl2119.partII.Evaluation.sims.Simulator;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.jl2119.partII.Noises.RayleighFadingTransformer;
import uk.ac.jl2119.partII.PSK.PSKDemodulator;
import uk.ac.jl2119.partII.PSK.PSKModulator;

import java.util.HashMap;
import java.util.Map;

public class PSKCyclesSimulator implements ISimulatorGenerator<Integer> {
    private final double stdDev;
    private final int rayleighLength;
    private final double rayleighStdDev;
    private final double factor;

    private final int step;
    private final int min;
    private final int max;

    public PSKCyclesSimulator(double stdDev, int rayleightLength, int min, int max, int step, double attenuationFactor) {
        this.factor = attenuationFactor;
        this.stdDev = stdDev;
        this.rayleighLength = rayleightLength;
        this.rayleighStdDev = attenuationFactor;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public PSKCyclesSimulator(double stdDev, int rayleightLength, int min, int max, int step) {
        this.factor = 0.5;
        this.stdDev = stdDev;
        this.rayleighLength = rayleightLength;
        this.rayleighStdDev = factor;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public Map<Integer, Simulator<Integer>> getSimulators() {
        Map<Integer, Simulator<Integer>> result = new HashMap<>();
        for (int i = min; i < max; i += step) {
            result.put(i, new PSKSim(i));
        }
        return result;
    }

    private class PSKSim extends Simulator<Integer> {
        private final ITransformer<Byte, Double> modulator;
        private final ITransformer<Double, Byte> demodulator;
        private final ITransformer<Double, Double> noise;

        protected PSKSim(Integer cycles) {
            super(cycles);
            modulator = new PSKModulator(SchemeModulatorMap.DEFAULT_BASE_FREQUENCY,
                    cycles, SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
            demodulator = new PSKDemodulator(SchemeModulatorMap.DEFAULT_BASE_FREQUENCY,
                    cycles, SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
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
