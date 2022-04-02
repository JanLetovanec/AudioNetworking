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
    private final double batchDurationHeader;
    private final double batchDurationPayload;
    private final int bitPerBatch;
    private final ITransformer<Double, Byte> demodHeader;
    private final ITransformer<Double, Byte> demodPayload;

    private final byte seek;
    private  final int preambleLength;
    private final byte start;
    private final int payloadLength;
    private final int footerLength;

    private int currentOffset;

    public PacketDemodulator(ITransformer<Double, Byte> headerDemod,
                             ITransformer<Double, Byte> payloadDemod,
                             double stepSizeInSeconds,
                             byte seek, int preambleLength, byte startOfPacket, int payloadLength, int footerLength,
                             long sampleRate, double timePerBatchHeader, double timePerBatchPayload, int bitPerBatch) {
        this.stepSize = stepSizeInSeconds;
        this.bigStepSize = 4 * stepSizeInSeconds;
        this.demodHeader = headerDemod;
        this.demodPayload = payloadDemod;
        this.sampleRate = sampleRate;
        this.batchDurationHeader = timePerBatchHeader;
        this.batchDurationPayload = timePerBatchPayload;
        this.bitPerBatch = bitPerBatch;

        this.seek = seek;
        this.preambleLength = preambleLength;
        this.start = startOfPacket;
        this.payloadLength = payloadLength;
        this.footerLength = footerLength;
    }

    public PacketDemodulator(ITransformer<Double, Byte> headerDemod,
                             ITransformer<Double, Byte> payloadDemod,
                             long sampleRate, double timePerBatchHeader, double timePerBatchPayload, int bitPerBatch) {
        this.stepSize = timePerBatchHeader / 30;
        this.bigStepSize = 4 * stepSize;
        this.demodHeader = headerDemod;
        this.demodPayload = payloadDemod;
        this.sampleRate = sampleRate;
        this.batchDurationHeader = timePerBatchHeader;
        this.batchDurationPayload = timePerBatchPayload;
        this.bitPerBatch = bitPerBatch;

        this.seek = (byte) 0x01111111;
        this.preambleLength = 3;
        this.start = (byte) 0xEC;
        this.payloadLength = 255;
        this.footerLength = 5;
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
        if (!isAtBeginning()) {
            fineLock(input);
        }
    }
     private boolean isAtBeginning() {
        return currentOffset <= 1;
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
        double byteDuration = getTimePerByte(true);
        double maxTime = time + (preambleLength + 1) * byteDuration;
        byte currentByte;
        do {
            currentByte = getByte(time, input);
            time = time + byteDuration;
        }
        while(currentByte != start && time < maxTime);

        currentOffset = getOffsetFromTime(time);
    }

    private byte getByte(double time, Double[] input) {
        double duration = getTimePerByte(true);
        Double[] byteSignal = StreamUtils.timeSlice(input, time, duration, sampleRate);
        return demodHeader.transform(byteSignal)[0];
    }

    private List<Byte> getPayload(Double[] input) {
        double startTime = getTimeFromOffset(currentOffset);
        double length = getTimePerByte(false) * payloadLength;
        currentOffset = getOffsetFromTime(startTime + length);
        Double[] payload = StreamUtils.timeSlice(input, startTime, length, sampleRate);
        Byte[] payloadData = demodPayload.transform(payload);
        return Arrays.stream(payloadData).toList();
    }

    private void skipFooter() {
        double time = getTimeFromOffset(currentOffset);
        time += footerLength * getTimePerByte(true);
        currentOffset = getOffsetFromTime(time);
    }

    private double getTimeFromOffset(int offset) {
        return (double) offset / (double) sampleRate;
    }

    private int getOffsetFromTime(double time) {
        return (int) Math.floor(time * sampleRate);
    }

    private double getTimePerByte(boolean isHeader) {
        if (isHeader) {
            return batchDurationHeader * (8.0 / (double) bitPerBatch);
        }
        return batchDurationPayload * (8.0 / (double) bitPerBatch);
    }
}
