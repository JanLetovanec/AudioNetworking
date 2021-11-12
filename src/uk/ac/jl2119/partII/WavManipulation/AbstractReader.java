package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;
import java.io.IOException;

public abstract class AbstractReader {

    public abstract int readFrames(double[][] sampleBuffer, int numFramesToRead) throws IOException, WavFileException;

    public abstract int readFrames(double[][] sampleBuffer, int offset, int numFramesToRead) throws IOException, WavFileException;

    public abstract long getRemainingSamples();
    public abstract long getSampleRate();
    public abstract void close() throws IOException;
}
