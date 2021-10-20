package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFile;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.File;
import java.io.IOException;

public class WavWriter {
    WavFile wavFile;
    private static final int AUDIO_BIT_DEPTH = 16;

    protected WavWriter(WavFile wavFile) {
        this.wavFile = wavFile;
    }

    public static WavWriter getWriter(String filename, long numFrames, long sampleRate) throws IOException, WavFileException {
        File file = new File(filename);
        WavFile newWavFile = WavFile.newWavFile(file, 1, numFrames, AUDIO_BIT_DEPTH, sampleRate);
        return new WavWriter(newWavFile);
    }

    public int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException, WavFileException
    {
        return wavFile.writeFrames(sampleBuffer, 0, numFramesToWrite);
    }

    public int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException
    {
        return wavFile.writeFrames(sampleBuffer, offset, numFramesToWrite);
    }

    /***
     * Writes a sine wave lasting 'lengthInSamples' with specified 'frequency'
     */
    public void writeFrequency(double frequency, int lengthInSamples) throws WavFileException, IOException {
        if (lengthInSamples > wavFile.getFramesRemaining()) {
            throw new WavFileException("Cannot write more than remaining number of samples");
        }

        double[] buffer = new double[lengthInSamples];
        for (int offset = 0; offset < lengthInSamples; offset++) {
            buffer[offset] = Math.sin(2.0 * Math.PI * frequency * offset / wavFile.getSampleRate());
        }
        writeFrames(buffer, lengthInSamples);
    }

    public void close() throws IOException {
        wavFile.close();
    }
}
