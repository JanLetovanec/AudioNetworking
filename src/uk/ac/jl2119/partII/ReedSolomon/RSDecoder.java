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
        Polynomial errorEvaluatorPoly = null;
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
        FiniteFieldElement[] results = new FiniteFieldElement[eccSymbolCount];

        for (int i = 0; i < eccSymbolCount; i++) {
            FiniteFieldElement evaluationPoint = new FiniteFieldElement(FIELD_GENERATOR).power(i + SHIFT);
            FiniteFieldElement evaluation = poly.eval(evaluationPoint);
            results[i] = evaluation;
        }
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

        //Initialize
        Polynomial C = getPolyOfOne();
        Polynomial B = getPolyOfOne();
        int L = 0;
        Polynomial synPoly = getReversedSyndromePoly(syndromes); // BM uses reversed syn poly

        for (int i = 0; i < BLOCK_SIZE - DATA_SIZE; i++) {
            // Calculate d (discrepancy
            FiniteFieldElement discrepancy = getDiscrepancy(synPoly, C, i);

            // Update C <- C - d*(B<<1)
            Polynomial copiedC = new Polynomial(C);
            updateC(C, B, discrepancy);

            // If everything is fine, just carry on
            if (discrepancy.isZero()) {
                B.multiply(getShiftPoly()); // B << 1 (because it needs to 'follow' the syndromes
                continue;
            }

            // Things should converge by i = 2L
            // so do not increase L or adjust B
            // just carry on
            if(2*L > i) {
                B.multiply(getShiftPoly());
                continue;
            }

            // B <- 1/d * C though note that this is the C before the current update
            B = getNewB(copiedC, discrepancy);
            L = i - L;
        }

        C.contract();
        return C;
    }

    /**
     *  This is same as discrepancy in the desc. algo,
     *  but they use inverted indexes...
     *  so the indexes are not 1:1 to the example
     */
    private FiniteFieldElement getDiscrepancy(Polynomial syndromePoly, Polynomial C, int i) {
        Polynomial temp = new Polynomial(syndromePoly);
        temp.multiply(C);
        return temp.getCoefficients()[i];
    }

    private Polynomial getReversedSyndromePoly(FiniteFieldElement[] syndromes) {
        FiniteFieldElement[] reversed = new FiniteFieldElement[syndromes.length];
        for(int i = 0; i < reversed.length; i++) {
            reversed[i] = syndromes[syndromes.length - 1 - i];
        }
        return new Polynomial(reversed);
    }

    private void updateC(Polynomial C,
                         Polynomial B,
                         FiniteFieldElement delta) {
        Polynomial temp = new Polynomial(B);
        temp.multiply(getShiftPoly());
        temp.multiplyByScalar(delta);
        C.add(temp);
    }

    private Polynomial getNewB(Polynomial C,
                         FiniteFieldElement delta) {
        Polynomial temp = new Polynomial(C);
        FiniteFieldElement deltaInverse = FiniteFieldElement.getOne().divide(delta);
        temp.multiplyByScalar(deltaInverse);
        return temp;
    }

    private Polynomial getPolyOfOne() {
        FiniteFieldElement[] coefs = new FiniteFieldElement[] {
                FiniteFieldElement.getOne()
        };
        return new Polynomial(coefs);
    }

    private Polynomial getShiftPoly() {
        FiniteFieldElement[] coefs = new FiniteFieldElement[] {
                FiniteFieldElement.getOne(),
                FiniteFieldElement.getZero()
        };
        return new Polynomial(coefs);
    }
}
