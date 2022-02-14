package uk.ac.jl2119.partII.ReedSolomon;

public class Polynomial {
    private FiniteFieldElement[] coefficients;

    /**
     *
     * @param coefficients - high degree coef. first (coef. of the polynomial)
     */
    public Polynomial(FiniteFieldElement[] coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial(Polynomial poly) {
        this.coefficients = new FiniteFieldElement[poly.coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = new FiniteFieldElement(poly.coefficients[i]);
        }
    }

    public void multiply(Polynomial second) {
        FiniteFieldElement[] coefs = freshCoefficients(coefficients.length + second.degree());
        for(int i = 0; i < coefficients.length; i++){
            for (int j = 0; j < second.coefficients.length; j++) {
                coefs[i + j] = coefs[i + j].add(coefficients[i].multiply(second.coefficients[j]));
            }
        }
        coefficients = coefs;
    }

    public void divideByScalar(FiniteFieldElement element) {
        if (element.isZero() || element.isOne()) {
            return;
        }

        for(int i = 0; i < coefficients.length; i++) {
            if (coefficients[i].isZero() || coefficients[i].isOne()) {
                continue;
            }

            coefficients[i] = coefficients[i].divide(element);
        }
    }

    public void add(Polynomial second) {
        handleExtension(second);

        int diff = coefficients.length - second.coefficients.length;
        for (int i = 0; i < second.coefficients.length; i++) {
            coefficients[i + diff] = coefficients[i + diff].add(second.coefficients[i]);
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
            newCoefficients[i + diff] = new FiniteFieldElement(coefficients[i]);
        }
        coefficients = newCoefficients;
    }

    private FiniteFieldElement[] freshCoefficients(int length) {
        FiniteFieldElement[] newCoefficients = new FiniteFieldElement[length];
        for (int i = 0; i < length; i++) {
            newCoefficients[i] = FiniteFieldElement.getZero();
        }
        return newCoefficients;
    }

    public int degree(){return coefficients.length -1;}

    /***
     * Does not mutate the poly.
     * @param divisor - divisor to divide by
     * @return - array containing quotient and remainder resp.
     */
    public Polynomial[] divAndMod(Polynomial divisor) {
        Polynomial copiedPoly = new Polynomial(this);

        // Standard algorithm as described here:
        // https://en.wikipedia.org/wiki/Synthetic_division#Expanded_synthetic_division
        int expectedDegree = copiedPoly.coefficients.length - (divisor.coefficients.length - 1);
        for (int i = 0; i < expectedDegree; i++) {
            if (copiedPoly.coefficients[i].isZero()) {
                continue;
            }

            for (int j = 1; j < divisor.coefficients.length; j++) {
                if (divisor.coefficients[j].isZero()) {
                    continue;
                }

                //copy[i + j] -= divisor[j] * copy[i] but using GF tricks
                FiniteFieldElement additionalTerm = divisor.coefficients[j].multiply(copiedPoly.coefficients[i]);
                copiedPoly.coefficients[i + j] = copiedPoly.coefficients[i + j].add(additionalTerm);
            }
        }

        int boundary = coefficients.length - divisor.degree();
        Polynomial quotient =copiedPoly.getSubPoly(0, boundary);
        Polynomial remainder = copiedPoly.getSubPoly(boundary, coefficients.length);
        return new Polynomial[]{quotient, remainder};
    }

    private Polynomial getSubPoly(int startIndex, int endIndexExcluded) {
        FiniteFieldElement[] coefs = new FiniteFieldElement[endIndexExcluded - startIndex];
        for (int i = 0; i < coefs.length; i++) {
            coefs[i] = new FiniteFieldElement(coefficients[i + startIndex]);
        }
        return new Polynomial(coefs);
    }

    public FiniteFieldElement[] getCoefficients() {
        return coefficients;
    }

}
