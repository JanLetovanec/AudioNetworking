package uk.ac.jl2119.partII.Packets;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketDemodulator implements ITransformer<Double, Byte> {
    private final double stepSize;
    private final double bigStepSize;
    private final long sampleRate;
    private final double batchDuration;
    private final int bitPerBatch;
    private final ITransformer<Double, Byte> demod;

    private final byte seek;
    private  final int preambleLength;
    private final byte start;
    private final int payloadLength;
    private final int footerLength;

    private int currentOffset;

    public PacketDemodulator(ITransformer<Double, Byte> demod, double stepSizeInSeconds,
                             byte seek, int preambleLength, byte startOfPacket,
                             int payloadLength, int footerLength,
                             long sampleRate, double timePerBatch, int bitPerBatch) {
        this.stepSize = stepSizeInSeconds;
        this.bigStepSize = 5 * stepSizeInSeconds;
        this.demod = demod;
        this.sampleRate = sampleRate;
        this.batchDuration = timePerBatch;
        this.bitPerBatch = bitPerBatch;

        this.seek = seek;
        this.preambleLength = preambleLength;
        this.start = startOfPacket;
        this.payloadLength = payloadLength;
        this.footerLength = footerLength;
    }

    @Override
    public Byte[] transform(Double[] input) {
        currentOffset = 0;
        List<Byte> data = new ArrayList<>();
        while (hasDataRemaining(input)) {
            homeAnchor(input);
            seekStartSymbol(input);
            data.addAll(getPayload(input));
            skipFooter();
        }
        return data.toArray(Byte[]::new);
    }

    private boolean hasDataRemaining(Double[] input) {
        return currentOffset < input.length - 1;
    }

    private void homeAnchor(Double[] input) {
        coarseLock(input);
        fineLock(input);
    }

    private void coarseLock(Double[] input) {
        double time = getTimeFromOffset(currentOffset);
        byte currentByte = getByte(time, input);
        while(currentByte != seek) {
            time += bigStepSize;
            currentByte = getByte(time, input);
        }

        currentOffset = getOffsetFromTime(time);
    }

    private void fineLock(Double[] input) {
        double coarseTime = getTimeFromOffset(currentOffset);
        double forwardWrong = findFirstWrong(coarseTime, stepSize, input);
        double backwardWrong = findFirstWrong(coarseTime, -stepSize, input);
        double targetTime = (forwardWrong + backwardWrong) / 2;
        currentOffset = getOffsetFromTime(targetTime);
    }

    private double findFirstWrong(double time, double stepSize, Double[] input) {
        byte currentByte = seek;
        while (currentByte == seek) {
            time += stepSize;
            currentByte = getByte(time, input);
        }
        return time;
    }

    /**
     * Changes the `currentOffset` s.t. it points behind the startSymbol
     * or its best guess
     */
    private void seekStartSymbol(Double[] input) {
        double time = getTimeFromOffset(currentOffset);
        double byteDuration = batchDuration * (8.0/ bitPerBatch);
        double maxGuess = (time * preambleLength + 1) * byteDuration;
        byte currentByte;
        do {
            currentByte = getByte(time, input);
            time = time + byteDuration;
        }
        while(currentByte != start && time < maxGuess);

        currentOffset = getOffsetFromTime(time);
    }

    private byte getByte(double time, Double[] input) {
        double duration = batchDuration * (8.0 / bitPerBatch);
        Double[] byteSignal = StreamUtils.timeSlice(input, time, duration, sampleRate);
        return demod.transform(byteSignal)[0];
    }

    private List<Byte> getPayload(Double[] input) {
        double startTime = getTimeFromOffset(currentOffset);
        double length = batchDuration * payloadLength * (8.0 / bitPerBatch);
        currentOffset = getOffsetFromTime(startTime + length);
        Double[] payload = StreamUtils.timeSlice(input, startTime, length, sampleRate);
        Byte[] payloadData = demod.transform(payload);
        return Arrays.stream(payloadData).toList();
    }

    private void skipFooter() {
        double time = getTimeFromOffset(currentOffset);
        time += batchDuration * footerLength * (8.0 / (double) sampleRate);
        currentOffset = getOffsetFromTime(time);
    }

    private double getTimeFromOffset(int offset) {
        return (double) offset / (double) sampleRate;
    }

    private int getOffsetFromTime(double time) {
        return (int) Math.floor(time * sampleRate);
    }
}
