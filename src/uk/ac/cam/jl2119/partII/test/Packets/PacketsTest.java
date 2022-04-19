package uk.ac.cam.jl2119.partII.test.Packets;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Enrichments.Packets.PacketDemodulator;
import uk.ac.cam.jl2119.partII.Enrichments.Packets.PacketModulator;
import uk.ac.cam.jl2119.partII.test.GenericTest;
import uk.ac.cam.jl2119.partII.utils.Boxer;

public class PacketsTest extends GenericTest {


    @Test
    void transmitsAllAsWithPSK() {
        String text = Strings.repeat("A",255);
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.DPSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsHelloWorldsWithPSK() {
        String text = Strings.repeat("HelloWorld",25) + "!!!!!";
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.DPSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsTwoPacketsOfAsPSK() {
        String text = Strings.repeat("HelloWorld",51);
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.DPSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsAllAsWithFSK() {
        String text = Strings.repeat("A",255);
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.FSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsHelloWorldsWithFSK() {
        String text = Strings.repeat("HelloWorld",25) + "!!!!!";
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.FSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsTwoPacketsOfAsFSK() {
        String text = Strings.repeat("HelloWorld",51);
        Byte[] original = dataAsBytes(text);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.FSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsRandomPacketWithPSK() {
        Byte[] original = generateRandomBytes(255);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.DPSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsRandomPacketWithFSK() {
        Byte[] original = generateRandomBytes(255);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.FSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsRandom3PacketsWithPSK() {
        Byte[] original = generateRandomBytes(255*3);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.DPSK);

        assertBoxedArrayEquals(original, received);
    }

    @Test
    void transmitsRandom3PacketsWithFSK() {
        Byte[] original = generateRandomBytes(255*3);
        Byte[] received = transmitData(original, SchemeModulatorMap.CodingScheme.FSK);

        assertBoxedArrayEquals(original, received);
    }

    private Byte[] transmitData(Byte[] data, SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> mod = pair.modem;
        ITransformer<Double, Byte> demod = pair.demodem;

        PacketModulator pMod = new PacketModulator(mod, mod);
        PacketDemodulator pDemod = new PacketDemodulator(demod, demod,
                SchemeModulatorMap.DEFAULT_SAMPLE_RATE,
                1/SchemeModulatorMap.DEFAULT_BASE_FREQUENCY,
                255*8/SchemeModulatorMap.DEFAULT_BASE_FREQUENCY, 1);

        Double[] transmittedSignal = pMod.transform(data);
        return pDemod.transform(transmittedSignal);
    }

    private Byte[] dataAsBytes(String s) {
        return Boxer.box(s.getBytes());
    }
}
