package uk.ac.jl2119.partII.ReedSolomon;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.ejml.simple.SimpleMatrix;
import uk.ac.jl2119.partII.ITransformer;

import java.util.Arrays;
import java.util.List;

public class RSDecoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;
    private static final byte PRIMITIVE_ELEMENT = 11;

    @Override
    public Byte[] transform(Byte[] input) {
        return partitionData(input,BLOCK_SIZE).stream()
                .flatMap(block -> Arrays.stream(transformBlock(block.toArray(Byte[]::new))))
                .toArray(Byte[]::new);
    }

    private List<List<Byte>> partitionData(Byte[] input, int batchSize) {
        UnmodifiableIterator<List<Byte>> batchedIterator = Iterators
                .partition(Arrays.stream(input).iterator(), batchSize);
        List<List<Byte>> batchedInput = Lists.newArrayList(batchedIterator);
        return batchedInput;
    }

    private Byte[] transformBlock(Byte[] block) {
        final int e = 16;
        SimpleMatrix resultVector = new SimpleMatrix(BLOCK_SIZE, 1);
        SimpleMatrix equationMatrix = new SimpleMatrix(BLOCK_SIZE, BLOCK_SIZE);
        for(int row = 0; row < BLOCK_SIZE; row++) {
            byte alphaValue = (byte)(PRIMITIVE_ELEMENT^row % 0xFF);
            byte bValue = block[row];
            for (int col = 0; col < BLOCK_SIZE; col++) {
                double value;
                if (col < e) {
                    value = bValue * (alphaValue^col);
                }
                else {
                    value = -(PRIMITIVE_ELEMENT^(col - e));
                }
                equationMatrix.set(row, col, value);
            }
            resultVector.set(row, 0, - bValue * (alphaValue^e));
        }

        SimpleMatrix solvedVector = equationMatrix.solve(resultVector);
        
        return new Byte[0];
    }
}
