package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFile;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.File;
import java.io.IOException;

public class WavWriter extends AbstractWriter {
    WavFile wavFile;

    protected WavWriter(WavFile wavFile) {
        this.wavFile = wavFile;
    }

    public static WavWriter getWriter(String filename, long numFrames, long sampleRate) throws IOException, WavFileException {
        File file = new File(filename);
        WavFile newWavFile = WavFile.newWavFile(file, 1, numFrames, AUDIO_BIT_DEPTH, sampleRate);
        return new WavWriter(newWavFile);
    }

    @Override
    public int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException
    {
        return wavFile.writeFrames(sampleBuffer, offset, numFramesToWrite);
    }

    @Override
    public long getSampleRate() {
        return wavFile.getSampleRate();
    }

    @Override
    public void close() throws IOException {
        wavFile.close();
    }

    @Override
    protected int getFramesRemaining() {
        return (int) wavFile.getFramesRemaining();
    }
}
