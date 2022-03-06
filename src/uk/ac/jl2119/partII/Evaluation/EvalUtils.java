package uk.ac.jl2119.partII.Evaluation;

public class EvalUtils {
    public static int getCorrectBits(Byte[] expected, Byte[] actual) {
        int total = 0;
        for(int i = 0; i < expected.length & i < actual.length; i++) {
            total += getCorrectBitsInByte(expected[i], actual[i]);
        }
        return total;
    }

    private static int getCorrectBitsInByte(Byte expected, Byte actual) {
        int total = 0;
        for (int i = 0; i < 8; i++) {
            int bitMask = 1 << i;
            boolean isBitSame = (bitMask & expected) == (bitMask & actual);
            if(isBitSame) {
                total++;
            }
        }
        return total;
    }
}
