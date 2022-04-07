package uk.ac.cam.jl2119.partII.demo;

import com.google.common.base.Strings;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.cam.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKModulator;
import uk.ac.cam.jl2119.partII.WavManipulation.WavReader;
import uk.ac.cam.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class PresentationDemo {
    static final int SAMPLE_RATE = 44100;
    static final String FILENAME = "./output/Presentation/demo.wav";
    static final double NOISE_VOLUME = 1;

    public static void main(String[] args) throws IOException, WavFileException {
        Byte[] data = Boxer.box(
                Strings.repeat("This is the string we are going to transmit\n", 10)
                .getBytes());
        Double[] signal = simulate(data);
        writeSignal(signal);

        Double[] retrievedDSignal = readSignal();
        Byte[] decodedData = decodeSignal(retrievedDSignal);
        prettyPrint(decodedData);
    }

    public static Double[] simulate(Byte[] data) {
        String filename = "./output/Presentation/demo.vaw";
        ITransformer<Byte, Double> psk = new PSKModulator(SAMPLE_RATE);
        ITransformer<Double, Double> attenuate = new AttenuatorTransformer(0.2);
        ITransformer<Double, Double> noise = new AWGNTransformer(0.2 * NOISE_VOLUME);
        return noise.transform(attenuate.transform(psk.transform(data)));
    }

    public static void writeSignal(Double[] data) throws IOException, WavFileException {
        WavWriter writer = WavWriter.getWriter(FILENAME, data.length, SAMPLE_RATE);
        writer.writeFrames(Boxer.unBox(data), data.length);
        writer.close();
    }

    public static Double[] readSignal() throws IOException, WavFileException {
        WavReader reader = WavReader.getReader(FILENAME);
        Double[] data = Boxer.box(reader.readFrames((int)reader.getRemainingSamples()));
        reader.close();
        return data;
    }

    public static Byte[] decodeSignal(Double[] data) {
        ITransformer<Double, Byte> psk = new PSKDemodulator(SAMPLE_RATE);
        return psk.transform(data);
    }

    public static void prettyPrint(Byte[] data) {
        System.out.println(new String(Boxer.unBox(data)));
    }
}
