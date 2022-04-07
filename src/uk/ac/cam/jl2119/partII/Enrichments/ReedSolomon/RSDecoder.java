 package uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

import java.util.Arrays;

import static uk.ac.cam.jl2119.partII.utils.StreamUtils.padData;
import static uk.ac.cam.jl2119.partII.utils.StreamUtils.partitionData;

public class RSDecoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private final int DATA_SIZE;
    private static final byte FIELD_GENERATOR = 3;
    private static final byte SHIFT = 1;

    public RSDecoder() {
        DATA_SIZE = 223;
    }
    public RSDecoder(int correctionSymbols) {
        DATA_SIZE = BLOCK_SIZE - correctionSymbols;
    }

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
        
        Polynomial errorLocatorPoly = getErrorPolys(syndromes);
        Polynomial errorEvaluatorPoly = getErrorEvaluatorPoly(syndromes, errorLocatorPoly);


        FiniteFieldElement[] locationElements = getErrorLocations(errorLocatorPoly);
        // Cannot fix errors, just give up and forward the message
        if (locationElements == null) {
            return extractData(blockData);
        }

        FiniteFieldElement[] magnitudes = getErrorMagnitudes(locationElements,
                                                            errorEvaluatorPoly,
                                                            errorLocatorPoly);

        Polynomial msgErrorPoly = getMsgError(locationElements, magnitudes);
        msgPoly.add(msgErrorPoly);

        return extractData(msgPoly);
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

    private Byte[] extractData(Polynomial rawData) {
        Byte[] rawDataBytes =  Arrays.stream(rawData.getCoefficients())
                .map(x -> (byte) x.getValue())
                .toArray(Byte[]::new);
        return extractData(rawDataBytes);
    }

    /**
     * Find Lambda(x) = Product(x*alpha^j - 1), where j is error location
     * and Omega(x) = S(x) * Lambda(x), where S(x) is the Syndrome poly
     *
     * Do this by using **Berlekamp–Massey** algorithm:
     * https://en.wikipedia.org/wiki/Berlekamp%E2%80%93Massey_algorithm
     * @param syndromes -- syndromes of the message
     * @return -- An array of polynomials,
     *      where first element is the Lambda(x) polynomial
     *      and the second one is the Omega(x) polynomial
     */
    private Polynomial getErrorPolys(FiniteFieldElement[] syndromes) {
        // Berlekamp-Massey algo

        //Initialize
        Polynomial Lambda = getPolyOfOne();
        Polynomial B = getPolyOfOne();
        int L = 0;

        Polynomial synPoly = getReversedSyndromePoly(syndromes); // BM uses reversed syn poly

        for (int i = 0; i < BLOCK_SIZE - DATA_SIZE; i++) {
            // Calculate d (discrepancy
            FiniteFieldElement discrepancy = getDiscrepancy(synPoly, Lambda, i);

            // Update Lam <- Lam - d*(B<<1) in the algo
            Polynomial copiedLambda = new Polynomial(Lambda);
            updatePoly(Lambda, B, discrepancy);

            // If everything is fine, just carry on
            if (discrepancy.isZero()) {
                B.multiply(getShiftPoly()); // B << 1 (because it needs to 'follow' the syndromes)
                continue;
            }

            // Things should converge by i = 2L
            // so do not increase L or adjust B
            // just carry on
            if(2*L > i) {
                B.multiply(getShiftPoly()); // B << 1 (because it needs to 'follow' the syndromes)
                continue;
            }

            // B <- 1/d * Lam though note that this is the C before the current update
            B = getNewHelper(copiedLambda, discrepancy);
            L = i - L;
        }

        Lambda.contract();
        return Lambda;
    }

    /**
     *  This is same as discrepancy in the desc. algo,
     *  but they use inverted indexes...
     *  so the indexes are not 1:1 to the example
     */
    private FiniteFieldElement getDiscrepancy(Polynomial syndromePoly, Polynomial C, int i) {
        Polynomial temp = new Polynomial(syndromePoly);
        temp.multiply(C);
        int targetCoefficient = temp.getCoefficients().length - i -1;
        return temp.getCoefficients()[targetCoefficient];
    }

    private Polynomial getReversedSyndromePoly(FiniteFieldElement[] syndromes) {
        FiniteFieldElement[] reversed = new FiniteFieldElement[syndromes.length];
        for(int i = 0; i < reversed.length; i++) {
            reversed[i] = syndromes[syndromes.length - 1 - i];
        }
        return new Polynomial(reversed);
    }

    private void updatePoly(Polynomial Lambda,
                         Polynomial B,
                         FiniteFieldElement delta) {
        Polynomial temp = new Polynomial(B);
        temp.multiply(getShiftPoly());
        temp.multiplyByScalar(delta);
        Lambda.add(temp);
    }

    private Polynomial getNewHelper(Polynomial C,
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

    /**
     * Since there are at most 256 location, just brute-force through it
     * @param errorPoly - The error locator polynomial. Its roots are the inverses of the 'locations'
     * @return - The 'locations'. That is alpha^j, where j is the position of the error
     */
    private FiniteFieldElement[] getErrorLocations(Polynomial errorPoly) {
        FiniteFieldElement[] roots = new FiniteFieldElement[errorPoly.degree()];
        int foundRoots = 0;
        for(int i = 0; i < BLOCK_SIZE; i++) {
            FiniteFieldElement evalPoint = new FiniteFieldElement(FIELD_GENERATOR).power(i);

            if (errorPoly.eval(evalPoint).isZero()) {
                addRoot(foundRoots, evalPoint, roots);
                foundRoots++;
            }
        }

        // If inconsistent numbers, there are unfixable errors
        // let the caller handle it
        if (foundRoots != roots.length) {
            return  null;
        }

        return  invertedRoots(roots);
    }

    private void addRoot(int foundRoots, FiniteFieldElement root, FiniteFieldElement[] roots) {
        if (foundRoots < roots.length) {
            roots[foundRoots] = root;
        }
    }

    private FiniteFieldElement[] invertedRoots(FiniteFieldElement[] roots) {
        return Arrays.stream(roots)
                .map(x -> FiniteFieldElement.getOne().divide(x))
                .toArray(FiniteFieldElement[]::new);
    }

    private Polynomial getErrorEvaluatorPoly(FiniteFieldElement[] syndromes,
                                             Polynomial errorLocatorPoly) {
        Polynomial syndromePoly = getReversedSyndromePoly(syndromes);
        Polynomial errorEvaluatorPoly = new Polynomial(syndromePoly);
        //
        errorEvaluatorPoly.multiply(errorLocatorPoly);
        errorEvaluatorPoly.trimTo(errorLocatorPoly.getCoefficients().length);
        errorEvaluatorPoly.contract();
        return  errorEvaluatorPoly;
    }

    /**
     * Find the error magnitudes e_j
     * Using Forney algo for this:
     * https://en.wikipedia.org/wiki/Forney_algorithm
     * @param errorLocations - Error location elements (alpha^j,
     *                       where j is error position, starting from low degrees)
     * @param errorEvalPoly - The evaluator Poly Omega - equal to S(x) * Lambda(x)
     * @param errorLocPoly - The locator Poly Lambda
     * @return - the error magnitudes corresponding to the 'errorLocations'
     */
    private FiniteFieldElement[] getErrorMagnitudes(FiniteFieldElement[] errorLocations,
                                                    Polynomial errorEvalPoly,
                                                    Polynomial errorLocPoly) {
        Polynomial LambdaPrime = getFormalDerivative(errorLocPoly);
        return Arrays.stream(errorLocations)
                .map(x -> getMagnitude(x, errorEvalPoly, LambdaPrime))
                .toArray(FiniteFieldElement[]::new);
    }


    private FiniteFieldElement getMagnitude(FiniteFieldElement errorLocation,
                                            Polynomial errorPoly,
                                            Polynomial LambdaPrime) {
        FiniteFieldElement shiftCorrection = errorLocation.power(1 - SHIFT);
        FiniteFieldElement invertedLoc = FiniteFieldElement.getOne().divide(errorLocation);
        FiniteFieldElement OmegaEval = errorPoly.eval(invertedLoc);
        FiniteFieldElement LambdaEval = LambdaPrime.eval(invertedLoc);
        return OmegaEval.divide(LambdaEval).multiply(shiftCorrection);
    }

    private Polynomial getFormalDerivative(Polynomial Lambda) {
        FiniteFieldElement[] newCoefs = new FiniteFieldElement[Lambda.getCoefficients().length - 1];
        Polynomial LambdaPrime = new Polynomial(newCoefs);
        for (int i = 1; i < Lambda.getCoefficients().length; i++) {
            // The coefficient is 0 for even i, otherwise stays same
            FiniteFieldElement coef = (i % 2 == 0)
                    ? FiniteFieldElement.getZero()
                    : Lambda.getIndexedFromLow(i);
            LambdaPrime.setIndexedFromLow(i - 1, coef);
        }
        return LambdaPrime;
    }

    private Polynomial getMsgError(FiniteFieldElement[] errorLocations,
                                   FiniteFieldElement[] errorMagnitudes) {
        Polynomial errorPoly = new Polynomial(getFreshElems(BLOCK_SIZE));
        for(int i = 0; i < errorLocations.length; i++) {
            adjustPoly(errorPoly, errorLocations[i], errorMagnitudes[i]);
        }
        return  errorPoly;
    }

    // Returns Object so that we can use it in zip
    // We are only interested in the side effect so returning null
    private Object adjustPoly(Polynomial poly, FiniteFieldElement location, FiniteFieldElement magnitude) {
        int index = (int) location.logBaseGenerator();
        poly.setIndexedFromLow(index, magnitude);
        return null;
    }

    private FiniteFieldElement[] getFreshElems(int length) {
        FiniteFieldElement[] elems = new FiniteFieldElement[length];
        return Arrays.stream(elems).map(x-> FiniteFieldElement.getZero())
                .toArray(FiniteFieldElement[]::new);
    }
}
