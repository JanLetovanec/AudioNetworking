package uk.ac.jl2119.partII.Evaluation;

import uk.ac.jl2119.partII.ITransformer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Evaluator<P,M> {
    protected ISimulatorGenerator<P> simGen;
    protected IDataGenerator dataGen;
    protected IMetricCalculator<M,P> metricCalc;

    protected Byte[][] cachedData;

    public Evaluator(ISimulatorGenerator<P> simGen, IDataGenerator dataGen, IMetricCalculator<M, P> metricCalc) {
        this.simGen = simGen;
        this.dataGen = dataGen;
        this.metricCalc = metricCalc;
    }


    /**
     * Runs the evaluation.
     * All runs between different
     * @return returns a parameter-metrics map, where:
     *      parameter
     *      metrics - is a list of metric associated to each parameter-data pair
     */
    public Map<P, List<M>> evaluate() {
        Map<P, ITransformer<Byte, Byte>> sims = simGen.getSimulators();
        Map<P, List<M>> result = new HashMap<>();

        sims.keySet()
                .forEach(param -> result.put(param, evaluateSingle(param, sims.get(param))));
        return result;
    }

    private List<M> evaluateSingle(P parameter, ITransformer<Byte, Byte> sim){
        Byte[][] sourceData = getDataArray();
        return Arrays.stream(sourceData)
                .map(data -> metricCalc.getMetric(data, sim.transform(data), parameter))
                .collect(Collectors.toList());
    }

    /**
     * Used to avoid calling `dataGen` more than once.
     * That way, if the data is random,
     * it will be the same for all the runs for a given evaluator.
     *
     * @return -- the data returned by `dataGen` for the first-call
     */
    public Byte[][] getDataArray() {
        if (cachedData == null) {
            cachedData = dataGen.getData();
        }
        return cachedData;
    }
}
