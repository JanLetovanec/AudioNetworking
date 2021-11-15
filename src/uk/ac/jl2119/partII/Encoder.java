package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;
import java.util.Arrays;

public abstract class Encoder {
    protected AbstractWriterFactory writerFactory;
    protected AbstractWriter writer;
    protected ITransformer<Byte, Double> digitalToAnalogueTransformer;

    public Encoder(AbstractWriterFactory writerFactory, ITransformer<Byte, Double> transformer) {
        this.writerFactory = writerFactory;
        this.digitalToAnalogueTransformer = transformer;
        writer = null;
    }

    public void generateSignal(byte[] input) throws IOException, WavFileException {
        // Java cannot box byte arrays :/
        Byte[] inputBuffer = new Byte[input.length];
        for (int i = 0; i < input.length; i++) {inputBuffer[i] = input[i];}

        Double[] signalBytes = digitalToAnalogueTransformer.transform(inputBuffer);

        // Or unbox them :/
        double[] buffer = Arrays.stream(signalBytes)
                .mapToDouble(Double::doubleValue)
                .toArray();
        writer = writerFactory.createWriter(signalBytes.length);
        writer.writeFrames(buffer, signalBytes.length);
        writer.close();
    }

    public AbstractWriter getWriter() {
        return writer;
    }
}
