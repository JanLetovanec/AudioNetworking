package uk.ac.cam.jl2119.partII.demo;

import com.google.common.base.Strings;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKModulator;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.cam.jl2119.partII.Noises.RayleighFadingTransformer;
import uk.ac.cam.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class DemonstrationModule4 {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    public static void main(String[] args) throws IOException, WavFileException {
        Double[] noise = new AWGNTransformer(0.2).transform(Boxer.box(new double[LENGTH]));
        writeOut("./output/Module4/pureWhite.wav", noise);

        String data = Strings.repeat("This is some sample data to be encodded, so be careful about it",10);
        Double[] signal = new PSKModulator(SAMPLE_RATE).transform(Boxer.box(data.getBytes()));
        writeOut("./output/Module4/pureSignal.wav", signal);

        Double[] signalWithNoise = new AWGNTransformer(0.2).transform(signal);
        writeOut("./output/Module4/noiseSignal.wav", signalWithNoise);

        Double[] signalFaded = new RayleighFadingTransformer(20, 0.2).transform(signal);
        writeOut("./output/Module4/fadedSignal.wav", signal);

        System.out.println("Done");
    }

    private static void writeOut(String fileName, Double[] samples) throws IOException, WavFileException {
        WavWriter writer = WavWriter.getWriter(fileName, samples.length, SAMPLE_RATE);
        writer.writeFrames(Boxer.unBox(samples), samples.length);
        writer.close();

        System.out.println("Wrote: " + fileName);
    }

}
