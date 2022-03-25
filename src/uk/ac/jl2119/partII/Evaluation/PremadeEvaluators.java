package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ComposedTransformer;
import uk.ac.jl2119.partII.Evaluation.datas.IDataGenerator;
import uk.ac.jl2119.partII.Evaluation.datas.RandomDataGen;
import uk.ac.jl2119.partII.Evaluation.metrics.ErrorRateCalc;
import uk.ac.jl2119.partII.Evaluation.metrics.IMetricCalculator;
import uk.ac.jl2119.partII.Evaluation.metrics.UsefulRateCalc;
import uk.ac.jl2119.partII.Evaluation.sims.*;
import uk.ac.jl2119.partII.ITransformer;
import uk.ac.jl2119.partII.ReedSolomon.RSDecoder;
import uk.ac.jl2119.partII.ReedSolomon.RSEncoder;

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
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 100, min, max);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> defaultPowerVsUsefulRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 100, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
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
                new BurstMeanTimeSim(pair.modem, pair.demodem, burstLength, noiseLevel, 10, 2000, 10);
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
        ISimulatorGenerator<Double> simGen = new ClockDriftSim(scheme, 0.5, 1.5, 200, noiseLvl);
        IDataGenerator dataGen = new RandomDataGen(lengthOfSingle, numberOfSamples);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }
}
