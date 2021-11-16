package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.jl2119.partII.utils.Boxer;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

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
        double[] signalBytes = getAnalogueSignal(input);
        writeSignalFile(signalBytes);
    }

    private double[] getAnalogueSignal(byte[] input) {
        Byte[] inputBuffer = Boxer.box(input);
        Double[] signalBytes = digitalToAnalogueTransformer.transform(inputBuffer);
        double[] buffer = Boxer.unBox(signalBytes);
        return buffer;
    }

    private void writeSignalFile(double[] signalBytes) throws IOException, WavFileException {
        writer = writerFactory.createWriter(signalBytes.length);
        writer.writeFrames(signalBytes, signalBytes.length);
        writer.close();
    }
}
