package uk.ac.jl2119.partII.ReedSolomon;

import uk.ac.jl2119.partII.ITransformer;

public class RSDecoder implements ITransformer<Byte, Byte> {
    private static final int BLOCK_SIZE = 255;
    private static final int DATA_SIZE = 223;
    private static final byte PRIMITIVE_ELEMENT = 11;

    @Override
    public Byte[] transform(Byte[] input) {
        return null;
    }
}
