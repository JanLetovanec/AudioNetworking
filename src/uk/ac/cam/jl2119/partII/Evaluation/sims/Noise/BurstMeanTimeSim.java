package uk.ac.cam.jl2119.partII.Evaluation.sims.Noise;

import uk.ac.cam.jl2119.partII.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;
import uk.ac.cam.jl2119.partII.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AttenuatorTransformer;
import uk.ac.cam.jl2119.partII.Noises.BurstAWGNTransformer;

import java.util.HashMap;
import java.util.Map;

public class BurstMeanTimeSim implements ISimulatorGenerator<Integer> {
    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;
    private final int burstLength;
    private final double stdDev;
    private final int step;

    private final int min;
    private final int max;

    public BurstMeanTimeSim(ITransformer<Byte, Double> modulator,
                            ITransformer<Double, Byte> demodulator,
                            int burstLength, double stdDev,
                            int min, int max, int step, double attenuationFactor) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(attenuationFactor));
        this.demodulator = demodulator;
        this.burstLength = burstLength;
        this.stdDev = stdDev;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public BurstMeanTimeSim(ITransformer<Byte, Double> modulator,
                            ITransformer<Double, Byte> demodulator,
                            int burstLength, double stdDev,
                            int min, int max, int step) {
        this.modulator = new ComposedTransformer<>(modulator, new AttenuatorTransformer(0.5));
        this.demodulator = demodulator;
        this.burstLength = burstLength;
        this.stdDev = stdDev;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public Map<Integer, Simulator<Integer>> getSimulators() {
        Map<Integer, Simulator<Integer>> result = new HashMap<>();
        for (int i = min; i < max; i += step) {
            result.put(i, new BurstSim(i));
        }
        return result;
    }

    private class BurstSim extends Simulator<Integer> {
        private ITransformer<Double, Double> noise;

        protected BurstSim(Integer meanTime) {
            super(meanTime);
            noise = new BurstAWGNTransformer(meanTime, burstLength, stdDev);
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
