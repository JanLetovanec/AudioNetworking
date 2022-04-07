package uk.ac.cam.jl2119.partII.Noises;

import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;

/**
 * Simulates clock-drift.
 * We do this by 'down-sampling' an over-sampled signal.
 *
 * This is a noise sim. We down-sample the signal as if the sample-rate was a little of,
 * but the rest of the code should set its SAMPLE_RATEs to default
 * (this simulates receiver thinking it is right, but it has clock-drifted signal).
 */
public class ClockDriftTransformer implements ITransformer<Double, Double> {
    private final long currentRate;
    private final long defaultSampledRate;
    public static final long OVERSAMPLE_FACTOR = 120;

    public ClockDriftTransformer(double tickLengthFactor) {
        defaultSampledRate = SchemeModulatorMap.DEFAULT_SAMPLE_RATE * OVERSAMPLE_FACTOR;
        // Double tick-length => Half sample-rate
        currentRate = Math.round(SchemeModulatorMap.DEFAULT_SAMPLE_RATE / tickLengthFactor);
    }

    /***
     * Implements clock-drift
     * @param input -- Assumed to be of sample-rate 100*DEFAULT_SAMPLE_RATE
     * @return -- Picks the samples from input as if it was sampled at tick-length: factor*DEFAULT_TICK_RATE.
     *             (Or closest to it)
     */
    @Override
    public Double[] transform(Double[] input) {
        double totalSeconds = ((double)input.length / (double)defaultSampledRate);
        int numberOfSamples = (int) Math.floor(totalSeconds * currentRate);
        Double[] result = new Double[numberOfSamples];
        for (int i = 0; i < result.length; i++) {
            int sampleInOriginal = (int)((i * defaultSampledRate) / currentRate);
            result[i] = input[sampleInOriginal];
        }
        return result;
    }
}
