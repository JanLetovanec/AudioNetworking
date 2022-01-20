package uk.ac.jl2119.partII.test;

import uk.ac.jl2119.partII.utils.Boxer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class GenericTest {
    protected void assertBoxedArrayEquals(Byte[] expected, Byte[] actual) {
        assertArrayEquals(Boxer.unBox(expected), Boxer.unBox(actual));
    }

    protected void assertBoxedArrayEquals(Double[] expected,Double[] actual) {
        assertArrayEquals(Boxer.unBox(expected), Boxer.unBox(actual));
    }

    protected Byte[] generateRandomBytes(int length) {
        Random rng = new Random();
        byte[] bytes = new byte[length];
        rng.nextBytes(bytes);
        return Boxer.box(bytes);
    }

    protected byte[] generateRandomBytesUnboxed(int length) {
        Random rng = new Random();
        byte[] bytes = new byte[length];
        rng.nextBytes(bytes);
        return bytes;
    }
}
