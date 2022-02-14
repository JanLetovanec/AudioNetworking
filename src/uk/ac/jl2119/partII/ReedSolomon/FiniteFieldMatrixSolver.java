package uk.ac.jl2119.partII.ReedSolomon;

import cc.redberry.rings.poly.FiniteField;
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64;

import static cc.redberry.rings.Rings.GF;

public class FiniteFieldMatrixSolver {
    private final UnivariatePolynomialZp64[][] matrix;
    private FiniteField<UnivariatePolynomialZp64> gf;
    private final UnivariatePolynomialZp64 primitiveElement;

    /**
     * Assumes the last column is the 'result' column
     * */
    public FiniteFieldMatrixSolver(long[][] matrix) {
        //gf = GF(7,1);
        //gf =GF(UnivariatePolynomialZ64.parse("1 +2*x +3*x^2").modulus(7));
        gf = GF(2, 8);
        //gf = GF(UnivariatePolynomialZ64.parse("1 + x^2 + x^3 + x^4 + x^8").modulus(255));
        primitiveElement = gf.valueOf(11);
        //primitiveElement = gf.valueOf(3);
        //GF(UnivariatePolynomialZ64.parse("1 +2*x +3*x^2").modulus(7));
        this.matrix = getGFMatrix(matrix, gf);
    }

    private static UnivariatePolynomialZp64[][] getGFMatrix(long[][] matrix, FiniteField gf) {
        UnivariatePolynomialZp64[][] thisMatrix =
                new UnivariatePolynomialZp64[matrix.length][matrix[0].length];
        for(int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                thisMatrix[row][col] = (UnivariatePolynomialZp64) gf.valueOf(matrix[row][col]);
            }
        }
        return thisMatrix;
    }

    public int getRows() {return matrix.length;}
    public int getColumns() {
        if (matrix.length > 0) {
            return matrix[0].length;
        }
        return 0;
    }

    public long[] getResult() {
        long[] result = new long[getRows()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (matrix[i][getColumns() - 1]).getDataReferenceUnsafe()[0];
        }
        return result;
    }

    public void solve(){
        for (int row = 0; row < getRows(); row++) {
            boolean needsSwapping = matrix[row][row].isZero();
            if(needsSwapping) {
                if (findNextSwapIndex(row, row) < 0) {
                    break;
                }
                swap(row, findNextSwapIndex(row, row));
            }
            eliminate(row);
        }
        checkResult();
    }

    private void eliminate(int rowIndex) {
        normalize(rowIndex);
        for(int row = 0; row < getColumns() - 1; row++) {
            boolean isCurrentRow = row == rowIndex;
            boolean isZero = matrix[row][rowIndex].isZero();
            if (isCurrentRow || isZero) {
                continue;
            }

            UnivariatePolynomialZp64 factor = matrix[row][rowIndex].copy();
            subMultiples(row, rowIndex, factor);
        }
    }

    private boolean checkResult() {
        return true;
    }

    private void normalize(int rowIndex) {
        if (matrix[rowIndex][rowIndex].isZero() || matrix[rowIndex][rowIndex].isOne()) {
            return;
        }

        divide(rowIndex, matrix[rowIndex][rowIndex].copy());
    }

    private void swap(int rowIndex1, int rowIndex2) {
        UnivariatePolynomialZp64[] temp = matrix[rowIndex1];
        matrix[rowIndex1] = matrix[rowIndex2];
        matrix[rowIndex2] = temp;
    }

    private int findNextSwapIndex(int startingIndex, int positionInVector) {
        for (int i = startingIndex; i < getRows(); i++) {
            if (!matrix[i][positionInVector].isZero()) {
                return i;
            }
        }
        return -1;
    }

    private void subMultiples(int targetRowIndex, int sourceRowIndex, UnivariatePolynomialZp64 sourceFactor) {
        for (int i = 0; i < getColumns(); i++) {
            matrix[targetRowIndex][i].subtract(
                    matrix[sourceRowIndex][i].copy().multiply(sourceFactor.copy())
            );
        }
    }

    private void divide(int targetRowIndex, UnivariatePolynomialZp64 factor) {
        for (int i = 0; i < getColumns(); i++) {
            matrix[targetRowIndex][i] = gf.divideExact(matrix[targetRowIndex][i], factor);
        }
    }
}
