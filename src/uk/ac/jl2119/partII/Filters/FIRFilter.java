package uk.ac.jl2119.partII.Filters;

import uk.ac.jl2119.partII.ITransformer;

public class FIRFilter implements ITransformer<Double, Double> {
    private Double[] coefficients;

    public FIRFilter(Double[] coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public Double[] transform(Double[] input) {
        Double[] result = new Double[input.length];

        for (int i = 0; i < input.length; i++) {
            result[i] = getSample(input, i);
        }

        return result;
    }

    private Double getSample(Double[] input, int index) {
        Double sum = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            int delayedIndex = index - i;
            if (delayedIndex < 0) {
                   return sum;
            }

            sum += input[delayedIndex] * coefficients[i];
        }
        return sum;
    }
}