package uk.ac.jl2119.partII.Noises;

import com.google.common.collect.Streams;
import uk.ac.jl2119.partII.ITransformer;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class AWGNTransformer implements ITransformer<Double, Double> {
    private double stdDeviation;

    public AWGNTransformer(double stdDeviation) {
        this.stdDeviation = stdDeviation;
    }

    @Override
    public Double[] transform(Double[] input) {
        Double[] noise = generatePureNoise(input.length);
        return addSignals(input, noise);
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
        double randomGauss = rng.nextGaussian() * stdDeviation;
        return clip(randomGauss);
    }

    private double clip(double input) {
        return Math.min(1, Math.max(-1, input));
    }

    private Double[] addSignals(Double[] signal, Double[] noise) {
        Stream<Double> signalStream = Arrays.stream(signal);
        Stream<Double> noiseStream = Arrays.stream(noise);
        return Streams
            .zip(signalStream, noiseStream,
                (signalFrame, noiseFrame) -> signalFrame + noiseFrame)
            .toArray(Double[]::new);
    }
}
