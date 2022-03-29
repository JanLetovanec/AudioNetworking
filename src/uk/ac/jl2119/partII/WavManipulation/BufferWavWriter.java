package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.jl2119.partII.utils.Boxer;

import java.io.IOException;

/**
 * Writer, that writes to internal buffer
 * This buffer is accessible afterwards
 */
public class BufferWavWriter extends AbstractWriter{
    private final double[] buffer;
    private double elapsedTime;
    private final long sampleRate;

    public BufferWavWriter(double durationInSeconds, long sampleRate) {
        int length = (int) Math.floor(durationInSeconds * sampleRate);
        buffer = new double[length];
        elapsedTime = 0;
        this.sampleRate = sampleRate;
    }

    public BufferWavWriter(int bufferSize, long sampleRate) {
        buffer = new double[bufferSize];
        elapsedTime = 0;
        this.sampleRate = sampleRate;
    }

    @Override
    public int writeFrames(double[] sampleBuffer, int offset, int numFramesToWrite) {
        int headPointer = getOffsetFromTime(elapsedTime);
        int framesToWrite = Math.min(numFramesToWrite, getFramesRemaining());
        elapsedTime = getTimeFromOffset(headPointer + framesToWrite);

        for(int i = 0; i < framesToWrite; i++) {
            buffer[headPointer] = sampleBuffer[offset + i];
            headPointer++;
        }

        return framesToWrite;
    }

    public int writeSignal(double[] sampleBuffer, double lengthToWrite) {
        int startOffset = getOffsetFromTime(elapsedTime);
        elapsedTime += lengthToWrite;
        int finishOffset = getOffsetFromTime(elapsedTime);

        for(int i = startOffset; i < finishOffset; i++) {
            int offsetInSample = Math.min(i - startOffset, sampleBuffer.length - 1);
            buffer[i] = sampleBuffer[offsetInSample];
        }

        return finishOffset - startOffset;
    }

    public void writeFrequency(double frequency, double phase) {
        double lengthInSeconds = getTimeFromOffset(buffer.length);
        writeFrequency(frequency, lengthInSeconds, phase);
    }

    public void writeFrequency(double frequency, double lengthInSeconds, double phaseOffset) {
        int startOffset = getOffsetFromTime(elapsedTime);
        elapsedTime += lengthInSeconds;
        int finishOffset = Math.min(getOffsetFromTime(elapsedTime), buffer.length);

        for (int i = startOffset; i < finishOffset; i++) {
            double frequencyScale = (2.0 * Math.PI * frequency * i) / getSampleRate();
            buffer[i] = Math.sin(frequencyScale + phaseOffset);
        }
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
        int headPointer = (int) Math.floor(elapsedTime * sampleRate );
        return buffer.length - headPointer;
    }

    private int getOffsetFromTime(double time) {
        return (int)Math.floor(time * sampleRate);
    }
    private double getTimeFromOffset(int offset) {
        return ((double) offset)/((double) sampleRate);
    }

    public Double[] getBuffer() {return Boxer.box(buffer);}
}
