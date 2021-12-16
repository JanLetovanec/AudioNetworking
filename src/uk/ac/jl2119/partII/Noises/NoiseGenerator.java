package uk.ac.jl2119.partII.Noises;

import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.WavManipulation.WavReader;
import uk.ac.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.jl2119.partII.utils.Boxer;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

public abstract class NoiseGenerator {
    protected String inputFile;
    protected String outputFile;
    protected long sampleRate;
    protected ITransformer<Double, Double> noiseTransformer;

    public NoiseGenerator(String inputFile, String outputFile,
                          ITransformer<Double, Double> transformer) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.noiseTransformer = transformer;
    }

    public void addNoise() throws IOException, WavFileException {
        sampleRate = getSampleRate();
        Double[] signalBytes = readSignalBytes();
        Double[] signalWithNoise = noiseTransformer.transform(signalBytes);

    }

    private long getSampleRate() throws IOException, WavFileException {
        WavReader reader = WavReader.getReader(inputFile);
        long sampleRate = reader.getSampleRate();
        reader.close();
        return sampleRate;
    }

    private Double[] readSignalBytes() throws IOException, WavFileException {
        WavReader reader = WavReader.getReader(inputFile);
        sampleRate = reader.getSampleRate();
        Double[] buffer =  readSignalBytes();
        reader.close();

        return  buffer;
    }

    private void writeSignalBytes(Double[] signal) throws IOException, WavFileException {
        WavWriter writer = WavWriter.getWriter(outputFile, signal.length, sampleRate);
        writer.writeFrames(Boxer.unBox(signal), signal.length);
        writer.close();
    }
}
