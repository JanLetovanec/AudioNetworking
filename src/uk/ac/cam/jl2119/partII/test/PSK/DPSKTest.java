package uk.ac.cam.jl2119.partII.test.PSK;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKModulator;
import uk.ac.cam.jl2119.partII.test.GenericTest;

class DPSKTest extends GenericTest {
    final int SAMPLE_RATE = 44100;
    final double BASE_FREQUENCY = 1200;
    final int CYCLES_PER_SYMBOL = 1;

    DPSKModulator modem;
    DPSKDemodulator demod;

    @BeforeEach
    void setUp() {
        modem = new DPSKModulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);
        demod = new DPSKDemodulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);
    }

    @Test
    void tranlatesZeroAsNormal() {
        Byte[] input = {0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesFFAsNormal() {
        Byte[] input = {(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesLotsOfZeoesAsNormal() {
        Byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesLotsOfFFsAsNormal() {
        Byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesHighLowAsNormal() {
        Byte[] input = {5,124,5, 110, 25, 25};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesRandomAsNormal() {
        Byte[] input = generateRandomBytes(500);
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    private Byte[] translateBytes(Byte[] in) {
        return demod.transform(modem.transform(in));
    }
}