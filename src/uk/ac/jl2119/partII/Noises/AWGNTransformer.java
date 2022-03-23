package uk.ac.jl2119.partII.Noises;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.Arrays;
import java.util.Random;

public class AWGNTransformer implements ITransformer<Double, Double> {
    private double stdDeviation;

    public AWGNTransformer(double stdDeviation) {
        this.stdDeviation = stdDeviation;
    }

    @Override
    public Double[] transform(Double[] input) {
        Double[] noise = generatePureNoise(input.length);
        return StreamUtils.addSignals(input, noise);
    }

    private Double[] generatePureNoise(int length) {
        Double[] buffer = new Double[length];
        Double[] result = Arrays.stream(buffer)
                .map(x -> generateSingleSample())
                .toArray(Double[]::new);
        return result;
    }

    private Double generateSingleSample() {
        Random rng = new Random();
        return rng.nextGaussian() * stdDeviation;
    }
}
