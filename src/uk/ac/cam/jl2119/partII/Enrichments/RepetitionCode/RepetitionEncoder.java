package uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class RepetitionEncoder implements ITransformer<Byte, Byte> {
    private final int repetitions;

    public RepetitionEncoder(int numOfRepetitions) {
        repetitions = numOfRepetitions;
    }

    @Override
    public Byte[] transform(Byte[] input) {
        return Arrays.stream(input)
                .flatMap(b -> repeatByte(b))
                .toArray(Byte[]::new);
    }

    private Stream<Byte> repeatByte(Byte value) {
        return Collections.nCopies(repetitions, value).stream();
    }
}
