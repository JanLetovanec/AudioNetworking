package uk.ac.jl2119.partII.Evaluation.metrics;

import uk.ac.jl2119.partII.Evaluation.sims.Simulator;

/***
 * Calculates total rate by digital bits sent / analogue samples sent
 * @param <P>
 */
public class RateCalc<P> implements IMetricCalculator<Double, P> {
    private final long sampleRate;

    public RateCalc(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    @Override
    public Double getMetric(Byte[] input, Simulator<P> sim) {
        Double[] signal = sim.getReceivedSignal(input);
        double signalLengthInSeconds = ((double) signal.length) / ((double) sampleRate);
        return ((double) input.length) / signalLengthInSeconds;
    }
}