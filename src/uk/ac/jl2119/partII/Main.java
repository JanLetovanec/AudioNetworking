package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.UEF.UEFEncoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.jl2119.partII.WavManipulation.WavWriterFactory;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, WavFileException {
        final int SAMPLE_RATE = 44100;

        String data = "Hi this is a string with some length, right? " +
                "Hopefully, this will not last like nothing..." +
                "Hopefully, this will not last like nothing..." +
                "Hopefully, this will not last like nothing..." +
                "Hopefully, this will not last like nothing..." +
                "Hopefully, this will not last like nothing...";

        AbstractWriterFactory factory = new WavWriterFactory("./output/Module2/test.wav", SAMPLE_RATE);
        Encoder encoder = new UEFEncoder(factory, false);

        encoder.generateSignal(data.getBytes());

    }
}
