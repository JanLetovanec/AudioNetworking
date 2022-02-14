package uk.ac.jl2119.partII.ReedSolomon;

public class Polynomial {
    private final FiniteField field;
    private FiniteFieldElement[] coefficients;

    /**
     *
     * @param coefficients - high degree coef. first (coef. of the polynomial)
     */
    Polynomial(FiniteFieldElement[] coefficients) {
        field = new FiniteField();
        this.coefficients = coefficients;
    }

    public void multiply(Polynomial second) {
    }


    public void add(Polynomial second) {
        handleExtension(second);

        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i].add(second.coefficients[i]);
        }
    }

    private void handleExtension(Polynomial second) {
        int lengthDiff = coefficients.length - second.coefficients.length;
        if (lengthDiff < 0) {
            extendCoefficients(second.coefficients.length);
        }
    }

    private void extendCoefficients(int targetValue) {
        FiniteFieldElement[] newCoefficients = freshCoefficients(targetValue);
        int diff = targetValue - coefficients.length;
        for (int i = 0; i < coefficients.length; i++) {
            newCoefficients[i + diff] = coefficients[i];
        }
        coefficients = newCoefficients;
    }

    private FiniteFieldElement[] freshCoefficients(int length) {
        FiniteFieldElement[] newCoefficients = new FiniteFieldElement[length];
        for (int i = 0; i < length; i++) {
            newCoefficients[i] = field.getZero();
        }
        return newCoefficients;
    }

    public Polynomial modulo(Polynomial divisor) {
        return null;
    }

    public FiniteFieldElement[] getCoefficients() {
        return null;
    }
}
