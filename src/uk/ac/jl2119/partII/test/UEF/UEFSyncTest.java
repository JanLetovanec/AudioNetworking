package uk.ac.jl2119.partII.test.UEF;

import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.UEF.UEFModulator;
import uk.ac.jl2119.partII.UEF.UEFSyncDemodulator;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.jl2119.partII.utils.Boxer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UEFSyncTest extends GenericTest {
    private final long SAMPLE_RATE = 44100;

    @Test
    void transmitsZeroInOriginal() {
        byte[] input = {0};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsZeroInAlternative() {
        byte[] input = {0};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsFFInOriginal() {
        byte[] input = {(byte) 0xFF};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsFFInAlternative() {
        byte[] input = {(byte) 0xFF};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeoesInOriginal() {
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeroesInAlternative() {
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfFFsInOriginal() {
        byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsHighLowInOriginal() {
        byte[] input = {5,124,5, 110, 25, 25};
        byte[] output = transmitBytes(input,true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmittsRandomInOriginal() {
        byte[] input = generateRandomBytesUnboxed(500);
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsRandomInAlternative() {
        byte[] input = generateRandomBytesUnboxed(500);
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }


    private byte[] transmitBytes(byte[] in, boolean originalMode){
        double stepSize = 3.0 / SAMPLE_RATE;
        UEFModulator modulator = new UEFModulator(originalMode, SAMPLE_RATE);
        UEFSyncDemodulator demodulator = new UEFSyncDemodulator(1200, originalMode, SAMPLE_RATE, stepSize);

        Double[] signal = modulator.transform(Boxer.box(in));
        return Boxer.unBox(demodulator.transform(signal));
    }
}