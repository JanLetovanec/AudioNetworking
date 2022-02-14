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
        String data = Strings.repeat("AAAAAAAA", 20);
        Byte[] encoded = new RSEncoder().transform(Boxer.box(data.getBytes()));
        printStuff(encoded);
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

    public static void printStuff(Byte[] data) {
        System.out.println(new String(Boxer.unBox(data)));
    }
}
