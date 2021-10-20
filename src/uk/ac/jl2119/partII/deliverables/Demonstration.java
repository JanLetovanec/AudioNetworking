package uk.ac.jl2119.partII.deliverables;

import uk.ac.jl2119.partII.WavManipulation.WavReader;
import uk.ac.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.thirdParty.WavFile.WavFileException;
import java.io.IOException;

public class Demonstration {
    public static void main(String[] args) throws IOException, WavFileException {
        final int SAMPLE_RATE = 44100;		// Samples per second
        final String STABLE_TONE = "./output/stable1k.wav";
        final String CHANGING_TONE = "./output/change1kTo2k.wav";
        final String SWAPPED_CHANGING_TONE = "./output/swappedChange1kTo2k.wav";

        //Demonstrate that a wav file can be created
        // First Create a 3s 1kHz wav
        long numOfFrames = 3 * SAMPLE_RATE;
        double stableFreq = 1000;
        WavWriter writer = WavWriter.getWriter(STABLE_TONE, numOfFrames, SAMPLE_RATE);
        writer.writeFrequency(stableFreq, (int)numOfFrames);
        writer.close();

        // Demonstrate that we are not generating reversed audio or similar
        // Create 1s 1kHz followed by 3s 2kHz
        // Should be 1s followed by 3s of higher pitched tone
        numOfFrames = (1 + 3) * SAMPLE_RATE;
        double firstFreq = 1000;
        double secondFreq = 2000;
        writer = WavWriter.getWriter(CHANGING_TONE, numOfFrames, SAMPLE_RATE);
        writer.writeFrequency(firstFreq, 1 * SAMPLE_RATE);
        writer.writeFrequency(secondFreq, 3 * SAMPLE_RATE);
        writer.close();

        // Demonstrate reading works by swapping halves of previous file
        // Should be 2s of high pitch followed by 1s of lower pitch followed by 1s of high pitch again
        // Allocate buffers
        WavReader reader = WavReader.getReader(CHANGING_TONE);
        numOfFrames = reader.getRemainingSamples();
        writer = WavWriter.getWriter(SWAPPED_CHANGING_TONE, numOfFrames, SAMPLE_RATE);
        double[][] buffer = new double[1][(int)numOfFrames];
        //Read the first half into the second half of the buffer (and vice versa
        long halfSamples = numOfFrames / 2;
        long halfOffset = numOfFrames - halfSamples; // Off by one error possible
        reader.readFrames(buffer, (int)halfOffset, (int)halfSamples);
        reader.readFrames(buffer, 0, (int)reader.getRemainingSamples());
        reader.close();
        // Write the buffer to the file (chose the first channel)
        writer.writeFrames(buffer[0], (int)numOfFrames);
        writer.close();

        // Demonstrate a fine-grain read control
        // E.g. determine how many times sine changes sign in first second (for stable file)
        // Should be ~1999 (cuz first is not counted) but rounding errors
        // Read the buffer in
        reader = WavReader.getReader(STABLE_TONE);
        numOfFrames = 1 * SAMPLE_RATE;
        buffer = new double [1][(int)numOfFrames];  // STABLE_TONE only has 1 channel
        reader.readFrames(buffer, (int)numOfFrames);
        reader.close();
        // Arbitrary logic with buffer
        boolean isPositive = true;
        long changeCount = 0;
        for (int offset = 0; offset < buffer[0].length; offset++) {
            if (isPositive && buffer[0][offset] < 0) {
                isPositive = false;
                changeCount++;
            }
            else if (!isPositive && buffer[0][offset] > 0) {
                isPositive = true;
                changeCount++;
            }
        }
        System.out.println("Change count is: " + changeCount);
    }
}
