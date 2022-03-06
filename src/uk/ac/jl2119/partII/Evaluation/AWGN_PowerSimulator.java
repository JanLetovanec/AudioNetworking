package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.Noises.AttenuatorTransformer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AWGN_PowerSimulator implements ISimulatorGenerator<Double>{
    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;
    private final int samples;
    private final double min;
    private final double max;

    public AWGN_PowerSimulator(ITransformer<Byte, Double> modulator,
                             ITransformer<Double, Byte> demodulator, int samples, double min, double max) {
        this.modulator = new ComposedTransformer<Byte, Double>(modulator, new AttenuatorTransformer(0.5));
        this.demodulator = demodulator;
        this.samples = samples;
        this.min = min;
        this.max = max;
    }

    @Override
    public Map<Double, ITransformer<Byte, Byte>> getSimulators() {
        Map<Double, ITransformer<Byte, Byte>> result = new HashMap<>();
        Double[] parameters = getPowervalues();
        Arrays.stream(parameters)
                .forEach(param -> result.put(param, getSimulatorForPowerLevel(param)));
        return result;
    }

    private ITransformer<Byte, Byte> getSimulatorForPowerLevel(Double powerValue) {
        double stdDeviation = Math.sqrt(powerValue);
        ITransformer<Double, Double> noise = new AWGNTransformer(stdDeviation);
        return new ComposedTransformer<>(modulator, noise, demodulator);
    }

    private Double[] getPowervalues() {
        Double[] values = new Double[samples];
        for (int i = 0; i < samples; i++) {
            Double value = ((max - min) * i) / samples;
            values[i] = value;
        }
        return values;
    }
}
