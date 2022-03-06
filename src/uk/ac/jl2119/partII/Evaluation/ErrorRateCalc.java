package uk.ac.jl2119.partII.Evaluation;

public class ErrorRateCalc implements IMetricCalculator<Double, Double>{

    @Override
    public Double getMetric(Byte[] input, Byte[] output, Double parameter) {
        int correctBits = EvalUtils.getCorrectBits(input, output);
        int totalBits = input.length;
        int wrongBits = totalBits - correctBits;
        return ((double) wrongBits) / ((double) totalBits);
    }
}
