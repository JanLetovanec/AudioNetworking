package uk.ac.jl2119.partII.Packets;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Slits the stream into packets.
 * Each packet consists of:
 *  - preamble symbol
 *  - start of packet symbol
 *  - data
 *  - footer (all zeroes)
 */
public class PacketEncoder implements ITransformer<Byte, Byte> {
    private final Byte[] preamble;
    private final byte startSymbol;
    private final int packetLength;
    private final int footerLength;

    public PacketEncoder(Byte[] preamble, byte startOfPacket, int payloadLength, int footerLength) {
        this.preamble = preamble;
        this.startSymbol = startOfPacket;
        this.packetLength = payloadLength;
        this.footerLength = footerLength;
    }

    @Override
    public Byte[] transform(Byte[] input) {
        List<List<Byte>> payloads = StreamUtils.partitionData(input, packetLength);
        return payloads.stream()
                .flatMap(this::makePacket)
                .toArray(Byte[]::new);
    }

    private Stream<Byte> makePacket(List<Byte> payload) {
        List<Byte> footer = Collections.nCopies(footerLength, (byte) 0);

        List<Byte> packet = new ArrayList<>(Arrays.stream(preamble).toList());
        packet.add(startSymbol);
        packet.addAll(payload);
        packet.addAll(footer);
        return packet.stream();
    }
}
