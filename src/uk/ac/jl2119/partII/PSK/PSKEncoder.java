package uk.ac.jl2119.partII.PSK;

import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;

public class PSKEncoder extends Encoder {
    private static final double DEFAULT_FREQUENCY = 1000;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    public PSKEncoder(AbstractWriterFactory writerFactory) {
        super(writerFactory,
                new PSKModulator(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT, writerFactory.getSampleRate()));
    }

    public PSKEncoder(AbstractWriterFactory writerFactory, double frequency) {
        super(writerFactory,
                new PSKModulator(frequency, DEFAULT_CYCLES_PER_BIT, writerFactory.getSampleRate()));
    }

    public PSKEncoder(AbstractWriterFactory writerFactory, double frequency, int cyclesPerBit) {
        super(writerFactory,
                new PSKModulator(frequency, cyclesPerBit, writerFactory.getSampleRate()));
    }
}
