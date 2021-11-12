package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class AbstractWriter {
    protected static final int AUDIO_BIT_DEPTH = 16;

    public abstract int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException, WavFileException;

    public abstract int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException;

    public abstract long getSampleRate();
    public abstract void close() throws IOException;

    protected abstract int getFramesRemaining();

    /***
     * Writes a sine wave lasting 'lengthInSamples' with specified 'frequency'
     */
    public void writeFrequency(double frequency, int lengthInSamples) throws WavFileException, IOException {
        if (lengthInSamples > getFramesRemaining()) {
            throw new WavFileException("Cannot write more than remaining number of samples");
        }

        double[] buffer = new double[lengthInSamples];
        for (int offset = 0; offset < lengthInSamples; offset++) {
            buffer[offset] = Math.sin(2.0 * Math.PI * frequency * offset / getSampleRate());
        }
        writeFrames(buffer, lengthInSamples);
    }
}
