package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.Filters.LowPassFilterTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

public class UEFSyncDemodulator implements ITransformer<Double, Byte> {
    private final ITransformer<Double, Byte> fsk;
    private final ITransformer<Double, Double> lpf;
    private final int framesPerBit;
    private final int stepSize;

    public UEFSyncDemodulator(double baseFrequency, boolean originalMode, long sampleRate, int stepSize) {
        framesPerBit = (int)getFramesPerBit(baseFrequency, originalMode, sampleRate);
        this.stepSize = stepSize;
        fsk = new FSKDemodulator(baseFrequency, framesPerBit, sampleRate);
        lpf = new LowPassFilterTransformer(sampleRate, 2*baseFrequency);
    }

    private static long getFramesPerBit(double baseFrequency, boolean originalMode, long sampleRate) {
        int cyclesPerZero = originalMode ? 1 : 4;
        long framesPerCycle = (Math.round(Math.floor(sampleRate / baseFrequency)));
        return framesPerCycle * cyclesPerZero;
    }

    @Override
    public Byte[] transform(Double[] input) {
        input = lpf.transform(input);
        return transformFilteredInput(input);
    }

    private Byte[] transformFilteredInput(Double[] input) {
        int offset = 0;
        int index = 0;
        int totalLength = (int)Math.floor((double)input.length / ((double) framesPerBit*10));
        Byte[] result = StreamUtils.padData(totalLength);
        while (offset < input.length) {
            result[index] = getByte(input, offset);

            offset += framesPerBit*10;
            index++;
            offset = synchronise(input, offset);
        }
        return result;
    }


    private byte getByte(Double[] source, int offset) {
        int length = framesPerBit * 10;
        Double[] byteSignal = StreamUtils.slice(source, offset, length);
        Byte[] byteData = fsk.transform(byteSignal);

        // If we do not have data, just return 0
        if (byteData.length != 2) {
            return 0;
        }

        int data = (byteData[0] << 1) | ((byteData[1] & 0xFF) >>> 7);
        data = data & 0xFF;
        return (byte)data;
    }

    private int synchronise(Double[] source, int offset) {
        if (offset + framesPerBit*8 > source.length) {
            return source.length;
        }

        if(!checkBits(source, offset)) {
            return offset;
        }

        // Else re-center yourself
        int wrongForward = getFirstWrongOffset(source, offset, stepSize);
        int wrongBackwards = getFirstWrongOffset(source, offset, -stepSize);
        return (wrongForward + wrongBackwards) / 2;
    }

    private int getFirstWrongOffset(Double[] source, int startOffset, int stepSize) {
        int offset = startOffset;
        while(checkBits(source, offset)) {
            offset += stepSize;
        }
        return offset;
    }

    private boolean checkBits(Double[] source, int offset) {
        int start = offset - framesPerBit;
        int length = framesPerBit * 8; // We will only use the first 2 bits, but fsk can only do Byte at a time
        Double[] syncSignal = StreamUtils.slice(source, start, length);
        byte bits = fsk.transform(syncSignal)[0];
        int relevantBits = (bits & 0b11000000);
        return relevantBits == 0b10000000;
    }
}
