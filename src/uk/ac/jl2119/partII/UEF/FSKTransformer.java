package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.WavManipulation.BufferWavWriter;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public class FSKTransformer implements ITransformer<Byte, Double> {
    private final int symbolDurationInFrames;
    private final double baseFrequency;
    protected long sampleRate;

    /**
     * Binary Frequency shift keying
     */
    protected FSKTransformer(double baseFrequency, int symbolDurationInFrames, long sampleRate) {
        this.sampleRate = sampleRate;
        this.baseFrequency = baseFrequency;
        this.symbolDurationInFrames = symbolDurationInFrames;
    }

    @Override
    public Double[] transform(Byte[] input) {
        int numOfSamples = input.length * symbolDurationInFrames * 8; // Byte is 8 bits
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
        double frequency = bit ? (baseFrequency * 2) : baseFrequency;
        writer.writeFrequency((float)frequency, symbolDurationInFrames);
    }
}
