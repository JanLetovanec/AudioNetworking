package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.Boxer;

/**
 * Adds 0 start bit and 1 end bit to each byte
 */
public class StartStopAdder implements ITransformer<Byte, Byte> {

    @Override
    public Byte[] transform(Byte[] input) {
        // Each byte will be 10bits longs... but we want length in bytes (round up)
        byte[] outputBuffer = allocateBufferSpace(input);
        int outputIndexInBits = 0;

        for(int inputIndex = 0; inputIndex < input.length; inputIndex++) {
            // Add start bit (0)
            outputIndexInBits++;

            byte currentByte = input[inputIndex];
            copyByte(outputBuffer, outputIndexInBits, currentByte);
            outputIndexInBits += 8;

            //Add stop bit (1)
            addStopBit(outputBuffer, outputIndexInBits);
            outputIndexInBits++;
        }

        return Boxer.box(outputBuffer);
    }

    private byte[] allocateBufferSpace(Byte[] input) {
        int newLength = (int) Math.ceil(input.length * (10.0/8.0));
        return new byte[newLength];
    }

    private void copyByte(byte[] buffer, int outputIndexInBits, byte currentByte) {
        copyFirstHalf(buffer, outputIndexInBits, currentByte);
        copySecondHalf(buffer, outputIndexInBits, currentByte);
    }

    private void copyFirstHalf(byte[] buffer, int outputIndexInBits, byte currentByte) {
        int byteOffset = outputIndexInBits / 8;
        int bitMask = 0xFF << (outputIndexInBits % 8);
        int data = (currentByte & bitMask) & 0xFF;// Clip input to byte, in case some bits spilled out
        data = data >>> outputIndexInBits % 8;
        buffer[byteOffset] = (byte) (buffer[byteOffset] | data);
    }

    private void copySecondHalf(byte[] buffer, int outputIndexInBits, byte currentByte) {
        int byteOffset = outputIndexInBits / 8;
        int bitMask = 0xFF >>> (8 -(outputIndexInBits % 8));
        int data = (currentByte & bitMask); // No need to clip to byte here
        data = data << (8 -(outputIndexInBits % 8));
        buffer[byteOffset + 1] = (byte) (buffer[byteOffset + 1] | data);
    }

    private void addStopBit(byte[] buffer, int outputIndexInBits){
        int byteOffset = outputIndexInBits / 8;
        int bitMask = 0x80 >> (outputIndexInBits % 8); //0x80 = 1000 0000
        buffer[byteOffset] = (byte) (buffer[byteOffset] | bitMask);
    }
}
