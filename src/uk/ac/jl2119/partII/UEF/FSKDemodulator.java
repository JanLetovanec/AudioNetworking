package uk.ac.jl2119.partII.UEF;

import uk.ac.jl2119.partII.FixedBatchDemodulator;

/**
 * ADC Transformer that demodulates FSK signal
 */
public class FSKDemodulator extends FixedBatchDemodulator {
    // Detecting zero crossing @ 0 causes problems
    // cuz of noise and other imperfections,
    // so detect them slightly higher
    private final double NEAR_ZERO_CROSSING_THRESHOLD = 0.1;
    private final int crossingsDecisionThreshold;

    protected FSKDemodulator(double baseFrequency, int symbolDurationInFrames, long sampleRate) {
        super(symbolDurationInFrames);
        int crossingsPerZero = getCrossingsPerZero(baseFrequency, symbolDurationInFrames, sampleRate);
        int crossingsPerOne = 2 * crossingsPerZero;
        crossingsDecisionThreshold = (crossingsPerZero + crossingsPerOne) / 2;
    }

    @Override
    protected Boolean transformBit(Double[] batch) {
        int actualCrossings = countNearZeroCrossings(batch);
        return actualCrossings > crossingsDecisionThreshold;
    }

    private int countNearZeroCrossings(Double[] batch) {
        int numberOfCrossings = 0;
        boolean isBelowThreshold = true;
        for (Double sample : batch) {
            if (isBelowThreshold && sample > NEAR_ZERO_CROSSING_THRESHOLD) {
                isBelowThreshold = false;
                numberOfCrossings++;
            }
            else if (!isBelowThreshold && sample < NEAR_ZERO_CROSSING_THRESHOLD) {
                isBelowThreshold = true;
                numberOfCrossings++;
            }
        }

        return numberOfCrossings;
    }

    private static int getCrossingsPerZero(double baseFrequency, int symbolDurationInFrames, long sampleRate) {
        int samplesPerCycle = (int) Math.round(sampleRate / baseFrequency);
        int numberOfCycles = symbolDurationInFrames / samplesPerCycle;
        int numberOfZeroCrossings =  2 * numberOfCycles;
        return numberOfZeroCrossings;
    }
}
