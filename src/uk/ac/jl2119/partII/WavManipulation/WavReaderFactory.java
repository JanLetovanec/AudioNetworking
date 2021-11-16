package uk.ac.jl2119.partII.WavManipulation;

public class WavWriterFactory extends AbstractWriterFactory{
    private final String fileName;

    public WavWriterFactory(String fileName, long sampleRate) {
        super(sampleRate);
        this.fileName = fileName;
    }

    @Override
    public AbstractWriter createWriter(long numFrames) {
        try {
            return WavWriter.getWriter(fileName, numFrames, sampleRate);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
