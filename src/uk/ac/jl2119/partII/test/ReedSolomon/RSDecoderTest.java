package uk.ac.jl2119.partII.test.ReedSolomon;

import com.google.common.base.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.jl2119.partII.utils.Boxer;

public class RSDecoderTest extends GenericTest {
    RSEncoder encoder;
    RSDecoder decoder;
    final int DATA_SIZE = 223;

    @BeforeEach
    void setUp() {
        encoder = new RSEncoder();
        decoder = new RSDecoder();
    }

    @Test
    void decodeAllAsUncorrupted() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsUncorrupted() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    private Byte[] dataAsBytes(String s) {
        return Boxer.box(s.getBytes());
    }
}
