package uk.ac.cam.jl2119.partII.Evaluation.datas;

/**
 * IDataGenerator
 *      simply returns all the data we wish to evaluate on.
 *      This will be used as input to the simulators
 */
public interface IDataGenerator {
    /**
     * Gets the testing data
     * @return Gets all the data we wish to test against
     */
    Byte[][] getData();
}
