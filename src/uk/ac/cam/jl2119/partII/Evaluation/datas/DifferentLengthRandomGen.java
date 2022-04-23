package uk.ac.cam.jl2119.partII.Evaluation.datas;

import uk.ac.cam.jl2119.partII.utils.Boxer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DifferentLengthRandomGen implements IDataGenerator {
    private final int lengthMin;
    private final int lengthMax;
    private final int lengthStep;

    public DifferentLengthRandomGen(int lenMin, int lenMax, int step){
        this.lengthMin = lenMin;
        this.lengthMax = lenMax;
        this.lengthStep = step;
    }

    @Override
    public Byte[][] getData() {
        List<Byte[]> allData = new ArrayList<>();
        for(int i = lengthMin; i < lengthMax; i += lengthStep) {
            allData.add(generateRandomBytes(i));
        }

        return allData.toArray(Byte[][]::new);
    }

    protected Byte[] generateRandomBytes(int length) {
        Random rng = new Random();
        byte[] bytes = new byte[length];
        rng.nextBytes(bytes);
        return Boxer.box(bytes);
    }
}
