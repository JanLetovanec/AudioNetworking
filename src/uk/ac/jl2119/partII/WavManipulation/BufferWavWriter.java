package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

/**
 * Writer, that writes to internal buffer
 * This buffer is accessible afterwards
 */
public class BufferWavWriter extends AbstractWriter{
    private double[] buffer;
    private int headPointer;
    private long sampleRate;

    public BufferWavWriter(long numFrames, long sampleRate) {
        buffer = new double[(int)numFrames];
        headPointer = 0;
        this.sampleRate = sampleRate;
    }

    @Override
    public int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException, WavFileException {
        return writeFrames(sampleBuffer, 0, numFramesToWrite);
    }

    @Override
    public int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws IOException, WavFileException {
        // Do we have enough space?
        if (headPointer + numFramesToWrite >= buffer.length) {
            throw new WavFileException("Trying to write more than allocated!");
        }

        for(int i = 0; i < numFramesToWrite; i++) {
            buffer[headPointer] = sampleBuffer[offset + i];
            headPointer++;
        }

        return numFramesToWrite;
    }

    @Override
    public long getSampleRate() {
        return sampleRate;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    protected int getFramesRemaining() {
        return buffer.length - headPointer - 1;
    }

    public double[] getBuffer() {return buffer;}
}
