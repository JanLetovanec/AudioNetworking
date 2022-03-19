package uk.ac.jl2119.partII.Evaluation;

public abstract class Simulator<P> {
    protected P param;

    public P getParam() {return param;}

    protected Simulator(P param) {
        this.param = param;
    }

    public abstract Byte[] getReceivedData(Byte[] input);
    public abstract Double[] getTransmittedSignal(Byte[] input);
    public abstract Double[] getReceivedSignal(Byte[] input);
}
