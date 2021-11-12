package uk.ac.jl2119.partII.WavManipulation;

import uk.ac.thirdParty.WavFile.WavFile;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.File;
import java.io.IOException;

public class WavReader extends AbstractReader{
    WavFile wavFile;

    protected WavReader(WavFile wavFile) {
        this.wavFile = wavFile;
    }

    public static WavReader getReader(String filename) throws IOException, WavFileException {
        File file = new File(filename);
        WavFile newWavFile = WavFile.openWavFile(file);
        return new WavReader(newWavFile);
    }

    @Override
    public int readFrames(double[][] sampleBuffer, int numFramesToRead) throws IOException, WavFileException
    {
        return wavFile.readFrames(sampleBuffer, 0, numFramesToRead);
    }

    @Override
    public int readFrames(double[][] sampleBuffer, int offset, int numFramesToRead) throws IOException, WavFileException
    {
        return wavFile.readFrames(sampleBuffer, offset, numFramesToRead);
    }

    @Override
    public long getRemainingSamples() {return wavFile.getFramesRemaining();}

    @Override
    public long getSampleRate() {return wavFile.getSampleRate();}

    @Override
    public void close() throws IOException {wavFile.close();}
}
