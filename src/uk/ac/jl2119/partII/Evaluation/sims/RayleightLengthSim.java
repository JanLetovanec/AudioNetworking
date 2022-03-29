package uk.ac.jl2119.partII.Evaluation.sims;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.jl2119.partII.Noises.RayleighFadingTransformer;

import java.util.HashMap;
import java.util.Map;

public class RayleightLengthSim implements ISimulatorGenerator<Integer>{
    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;
    private final int stepSize;
    private final int min;
    private final int max;

    public RayleightLengthSim(ITransformer<Byte, Double> modulator,
                               ITransformer<Double, Byte> demodulator,
                               int stepSize, int min, int max) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(0.5));
        this.demodulator = demodulator;
        this.stepSize = stepSize;
        this.min = min;
        this.max = max;
    }

    @Override
    public Map<Integer, Simulator<Integer>> getSimulators() {
        Map<Integer, Simulator<Integer>> result = new HashMap<>();
        for(int i = min; i < max; i += stepSize) {
            result.put(i, new RayleighSim(i));
        }
        return result;
    }

    private class RayleighSim extends Simulator<Integer> {
        private ITransformer<Double, Double> noise;

        protected RayleighSim(Integer length) {
            super(length);
            noise = new RayleighFadingTransformer(length, 0.5);
        }

        @Override
        public Byte[] getReceivedData(Byte[] input) {
            return (new ComposedTransformer<>(modulator, noise, demodulator)).transform(input);
        }

        @Override
        public Double[] getTransmittedSignal(Byte[] input) {
            return modulator.transform(input);
        }

        @Override
        public Double[] getReceivedSignal(Byte[] input) {
            return (new ComposedTransformer<>(modulator, noise)).transform(input);
        }
    }
}
