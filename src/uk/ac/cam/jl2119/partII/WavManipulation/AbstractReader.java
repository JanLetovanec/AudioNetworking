package uk.ac.cam.jl2119.partII.WavManipulation;

import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;
import java.io.IOException;

public abstract class AbstractReader {

    public abstract double[] readFrames(int numFramesToRead) throws IOException, WavFileException;

    public abstract long getRemainingSamples();
    public abstract long getSampleRate();
    public abstract void close() throws IOException;
}
