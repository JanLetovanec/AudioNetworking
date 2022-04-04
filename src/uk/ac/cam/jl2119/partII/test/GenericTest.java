package uk.ac.cam.jl2119.partII.test;

import uk.ac.cam.jl2119.partII.ReedSolomon.FiniteFieldElement;
import uk.ac.cam.jl2119.partII.ReedSolomon.Polynomial;
import uk.ac.cam.jl2119.partII.utils.Boxer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericTest {
    protected void assertBoxedArrayEquals(Byte[] expected, Byte[] actual) {
        assertArrayEquals(Boxer.unBox(expected), Boxer.unBox(actual));
    }

    protected void assertBoxedArrayEquals(Double[] expected,Double[] actual) {
        assertArrayEquals(Boxer.unBox(expected), Boxer.unBox(actual));
    }

    protected void assertPolyEquals(Polynomial expected, Polynomial actual) {
        FiniteFieldElement[] expectedElems = expected.getCoefficients();
        FiniteFieldElement[] actualElems = actual.getCoefficients();

        assertEquals(expectedElems.length, actualElems.length);
        for (int i = 0; i < expectedElems.length; i++) {
            assertEquals(expectedElems[i].getValue(), actualElems[i].getValue());
        }
    }

    protected void assertElementsEquals(FiniteFieldElement expected, FiniteFieldElement actual) {
        assertEquals(expected.getValue(), actual.getValue());
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

    protected int generateRandomInt() {
        Random rng = new Random();
        return rng.nextInt();
    }

}
