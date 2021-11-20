package uk.ac.jl2119.partII.test.PSK;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.Decoder;
import uk.ac.jl2119.partII.Encoder;
import uk.ac.jl2119.partII.PSK.PSKDecoder;
import uk.ac.jl2119.partII.PSK.PSKEncoder;
import uk.ac.jl2119.partII.WavManipulation.AbstractReaderFactory;
import uk.ac.jl2119.partII.WavManipulation.AbstractWriterFactory;
import uk.ac.jl2119.partII.WavManipulation.WavReaderFactory;
import uk.ac.jl2119.partII.WavManipulation.WavWriterFactory;
import uk.ac.jl2119.partII.test.GenericTest;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PSKCoderTest extends GenericTest {
    private final String FILE_NAME = "./test/testfile.wav";
    private final long SAMPLE_RATE = 44100;
    private final long BASE_FREQUENCY = 1000;

    private Encoder encoder;
    private Decoder decoder;

    @BeforeEach
    void setUp() {
        AbstractWriterFactory writerFactory = new WavWriterFactory(FILE_NAME, SAMPLE_RATE);
        AbstractReaderFactory readerFactory = new WavReaderFactory(FILE_NAME);
        encoder = new PSKEncoder(writerFactory);
        decoder = new PSKDecoder(readerFactory);
    }

    @Test
    void transmitsZero() throws IOException, WavFileException {
        byte[] input = {0};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsFF() throws IOException, WavFileException {
        byte[] input = {(byte) 0xFF};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeoes() throws IOException, WavFileException {
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfZeoesWithMoreCycles() throws IOException, WavFileException {
        setCustomCoders(BASE_FREQUENCY, 5);
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }
    @Test
    void transmitsLotsOfZeoesWithHighFrequency() throws IOException, WavFileException {
        setCustomCoders(10000, 1);
        byte[] input = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfFFs() throws IOException, WavFileException {
        byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsLotsOfFFsWithMoreCycles() throws IOException, WavFileException {
        setCustomCoders(BASE_FREQUENCY, 5);
        byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }
    @Test
    void transmitsLotsOfFFsWithHighFrequency() throws IOException, WavFileException {
        setCustomCoders(10000, 1);
        byte[] input = {(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmitsHighLow() throws IOException, WavFileException {
        byte[] input = {5,124,5, 110, 25, 25};
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmittsRandom() throws IOException, WavFileException {
        byte[] input = generateRandomBytesUnboxed(500);
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmittsRandomWithMoreCycles() throws IOException, WavFileException {
        setCustomCoders(BASE_FREQUENCY, 5);
        byte[] input = generateRandomBytesUnboxed(500);
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    @Test
    void transmittsRandomWithHighFrequency() throws IOException, WavFileException {
        setCustomCoders(10000, 1);
        byte[] input = generateRandomBytesUnboxed(500);
        byte[] output = transmitBytes(input);
        assertArrayEquals(input, output);
    }

    private byte[] transmitBytes(byte[] in) throws IOException, WavFileException {
        encoder.generateSignal(in);
        return decoder.decodeSignal();
    }

    private void setCustomCoders(double frequency, int cyclesPerBit) {
        AbstractWriterFactory writerFactory = new WavWriterFactory(FILE_NAME, SAMPLE_RATE);
        AbstractReaderFactory readerFactory = new WavReaderFactory(FILE_NAME);
        encoder = new PSKEncoder(writerFactory, frequency, cyclesPerBit);
        decoder = new PSKDecoder(readerFactory, frequency, cyclesPerBit);
    }
}