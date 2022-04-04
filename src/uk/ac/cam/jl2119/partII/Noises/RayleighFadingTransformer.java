package uk.ac.cam.jl2119.partII.Noises;

import uk.ac.cam.jl2119.partII.Filters.FIRFilter;

import java.util.Arrays;
import java.util.Random;

/**
 * Models Rayleigh fading of the signal
 * It is a FIR filter, whose coefficients are sampled from Rayleigh distribution
 */
public class RayleighFadingTransformer extends FIRFilter {

    /**
     * Creates the noise generator
     * @param length - number of coefficients in FIR filter
     * @param stdDeviation - the sigma parameter of the distribution
     */
    public RayleighFadingTransformer(int length, double stdDeviation) {
        super(sampleCoefficients(length, stdDeviation));
    }

    private static Double[] sampleCoefficients(int length, double stdDeviation) {
        return Arrays.stream(new Double[length])
                .map(x -> sampleRayleigh(stdDeviation))
                .toArray(Double[]::new);
    }

    private static double sampleRayleigh(double stdDeviation){
        Random rng = new Random();
        double x = rng.nextGaussian() * stdDeviation;
        double y = rng.nextGaussian() * stdDeviation;
        return Math.sqrt(x*x + y*y);
    }
}
