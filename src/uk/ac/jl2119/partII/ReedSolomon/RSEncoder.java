package uk.ac.jl2119.partII.ReedSolomon;

import org.ejml.simple.SimpleMatrix;
import uk.ac.jl2119.partII.ITransformer;

import java.util.Arrays;

import static uk.ac.jl2119.partII.utils.StreamUtils.padData;
import static uk.ac.jl2119.partII.utils.StreamUtils.partitionData;

public class RSEncoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;

    private final SimpleMatrix generator;

    public RSEncoder() {
        double[][] matrixData = new double[BLOCK_SIZE][DATA_SIZE];
        for(int row = 0; row < matrixData.length; row++) {
            for(int column = 0; column < matrixData[row].length; column++) {
                matrixData[row][column] = Math.pow(row, column);
            }
        }
        generator = new SimpleMatrix(matrixData);
    }

    @Override
    public Byte[] transform(Byte[] input) {
        input = padData(input, DATA_SIZE);
        Byte[] output = partitionData(input, DATA_SIZE).stream()
                .map(x -> transformBlock(x.toArray(Byte[]::new)))
                .toArray(Byte[]::new);

        return output;
    }

    private Byte[] transformBlock(Byte[] blockData) {
        Polynomial msgPoly = getMessagePoly(blockData);
        Polynomial genPoly = getGeneratorPoly();
        Polynomial remainder = msgPoly.modulo(genPoly);
        msgPoly.add(remainder);

        return extractResult(msgPoly);
    }

    private Polynomial getMessagePoly(Byte[] data) {
        data = padData(data, BLOCK_SIZE);
        FiniteField gf = new FiniteField();
        FiniteFieldElement[] coefficients = Arrays.stream(data)
                .map(gf::valueOf)
                .toArray(FiniteFieldElement[]::new);
        return new Polynomial(coefficients);
    }

    private Polynomial getGeneratorPoly() {
        final int SHIFT = 1;

        FiniteField gf = new FiniteField();
        Polynomial genPoly = new Polynomial(new FiniteFieldElement[]{gf.getOne()});
        for (int i = 0; i < BLOCK_SIZE - DATA_SIZE; i++) {
            // term = (x + alpha^i)
            Polynomial term = new Polynomial(new FiniteFieldElement[]{
                    gf.getOne(),
                    gf.power(gf.getGenerator(), i + SHIFT)
            });
            genPoly.multiply(term);
        }
        return genPoly;
    }

    private Byte[] extractResult(Polynomial poly) {
        return Arrays.stream(poly.getCoefficients())
                .map(x -> (byte)x.getValue())
                .toArray(Byte[]::new);
    }
}
