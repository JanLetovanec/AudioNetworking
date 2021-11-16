package uk.ac.jl2119.partII.WavManipulation;

public class WavReaderFactory extends AbstractReaderFactory{
    private final String fileName;

    public WavReaderFactory(String fileName) {
        super(extractSampleRate(fileName));
        this.fileName = fileName;
    }

    @Override
    public AbstractReader createReader() {
        try {
            return WavReader.getReader(fileName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long extractSampleRate(String fileName) {
        try {
            WavReader reader = WavReader.getReader(fileName);
            long sampleRate = reader.getSampleRate();
            reader.close();
            return sampleRate;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
