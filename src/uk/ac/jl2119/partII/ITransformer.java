package uk.ac.jl2119.partII;

public interface ITransformer<S, T> {
    T[] transform(S[] input);
}
