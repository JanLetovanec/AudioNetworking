package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;

public class UEFEncoder extends Encoder {
    private static final double BASE_FREQUENCY = 1200;

    public UEFEncoder(AbstractWriterFactory writerFactory, boolean originalMode) {
        super(writerFactory,
                new UEFModulator(BASE_FREQUENCY, originalMode, writerFactory.getSampleRate()));
    }
}
