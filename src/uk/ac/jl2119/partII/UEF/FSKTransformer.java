package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.DigitalToAnalogueTransformer;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class FSKTransformer extends DigitalToAnalogueTransformer {
    private final int symbolDurationInFrames;
    private final long baseFrequency;

    /**
     * Binary Frequency shift keying
     */
    protected FSKTransformer(long baseFrequency, int symbolDurationInFrames, long sampleRate) {
        super(sampleRate);
        this.baseFrequency = baseFrequency;
        this.symbolDurationInFrames = symbolDurationInFrames;
    }

    @Override
    public double[] transform(byte[] input) {
        int numOfSamples = input.length * symbolDurationInFrames;
        BufferWavWriter writer = new BufferWavWriter(numOfSamples, sampleRate);

        try{
            for(byte dataByte : input) {
                for (int maskBit = 0x80; maskBit > 0; maskBit = maskBit >> 1) {
                    Boolean currentBit = (dataByte & maskBit) > 0;
                    transformBit(currentBit, writer);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return writer.getBuffer();
    }

    private void transformBit(Boolean bit, BufferWavWriter writer) throws IOException, WavFileException {
        long frequency = bit ? baseFrequency*2 : baseFrequency;
        writer.writeFrequency(frequency, symbolDurationInFrames);
    }

}
