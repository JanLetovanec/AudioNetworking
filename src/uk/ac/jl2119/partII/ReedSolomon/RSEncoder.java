package uk.ac.jl2119.partII.ReedSolomon;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.ejml.simple.SimpleMatrix;
import uk.ac.jl2119.partII.ITransformer;

import java.util.Arrays;
import java.util.List;

public class RSEncoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;
    private static final byte PRIMITIVE_ELEMENT = 11;

    private SimpleMatrix generator;

    public RSEncoder() {
        double[][] matrixData = new double[BLOCK_SIZE][DATA_SIZE];
        for(int row = 0; row < matrixData.length; row++) {
            byte elementRow = (byte)(PRIMITIVE_ELEMENT^row % 0xFF);
            for(int column = 0; column < matrixData[row].length; column++) {
                matrixData[row][column] = elementRow^column;
            }
        }
        generator = new SimpleMatrix(matrixData);
    }

    @Override
    public Byte[] transform(Byte[] input) {
        return partitionData(input, DATA_SIZE).stream()
                .flatMap(block -> Arrays.stream(transformBlock(block.toArray(Byte[]::new))))
                .toArray(Byte[]::new);
    }

    private List<List<Byte>> partitionData(Byte[] input, int batchSize) {
        UnmodifiableIterator<List<Byte>> batchedIterator = Iterators
                .partition(Arrays.stream(input).iterator(), batchSize);
        List<List<Byte>> batchedInput = Lists.newArrayList(batchedIterator);
        return batchedInput;
    }

    Byte[] transformBlock(Byte[] block) {
        SimpleMatrix blockVector = new SimpleMatrix(DATA_SIZE, 1);
        for (int i = 0; i < block.length; i++){
            blockVector.set(i, 0, block[i]);
        }

        Byte[] result = new Byte[BLOCK_SIZE];
        SimpleMatrix resultVector = generator.mult(blockVector);
        for (int i = 0; i < BLOCK_SIZE; i++) {
            result[i] = (byte)(Math.round(resultVector.get(i, 0)) % 0xFF);
        }
        return result;
    }
}
