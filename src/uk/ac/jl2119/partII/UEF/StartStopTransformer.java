package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.DigitalToDigitalTransformer;

/**
 * Adds 0 start bit and 1 end bit to each byte
 */
public class StartStopTransformer extends DigitalToDigitalTransformer {
    @Override
    public byte[] transform(byte[] input) {
        // Each byte will be 10bits longs... but we want length in bytes (round up)
        int newLength = (int) Math.ceil(input.length * (10.0/8.0));
        byte[] buffer = new byte[newLength];
        int inputIndex = 0;
        int outputIndexInBits = 0;

        while (inputIndex < input.length) {
            byte currentByte = input[inputIndex];

            // Add start bit (0)
            outputIndexInBits++;

            // Copy the first part of byte
            int byteOffset = outputIndexInBits / 8;
            byte bitMask = (byte) (0xFF << (outputIndexInBits % 8));
            byte data = (byte) (currentByte & bitMask);
            data = (byte) (data >> outputIndexInBits % 8);
            buffer[byteOffset] = (byte) (buffer[byteOffset] | data);
            // Copy the second part of byte
            bitMask = (byte) (0xFF >> (8 -(outputIndexInBits % 8)));
            data = (byte) (currentByte & bitMask);
            data = (byte) (0xFF << (8 -(outputIndexInBits % 8)));
            buffer[byteOffset + 1] = (byte) (buffer[byteOffset + 1] | data);
            //Advance counters
            outputIndexInBits += 8;

            //Add stop bit (1)
            byteOffset = outputIndexInBits / 8;
            bitMask = (byte) (0x80 >> (outputIndexInBits % 8)); //0x80 = 1000 0000
            buffer[byteOffset] = (byte) (buffer[byteOffset] | bitMask);
            outputIndexInBits++;

            inputIndex++;
        }
        return buffer;
    }
}