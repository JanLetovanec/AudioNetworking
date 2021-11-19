package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;

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


    public void writeFrequency(double frequency, int lengthInSamples) throws WavFileException, IOException {
        writeFrequency(frequency, lengthInSamples, 0);
    }

    public void writeFrequency(double frequency, int lengthInSamples, double phaseOffset) throws WavFileException, IOException {
        if (lengthInSamples > getFramesRemaining()) {
            throw new WavFileException("Cannot write more than remaining number of samples");
        }

        double[] buffer = new double[lengthInSamples];
        for (int bufferOffset = 0; bufferOffset < lengthInSamples; bufferOffset++) {
            double frequencyScale = 2.0 * Math.PI * frequency / getSampleRate();
            buffer[bufferOffset] = Math.sin(bufferOffset * frequencyScale + phaseOffset);
        }
        writeFrames(buffer, lengthInSamples);
    }
}
