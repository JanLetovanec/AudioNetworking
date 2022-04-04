package uk.ac.cam.jl2119.partII;

import uk.ac.cam.jl2119.partII.Evaluation.Evaluator;
import uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap;
import uk.ac.cam.jl2119.partII.Evaluation.datas.IDataGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.datas.RandomDataGen;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.ErrorRateCalc;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.IMetricCalculator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Noise.ClockDriftSim;
import uk.ac.cam.jl2119.partII.Evaluation.sims.ISimulatorGenerator;

import java.util.List;
import java.util.Map;

public class Main {
    static final int SAMPLE_RATE = 44100;
    static final int LENGTH = SAMPLE_RATE * 5;

    public static void main(String[] args) {
        SchemeModulatorMap.CodingScheme scheme = SchemeModulatorMap.CodingScheme.QAM;
        ISimulatorGenerator<Double> simGen = new ClockDriftSim(scheme, 1, 1, 1, 0);
        IDataGenerator dataGen = new RandomDataGen(1, 1);
        IMetricCalculator metricCalc = new ErrorRateCalc();
        Evaluator<Double,Double> e = new Evaluator(simGen, dataGen, metricCalc);
        Map<Double, List<Double>> a = e.evaluate();
        for (double k : a.keySet()) {
            System.out.println(k + ": " + a.get(k).get(0));
        }
    }
}
