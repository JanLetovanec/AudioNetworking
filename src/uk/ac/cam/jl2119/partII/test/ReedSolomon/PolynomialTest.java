package uk.ac.cam.jl2119.partII.test.ReedSolomon;

import org.junit.jupiter.api.Test;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.FiniteFieldElement;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.Polynomial;
import uk.ac.cam.jl2119.partII.test.GenericTest;

import java.util.Arrays;

public class PolynomialTest extends GenericTest {

    @Test
    void addIndependentAsIndependent() {
        Polynomial p1 = getPoly(new int[]{1,0,1});
        Polynomial p2 = getPoly(new int[]{1,0});
        p1.add(p2);

        Polynomial expectedPoly = getPoly(new int[]{1,1,1});
        assertPolyEquals(expectedPoly, p1);
    }

    @Test
    void addXorsSame() {
        Polynomial p1 = getPoly(new int[]{1,0,1});
        Polynomial p2 = getPoly(new int[]{1,1});
        p1.add(p2);

        Polynomial expectedPoly = getPoly(new int[]{1,1,0});
        assertPolyEquals(expectedPoly, p1);
    }

    @Test
    void addFromSmallerResizes() {
        Polynomial p1 = getPoly(new int[]{1,0});
        Polynomial p2 = getPoly(new int[]{1,0,1});
        p1.add(p2);

        Polynomial expectedPoly = getPoly(new int[]{1,1,1});
        assertPolyEquals(expectedPoly, p1);
    }

    @Test
    void multiplySimple() {
        // (x^4 + x^3) * (x^2 + 1)
        Polynomial p1 = getPoly(new int[]{1,1,0,0,0});
        Polynomial p2 = getPoly(new int[]{1,0,1});
        p1.multiply(p2);

        // =? x^6 + x^5 + x^4 + x^3
        Polynomial expectedPoly = getPoly(new int[]{1,1,1,1,0,0,0});
        assertPolyEquals(expectedPoly, p1);
    }

    @Test
    void multiplyWithCollision() {
        // (x^2 + 1) * (x^3 + x)
        Polynomial p1 = getPoly(new int[]{1,0,1});
        Polynomial p2 = getPoly(new int[]{1,0,1,0});
        p1.multiply(p2);

        // =? x^5 + (x^4 + x^4 = 0) + x
        Polynomial expectedPoly = getPoly(new int[]{1,0,0,0,1,0});
        assertPolyEquals(expectedPoly, p1);
    }

    @Test
    void divisionExact() {
        // x^2 / x
        Polynomial p1 = getPoly(new int[]{1,0, 0});
        Polynomial p2 = getPoly(new int[]{1,0});
        Polynomial[] qr = p1.divAndMod(p2);

        // = x ,rem = 0
        Polynomial expectedQuotient = getPoly(new int[]{1,0});
        Polynomial expectedRemainder = getPoly(new int[]{});
        assertPolyEquals(expectedQuotient, qr[0]);
        assertPolyEquals(expectedRemainder, qr[1]);
    }

    @Test
    void divisionExactThatRequiresXor() {
        // x^2 + 1 / x + 1
        Polynomial p1 = getPoly(new int[]{1,0, 1});
        Polynomial p2 = getPoly(new int[]{1,1});
        Polynomial[] qr = p1.divAndMod(p2);

        // = x + 1,rem = 0
        Polynomial expectedQuotient = getPoly(new int[]{1,1});
        Polynomial expectedRemainder = getPoly(new int[]{});
        assertPolyEquals(expectedQuotient, qr[0]);
        assertPolyEquals(expectedRemainder, qr[1]);
    }

    @Test
    void divisionWithRemainder() {
        // x^2 + 1 / x + 1
        Polynomial p1 = getPoly(new int[]{1,1,1});
        Polynomial p2 = getPoly(new int[]{1,1});
        Polynomial[] qr = p1.divAndMod(p2);

        // = x + 1,rem = 0
        Polynomial expectedQuotient = getPoly(new int[]{1,0});
        Polynomial expectedRemainder = getPoly(new int[]{1});
        assertPolyEquals(expectedQuotient, qr[0]);
        assertPolyEquals(expectedRemainder, qr[1]);
    }

    @Test
    void evaluateSimplePolyAtTwo() {
        Polynomial p1 = getPoly(new int[]{1,1,1});
        FiniteFieldElement point = new FiniteFieldElement((byte) 2);
        FiniteFieldElement evaluation = p1.eval(point);

        FiniteFieldElement expectedResult = new FiniteFieldElement((byte) 7);
        assertElementsEquals(expectedResult, evaluation);
    }

    @Test
    void evaluateComplexPolyAtThree() {
        Polynomial p1 = getPoly(new int[]{5,4,1,2});
        FiniteFieldElement point = new FiniteFieldElement((byte) 7);
        FiniteFieldElement evaluation = p1.eval(point);

        FiniteFieldElement expectedResult = new FiniteFieldElement((byte) 141);
        assertElementsEquals(expectedResult, evaluation);
    }

    @Test
    void contractRemovesZeroesButNotData() {
        Polynomial p1 = getPoly(new int[]{0,0,0,5,4,1,2});
        p1.contract();

        Polynomial expectedResult = getPoly(new int[] {5,4,1,2});
        assertPolyEquals(expectedResult, p1);
    }

    @Test
    void trimLeavesDataAlone() {
        Polynomial p1 = getPoly(new int[]{5,0,0,1,2,3,4});
        p1.trimTo(4);

        Polynomial expectedResult = getPoly(new int[] {1,2,3,4});
        assertPolyEquals(expectedResult, p1);
    }

    @Test
    void trimNothingIfShorter() {
        Polynomial p1 = getPoly(new int[]{1,2,3,4});
        p1.trimTo(250);

        Polynomial expectedResult = getPoly(new int[] {1,2,3,4});
        assertPolyEquals(expectedResult, p1);
    }

    @Test
    void indexingFromLowRetrievesCorrect() {
        Polynomial p1 = getPoly(new int[]{1,2,3,4,5});
        FiniteFieldElement a = p1.getIndexedFromLow(1);

        FiniteFieldElement expected = new FiniteFieldElement((byte) 4);
        assertElementsEquals(expected, a);
    }

    @Test
    void indexingFromLowRetrievesZero() {
        Polynomial p1 = getPoly(new int[]{1,2,3,4,5});
        FiniteFieldElement a = p1.getIndexedFromLow(0);

        FiniteFieldElement expected = new FiniteFieldElement((byte) 5);
        assertElementsEquals(expected, a);
    }

    @Test
    void indexingFromLowRetrievesMax() {
        Polynomial p1 = getPoly(new int[]{1,2,3,4,5});
        FiniteFieldElement a = p1.getIndexedFromLow(4);

        FiniteFieldElement expected = new FiniteFieldElement((byte) 1);
        assertElementsEquals(expected, a);
    }

    @Test
    void settingFromLowSetsCorrectly() {
        Polynomial p1 = getPoly(new int[]{1,2,3,4,5});
        p1.setIndexedFromLow(1, new FiniteFieldElement((byte) 1));

        Polynomial expected = getPoly(new int[]{1,2,3,1,5});;
        assertPolyEquals(expected, p1);
    }

    protected FiniteFieldElement[] getElems(int[] elems) {
        return Arrays.stream(elems)
                .mapToObj(x -> new FiniteFieldElement((byte) x))
                .toArray(FiniteFieldElement[]::new);
    }

    protected Polynomial getPoly(int[] coefs) {
        FiniteFieldElement[] newCoefs = getElems(coefs);
        return new Polynomial(newCoefs);
    }
}
