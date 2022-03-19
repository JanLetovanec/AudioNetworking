package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.Evaluation.datas.IDataGenerator;
import uk.ac.jl2119.partII.Evaluation.datas.RandomDataGen;
import uk.ac.jl2119.partII.Evaluation.metrics.ErrorRateCalc;
import uk.ac.jl2119.partII.Evaluation.metrics.IMetricCalculator;
import uk.ac.jl2119.partII.Evaluation.metrics.UsefulRateCalc;
import uk.ac.jl2119.partII.Evaluation.sims.AWGN_PowerSimulator;
import uk.ac.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.jl2119.partII.Evaluation.sims.RS_CorrectionRateSimulator;

/**
 * Evaluators ready to be fired
 */
public class PremadeEvaluators {
    public static Evaluator<Double, Double> defaultPowerVsErrorRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 200, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(1000, 1);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> defaultPowerVsUsefulRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Double> simGen =
                new AWGN_PowerSimulator(pair.modem, pair.demodem, 200, 0, 5);
        IDataGenerator dataGen = new RandomDataGen(1000, 1);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }

    public static Evaluator<Double, Double> RSCorrectionRateVsErrorRate(SchemeModulatorMap.CodingScheme scheme) {
        SchemeModulatorMap.SchemePair pair = SchemeModulatorMap.getDefaultScheme(scheme);
        ISimulatorGenerator<Integer> simGen =
                new RS_CorrectionRateSimulator(pair.modem, pair.demodem, 0.7, 5, 200);
        IDataGenerator dataGen = new RandomDataGen(1000, 1);
        IMetricCalculator metricCalc = new UsefulRateCalc(SchemeModulatorMap.DEFAULT_SAMPLE_RATE);
        return new Evaluator<>(simGen, dataGen, metricCalc);
    }
}
