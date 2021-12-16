package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.jl2119.partII.utils.Boxer;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, WavFileException {
        final int SAMPLE_RATE = 44100;

        int length = SAMPLE_RATE * 5;
        double[] buffer = new double[length];
        ITransformer<Double, Double> noiseGen = new AWGNTransformer(0.2);
        Double[] output = noiseGen.transform(Boxer.box(buffer));

        WavWriter writer = WavWriter.getWriter("./output/Module4/whiteNoise.wav", length, SAMPLE_RATE);
        writer.writeFrames(Boxer.unBox(output), length);
        writer.close();

        System.out.println("Done");
    }
}
