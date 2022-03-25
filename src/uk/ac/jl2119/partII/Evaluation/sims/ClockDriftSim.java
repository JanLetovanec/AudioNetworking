package uk.ac.jl2119.partII.Evaluation.sims;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.Noises.AWGNTransformer;
import uk.ac.jl2119.partII.Noises.ClockDriftTransformer;
import uk.ac.jl2119.partII.PSK.PSKModulator;
import uk.ac.jl2119.partII.UEF.FSKModulator;
import uk.ac.jl2119.partII.UEF.UEFModulator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClockDriftSim implements ISimulatorGenerator<Double> {
    private final double min;
    private final double max;
    private final int samples;

    private final double stdDev;

    private final ITransformer<Byte, Double> modulator;
    private final ITransformer<Double, Byte> demodulator;

    public ClockDriftSim(SchemeModulatorMap.CodingScheme scheme,
                         double min, double max, int samples,
                         double stdDev){
        this.samples = samples;
        this.min = min;
        this.max = max;
        this.stdDev = stdDev;

        SchemeModulatorMap.SchemePair pair = getSourceScheme(scheme);
        this.modulator = pair.modem;
        this.demodulator = pair.demodem;
    }

    private static SchemeModulatorMap.SchemePair getSourceScheme(SchemeModulatorMap.CodingScheme scheme) {
        long sampleRate = SchemeModulatorMap.DEFAULT_SAMPLE_RATE * 100;
        ITransformer<Double, Byte> demodem = SchemeModulatorMap.getDefaultScheme(scheme).demodem;
        ITransformer<Byte, Double> modem;
        switch (scheme) {
            case PSK:
                modem = new PSKModulator(sampleRate);
                break;
            case FSK:
                long samplesPerCycle = Math.round(Math.floor(sampleRate / SchemeModulatorMap.DEFAULT_BASE_FREQUENCY));
                modem = new FSKModulator(SchemeModulatorMap.DEFAULT_BASE_FREQUENCY, (int)samplesPerCycle, sampleRate);
                break;
            case UEF:
            case SYNC_UEF:
                modem = new UEFModulator(true, sampleRate);
                break;
            default:
                return null;
        }
        return new SchemeModulatorMap.SchemePair(modem, demodem);
    }

    private Double[] getParams() {
        Double[] values = new Double[samples];
        for (int i = 0; i < samples; i++) {
            double value = ((max - min) * i) / samples;
            values[i] = value;
        }
        return values;
    }

    @Override
    public Map<Double, Simulator<Double>> getSimulators() {
        Map<Double, Simulator<Double>> result = new HashMap<>();
        Double[] parameters = getParams();
        Arrays.stream(parameters)
                .forEach(param -> result.put(param, new ClockSim(param)));
        return result;
    }

    private class ClockSim extends Simulator<Double> {
        private ITransformer<Double, Double> noise;

        protected ClockSim(Double factor) {
            super(factor);
            noise = new ComposedTransformer<Double, Double>(
                    new AWGNTransformer(stdDev),
                    new ClockDriftTransformer(factor)
            );
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
