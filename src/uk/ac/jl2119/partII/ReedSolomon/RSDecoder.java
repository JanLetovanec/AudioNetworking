package uk.ac.jl2119.partII.ReedSolomon;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.Arrays;

import static uk.ac.jl2119.partII.utils.StreamUtils.padData;
import static uk.ac.jl2119.partII.utils.StreamUtils.partitionData;

public class RSDecoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;
    private static final byte FIELD_GENERATOR = 3;
    private static final byte SHIFT = 1;

    @Override
    public Byte[] transform(Byte[] input) {
        Byte[] paddedData = padData(input, BLOCK_SIZE);
        return partitionData(paddedData, BLOCK_SIZE).stream()
                .map(x -> x.toArray(Byte[]::new))
                .flatMap(x -> Arrays.stream(transformBlock(x)))
                .toArray(Byte[]::new);
    }

    private Byte[] transformBlock(Byte[] blockData) {
        Polynomial msgPoly = getMessagePoly(blockData);

        FiniteFieldElement[] syndromes = calculateSyndromes(msgPoly);
        if(checkMessage(syndromes)) {
            return extractData(blockData);
        }

        Polynomial errorLocatorPoly = getErrorLocPoly(syndromes);

//          Compute the erasure/error evaluator polynomial (from the syndromes and erasure/error locator polynomial). Necessary to evaluate how much the characters were tampered (ie, helps to compute the magnitude).
//          Compute the erasure/error magnitude polynomial (from all 3 polynomials above): this polynomial can also be called the corruption polynomial, since in fact it exactly stores the values that need to be subtracted from the received message to get the original, correct message (i.e., with correct values for erased characters). In other words, at this point, we extracted the noise and stored it in this polynomial, and we just have to remove this noise from the input message to repair it.
//          Repair the input message simply by subtracting the magnitude polynomial from the input message.
        return new Byte[0];
    }

    private boolean checkMessage(FiniteFieldElement[] syndromes) {
        return Arrays.stream(syndromes)
                .allMatch(FiniteFieldElement::isZero);
    }

    private FiniteFieldElement[] calculateSyndromes(Polynomial poly) {
        int eccSymbolCount = BLOCK_SIZE - DATA_SIZE;
        FiniteFieldElement[] results = new FiniteFieldElement[eccSymbolCount + 1];

        for (int i = 0; i < eccSymbolCount; i++) {
            FiniteFieldElement evaluationPoint = new FiniteFieldElement(FIELD_GENERATOR).power(i + SHIFT);
            FiniteFieldElement evaluation = poly.eval(evaluationPoint);
            results[i] = evaluation;
        }

        results[results.length - 1] = FiniteFieldElement.getZero(); // The constant 0 is added
        return results;
    }

    private Polynomial getMessagePoly(Byte[] data) {
        FiniteFieldElement[] coefficients = Arrays.stream(data)
                .map(FiniteFieldElement::new)
                .toArray(FiniteFieldElement[]::new);
        return new Polynomial(coefficients);
    }

    private Byte[] extractData(Byte[] rawData) {
        Byte[] targetData = new Byte[DATA_SIZE];
        StreamUtils.copyBytesIn(targetData, rawData, 0, DATA_SIZE);
        return targetData;
    }

    /**
     * Find Lambda(x) = Product(x*alpha^j - 1), where j is error location
     * Do this by using **Berlekamp–Massey** algorithm:
     * https://en.wikipedia.org/wiki/Berlekamp%E2%80%93Massey_algorithm
     * @param syndromes -- syndromes of the message
     * @return -- the Lambda(x) polynomial
     */
    private Polynomial getErrorLocPoly(FiniteFieldElement[] syndromes) {
        // Berkley-Massey algo
        // Initialize
        Polynomial C = getPolyOfOne();  // The current guess poly
        Polynomial B = getPolyOfOne();  // The last guess poly
        int L = 0;                      // Estimated errors
        int m = 1;
        FiniteFieldElement b = FiniteFieldElement.getOne();// Last discrepancy

        syndromes[syndromes.length-1] = syndromes[syndromes.length-1].add(FiniteFieldElement.getOne());
        Polynomial syndromePoly = new Polynomial(syndromes);

        //NOT SURE:
        // * what direction the syndromes are indexed

        for (int n = 0; n < syndromes.length; n++) {
            // For odd numbers d = 0, so do not even bother
//            if (n % 2 == 1) {
//                m++;
//                continue;
//            }

            // Calculate discrepancy
            FiniteFieldElement d = getDiscrepancy(syndromePoly, C, L, n);
            // if guess is okay, just carry on
            if (d.isZero()) {
                m++;
                continue;
            }
            // discrepancies should converge by n = 2L
            // if we already reached n = 2L
            // adjust C and L and reset relevant vars
            if (2*L <= n) {
                Polynomial tempPoly = new Polynomial(C);
                adjustC(C,d, b, m, B);
                L = n + 1 - L;
                B = tempPoly;
                b = d;
                m = 1;
                continue;
            }
            // otherwise just adjust C and carry on
            adjustC(C,d, b, m, B);
            m++;
        }

        // If you terminated, C was correct guess
        return C;
    }

    /**
     *  This is same as discrepancy in the desc. algo,
     *  but they use inverted indexes...
     *  so the indexes are not 1:1 to the example
     */
    private FiniteFieldElement getDiscrepancy(Polynomial syndromePoly,
                                              Polynomial C,
                                              int L,
                                              int n) {
        int baseIndex = syndromePoly.getCoefficients().length - n - 1;
        FiniteFieldElement total = FiniteFieldElement.getZero();
        for (int i = 0; i <= L; i++) {
            total = total.add(C.getCoefficients()[L - i].multiply(syndromePoly.getCoefficients()[baseIndex + i]));
        }
        Polynomial temp = new Polynomial(syndromePoly);
        //temp.multiply(C);
        return temp.getCoefficients()[n];
    }

    private void adjustC(Polynomial C,
                         FiniteFieldElement d,
                         FiniteFieldElement b,
                         int m,
                         Polynomial B) {
        Polynomial temp = new Polynomial(B);
        FiniteFieldElement scalar = d.divide(b);
        Polynomial xToM = getXtoM(m);
        temp.multiply(xToM);
        temp.multiplyByScalar(scalar);

        C.add(temp);
    }

    private Polynomial getPolyOfOne() {
        FiniteFieldElement[] coefs = new FiniteFieldElement[] {
                FiniteFieldElement.getOne()
        };
        return new Polynomial(coefs);
    }

    private Polynomial getXtoM(int m) {
        FiniteFieldElement[] elems = new FiniteFieldElement[m + 1];
        elems = Arrays.stream(elems)
                .map(x -> FiniteFieldElement.getZero())
                .toArray(FiniteFieldElement[]::new);
        elems[0] = FiniteFieldElement.getOne();
        return new Polynomial(elems);
    }
}
