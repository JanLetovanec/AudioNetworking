package uk.ac.cam.jl2119.partII.Enrichments.Packets;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketDemodulator implements ITransformer<Double, Byte> {
    public static final int PREAMBLE_DEFAULT = 7;
    public static final int SEEK_SYMBOL = 0b11101010;

    private final double stepSize;
    private final double bigStepSize;
    private final long sampleRate;
    private final double batchDurationHeader;
    private final int bitPerBatch;
    private final ITransformer<Double, Byte> demodHeader;
    private final ITransformer<Double, Byte> demodPayload;

    private final byte seek;
    private  final int preambleLength;
    private final double durationPayload;
    private final byte start;
    private final int footerLength;

    private double currentTime;

    public PacketDemodulator(ITransformer<Double, Byte> headerDemod,
                             ITransformer<Double, Byte> payloadDemod,
                             double stepSizeInSeconds,
                             byte seek, int preambleLength, byte startOfPacket, int footerLength,
                             long sampleRate, double timePerBatchHeader, double durationPayload, int bitPerBatch) {
        this.stepSize = stepSizeInSeconds;
        this.bigStepSize = 2 * stepSizeInSeconds;
        this.demodHeader = headerDemod;
        this.demodPayload = payloadDemod;
        this.sampleRate = sampleRate;
        this.batchDurationHeader = timePerBatchHeader;
        this.bitPerBatch = bitPerBatch;

        this.seek = seek;
        this.preambleLength = preambleLength;
        this.start = startOfPacket;
        this.durationPayload = durationPayload;
        this.footerLength = footerLength;
    }

    public PacketDemodulator(ITransformer<Double, Byte> headerDemod,
                             ITransformer<Double, Byte> payloadDemod,
                             long sampleRate, double timePerBatchHeader, double timePerPayload, int bitPerBatch) {
        this.stepSize = timePerBatchHeader / 40.0;
        this.bigStepSize = 2 * stepSize;
        this.demodHeader = headerDemod;
        this.demodPayload = payloadDemod;
        this.sampleRate = sampleRate;
        this.batchDurationHeader = timePerBatchHeader;
        this.durationPayload = timePerPayload;
        this.bitPerBatch = bitPerBatch;

        this.seek = (byte) SEEK_SYMBOL;
        this.preambleLength = PREAMBLE_DEFAULT;
        this.start = (byte) 0xEC;
        this.footerLength = PREAMBLE_DEFAULT;
    }

    @Override
    public Byte[] transform(Double[] input) {
        currentTime = 0;
        List<Byte> data = new ArrayList<>();
        while (hasDataRemaining(input) && homeAnchor(input)) {
            seekStartSymbol(input);
            data.addAll(getPayload(input));
            skipFooter();
        }
        return data.toArray(Byte[]::new);
    }

    private boolean hasDataRemaining(Double[] input) {
        double totalDuration = (double) input.length / (double) sampleRate;
        return currentTime < (totalDuration - durationPayload); // Fuzzy arithmetic - check a batch ahead
    }

    private boolean homeAnchor(Double[] input) {
        coarseLock(input);
        if (!isAtBeginning()) {
            fineLock(input);
        }
        return hasDataRemaining(input);
    }
     private boolean isAtBeginning() {
        return currentTime <= batchDurationHeader;
     }

    private void coarseLock(Double[] input) {
        byte currentByte = getByte(currentTime, input);
        while(currentByte != seek & hasDataRemaining(input)) {
            currentTime += bigStepSize;
            currentByte = getByte(currentTime, input);
        }
    }

    private void fineLock(Double[] input) {
        double coarseTime = currentTime;
        double forwardWrong = findFirstWrong(coarseTime, stepSize, input);
        double backwardWrong = findFirstWrong(coarseTime, -stepSize, input);
        currentTime = (forwardWrong + backwardWrong) / 2;
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
        double time = currentTime;
        double byteDuration = getTimePerByte();
        double maxTime = time + (preambleLength + 1) * byteDuration;
        byte currentByte;
        do {
            currentByte = getByte(time, input);
            time = time + byteDuration;
        }
        while(currentByte != start && time < maxTime);

        currentTime = time;
    }

    private byte getByte(double time, Double[] input) {
        double duration = getTimePerByte();
        Double[] byteSignal = StreamUtils.timeSlice(input, time, duration, sampleRate);
        return demodHeader.transform(byteSignal)[0];
    }

    private List<Byte> getPayload(Double[] input) {
        Double[] payload = StreamUtils.timeSlice(input, currentTime, durationPayload, sampleRate);
        Byte[] payloadData = demodPayload.transform(payload);

        currentTime += durationPayload;
        return Arrays.stream(payloadData).toList();
    }

    private void skipFooter() {
        currentTime += footerLength * getTimePerByte();
    }

    private double getTimePerByte() {
        return batchDurationHeader * (8.0 / (double) bitPerBatch);
    }
}
