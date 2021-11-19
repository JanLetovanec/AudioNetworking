package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;

public class UEFEncoder extends Encoder {
    private static final double BASE_FREQUENCY = 1200;

    public UEFEncoder(AbstractWriterFactory writerFactory, boolean originalMode) {
        super(writerFactory,
                new UEFModulator(BASE_FREQUENCY, originalMode, writerFactory.getSampleRate()));
    }

    private static FSKModulator getAnalogueTransformer(AbstractWriterFactory writerFactory, boolean mode) {
        int cyclesPerZero = mode ? 1 : 4;
        long framesPerCycle = (Math.round(Math.floor(writerFactory.getSampleRate() / BASE_FREQUENCY)));
        long framesPerZero = framesPerCycle * cyclesPerZero;

        return new FSKModulator(BASE_FREQUENCY, (int)framesPerZero, writerFactory.getSampleRate());
    }
}
