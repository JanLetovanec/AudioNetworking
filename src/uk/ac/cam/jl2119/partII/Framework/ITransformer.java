package uk.ac.cam.jl2119.partII.Framework;

public interface ITransformer<S, T> {
    T[] transform(S[] input);
}
