package uk.ac.jl2119.partII;

import com.google.common.base.Strings;
import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.jl2119.partII.PSK.PSKDemodulator;
import uk.ac.jl2119.partII.PSK.PSKModulator;
import uk.ac.jl2119.partII.utils.Boxer;

public class Main {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    static ITransformer<Byte, Double> modulator;
    static ITransformer<Double, Double> noiseGenerator;
    static ITransformer<Double, Byte> demodulator;
    static ITransformer<Double, Double> attenuator;

    public static void main(String[] args) {
        String data = Strings.repeat("This is some sample data to be encodded, so be careful about it\n",5);
        attenuator = new AttenuatorTransformer(0.2);
        modulator = new PSKModulator(SAMPLE_RATE);
        //modulator = new UEFModulator(true, SAMPLE_RATE);
        //noiseGenerator = new RayleighFadingTransformer(20, 0.2);
        noiseGenerator = new AWGNTransformer(0.5);
        demodulator = new PSKDemodulator(SAMPLE_RATE);
        //demodulator = new UEFDemodulator(true, SAMPLE_RATE);

        prettyPrint(data);
    }

    public static String simulate(String data) {
        Byte[] bytes = Boxer.box(data.getBytes());
        Byte[] output =
                demodulator.transform(
                noiseGenerator.transform(
                attenuator.transform(
                modulator.transform(bytes))));
        return new String(Boxer.unBox(output));
    }

    public static void prettyPrint(String data) {
        System.out.println("IN:");
        System.out.println(data);
        System.out.println("OUT:");
        System.out.println(simulate(data));
    }
}
