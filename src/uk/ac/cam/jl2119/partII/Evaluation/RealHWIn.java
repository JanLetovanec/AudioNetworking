package uk.ac.cam.jl2119.partII.Evaluation;

import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.QAM.QAMDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.FSKDemodulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.UEFSyncDemodulator;
import uk.ac.cam.jl2119.partII.Enrichments.Packets.PacketDemodulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.WavManipulation.WavReader;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static uk.ac.cam.jl2119.partII.Evaluation.RealHWOut.CYCLES_DEFAULT;
import static uk.ac.cam.jl2119.partII.Evaluation.RealHWOut.PAYLOAD_LENGTH;
import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_SAMPLE_RATE;

public class RealHWIn {
    private static final String PREFIX = "./output/Eval/";

    public static void main(String[] args) throws IOException, WavFileException {
        printContentsPSK(PREFIX + "PSK_MED.wav");
        printContentsPurePSK(PREFIX + "PURE_MED.wav");
        //printContentsPSK(PREFIX + "PSK_MED_PERFECT.wav");
    }

    private static void printContentsPurePSK(String filename) throws IOException, WavFileException {
        PSKDemodulator psk1 = new PSKDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        printData(filename, psk1);
    }

    private static void printContentsPSK(String filename) throws IOException, WavFileException {
        PSKDemodulator psk1 = new PSKDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        PSKDemodulator psk2 = new PSKDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        double timePerBatch = (CYCLES_DEFAULT * 1.0) / DEFAULT_BASE_FREQUENCY;

        ITransformer<Double, Byte> transformer = new PacketDemodulator(psk1,psk2,
                SchemeModulatorMap.DEFAULT_SAMPLE_RATE, timePerBatch,
                timePerBatch * PAYLOAD_LENGTH * 8, 1);
        printData(filename, transformer);
    }

    private static void printContentsFSK(String filename) throws IOException, WavFileException {
        double timePerBatch = (CYCLES_DEFAULT * 1.0) / DEFAULT_BASE_FREQUENCY;
        FSKDemodulator fsk1 = new FSKDemodulator(DEFAULT_BASE_FREQUENCY,
                timePerBatch,
                DEFAULT_SAMPLE_RATE);
        FSKDemodulator fsk2 = new FSKDemodulator(DEFAULT_BASE_FREQUENCY,
                timePerBatch,
                DEFAULT_SAMPLE_RATE);


        ITransformer<Double, Byte> transformer = new PacketDemodulator(fsk1,fsk2,
                SchemeModulatorMap.DEFAULT_SAMPLE_RATE, timePerBatch, timePerBatch * 10 * 8, 1);
        printData(filename, transformer);
    }

    private static void printContentsQAM(String filename) throws IOException, WavFileException {
        QAMDemodulator qam1 = new QAMDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        QAMDemodulator qam2 = new QAMDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        double timePerBatch = (CYCLES_DEFAULT * 1.0) / DEFAULT_BASE_FREQUENCY;

        ITransformer<Double, Byte> transformer = new PacketDemodulator(qam1, qam2,
                SchemeModulatorMap.DEFAULT_SAMPLE_RATE, timePerBatch,
                timePerBatch * PAYLOAD_LENGTH * 4, 2);
        printData(filename, transformer);
    }

    private static void printContentsUEF(String filename) throws IOException, WavFileException {
        UEFSyncDemodulator uef = new UEFSyncDemodulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);

        printData(filename, uef);
    }

    private static void printData(String filename, ITransformer<Double, Byte> transformer) throws IOException, WavFileException {
        WavReader reader = WavReader.getReader(filename);
        System.out.println("SAMPLE RATE: " + reader.getSampleRate());

        double[] signal = reader.readFrames((int) reader.getRemainingSamples());
        Byte[] data = transformer.transform(Boxer.box(signal));
        String stringData = new String(Boxer.unBox(data), StandardCharsets.UTF_8);

        System.out.println(stringData);
        System.out.println("==========================");
    }
}
