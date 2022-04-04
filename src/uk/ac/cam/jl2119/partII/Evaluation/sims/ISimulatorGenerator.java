package uk.ac.cam.jl2119.partII.Evaluation.sims;

import java.util.Map;

/***
 * ISimulatorGenerator
 *      Returns a  key-value map of all parameters we wish to evaluate on:
 *          key -- a value of the parameter (e.g. Frequency)
 *          value -- a simulator to be used (e.g. FSK /w base frequency of 'key')
 *      This represents our 'independent variable'. This is the thing we change, trying to keep the rest constant.
 * @param <K> -- type of the parameter
 */
public interface ISimulatorGenerator<K> {
    Map<K, Simulator<K>> getSimulators();
}
