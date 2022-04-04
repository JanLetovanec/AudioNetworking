package uk.ac.cam.jl2119.partII;

import java.util.function.Function;

/**
 * Transformer that composes 2 transformation
 * @param <S> Source type of the resulting transformation
 * @param <T> Result type of the resulting transformation
 */
public class ComposedTransformer<S,T> implements ITransformer<S,T> {

    /**
     * Generics T1 are not available outside the constructor,
     * so generate the implementation transform function inside the constructor
     * and set this function to that implementation
     */
    Function<S[], T[]> composedTransform;

    /**
     * Consructs the composition of transformers
     * @param first transformer of type S[]->T1[]
     * @param second transformer of type T1[] -> T[]
     * @param <T1> intermediate result
     */
    public <T1> ComposedTransformer(ITransformer<S,T1> first, ITransformer<T1, T> second) {
        composedTransform = (S[] input) -> {
            T1[] intermediateResult = first.transform(input);
            return second.transform(intermediateResult);
        };
    }

    public <T1,T2> ComposedTransformer(ITransformer<S,T1> first, ITransformer<T1, T2> second, ITransformer<T2, T> third) {
        composedTransform = (S[] input) -> {
            return third.transform(
            second.transform(
            first.transform(input)));
        };
    }

    @Override
    public T[] transform(S[] input) {
        return composedTransform.apply(input);
    }
}
