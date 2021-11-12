package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.DigitalToAnalogueTransformer;
import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;

public class TmpPSKEncoder extends Encoder {
    public TmpPSKEncoder(AbstractWriter writer, long baseFrequency, boolean originalMode) {
        super(writer,
                new FSKTransformer(baseFrequency,
                        (int) getFramesPerSymbol(originalMode, writer, baseFrequency),
                        writer.getSampleRate()));
    }

    public TmpPSKEncoder(AbstractWriter writer, long baseFrequency, int cyclesPerZero) {
        super(writer,
                new FSKTransformer(baseFrequency,
                        (int) getFramesPerSymbol(cyclesPerZero, writer, baseFrequency),
                        writer.getSampleRate()));
    }

    private static long getFramesPerSymbol(boolean originalMode, AbstractWriter writer, long baseFrequency) {
        return originalMode
                ? (writer.getSampleRate() / baseFrequency)
                : (writer.getSampleRate() / baseFrequency) * 4; // Alternative mode lasts 4 cycles
    }

    private static long getFramesPerSymbol(int cyclesPerZero, AbstractWriter writer, long baseFrequency) {
        return (writer.getSampleRate() / baseFrequency) * cyclesPerZero;
    }
}
