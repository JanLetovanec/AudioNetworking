package uk.ac.jl2119.partII.test.ReedSolomon;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.jl2119.partII.utils.Boxer;

import static org.junit.Assert.assertEquals;

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
        String decodedText = decodeIntoString(encodeString(text));

        Assertions.assertEquals(text, decodedText);
    }

    @Test
    void decodeAllHelloWorldsUncorrupted() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        String decodedText = decodeIntoString(encodeString(text));

        assertEquals(text, decodedText);
    }

    private Byte[] encodeString(String s) {
        Byte[] data = Boxer.box(s.getBytes());
        return encoder.transform(data);
    }

    private String decodeIntoString(Byte[] data) {
        return new String(Boxer.unBox(encoder.transform(data)));
    }
}
