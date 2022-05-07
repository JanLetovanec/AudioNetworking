package uk.ac.cam.jl2119.partII.demo;

import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKModulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.cam.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.cam.jl2119.partII.WavManipulation.WavReader;
import uk.ac.cam.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_SAMPLE_RATE;

public class FinalDemo {
    static final int SAMPLE_RATE = (int) DEFAULT_SAMPLE_RATE;
    static final String TEXT_FILENAME = "./output/Presentation/sampleText.txt";
    static final String WAV_FILENAME = "./output/Presentation/demo.wav";
    static final double NOISE_VOLUME = 0;

    public static void main(String[] args) throws IOException, WavFileException {
        Byte[] data = Boxer.box(
                getStringFromFile(TEXT_FILENAME)
                .getBytes());
        Double[] signal = simulate(data);
        writeSignal(signal);

        Double[] retrievedDSignal = readSignal();
        Byte[] decodedData = decodeSignal(retrievedDSignal);
        prettyPrint(decodedData);
    }

    public static Double[] simulate(Byte[] data) {
        String filename = WAV_FILENAME;
        ITransformer<Byte, Double> psk = new DPSKModulator(SAMPLE_RATE);
        ITransformer<Double, Double> attenuate = new AttenuatorTransformer(0.2);
        ITransformer<Double, Double> noise = new AWGNTransformer(0.2 * NOISE_VOLUME);
        return noise.transform(attenuate.transform(psk.transform(data)));
    }

    public static void writeSignal(Double[] data) throws IOException, WavFileException {
        WavWriter writer = WavWriter.getWriter(WAV_FILENAME, data.length, SAMPLE_RATE);
        writer.writeFrames(Boxer.unBox(data), data.length);
        writer.close();
    }

    public static Double[] readSignal() throws IOException, WavFileException {
        WavReader reader = WavReader.getReader(WAV_FILENAME);
        Double[] data = Boxer.box(reader.readFrames((int)reader.getRemainingSamples()));
        reader.close();
        return data;
    }

    public static Byte[] decodeSignal(Double[] data) {
        ITransformer<Double, Byte> psk = new DPSKDemodulator(SAMPLE_RATE);
        return psk.transform(data);
    }

    private static String getStringFromFile(String filename) {
        try {
            return new Scanner(new File(filename)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void prettyPrint(Byte[] data) {
        System.out.println("Original Data:");
        System.out.println(getStringFromFile(TEXT_FILENAME));
        System.out.println("==============================");
        System.out.println("Received Data:");
        System.out.println(new String(Boxer.unBox(data)));
    }
}
