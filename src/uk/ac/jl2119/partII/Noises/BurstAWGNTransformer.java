package uk.ac.jl2119.partII.Noises;

import org.apache.commons.math3.distribution.PoissonDistribution;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.Random;

public class BurstAWGNTransformer implements ITransformer<Double, Double> {
    private final int burstLength;
    private final double burstStdDeviation;
    private PoissonDistribution poissonRng;
    private Random gaussRng;

    public BurstAWGNTransformer(long meanSamplesBetweenBurst, int burstLength, double burstStdDev) {
        this.burstLength = burstLength;
        this.burstStdDeviation = burstStdDev;

        this.poissonRng = new PoissonDistribution(meanSamplesBetweenBurst);
        this.gaussRng = new Random();
    }

    @Override
    public Double[] transform(Double[] input) {
        Double[] noise = generateNoise(input.length);
        Double[] signal = StreamUtils.addSignals(input, noise);
        return signal;
    }

    private Double[] generateNoise(int lengthInSamples) {
        Double[] result = new Double[lengthInSamples];
        int i = 0;
        while (i < result.length) {
            i = waitForBurst(result, i);
            i = addBurst(result, i);
        }
        return result;
    }

    private int waitForBurst(Double[] input, int offset) {
        int samplesTilNextBurst = poissonRng.sample();
        int endOffset = Math.min(offset + samplesTilNextBurst, input.length - 1);
        for (int i = offset; i < endOffset; i++) {
            input[i] = 0.0;
        }
        return endOffset + 1;
    }

    private int addBurst(Double[] input, int offset) {
        int endOffset = Math.min(offset + burstLength, input.length - 1);
        for (int i = offset; i < endOffset; i++) {
            input[i] = gaussRng.nextGaussian() * burstStdDeviation;
        }
        return endOffset + 1;
    }
}
