package uk.ac.jl2119.partII.WavManipulation;

public abstract class AbstractReaderFactory {
    protected long sampleRate;
    AbstractReaderFactory(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public abstract AbstractReader createReader();

    public long getSampleRate() {return sampleRate;}
}
