package uk.ac.jl2119.partII.ReedSolomon;

import cc.redberry.rings.poly.FiniteField;
import cc.redberry.rings.poly.univar.UnivariatePolynomialZ64;
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import org.ejml.data.SingularMatrixException;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.Boxer;

import java.util.Arrays;
import java.util.List;

import static cc.redberry.rings.Rings.GF;

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
        System.out.println("Transforming block!");
        int e = 0;//(BLOCK_SIZE - DATA_SIZE) / 2;
        long[] solvedVector = null;
        while (e >= 0 && solvedVector == null) {
            try{
                solvedVector = solve(block, e);
            }
            catch (SingularMatrixException exception) {
                e = e - 1;
            }
        }

        System.out.println(e);
        if (solvedVector == null) {throw new RuntimeException("HEK!");}

        Byte[] eVector = new Byte[e];
        Byte[] qVector = new Byte[DATA_SIZE + e];
        for(int i=0; i < solvedVector.length; i++) {
            if (i < e) {
                eVector[i] = (byte) solvedVector[i];
            }
            else {
                qVector[i - e] = (byte) solvedVector[i];
            }
        }

        return getBlock(eVector, qVector);
    }

    private long[] solve(Byte[] block, int e) {
        int vectorSize = DATA_SIZE + 2*e;
        long[][] equationMatrix = new long[BLOCK_SIZE][vectorSize + 1];
        for(int row = 0; row < equationMatrix.length; row++) {
            byte bValue = block[row];
            for (int col = 0; col < equationMatrix[row].length; col++) {
                long value;
                if (col < e) {
                    value = Math.round(bValue * (Math.pow(row,col))) % 0xFF;
                }
                else {
                    value = Math.round(-Math.pow(row,(col - e))) % 0xFF;
                }
                equationMatrix[row][col] = value;
            }
            equationMatrix[row][0] = Math.round(-(bValue * Math.pow(row,e))) % 0xFF;
        }
        FiniteFieldMatrixSolver solver = new FiniteFieldMatrixSolver(equationMatrix);
        solver.solve();
        return solver.getResult();
    }

    private Byte[] getBlock(Byte[] eVector, Byte[] qVector) {
        if (eVector.length == 0 || Arrays.stream(qVector).allMatch(x->x==0)) {return qVector;}
        // Generator polynomial used: 1+0x +xx+xxx+xxxx+x8
        UnivariatePolynomialZp64 ePoly = UnivariatePolynomialZ64.parse(polyString(eVector)).modulus(BLOCK_SIZE);
        UnivariatePolynomialZp64 qPoly = UnivariatePolynomialZ64.parse(polyString(qVector)).modulus(BLOCK_SIZE);

        final UnivariatePolynomialZp64 generatorPoly =  UnivariatePolynomialZ64.parse("1 + x^2 + x^3 + x^4 + x^8").modulus(BLOCK_SIZE);
        FiniteField<UnivariatePolynomialZp64> gf = GF(generatorPoly);
        UnivariatePolynomialZp64 fPoly = gf.divideExact(qPoly, ePoly);
        return Boxer.convert(fPoly.getDataReferenceUnsafe());
    }

    public static String polyString(Byte[] vector) {
        String totalString = "";
        for (int i=0; i < vector.length; i++) {
            totalString += String.format("%+d*x^%d", vector[i], i);
        }
        return totalString;
    }
}
