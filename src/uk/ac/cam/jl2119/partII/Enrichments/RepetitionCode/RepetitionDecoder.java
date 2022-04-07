package uk.ac.cam.jl2119.partII.Enrichments.RepetitionCode;

import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.utils.StreamUtils;

import java.util.List;

public class RepetitionDecoder implements ITransformer<Byte, Byte> {
    private final int repetitions;
    private final int threshold;

    public RepetitionDecoder(int numOfRepetitions) {
        repetitions = numOfRepetitions;
        threshold = repetitions / 2;
    }

    @Override
    public Byte[] transform(Byte[] input) {
        return StreamUtils.partitionData(input, repetitions).stream()
                .map(this::takeMajorityVote)
                .toArray(Byte[]::new);
    }

    private Byte takeMajorityVote(List<Byte> code) {
        byte result = 0;
        for(int i = 0; i < 8; i++) {
            int bitSelector = 1 << i;
            int voteResult = judgeVotes(code, bitSelector);
            result += bitSelector & voteResult;
        }
        return result;
    }

    /**
     * @param code - codeword to judge
     * @param bitSelector - 1 in the place of the judged bit
     * @return - 0xFF if 1, 0x00 if 0
     */
    private int judgeVotes(List<Byte> code, int bitSelector) {
        int voteCount = 0;
        for(Byte codeByte : code) {
            if ((codeByte & bitSelector) > 0) {
               voteCount++;
            }
        }
        boolean isOne = voteCount > threshold;
        return isOne ? 0xFF : 0x00;
    }
}
