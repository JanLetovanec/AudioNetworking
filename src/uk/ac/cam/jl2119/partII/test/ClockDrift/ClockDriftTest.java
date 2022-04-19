package uk.ac.cam.jl2119.partII.test.ClockDrift;

import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.DPSKModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.UEFDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.UEFModulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.ClockDriftTransformer;
import uk.ac.cam.jl2119.partII.test.GenericTest;

public class ClockDriftTest extends GenericTest {
    final int SAMPLE_RATE = 44100;
    final double BASE_FREQUENCY = 1200;
    final int CYCLES_PER_SYMBOL = 1;

    final long oversampleFactor = ClockDriftTransformer.OVERSAMPLE_FACTOR;

    ITransformer<Byte, Double> modem;
    ClockDriftTransformer noise;
    ITransformer<Double, Byte> demod;

    @Test
    void tranlatesZeroAsNormalPSK() {
        modem = new DPSKModulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new DPSKDemodulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);

        Byte[] input = {0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesFFAsNormalPSK() {
        modem = new DPSKModulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new DPSKDemodulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);

        Byte[] input = {(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesHighLowAsNormalPSK() {
        modem = new DPSKModulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new DPSKDemodulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);

        Byte[] input = {5,124,5, 110, 25, 25};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesRandomAsNormalPSK() {
        modem = new DPSKModulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new DPSKDemodulator(BASE_FREQUENCY, CYCLES_PER_SYMBOL, SAMPLE_RATE);

        Byte[] input = generateRandomBytes(500);
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }


    @Test
    void tranlatesZeroAsNormalUEF() {
        modem = new UEFModulator(true, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new UEFDemodulator(true, SAMPLE_RATE);

        Byte[] input = {0};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesFFAsNormalUEF() {
        modem = new UEFModulator(true, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new UEFDemodulator(true, SAMPLE_RATE);

        Byte[] input = {(byte) 0xFF};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesHighLowAsNormalUEF() {
        modem = new UEFModulator(true, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new UEFDemodulator(true, SAMPLE_RATE);

        Byte[] input = {5,124,5, 110, 25, 25};
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void tranlatesRandomAsNormalUEF() {
        modem = new UEFModulator(true, SAMPLE_RATE*oversampleFactor);
        noise = new ClockDriftTransformer(1);
        demod = new UEFDemodulator(true, SAMPLE_RATE);

        Byte[] input = generateRandomBytes(500);
        Byte[] output = translateBytes(input);
        assertBoxedArrayEquals(input, output);
    }

    private Byte[] translateBytes(Byte[] in) {
        return demod.transform(noise.transform(modem.transform(in)));
    }
}
