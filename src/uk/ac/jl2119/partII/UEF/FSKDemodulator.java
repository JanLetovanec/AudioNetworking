package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.FixedBatchDemodulator;

/**
 * ADC Transformer that demodulates FSK signal
 */
public class FSKDemodulator extends FixedBatchDemodulator {
    // Detecting zero crossing @ 0 causes problems
    // cuz of noise and timings are fuzzy (+noise),
    // so detect them slightly higher
    private final double NEAR_ZERO_CROSSING_THRESHOLD = 0.1;
    private final int crossingsDecisionThreshold;

    public FSKDemodulator(double baseFrequency, double symbolDurationInSeconds, long sampleRate) {
        super(symbolDurationInSeconds, sampleRate);
        int crossingsPerZero = getCrossingsPerZero(baseFrequency, symbolDurationInSeconds);
        int crossingsPerOne = 2 * crossingsPerZero;
        crossingsDecisionThreshold = (crossingsPerZero + crossingsPerOne) / 2;
    }

    @Override
    protected Boolean[] transformBits(Double[] batch) {
        int actualCrossings = countNearZeroCrossings(batch);
        boolean resultBit = actualCrossings > crossingsDecisionThreshold;
        return new Boolean[]{resultBit};
    }

    private int countNearZeroCrossings(Double[] batch) {
        int numberOfCrossings = 0;
        boolean isBelowThreshold = true;

        for (Double sample : batch) {
            if (hasCrossedThreshold(isBelowThreshold, sample)) {
                isBelowThreshold = !isBelowThreshold;
                numberOfCrossings++;
            }
        }

        return numberOfCrossings;
    }

    private boolean hasCrossedThreshold(boolean isBelowThreshold, Double sample) {
        return (isBelowThreshold && sample > NEAR_ZERO_CROSSING_THRESHOLD)
                || (!isBelowThreshold && sample < NEAR_ZERO_CROSSING_THRESHOLD);
    }

    private static int getCrossingsPerZero(double baseFrequency, double symbolDurationInSeconds) {
        int numberOfCycles = (int) Math.floor(baseFrequency * symbolDurationInSeconds);
        return  2 * numberOfCycles;
    }
}
