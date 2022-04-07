package uk.ac.cam.jl2119.partII.test.RepetitionCode;

import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode.RepetitionDecoder;
import uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode.RepetitionEncoder;
import uk.ac.cam.jl2119.partII.test.GenericTest;

class RepetitionCodeTest extends GenericTest {
    RepetitionEncoder modem;
    RepetitionDecoder demod;

    void setUp(int repetitions) {
        modem = new RepetitionEncoder(repetitions);
        demod = new RepetitionDecoder(repetitions);
    }

    @Test
    void tranlatesZeroAsNormalWith5() {
        setUp(5);
        Byte[] input = {0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesFFAsNormalWith5() {
        setUp(5);
        Byte[] input = {(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesLotsOfZeoesAsNormalWith5() {
        setUp(5);
        Byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesLotsOfFFsAsNormalWith5() {
        setUp(5);
        Byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesHighLowAsNormalWith5() {
        setUp(5);
        Byte[] input = {5,124,5, 110, 25, 25};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesHighLowAsNormalWith3() {
        setUp(3);
        Byte[] input = {5,124,5, 110, 25, 25};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesRandomAsNormalWith5() {
        setUp(5);
        Byte[] input = generateRandomBytes(500);
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesTamperedByte() {
        setUp(3);
        Byte[] input = {5};
        Byte[] toTransmit = modem.transform(input);
        toTransmit[2] = 0;
        Byte[] output = demod.transform(toTransmit);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlates3TamperedByteOutOf7() {
        setUp(7);
        Byte[] input = {5};
        Byte[] toTransmit = modem.transform(input);
        toTransmit[0] = 0;
        toTransmit[1] = 0;
        toTransmit[2] = 0;
        Byte[] output = demod.transform(toTransmit);
        assertBoxedArrayEquals(input, output);
    }

    private Byte[] translateBytes(Byte[] in) {
        return demod.transform(modem.transform(in));
    }
}