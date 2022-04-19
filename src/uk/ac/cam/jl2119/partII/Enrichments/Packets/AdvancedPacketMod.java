package uk.ac.cam.jl2119.partII.Enrichments.Packets;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Slits the stream into packets.
 * Each packet consists of:
 *  - preamble symbol
 *  - start of packet symbol
 *  - data
 *  - footer (all zeroes)
 */
public class AdvancedPacketMod extends PacketModulator {
    public static final int PRIMER_LENGTH_BYTES = 32;

    public AdvancedPacketMod(
            ITransformer<Byte, Double> headerTf, ITransformer<Byte, Double> payloadTf,
            int payloadLength) {
        super(headerTf, payloadTf, payloadLength);
    }

    @Override
    public Double[] transform(Byte[] input) {
        Byte[] zeroes = Collections.nCopies(PRIMER_LENGTH_BYTES, (byte) 0).toArray(Byte[]::new);
        Double[] primer = headerTf.transform(zeroes);
        Double[] msg = super.transform(input);
        return Stream.concat(Arrays.stream(primer), Arrays.stream(msg)).toArray(Double[]::new);
    }
}
