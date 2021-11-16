package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.Decoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;

public class UEFDecoder extends Decoder {
    private static final double BASE_FREQUENCY = 1200;

    public UEFDecoder(AbstractReaderFactory readerFactory, boolean originalMode) {
        super(readerFactory,
                new UEFDemodulator(BASE_FREQUENCY, originalMode, readerFactory.getSampleRate()));
    }
}
