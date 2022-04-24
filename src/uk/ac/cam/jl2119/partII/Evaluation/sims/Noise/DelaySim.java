package uk.ac.cam.jl2119.partII.Evaluation.sims.Noise;

import uk.ac.cam.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Noises.AWGNTransformer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DelaySim implements ISimulatorGenerator<Double> {
    private final double min;
    private final double max;
    private final int samples;

    private final long sampleRate;
    private final double noiseLvl;

    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;


    public DelaySim(ITransformer<Byte, Double> modem, ITransformer<Double, Byte> demodem,
                    double min, double max, int samples,
                    long sampleRate, double noiseLvl){
        this.samples = samples;
        this.min = min;
        this.max = max;
        this.sampleRate = sampleRate;
        this.noiseLvl = noiseLvl;

        this.modulator = modem;
        this.demodulator = demodem;
    }


    private Double[] getParams() {
        Double[] values = new Double[samples];
        for (int i = 0; i < samples; i++) {
            double value = min + ((max - min) * i) / samples;
            values[i] = value;
        }
        return values;
    }

    @Override
    public Map<Double, Simulator<Double>> getSimulators() {
        Map<Double, Simulator<Double>> result = new HashMap<>();
        Double[] parameters = getParams();
        Arrays.stream(parameters)
                .forEach(param -> result.put(param, new DelSim(param)));
        return result;
    }

    private class DelSim extends Simulator<Double> {
        private int delayInSamples;
        private ITransformer<Double, Double> noise;

        protected DelSim(Double delayInSeconds) {
            super(delayInSeconds);
            delayInSamples = (int) Math.floor(delayInSeconds * sampleRate);
            noise = new AWGNTransformer(noiseLvl);
        }

        @Override
        public Byte[] getReceivedData(Byte[] input) {
            return demodulator.transform(getReceivedSignal(input));
        }

        @Override
        public Double[] getTransmittedSignal(Byte[] input) {
            return modulator.transform(input);
        }

        @Override
        public Double[] getReceivedSignal(Byte[] input) {
            Stream<Double> zeros = Collections.nCopies(delayInSamples, 0.0).stream();
            Stream<Double> signal = Arrays.stream(getTransmittedSignal(input));
            Double[] delayedSignal = Stream.concat(zeros, signal).toArray(Double[]::new);
            return noise.transform(delayedSignal);
        }
    }
}
