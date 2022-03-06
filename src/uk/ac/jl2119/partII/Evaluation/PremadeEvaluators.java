package uk.ac.jl2119.partII.Evaluation;

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
}
