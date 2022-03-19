package uk.ac.jl2119.partII.Evaluation.datas;

import uk.ac.jl2119.partII.utils.Boxer;

import java.util.Random;

public class RandomDataGen implements IDataGenerator {
    private int lengthSingle;
    private int numberOfSamples;

    public RandomDataGen(int lengthSingle, int numberOfSamples){
        this.lengthSingle = lengthSingle;
        this.numberOfSamples = numberOfSamples;
    }

    @Override
    public Byte[][] getData() {
        Byte[][] allData = new Byte[numberOfSamples][];
        for(int i = 0; i < numberOfSamples; i++) {
            allData[i] = generateRandomBytes(lengthSingle);
        }

        return allData;
    }

    protected Byte[] generateRandomBytes(int length) {
        Random rng = new Random();
        byte[] bytes = new byte[length];
        rng.nextBytes(bytes);
        return Boxer.box(bytes);
    }
}
