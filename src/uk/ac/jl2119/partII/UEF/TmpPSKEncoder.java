package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;

public class TmpPSKEncoder extends Encoder {
    public TmpPSKEncoder(AbstractWriterFactory writerFactory, long baseFrequency, boolean originalMode) {
        super(writerFactory, getTransformer(writerFactory, baseFrequency, originalMode));
    }

    public TmpPSKEncoder(AbstractWriterFactory writerFactory, long baseFrequency, int cyclesPerZero) {
        super(writerFactory, getTransformer(writerFactory, baseFrequency, cyclesPerZero));
    }

    private static FSKTransformer getTransformer(AbstractWriterFactory writerFactory,
                                                 long baseFrequency,
                                                 boolean originalMode) {
        int cyclesPerZero = originalMode ? 1 : 4;
        return getTransformer(writerFactory, baseFrequency, cyclesPerZero);
    }

    private static FSKTransformer getTransformer(AbstractWriterFactory writerFactory,
                                                 long baseFrequency,
                                                 int cyclesPerZero) {
        long framesPerCycle = writerFactory.getSampleRate() / baseFrequency;
        long framesPerZero = framesPerCycle * cyclesPerZero;
        return new FSKTransformer(baseFrequency,
                (int) framesPerZero,
                writerFactory.getSampleRate());
    }
}
