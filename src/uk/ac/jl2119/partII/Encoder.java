package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class Encoder {
    protected AbstractWriterFactory writerFactory;
    protected AbstractWriter writer;
    protected DigitalToAnalogueTransformer digitalToAnalogueTransformer;

    public Encoder(AbstractWriterFactory writerFactory, DigitalToAnalogueTransformer transformer) {
        this.writerFactory = writerFactory;
        this.digitalToAnalogueTransformer = transformer;
        writer = null;
    }

    public void generateSignal(byte[] input) throws IOException, WavFileException {
        double[] signalBytes = digitalToAnalogueTransformer.transform(input);
        writer = writerFactory.createWriter(signalBytes.length);
        writer.writeFrames(signalBytes, signalBytes.length);
    }

    public AbstractWriter getWriter() {
        return writer;
    }
}
