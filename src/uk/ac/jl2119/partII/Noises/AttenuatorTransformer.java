package uk.ac.jl2119.partII.Noises;

import uk.ac.jl2119.partII.ITransformer;

import java.util.Arrays;

public class AttenuatorTransformer implements ITransformer<Double, Double> {
    private double factor;

    public AttenuatorTransformer(double factor) {
        this.factor = factor;
    }

    @Override
    public Double[] transform(Double[] input) {
        return Arrays.stream(input)
                .map(x -> attenuate(x))
                .toArray(Double[]::new);
    }

    private Double attenuate(Double input) {
        return Double.valueOf(clamp(input * factor));
    }

    private double clamp(double input) {
        return Math.min(1, Math.max(-1, input));
    }
}
