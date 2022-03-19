package uk.ac.jl2119.partII.Evaluation.metrics;

import uk.ac.jl2119.partII.Evaluation.sims.Simulator;

/**
 * Calculates metric of a single run.
 * This represents the 'dependent variable' - the thing that depends on a parameter
 * @param <M> -- type of the metric
 * @param <P> -- type of the parameter
 */
public interface IMetricCalculator<M, P> {
    /**
     * Calculates the metric
     * @param input -- input data to the simulator
     * @param sim -- simulator to evaluate
     * @return -- the metric of this particular run
     */
    M getMetric(Byte[] input, Simulator<P> sim);
}
