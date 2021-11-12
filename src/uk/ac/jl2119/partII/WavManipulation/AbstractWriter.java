package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class AbstractWriter {

    public abstract int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException, WavFileException;

    public abstract int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException;

    public abstract long getSampleRate();
    public abstract void close() throws IOException;
}
