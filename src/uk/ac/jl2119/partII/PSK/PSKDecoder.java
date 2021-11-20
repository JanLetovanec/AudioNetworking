package uk.ac.jl2119.partII.PSK;

import uk.ac.jl2119.partII.Decoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;

public class PSKDecoder extends Decoder {
    private static final double DEFAULT_FREQUENCY = 1000;
    private static final int DEFAULT_CYCLES_PER_BIT = 1;

    public PSKDecoder(AbstractReaderFactory readerFactory) {
        super(readerFactory,
                new PSKDemodulator(DEFAULT_FREQUENCY, DEFAULT_CYCLES_PER_BIT, readerFactory.getSampleRate()));
    }

    public PSKDecoder(AbstractReaderFactory readerFactory, double frequency, int cyclesPerBit) {
        super(readerFactory,
                new PSKDemodulator(frequency, cyclesPerBit, readerFactory.getSampleRate()));
    }
}
