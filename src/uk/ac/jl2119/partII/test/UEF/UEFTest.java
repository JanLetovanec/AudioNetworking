package uk.ac.jl2119.partII.test.UEF;

import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.UEF.UEFDecoder;
import uk.ac.jl2119.partII.UEF.UEFEncoder;
import uk.ac.jl2119.partII.WavManipulation.*;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UEFTest extends GenericTest {
    private final String FILE_NAME = "./test/testfile.wav";
    private final long SAMPLE_RATE = 44100;

    @Test
    void transmitsZeroInOriginal() throws IOException, WavFileException {
        byte[] input = {0};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsZeroInAlternative() throws IOException, WavFileException {
        byte[] input = {0};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsFFInOriginal() throws IOException, WavFileException {
        byte[] input = {(byte) 0xFF};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsFFInAlternative() throws IOException, WavFileException {
        byte[] input = {(byte) 0xFF};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeoesInOriginal() throws IOException, WavFileException {
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeroesInAlternative() throws IOException, WavFileException {
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfFFsInOriginal() throws IOException, WavFileException {
        byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsHighLowInOriginal() throws IOException, WavFileException {
        byte[] input = {5,124,5, 110, 25, 25};
        byte[] output = transmitBytes(input,true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmittsRandomInOriginal() throws IOException, WavFileException {
        byte[] input = generateRandomBytesUnboxed(100);
        byte[] output = transmitBytes(input, true);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsRandomInAlternative() throws IOException, WavFileException {
        byte[] input = generateRandomBytesUnboxed(100);
        byte[] output = transmitBytes(input, false);
        assertArrayEquals(input, output);
    }


    private byte[] transmitBytes(byte[] in, boolean originalMode) throws IOException, WavFileException {
        AbstractWriterFactory writerFactory = new WavWriterFactory(FILE_NAME, SAMPLE_RATE);
        UEFEncoder encoder = new UEFEncoder(writerFactory, originalMode);
        encoder.generateSignal(in);

        AbstractReaderFactory readerFactory = new WavReaderFactory(FILE_NAME);
        UEFDecoder decoder = new UEFDecoder(readerFactory, originalMode);
        return decoder.decodeSignal();
    }
}