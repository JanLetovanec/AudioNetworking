package uk.ac.cam.jl2119.partII.Evaluation.metrics;

import uk.ac.cam.jl2119.partII.utils.EvalUtils;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;

public class ErrorRateCalc<P> implements IMetricCalculator<Double, P> {

    @Override
    public Double getMetric(Byte[] input, Simulator<P> sim) {
        Byte[] output = sim.getReceivedData(input);
        int correctBits = EvalUtils.getCorrectBits(input, output);
        int totalBits = input.length * 8;   // Cuz counting bits, not bytes!
        int wrongBits = totalBits - correctBits;
        return ((double) wrongBits) / ((double) totalBits);
    }
}
