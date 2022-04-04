package uk.ac.cam.jl2119.partII.UEF;

import uk.ac.cam.jl2119.partII.Filters.LowPassFilterTransformer;
import uk.ac.cam.jl2119.partII.ITransformer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.List;

public class UEFSyncDemodulator implements ITransformer<Double, Byte> {
    private final ITransformer<Double, Byte> fsk;
    private final ITransformer<Double, Double> lpf;
    private final double secondsPerBit;
    private final double stepSize;
    private final long sampleRate;

    public UEFSyncDemodulator(double baseFrequency, boolean originalMode, long sampleRate, double stepSize) {
        secondsPerBit = getSecondsPerBit(baseFrequency, originalMode);
        this.sampleRate = sampleRate;
        this.stepSize = stepSize;
        fsk = new FSKDemodulator(baseFrequency, secondsPerBit, sampleRate);
        lpf = new LowPassFilterTransformer(sampleRate, 2*baseFrequency);
    }

    private static double getSecondsPerBit(double baseFrequency, boolean originalMode) {
        int cyclesPerZero = originalMode ? 1 : 4;
        return cyclesPerZero / baseFrequency;
    }

    @Override
    public Byte[] transform(Double[] input) {
        input = lpf.transform(input);
        return transformFilteredInput(input);
    }

    private Byte[] transformFilteredInput(Double[] input) {
        double time = 0;
        List<Byte> result = new ArrayList<>();
        while (getOffsetFromTime(time) < input.length) {
            result.add(getByte(input, time));
            time += secondsPerBit*10;
            time = synchronise(input, time);
        }
        return result.toArray(Byte[]::new);
    }


    private byte getByte(Double[] source, double time) {
        double length = secondsPerBit * 10;
        Double[] byteSignal = StreamUtils.timeSlice(source, time, length, sampleRate);
        Byte[] byteData = fsk.transform(byteSignal);

        // If we do not have data, just return 0
        if (byteData.length != 2) {
            return 0;
        }

       return extractByteWithStartStops(byteData);
    }

    private byte extractByteWithStartStops(Byte[] bytes) {
        int data = (bytes[0] << 1) | ((bytes[1] & 0xFF) >>> 7);
        data = data & 0xFF;
        return (byte)data;
    }

    private double synchronise(Double[] source, double time) {
        if (!hasMoreBytes(source, time)) {
            return ((double) source.length / (double) sampleRate);
        }

        // Carry on if bit-error
        if(!checkBits(source, time)) {
            return time;
        }

        // Else re-center yourself
        double wrongForward = getFirstWrongOffset(source, time, stepSize);
        double wrongBackwards = getFirstWrongOffset(source, time, -stepSize);
        return (wrongForward + wrongBackwards) / 2;
    }

    private boolean hasMoreBytes(Double[] source, double time) {
        double endTime = time + 8*secondsPerBit;
        int offset = getOffsetFromTime(endTime);
        return source.length - 1 > offset;
    }

    private double getFirstWrongOffset(Double[] source, double startTime, double stepSize) {
        double time = startTime;
        while(checkBits(source, time)) {
            time += stepSize;
        }
        return time;
    }

    private boolean checkBits(Double[] source, double time) {
        double start = time - secondsPerBit;
        double duration = secondsPerBit * 8; // We will only use the first 2 bits, but fsk can only do Byte at a time
        Double[] syncSignal = StreamUtils.timeSlice(source, start, duration, sampleRate);
        byte bits = fsk.transform(syncSignal)[0];
        int relevantBits = (bits & 0b11000000);
        return relevantBits == 0b10000000;
    }

    private int getOffsetFromTime(double time) {
        return (int) Math.floor(time*sampleRate);
    }
}
