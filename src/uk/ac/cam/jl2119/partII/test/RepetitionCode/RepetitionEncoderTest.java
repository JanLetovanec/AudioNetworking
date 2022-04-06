package uk.ac.cam.jl2119.partII.test.RepetitionCode;

import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.RepetitionCode.RepetitionEncoder;
import uk.ac.cam.jl2119.partII.test.GenericTest;

class RepetitionEncoderTest extends GenericTest {
    RepetitionEncoder modem;

    void setUp(int repetitions) {
        modem = new RepetitionEncoder(repetitions);
    }

    @Test
    void repeatsZero3Times() {
        setUp(3);
        Byte[] input = {0};
        Byte[] output = modem.transform(input);
        Byte[] expected = {0,0,0};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void repeatsZero5Times() {
        setUp(5);
        Byte[] input = {0};
        Byte[] output = modem.transform(input);
        Byte[] expected = {0,0,0,0,0};

        assertBoxedArrayEquals(expected, output);
    }

    @Test
    void repeats3Bytes3Times() {
        setUp(3);
        Byte[] input = {0,1,2};
        Byte[] output = modem.transform(input);
        Byte[] expected = {0,0,0,1,1,1,2,2,2};

        assertBoxedArrayEquals(expected, output);
    }
}