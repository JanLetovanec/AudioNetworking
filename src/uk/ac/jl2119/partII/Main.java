package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.UEF.UEFModulator;
import uk.ac.jl2119.partII.UEF.UEFSyncDemodulator;
import uk.ac.jl2119.partII.utils.Boxer;

public class Main {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    public static void main(String[] args) {
//        String a = Strings.repeat("A", 223);
//        String hello = Strings.repeat("HelloWorld", 22).concat("!!!");
//        prettyPrint(a);
//        prettyPrint(hello);
        Byte[] data = new Byte[]{ 0x02, 0x02, 0x02};
        Double[] encoded = new UEFModulator(true, SAMPLE_RATE).transform(data);
        Byte[] decoded = new UEFSyncDemodulator(1200, true, SAMPLE_RATE).transform(encoded);
    }

    public static String simulate(String data) {
        Byte[] bData = Boxer.box(data.getBytes());
        Double[] encoded = new UEFModulator(true, SAMPLE_RATE).transform(bData);
        Byte[] decoded = new UEFSyncDemodulator(1200, true, SAMPLE_RATE).transform(encoded);
        return new String(Boxer.unBox(decoded));
    }

    public static void prettyPrint(String data) {
        String simData = simulate(data);
        System.out.println("IN:");
        System.out.println(data);
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
