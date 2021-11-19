package uk.ac.jl2119.partII.test.UEF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.UEF.StartStopRemover;
import uk.ac.jl2119.partII.UEF.StartStopAdder;
import uk.ac.jl2119.partII.test.GenericTest;

class StartStopTest extends GenericTest {
    StartStopAdder adder;
    StartStopRemover remover;

    @BeforeEach
    void setUp() {
        adder = new StartStopAdder();
        remover = new StartStopRemover();
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
    void tranlatesOddNumberOfBytesAsNormal() {
        Byte[] input = {0,1,2,3,4};
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
        return remover.transform(adder.transform(in));
    }
}