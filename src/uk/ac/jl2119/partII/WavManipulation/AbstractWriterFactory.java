package uk.ac.jl2119.partII.WavManipulation;

public abstract class AbstractWriterFactory {
    protected long sampleRate;
    AbstractWriterFactory(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public abstract AbstractWriter createWriter(long numFrames);

    public long getSampleRate() {return sampleRate;}
}
