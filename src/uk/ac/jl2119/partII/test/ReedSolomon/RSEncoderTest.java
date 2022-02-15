package uk.ac.jl2119.partII.test.ReedSolomon;

import com.google.common.base.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.jl2119.partII.utils.Boxer;

import static org.junit.Assert.assertEquals;

public class RSEncoderTest extends GenericTest {
    // The test cases for stand-alone encoder
    // are compared against a relevant output of a 'reference implementation'
    // This reference implementation can be found here:
    // https://github.com/lrq3000/unireedsolomon/blob/887dd2ce49a7ebd81e94b6032d08888629ea577a/unireedsolomon/polynomial.py#L12

    RSEncoder encoder;
    final int DATA_SIZE = 223;

    @BeforeEach
    void setUp() {
        encoder = new RSEncoder();
    }

    @Test
    void encodeAllAs() {
        String text = Strings.repeat("A",223);
        Byte[] data = encodeString(text);

        Byte[] expectedParityBytes = Boxer.box(Strings.repeat("A",32).getBytes());
        assertMessageIsSame(text, data, DATA_SIZE);
        assertParityBits(expectedParityBytes, data, DATA_SIZE);
    }

    @Test
    void encodeAllBs() {
        String text = Strings.repeat("B",223);
        Byte[] data = encodeString(text);

        Byte[] expectedParityBytes = Boxer.box(Strings.repeat("B",32).getBytes());
        assertMessageIsSame(text, data, DATA_SIZE);
        assertParityBits(expectedParityBytes, data, DATA_SIZE);
    }

    @Test
    void encodeHelloWorlds() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] data = encodeString(text);

        Byte[] expectedParityBytes = Boxer.convert(new long[]{
                196,0,10,141,209,36,90,231,192,109,82,238,92,
                153,216,66,230,36,239,98,15,25,100,5,198,14,
                146,199,175,236,209,158});
        assertMessageIsSame(text, data, DATA_SIZE);
        assertParityBits(expectedParityBytes, data, DATA_SIZE);
    }

    private Byte[] encodeString(String s) {
        Byte[] data = Boxer.box(s.getBytes());
        return encoder.transform(data);
    }

    private void assertMessageIsSame(String orignial, Byte[] actual, int length) {
        Byte[] originalData = Boxer.box(orignial.getBytes());
        for (int i = 0; i < length; i++) {
            assertEquals(originalData[i], actual[i]);
        }
    }

    private void assertParityBits(Byte[] expected, Byte[] actual, int length) {
        assertEquals(expected.length, actual.length - length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i + length]);
        }
    }
}
