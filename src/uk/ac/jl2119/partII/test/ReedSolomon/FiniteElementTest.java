package uk.ac.jl2119.partII.test.ReedSolomon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.jl2119.partII.ReedSolomon.FiniteFieldElement;
import uk.ac.jl2119.partII.test.GenericTest;

public class FiniteElementTest extends GenericTest{
    @Test
    void addSelfIsZero() {
        FiniteFieldElement a = new FiniteFieldElement((byte) 5);
        FiniteFieldElement aa = a.add(a);
        Assertions.assertTrue(aa.isZero());
    }

    @Test
    void add4and2Is6() {
        FiniteFieldElement a = new FiniteFieldElement((byte) 4);
        FiniteFieldElement b = new FiniteFieldElement((byte) 2);
        FiniteFieldElement c = a.add(b);

        Assertions.assertEquals(6, c.getValue());
    }

    @Test
    void multByZeroIsZero() {
        FiniteFieldElement a = new FiniteFieldElement((byte) 4);
        FiniteFieldElement b = new FiniteFieldElement((byte) 0);
        FiniteFieldElement c = a.multiply(b);

        Assertions.assertEquals(0, c.getValue());
    }

    @Test
    void div1By65Is254() {
        FiniteFieldElement a = new FiniteFieldElement((byte) 1);
        FiniteFieldElement b = new FiniteFieldElement((byte) 65);
        FiniteFieldElement c = a.divide(b);

        Assertions.assertEquals(254, c.getValue());
    }

    @Test
    void log6Is26() {
        FiniteFieldElement a = new FiniteFieldElement((byte) 6);
        long c = a.logBaseGenerator();

        Assertions.assertEquals(26, c);
    }
}
