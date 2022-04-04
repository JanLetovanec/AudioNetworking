package uk.ac.cam.jl2119.partII.WavManipulation;

import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class AbstractWriter {
    protected static final int AUDIO_BIT_DEPTH = 16;

    public int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException, WavFileException
    {
        return writeFrames(sampleBuffer, 0, numFramesToWrite);
    }

    public abstract int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException;

    public abstract long getSampleRate();
    public abstract void close() throws IOException;

    protected abstract int getFramesRemaining();
}
