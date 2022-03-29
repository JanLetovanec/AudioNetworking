package uk.ac.jl2119.partII.Evaluation.sims.Noise;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.jl2119.partII.Evaluation.sims.Simulator;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.Noises.AttenuatorTransformer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AWGN_PowerSimulator implements ISimulatorGenerator<Double> {
    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;
    private final int samples;
    private final double min;
    private final double max;

    public AWGN_PowerSimulator(ITransformer<Byte, Double> modulator,
                               ITransformer<Double, Byte> demodulator,
                               int samples, double min, double max) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(0.5));
        this.demodulator = demodulator;
        this.samples = samples;
        this.min = min;
        this.max = max;
    }

    public AWGN_PowerSimulator(ITransformer<Byte, Double> modulator,
                               ITransformer<Double, Byte> demodulator,
                               double attenuationFactor,
                               int samples, double min, double max) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(attenuationFactor));
        this.demodulator = demodulator;
        this.samples = samples;
        this.min = min;
        this.max = max;
    }

    @Override
    public Map<Double, Simulator<Double>> getSimulators() {
        Map<Double, Simulator<Double>> result = new HashMap<>();
        Double[] parameters = getPowerValues();
        Arrays.stream(parameters)
                .forEach(param -> result.put(param, new PowerSim(param)));
        return result;
    }

    private Double[] getPowerValues() {
        Double[] values = new Double[samples];
        for (int i = 0; i < samples; i++) {
            Double value = min + (((max - min) * i) / samples);
            values[i] = value;
        }
        return values;
    }

    private class PowerSim extends Simulator<Double> {
        private ITransformer<Double, Double> noise;

        protected PowerSim(Double powerValue) {
            super(powerValue);
            noise = new AWGNTransformer(powerValue);
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
