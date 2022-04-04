package uk.ac.cam.jl2119.partII.Evaluation;

import uk.ac.cam.jl2119.partII.Evaluation.datas.IDataGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.metrics.IMetricCalculator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.ISimulatorGenerator;
import uk.ac.cam.jl2119.partII.Evaluation.sims.Simulator;

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
        Map<P, Simulator<P>> sims = simGen.getSimulators();
        Map<P, List<M>> result = new HashMap<>();

        sims.keySet()
                .forEach(param -> result.put(param, evaluateSingle(sims.get(param))));
        return result;
    }

    private List<M> evaluateSingle(Simulator<P> sim){
        Byte[][] sourceData = getDataArray();
        return Arrays.stream(sourceData)
                .map(data -> metricCalc.getMetric(data, sim))
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

    /***
     * @return A JSON string from the map results
     */
    public String stringFromMap(String analysisName, Map<P, List<M>> evaluationResult) {
        String header = "\"" + analysisName + "\" : {\n";
        String footer = "}";
        List<String> entries = evaluationResult.keySet().stream()
                .map(p -> entryToString(p, evaluationResult.get(p)))
                .toList();
        return header + String.join(",\n", entries) + footer;
    }

    private String entryToString(P param, List<M> metrics) {
        List<String> metricStrings = metrics.stream().map(Object::toString).toList();
        String metricArray = "[" + String.join(",", metricStrings) + "]";
        return "\"" + param.toString() + "\" :" + metricArray;
    }
}
