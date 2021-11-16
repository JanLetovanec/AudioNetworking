package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractReader;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;
import uk.ac.jl2119.partII.utils.Boxer;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class Decoder {
    protected AbstractReaderFactory readerFactory;
    protected AbstractReader reader;
    protected ITransformer<Double, Byte> analogueToDigitalTransformer;

    public Decoder(AbstractReaderFactory readerFactory, ITransformer<Double, Byte> transformer) {
        this.readerFactory = readerFactory;
        this.analogueToDigitalTransformer = transformer;
        reader = null;
    }

    public byte[] generateSignal() throws IOException, WavFileException {
        Double[] signalBytes = readSignalBytes();
        return getDigitalData(signalBytes);
    }

    private Double[] readSignalBytes() throws IOException, WavFileException {
        reader = readerFactory.createReader();
        double[] buffer = reader.readFrames((int)reader.getRemainingSamples());
        Double[] boxedBuffer = Boxer.box(buffer);
        return boxedBuffer;
    }

    private byte[] getDigitalData(Double[] signalBytes) {
        Byte[] output = analogueToDigitalTransformer.transform(signalBytes);
        return Boxer.unBox(output);
    }

    public AbstractReader getWriter() {
        return reader;
    }
}
