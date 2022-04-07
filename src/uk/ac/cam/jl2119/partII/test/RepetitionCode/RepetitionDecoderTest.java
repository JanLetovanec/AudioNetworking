package uk.ac.cam.jl2119.partII.test.RepetitionCode;

import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode.RepetitionDecoder;
import uk.ac.cam.jl2119.partII.test.GenericTest;

class RepetitionDecoderTest extends GenericTest {
    RepetitionDecoder demodem;

    void setUp(int repetitions) {
        demodem = new RepetitionDecoder(repetitions);
    }

    @Test
    void decodesRepeatedZero3Times() {
        setUp(3);
        Byte[] input = {0,0,0};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void decodesRepeatedZero5Times() {
        setUp(5);
        Byte[] input = {0,0,0,0,0};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void decodesRepeatedZero5TimesWith2BytesTampered() {
        setUp(5);
        Byte[] input = {0xF,0,0,0xF,0};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void decodesRepeated3Bytes3Times() {
        setUp(3);
        Byte[] input = {0,0,0,1,1,1,2,2,2};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0,1,2};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void decodesRepeated3Bytes3TimesWithBytesTampered() {
        setUp(3);
        Byte[] input = {0xF,0,0,1,0xF,1,2,2,0xF};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0,1,2};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void decodesRepeatedZero3TimesWithDifferentBitsBeingTampered() {
        setUp(5);
        Byte[] input = {(0b11), (0b1100), (0b110000)};
        Byte[] output = demodem.transform(input);
        Byte[] expected = {0};

        assertBoxedArrayEquals(expected, output);
    }
}