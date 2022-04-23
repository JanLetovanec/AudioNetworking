package uk.ac.cam.jl2119.partII.Evaluation.datas;

import java.util.Collections;

public class AllZeroGen implements IDataGenerator {
    private int lengthSingle;
    private int numberOfSamples;

    public AllZeroGen(int lengthSingle, int numberOfSamples){
        this.lengthSingle = lengthSingle;
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    public Byte[][] getData() {
        Byte[][] allData = new Byte[numberOfSamples][];
        for(int i = 0; i < numberOfSamples; i++) {
            allData[i] = Collections.nCopies(lengthSingle, 0)
                    .toArray(Byte[]::new);
        }

        return allData;
    }
}
