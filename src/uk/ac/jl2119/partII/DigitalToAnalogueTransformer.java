package uk.ac.jl2119.partII;

public abstract class DigitalToAnalogueTransformer {
    protected long sampleRate;

    protected DigitalToAnalogueTransformer(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public abstract double[] transform(byte[] input);
}
