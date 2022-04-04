package uk.ac.cam.jl2119.partII.test.ReedSolomon;

import com.google.common.base.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.cam.jl2119.partII.ReedSolomon.RSEncoder;
import uk.ac.cam.jl2119.partII.test.GenericTest;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

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

    @Test
    void decodeAllAsWithSingleErrorInMessage() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllAsWithSingleErrorInECCSymbols() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[253] = 41;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllAsWithFiveSparseErrors() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[70] = 110;
        encoded[78] = 4;
        encoded[109] = 0;
        encoded[200] = (byte) 250;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllAsWithFiveBurstErrors() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[51] = 110;
        encoded[52] = 4;
        encoded[53] = 0;
        encoded[54] = (byte) 250;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllAsWithMaxErrors() {
        String text = Strings.repeat("A",223);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[51] = 110;
        encoded[52] = 4;
        encoded[53] = 0;
        encoded[54] = 41;
        encoded[70] = 110;
        encoded[71] = 4;
        encoded[72] = 0;
        encoded[73] = 41;
        encoded[74] = 110;
        encoded[10] = 4;
        encoded[11] = 0;
        encoded[12] = 0;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsWithSingleErrorInMessage() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsWithSingleErrorInECCSymbols() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[253] = 41;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsWithFiveSparseErrors() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[70] = 110;
        encoded[78] = 4;
        encoded[109] = 0;
        encoded[200] = (byte) 250;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsWithFiveBurstErrors() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[51] = 110;
        encoded[52] = 4;
        encoded[53] = 0;
        encoded[54] = (byte) 250;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsWithMaxErrors() {
        String text = Strings.repeat("HelloWorld",22).concat("!!!");
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[51] = 110;
        encoded[52] = 4;
        encoded[53] = 0;
        encoded[54] = 41;
        encoded[70] = 110;
        encoded[71] = 4;
        encoded[72] = 0;
        encoded[73] = 41;
        encoded[74] = 110;
        encoded[10] = 4;
        encoded[11] = 0;
        encoded[12] = 0;
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(original, decoded);
    }

    @Test
    void decodeAllHelloWorldsOverMultipleBlocks() {
        String text = Strings.repeat("HelloWorld",30);
        Byte[] original = dataAsBytes(text);
        Byte[] encoded = encoder.transform(original);
        encoded[50] = 41;
        encoded[51] = 110;
        encoded[52] = 4;
        encoded[53] = 0;
        encoded[54] = 41;

        encoded[260] = 110;
        encoded[261] = 4;
        encoded[262] = 0;
        encoded[263] = 41;
        encoded[264] = 110;

        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(StreamUtils.padData(original, DATA_SIZE), decoded);
    }


    @Test
    void decodeUncorruptedRandomData() {
        Byte[] original = generateRandomBytes(DATA_SIZE);
        Byte[] encoded = encoder.transform(original);
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(StreamUtils.padData(original, DATA_SIZE), decoded);
    }

    @Test
    void decodeRandomDataWithFiveErrors() {
        Byte[] original = generateRandomBytes(DATA_SIZE);
        Byte[] encoded = encoder.transform(original);
        encoded = tamperRandomData(encoded, 5);
        Byte[] decoded = decoder.transform(encoded);

        assertBoxedArrayEquals(StreamUtils.padData(original, DATA_SIZE), decoded);
    }

    private Byte[] tamperRandomData(Byte[] input, int numOfErrors) {
        for (int i = 0; i < numOfErrors; i++) {
            int index = Math.floorMod(generateRandomInt(), 0x100);
            byte value = (byte) generateRandomInt();
            input[index] = value;
        }
        return input;
    }

    private Byte[] dataAsBytes(String s) {
        return Boxer.box(s.getBytes());
    }
}
