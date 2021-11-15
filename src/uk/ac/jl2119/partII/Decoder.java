package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractReader;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;
import java.util.Arrays;

public abstract class Decoder {
    protected AbstractReaderFactory readerFactory;
    protected AbstractReader reader;
    protected ITransformer<Double, Byte> analogueToDigitalTransformer;

    public Decoder(AbstractReaderFactory readerFactory, ITransformer<Double, Byte> transformer) {
        this.readerFactory = readerFactory;
        this.analogueToDigitalTransformer = transformer;
        reader = null;
    }

    public Byte[] generateSignal() throws IOException, WavFileException {
        reader = readerFactory.createReader();
        double[] buffer = reader.readFrames((int)reader.getRemainingSamples());
        Double[] boxedBuffer = Arrays.stream(buffer)
                .boxed()
                .toArray(Double[]::new);

        Byte[] output = analogueToDigitalTransformer.transform(boxedBuffer);
        return output;
    }

    public AbstractReader getWriter() {
        return reader;
    }
}
