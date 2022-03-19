package uk.ac.jl2119.partII.Evaluation.metrics;

import uk.ac.jl2119.partII.Evaluation.EvalUtils;
import uk.ac.jl2119.partII.Evaluation.sims.Simulator;

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
        Byte[] output = sim.getReceivedData(input);
        int correctBits = EvalUtils.getCorrectBits(input, output);

        Double[] signal = sim.getReceivedSignal(input);
        double signalLengthInSeconds = ((double) signal.length) / ((double) sampleRate);
        return ((double) correctBits) / signalLengthInSeconds;
    }
}
