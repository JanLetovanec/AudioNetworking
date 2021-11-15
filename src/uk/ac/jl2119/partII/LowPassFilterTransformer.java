package uk.ac.jl2119.partII;

import java.util.Arrays;

/**
 * Implements LPF 'circuit' in software.
 * Uses back-war Euler method to approx. derivative.
 */
public class LowPassFilterTransformer implements ITransformer<Double, Double> {
    // Coeficients in the circuit equation:
    private final double coefVin;
    private final double coefLastVout;
    private Double lastVout;

    /**
     * Creates LPF with given properties
     * @param sampleRate - sample rate of the input data
     * @param cutOffFrequencyInHz - cut-off frequency at -3dB
     */
    public LowPassFilterTransformer(long sampleRate, double cutOffFrequencyInHz) {
        double RC = getRCConstant(cutOffFrequencyInHz);
        double T = 1.0 / sampleRate;
        coefVin = getCoefVin(T, RC);
        coefLastVout = getCoefLastVout(T, RC);

        lastVout = 0.0;
    }

    @Override
    public Double[] transform(Double[] input) {
        Double[] output = Arrays.stream(input)
                .map(Vin -> transformSingle(Vin))
                .toArray(Double[]::new);
        return output;
    }

    private Double transformSingle(Double Vin) {
        Double Vout = Vin * coefVin + lastVout * coefLastVout;
        lastVout = Vout;
        return Vout;
    }

    private static double getRCConstant(double cutOffFrequency) {
        return 1.0 / (2 * Math.PI * cutOffFrequency);
    }
    private static double getCoefVin(double T, double RC) {
        return T / (T + RC);
    }
    private static double getCoefLastVout(double T, double RC) {
        return RC / (T + RC);
    }
}
