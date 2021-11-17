package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.UEF.UEFDecoder;
import uk.ac.jl2119.partII.UEF.UEFEncoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.jl2119.partII.WavManipulation.WavReaderFactory;
import uk.ac.jl2119.partII.WavManipulation.WavWriterFactory;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, WavFileException {
        final int SAMPLE_RATE = 44100;
        final double BASE_FREQUENCY = 1200;
        final String FILE_NAME = "./output/Module3/test.wav";

        String data = "Hi this is a string with some length, right? " +
                "Hopefully, this will not last like nothing...";
        System.out.println(data);

        AbstractWriterFactory writerFactory = new WavWriterFactory(FILE_NAME, SAMPLE_RATE);
        UEFEncoder encoder = new UEFEncoder(writerFactory, false);
        encoder.generateSignal(data.getBytes());

        AbstractReaderFactory readerFactory = new WavReaderFactory(FILE_NAME);
        UEFDecoder decoder = new UEFDecoder(readerFactory, false);
        byte[] transmittedData = decoder.decodeSignal();

        System.out.println(new String(transmittedData));
    }
}
