package uk.ac.jl2119.partII;

import uk.ac.jl2119.partII.WavManipulation.AbstractWriter;
import uk.ac.thirdParty.WavFile.WavFileException;

import java.io.IOException;

/**
 * Sometimes, we need to pre-code our digital data
 * before sending it.
 * PreCodedEncoder applies digital->digital transformation
 * before applying digital->analogue transformation
 */
public abstract class PreCodedEncoder extends Encoder{
    DigitalToDigitalTransformer digitalToDigitalTransformer;

    protected PreCodedEncoder(AbstractWriter writer,
                              DigitalToAnalogueTransformer digitalToAnalogueTransformer,
                              DigitalToDigitalTransformer digitalToDigitalTransformer) {
        super(writer, digitalToAnalogueTransformer);
        this.digitalToDigitalTransformer = digitalToDigitalTransformer;
    }


    @Override
    public void generateSignal(byte[] input) throws IOException, WavFileException {
        byte[] preCodedBytes = digitalToDigitalTransformer.transform(input);
        super.generateSignal(preCodedBytes);
    }
}
