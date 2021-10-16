package uk.ac.jl2119.partII;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ReadExample {
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        int totalFramesRead = 0;
        File wav = new File("wavFile.wav");

        AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(wav);
        int bytesPerFrame =
                audioInputStream.getFormat().getFrameSize();

        // Set an arbitrary buffer size of 1024 frames.
        System.out.println(audioInputStream.available());
        System.out.println(bytesPerFrame);

        int numBytes = 1024 * bytesPerFrame;
        byte[] audioBytes = new byte[numBytes];
        int numBytesRead = 0;
        int numFramesRead = 0;
        // Try to read numBytes bytes from the file.
        while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
            // Calculate the number of frames actually read.
            numFramesRead = numBytesRead / bytesPerFrame;
            totalFramesRead += numFramesRead;
            // Here, do something useful with the audio data that's
            // now in the audioBytes array...
            for (int i = 0; i < numBytesRead; i=i+2) {
                short data = (short) (audioBytes[i + 1] << 8);
                data += audioBytes[i];

                double dbData = (double) data / (double) 0x7FFF;
                System.out.println(dbData);
            }
            return;
        }
    }
}
