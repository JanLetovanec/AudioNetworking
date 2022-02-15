package uk.ac.jl2119.partII.ReedSolomon;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.Arrays;

import static uk.ac.jl2119.partII.utils.StreamUtils.padData;
import static uk.ac.jl2119.partII.utils.StreamUtils.partitionData;

public class RSDecoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;
    private static byte FIELD_GENERATOR = 3;
    private static byte SHIFT = 1;

    @Override
    public Byte[] transform(Byte[] input) {
        Byte[] paddedData = padData(input, BLOCK_SIZE);
        return partitionData(paddedData, BLOCK_SIZE).stream()
                .map(x -> x.toArray(Byte[]::new))
                .flatMap(x -> Arrays.stream(transformBlock(x)))
                .toArray(Byte[]::new);
    }

    private Polynomial getGeneratorPoly() {
        Polynomial genPoly = new Polynomial(new FiniteFieldElement[]{FiniteFieldElement.getOne()});
        for (int i = 0; i < BLOCK_SIZE - DATA_SIZE; i++) {
            // term = (x + alpha^i)
            Polynomial term = new Polynomial(new FiniteFieldElement[]{
                    FiniteFieldElement.getOne(),
                    new FiniteFieldElement(FIELD_GENERATOR).power(i + SHIFT)
            });
            genPoly.multiply(term);
        }
        return genPoly;
    }

    private Byte[] transformBlock(Byte[] blockData) {
        Polynomial msgPoly = getMessagePoly(blockData);

        FiniteFieldElement[] syndromes = calculateSyndromes(msgPoly);
        if(checkMessage(syndromes)) {
            return extractData(blockData);
        }
        return null;
    }

    private boolean checkMessage(FiniteFieldElement[] syndromes) {
        return Arrays.stream(syndromes)
                .allMatch(x -> x.isZero());
    }

    private FiniteFieldElement[] calculateSyndromes(Polynomial poly) {
        int eccSymbolCount = BLOCK_SIZE - DATA_SIZE - 1;
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
}
