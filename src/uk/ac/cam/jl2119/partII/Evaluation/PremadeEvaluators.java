package uk.ac.cam.jl2119.partII.Evaluation;

import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKModulator;
import uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode.RepetitionDecoder;
import uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode.RepetitionEncoder;
import uk.ac.cam.jl2119.partII.Framework.ComposedTransformer;
import uk.ac.cam.jl2119.partII.Evaluation.datas.IDataGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.datas.RandomDataGen;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.ErrorRateCalc;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.IMetricCalculator;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.UsefulRateCalc;
import uk.ac.cam.jl2119.partII.Evaluation.sims.*;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Noise.AWGN_PowerSimulator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Noise.BurstMeanTimeSim;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Noise.ClockDriftSim;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Noise.RayleightLengthSim;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Scheme.FSKSymbolTimeSim;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Scheme.PSKCyclesSimulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSDecoder;
import uk.ac.cam.jl2119.partII.Enrichments.ReedSolomon.RSEncoder;

import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.*;

/**
 * Evaluators ready to be fired
 */
public class PremadeEvaluators {
    public static int numberOfSamples = 1;
    public static int lengthOfSingle = 1000;

    public static Evaluator<Double, Double> defaultPowerVsErrorRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> defaultPowerVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                    double min, double max) {
        SchemeModulatorMap.SchemePair pair = getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 100, min, max);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Integer, Double> defaultRayleighVsErrorRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = getDefaultScheme(scheme);
        ISimulatorGenerator<Integer> simGen =
                new RayleightLengthSim(pair.modem, pair.demodem, 1, 1, 200);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> defaultPowerVsUsefulRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSCorrectionRateVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                        double noiseLevel) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Integer> simGen =
                new RS_CorrectionRateSimulator(pair.modem, pair.demodem, noiseLevel, 5, 150);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSPowerRVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                        int correctionSymbols) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(new RSEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(pair.demodem, new RSDecoder(correctionSymbols));
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(modem, demodem, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSPowerRVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                int correctionSymbols,
                                                                double min, double max) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(new RSEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(pair.demodem, new RSDecoder(correctionSymbols));
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(modem, demodem, 100, min, max);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSPowerVsUsefulRate(SchemeModulatorMap.CodingScheme scheme,
                                                                        int correctionSymbols) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(new RSEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(pair.demodem, new RSDecoder(correctionSymbols));
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(modem, demodem, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> burstMeanTimeVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                     double noiseLevel, int burstLength) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(pair.modem, pair.demodem, burstLength, noiseLevel, 10, 6000, 10);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> repetBurstMeanTimeVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                     double noiseLevel, int burstLength, int correctionSymbols) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(
                new RepetitionEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(
                pair.demodem, new RepetitionDecoder(correctionSymbols)
        );
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(modem, demodem, burstLength, noiseLevel, 50, 6000, 50);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> LongPSKBurstMeanTimeVsErrorRate(double noiseLevel, int burstLength, int cycleCount) {
        ITransformer<Byte, Double> modem = new PSKModulator(DEFAULT_BASE_FREQUENCY, cycleCount, DEFAULT_SAMPLE_RATE);
        ITransformer<Double, Byte> demodem = new PSKDemodulator(DEFAULT_BASE_FREQUENCY, cycleCount, DEFAULT_SAMPLE_RATE);
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(modem, demodem, burstLength, noiseLevel, 50, 6000, 50);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator RSburstMeanTimeVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                                       double noiseLevel, int burstLength,
                                                                       int correctionSymbols) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(
                new RSEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(
                pair.demodem, new RSDecoder(correctionSymbols)
        );
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(modem, demodem, burstLength, noiseLevel, 50, 6000, 50);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator RepetburstMeanTimeVsErrorRate(SchemeModulatorMap.CodingScheme scheme,
                                                       double noiseLevel, int burstLength,
                                                       int correctionCount) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(
                new RepetitionEncoder(correctionCount), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(
                pair.demodem, new RepetitionDecoder(correctionCount)
        );
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(modem, demodem, burstLength, noiseLevel, 10, 2000, 10);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSburstMeanTimeVsUsefulRate(SchemeModulatorMap.CodingScheme scheme,
                                                                       double noiseLevel, int burstLength,
                                                                       int correctionSymbols) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ITransformer<Byte, Double> modem = new ComposedTransformer<>(
                new RSEncoder(correctionSymbols), pair.modem);
        ITransformer<Double, Byte> demodem = new ComposedTransformer<>(
                pair.demodem, new RSDecoder(correctionSymbols)
        );
        ISimulatorGenerator<Integer> simGen =
                new BurstMeanTimeSim(modem, demodem, burstLength, noiseLevel, 10, 2000, 10);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator clockDriftVsErrorRate(SchemeModulatorMap.CodingScheme scheme, double noiseLvl) {
        ISimulatorGenerator<Double> simGen = new ClockDriftSim(scheme, 0.5, 1.5, 20, noiseLvl);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator PSKCyclesVsError(double noiseLevel, int rayleighLength) {
        ISimulatorGenerator<Integer> simGen = new PSKCyclesSimulator(noiseLevel, rayleighLength,
                1, 20, 1);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator PSKCyclesVsUseful(double noiseLevel, int rayleighLength) {
        ISimulatorGenerator<Integer> simGen = new PSKCyclesSimulator(noiseLevel, rayleighLength,
                1, 20, 1);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator FSKTimeVsError(double noiseLevel, int rayleighLength, double min, double max) {
        ISimulatorGenerator simGen = new FSKSymbolTimeSim(noiseLevel, rayleighLength,
                min, max, 100);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }
    public static Evaluator FSKTimeVsUseful(double noiseLevel, int rayleighLength, double min, double max) {
        ISimulatorGenerator simGen =  new FSKSymbolTimeSim(noiseLevel, rayleighLength,
                min, max, 100);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> tfVsErrorRate(ITransformer<Byte, Double> mod, ITransformer<Double, Byte> demod) {
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(mod, demod, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }
}
