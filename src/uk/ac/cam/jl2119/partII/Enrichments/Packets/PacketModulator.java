package uk.ac.cam.jl2119.partII.Enrichments.Packets;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static uk.ac.cam.jl2119.partII.Enrichments.Packets.PacketDemodulator.PREAMBLE_DEFAULT;

/**
 * Slits the stream into packets.
 * Each packet consists of:
 *  - preamble symbol
 *  - start of packet symbol
 *  - data
 *  - footer (all zeroes)
 */
public class PacketModulator implements ITransformer<Byte, Double> {
    private final ITransformer<Byte, Double> headerTf;
    private final ITransformer<Byte, Double> payloadTf;

    private final Byte[] preamble;
    private final int packetLength;
    private final int footerLength;

    public PacketModulator(
            ITransformer<Byte, Double> headerTf, ITransformer<Byte, Double> payloadTf,
            byte seek, int seekCount, byte startOfPacket, int payloadLength, int footerLength) {
        List<Byte> preambleList = new ArrayList<>(Collections.nCopies(seekCount, seek));
        preambleList.add(startOfPacket);
        this.preamble = preambleList.toArray(Byte[]::new);
        this.packetLength = payloadLength;
        this.footerLength = footerLength;

        this.headerTf = headerTf;
        this.payloadTf = payloadTf;
    }

    public PacketModulator(ITransformer<Byte, Double> headerTf, ITransformer<Byte, Double> payloadTf) {
        byte seek = (byte) 0x01111111;
        byte startOfPacket = (byte) 0xEC;
        List<Byte> preambleList = new ArrayList<>(Collections.nCopies(PREAMBLE_DEFAULT, seek));
        preambleList.add(startOfPacket);
        this.preamble = preambleList.toArray(Byte[]::new);
        this.packetLength = 255;
        this.footerLength = PREAMBLE_DEFAULT;


        this.headerTf = headerTf;
        this.payloadTf = payloadTf;
    }

    public PacketModulator(ITransformer<Byte, Double> headerTf, ITransformer<Byte, Double> payloadTf,
                           int packetLength) {
        byte seek = (byte) 0x01111111;
        byte startOfPacket = (byte) 0xEC;
        List<Byte> preambleList = new ArrayList<>(Collections.nCopies(PREAMBLE_DEFAULT, seek));
        preambleList.add(startOfPacket);
        this.preamble = preambleList.toArray(Byte[]::new);
        this.packetLength = packetLength;
        this.footerLength = PREAMBLE_DEFAULT;


        this.headerTf = headerTf;
        this.payloadTf = payloadTf;
    }

    @Override
    public Double[] transform(Byte[] input) {
        input = StreamUtils.padData(input, packetLength);
        List<List<Byte>> payloads = StreamUtils.partitionData(input, packetLength);
        return payloads.stream()
                .flatMap(this::makePacket)
                .toArray(Double[]::new);
    }

    private Stream<Double> makePacket(List<Byte> payload) {
        Double[] preambleSignal = headerTf.transform(preamble);

        Byte[] payloadBytes = payload.toArray(Byte[]::new);
        Double[] payloadSignal = payloadTf.transform(payloadBytes);

        Byte[] footerBytes = Collections.nCopies(footerLength, (byte) 0).toArray(Byte[]::new);
        Double[] footerSignal = headerTf.transform(footerBytes);

        return Stream.concat(Arrays.stream(preambleSignal),
                Stream.concat(Arrays.stream(payloadSignal),
                        Arrays.stream(footerSignal))
        );
    }
}
