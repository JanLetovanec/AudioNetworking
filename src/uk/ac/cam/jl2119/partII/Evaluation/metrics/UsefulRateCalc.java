package uk.ac.cam.jl2119.partII.Evaluation.metrics;

import uk.ac.cam.jl2119.partII.utils.EvalUtils;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;

/***
 * Calculates a useful bandwidth (rate) by correct bits / total sent samples.
 * Result is in bits/second.
 * @param <P> - the parameter type
 */
public class UsefulRateCalc<P> implements IMetricCalculator<Double, P>{
    private final long sampleRate;

    public UsefulRateCalc(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    @Override
    public Double getMetric(Byte[] input, Simulator<P> sim) {
        int sentBytes = input.length;
        double signalDuration = (double) sim.getReceivedSignal(input).length / sampleRate;
        double mutualInfo = getMutualInfo(input, sim);

        return mutualInfo * 8.0 * (sentBytes / signalDuration);
    }

    private double getMutualInfo(Byte[] input, Simulator<P> sim) {
        Byte[] output = sim.getReceivedData(input);
        int correctBits = EvalUtils.getCorrectBits(input, output);
        double errorRate = ((double) correctBits) / input.length;

        return 1.0
                + errorRate * log2(errorRate)
                + (1 - errorRate) * log2(errorRate);
    }

    private double log2(double n) {
        return Math.log(n) / Math.log(2);
    }
}
