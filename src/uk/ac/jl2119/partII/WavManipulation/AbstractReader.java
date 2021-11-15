package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;
import java.io.IOException;

public abstract class AbstractReader {

    public int readFrames(double[][] sampleBuffer, int numFramesToRead) throws IOException, WavFileException
    {
        return readFrames(sampleBuffer, 0, numFramesToRead);
    }

    public abstract int readFrames(double[][] sampleBuffer, int offset, int numFramesToRead) throws IOException, WavFileException;

    public abstract long getRemainingSamples();
    public abstract long getSampleRate();
    public abstract void close() throws IOException;
}
