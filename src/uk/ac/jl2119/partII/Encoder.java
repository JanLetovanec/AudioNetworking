package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class Encoder {
    AbstractWriter writer;
    DigitalToAnalogueTransformer digitalToAnalogueTransformer;

    public Encoder(AbstractWriter writer, DigitalToAnalogueTransformer transformer) {
        this.writer = writer;
        this.digitalToAnalogueTransformer = transformer;
    };

    public void generateSignal(byte[] input) throws IOException, WavFileException {
        double[] signalBytes = digitalToAnalogueTransformer.transform(input);
        writer.writeFrames(signalBytes, signalBytes.length);
    }

    public AbstractWriter getWriter() {
        return writer;
    }
}
