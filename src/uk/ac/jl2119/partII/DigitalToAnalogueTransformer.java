package uk.ac.jl2119.partII;

public abstract class DigitalToAnalogueTransformer implements ITransformer<Byte, Double> {
    protected long sampleRate;

    protected DigitalToAnalogueTransformer(long sampleRate) {
        this.sampleRate = sampleRate;
    }
}
