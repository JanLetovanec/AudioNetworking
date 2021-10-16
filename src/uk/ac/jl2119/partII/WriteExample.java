package uk.ac.jl2119.partII;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

public class WriteExample {

    public static void main(String[] args) throws IOException {
        File wav = new File("wavFile.wav");
        int channels = 1;
        int sampleRate = 44000;
        float time = 3;

        int frames = (int)(time * sampleRate);
        int samples = frames * channels;
        short [] data = new short[samples];

        for(int i=0 ; i<data.length ; i++){
            double currentTime = (double) i/sampleRate;
            double frequency = 1000 * (Math.PI * 2);
            double maxAmplitude = 0xFFFF;
            double amplitudeAtTime = Math.sin(currentTime*frequency);
            data[i] = (short) (Math.round(amplitudeAtTime*maxAmplitude));
        }

        byte [] buf = new byte[data.length * 2];
        for(int i=0 ; i<data.length ; i++){
            buf[i*2+0] = (byte)(data[i] & 0xFF);
            buf[i*2+1] = (byte)((data[i] >> 8) & 0xFF);
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(buf);
        AudioFormat format = new AudioFormat(sampleRate, 16, channels, true, false);
        AudioInputStream audioStream = new AudioInputStream(stream, format, data.length);
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wav);
    }
}
