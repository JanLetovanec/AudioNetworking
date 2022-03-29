package uk.ac.jl2119.partII.test.FIRFilter;

import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.Filters.FIRFilter;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

class FIRFilterTest extends GenericTest {

    private static FIRFilter getIdFIR() {
        Double[] coefficients = {0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d};
        coefficients[0] = 1.0;
        return new FIRFilter(coefficients);
    }

    private static FIRFilter getDecayingFIR() {
        Double[] coefficients = {1.0, 0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1,0.0};
        return new FIRFilter(coefficients);
    }

    private static Double[] get1SecondSineWave() throws IOException, WavFileException {
        final int SAMPLE_RATE = 44100;
        BufferWavWriter writer = new BufferWavWriter(1000, SAMPLE_RATE);
        writer.writeFrequency(1200, 0);
        return writer.getBuffer();
    }

    @Test
    void identityFIRLeavesZereos() {
        FIRFilter filter = getIdFIR();
        Double[] input = {0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d};
        Double[] output = filter.transform(input);
        assertBoxedArrayEquals(input, output);
    }


    @Test
    void identityFIRLeavesSine() throws IOException, WavFileException {
        FIRFilter filter = getIdFIR();
        Double[] input = get1SecondSineWave();
        Double[] output = filter.transform(input);
        assertBoxedArrayEquals(input, output);
    }

    @Test
    void decayingFIRProducesItsCoefficientsOnImpulse() {
        FIRFilter filter = getDecayingFIR();
        Double[] input = {1d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d};
        Double[] output = filter.transform(input);
        Double[] expectedOutput = {1.0, 0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1,0.0};
        assertBoxedArrayEquals(output, expectedOutput);
    }
}