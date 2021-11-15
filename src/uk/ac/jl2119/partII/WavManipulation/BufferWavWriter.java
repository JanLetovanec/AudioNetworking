package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

/**
 * Writer, that writes to internal buffer
 * This buffer is accessible afterwards
 */
public class BufferWavWriter extends AbstractWriter{
    private final Double[] buffer;
    private int headPointer;
    private final long sampleRate;

    public BufferWavWriter(long numFrames, long sampleRate) {
        buffer = new Double[(int)numFrames];
        headPointer = 0;
        this.sampleRate = sampleRate;
    }

    @Override
    public int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) throws WavFileException {
        // Do we have enough space?
        if (numFramesToWrite > getFramesRemaining()) {
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
        return buffer.length - headPointer;
    }

    public Double[] getBuffer() {return buffer;}
}
