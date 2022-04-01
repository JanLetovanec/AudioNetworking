package uk.ac.jl2119.partII.Packets;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.FixedBatchModulator;

import java.util.Collections;

public class PaketModulator extends ComposedTransformer<Byte, Double> {
    public PaketModulator(FixedBatchModulator modulator,
                          byte seek, int seekCount, byte startSymbol,
                          int payloadLength,
                          int footerLength) {
        super(getEncoder(seek, seekCount, startSymbol, payloadLength, footerLength),
                modulator);
    }

    private static PacketEncoder getEncoder(byte seek, int seekCount, byte startSymbol,
                                            int payloadLength,
                                            int footerLength) {
        Byte[] preamble = Collections.nCopies(seekCount, seek).toArray(Byte[]::new);
        return new PacketEncoder(preamble, startSymbol, payloadLength, footerLength);
    }
}
