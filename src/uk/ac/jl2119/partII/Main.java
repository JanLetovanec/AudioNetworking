package uk.ac.jl2119.partII;

import com.google.common.base.Strings;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.utils.Boxer;

public class Main {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    public static void main(String[] args) {
        String a = Strings.repeat("A", 223);
        String hello = Strings.repeat("HelloWorld", 22).concat("!!!");
        prettyPrint(a);
        prettyPrint(hello);
    }

    public static String simulate(String data) {
        Byte[] encoded = new RSEncoder().transform(Boxer.box(data.getBytes()));
        encoded = tamper(encoded);
        Byte[] decoded = new RSDecoder().transform(encoded);
        return new String(Boxer.unBox(decoded));
    }

    public static String tampered(String data) {
        Byte[] encoded = new RSEncoder().transform(Boxer.box(data.getBytes()));
        encoded = tamper(encoded);
        return new String(Boxer.unBox(encoded));
    }

    private static Byte[] tamper(Byte[] data) {
        data[50] = 0;
        data[145] = 17;
        data[208] = (byte) 250;
        return data;
    }

    public static void prettyPrint(String data) {
        String simData = simulate(data);
        System.out.println("IN:");
        System.out.println(data);
        System.out.println("CORRUPTED:");
        System.out.println(tampered(data));
        System.out.println("OUT:");
        System.out.println(simData);
        System.out.println("===");
        System.out.println(data.compareTo(simData));
        System.out.println("::::::::::");
    }

    public static void printStuff(Byte[] data) {
        System.out.println(new String(Boxer.unBox(data)));
    }
    public static void printStuff(Byte[] data, int finalDigits) {
        for(int i = data.length - finalDigits; i < data.length; i++) {
            String toPrint = String.format("%x |", data[i]);
            System.out.print(toPrint);
        }
    }
}
