package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.utils.Boxer;

/**
 * Removes 0 start bit and 1 end bit for each byte
 */
public class StartStopRemover implements ITransformer<Byte, Byte> {
    @Override
    public Byte[] transform(Byte[] input) {
        byte[] outputBuffer = allocateBuffer(input);
        int inputIndexInBits = 0;

        for(int outputIndex = 0; outputIndex < outputBuffer.length; outputIndex++){
            // Skip the start bit
            inputIndexInBits++;

            outputBuffer[outputIndex] = extractByte(input, inputIndexInBits);
            inputIndexInBits += 8;

            //Skip the end bit
            inputIndexInBits++;
        }

        return Boxer.box(outputBuffer);
    }

    private byte[] allocateBuffer(Byte[] input) {
        int newLength = (int) Math.ceil(input.length * (8.0/10.0));
        return new byte[newLength];
    }

    private byte extractByte(Byte[] inputBuffer, int inputIndexInBits) {
        int inputIndexInBytes = inputIndexInBits / 8;

        //Copy the first half
        byte currentByte = inputBuffer[inputIndexInBytes];
        int contentsOfFirstHalf = getFirstHalfBits(currentByte, inputIndexInBits);

        //Copy the second half
        byte nextByte = inputBuffer[inputIndexInBytes + 1];
        int contentsOfSecondHalf = getSecondHalfBits(nextByte, inputIndexInBits);

        // Combine
        byte result = (byte) (contentsOfFirstHalf | contentsOfSecondHalf);
        return result;
    }

    private byte getFirstHalfBits(byte currentByte, int inputIndexInBits) {
        int shiftAmount = inputIndexInBits % 8;
        int mask = 0xFF >> shiftAmount;
        int contentsOfFirstHalf = currentByte & mask;
        contentsOfFirstHalf = contentsOfFirstHalf << shiftAmount;
        return (byte) contentsOfFirstHalf;
    }

    private byte getSecondHalfBits(byte nextByte, int inputIndexInBits) {
        int shiftAmount = (8 - inputIndexInBits % 8);
        int mask = 0xFF << shiftAmount;
        int contents = nextByte & mask;
        contents = contents >> shiftAmount;
        return (byte) contents;
    }
}
