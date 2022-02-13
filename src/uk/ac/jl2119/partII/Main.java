package uk.ac.jl2119.partII;

import com.google.common.base.Strings;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.utils.Boxer;

public class Main {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    static ITransformer<Byte, Double> modulator;
    static ITransformer<Double, Double> noiseGenerator;
    static ITransformer<Double, Byte> demodulator;
    static ITransformer<Double, Double> attenuator;

    public static void main(String[] args) {
        String data = Strings.repeat("AAAAAAAA", 27);
        long[][] matrix = {
                {1,0,6,0,0,0,0,0},
                {5,5,6,6,6,6,6,2},
                {3,6,6,5,3,6,5,2},
                {6,4,6,4,5,1,3,2},
                {3,5,6,3,5,6,3,1},
                {2,3,6,2,3,1,5,6},
                {2,5,6,1,6,1,6,5}
        };
        printStuff(matrix);
        System.out.println("=====");
        //FiniteFieldMatrixSolver solver = new FiniteFieldMatrixSolver(matrix);
        //solver.solve();
        //long[][] result = {solver.getResult()};
        //printStuff(result);
        prettyPrint(data);
    }

    public static String simulate(String data) {
        Byte[] encoded = new RSEncoder().transform(Boxer.box(data.getBytes()));
        Byte[] decoded = new RSDecoder().transform(encoded);
        return new String(Boxer.unBox(decoded));
    }

    public static void prettyPrint(String data) {
        System.out.println("IN:");
        System.out.println(data);
        System.out.println("OUT:");
        System.out.println(simulate(data));
        System.out.println("Done");
    }

    public static void printStuff(long[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(Math.round(matrix[i][j]));
                System.out.print(", ");
            }
            System.out.println();
        }
    }
}
