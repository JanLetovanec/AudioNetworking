package uk.ac.cam.jl2119.partII.Evaluation;

import uk.ac.cam.jl2119.partII.CodingSchemes.PSK.PSKModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.QAM.QAMModulator;
import uk.ac.cam.jl2119.partII.CodingSchemes.UEF.UEFModulator;
import uk.ac.cam.jl2119.partII.Enrichments.Packets.AdvancedPacketMod;
import uk.ac.cam.jl2119.partII.Enrichments.Packets.PacketModulator;
import uk.ac.cam.jl2119.partII.Framework.ITransformer;
import uk.ac.cam.jl2119.partII.WavManipulation.WavWriter;
import uk.ac.cam.jl2119.partII.utils.Boxer;
import uk.ac.cam.jl2119.thirdParty.WavFile.WavFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;
import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_SAMPLE_RATE;

public class RealHWOut {
    public static final int CYCLES_DEFAULT = 10;
    public static final int PAYLOAD_LENGTH = 128;

    private static final String FILENAME = "./output/Eval/HW.wav";

    private static final String MED_STRING = getStringFromFile("./output/Eval/shortText.txt");
    //private static final String LONG_STRING = getStringFromFile("./output/Eval/sampleText.txt");

    private static String getStringFromFile(String filename) {
        try {
            return new Scanner(new File(filename)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws IOException, WavFileException {

        Double[] apskMed = getSignal(MED_STRING, getAdvancedPSK());
        //Double[] pskMed = getSignal(MED_STRING, getPSK());
        //Double[] badMed = getSignal(MED_STRING, getPurePSK());
        //Double[] apskLong = getSignal(LONG_STRING, getAdvancedPSK());

        Double[][] allData = new Double[][] {apskMed};
        writeAll(FILENAME, allData);
    }

    private static ITransformer<Byte, Double> getPSK() {
        PSKModulator psk1 = new PSKModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        PSKModulator psk2 = new PSKModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        return new PacketModulator(psk1, psk2, PAYLOAD_LENGTH);
    }

    private static ITransformer<Byte, Double> getPurePSK() {
        PSKModulator psk1 = new PSKModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        return psk1;
    }

    private static ITransformer<Byte, Double> getAdvancedPSK() {
        PSKModulator psk1 = new PSKModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        PSKModulator psk2 = new PSKModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        return new AdvancedPacketMod(psk1, psk2, PAYLOAD_LENGTH);
    }

    private static ITransformer<Byte, Double> getQAM() {
        QAMModulator qam1 = new QAMModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        QAMModulator qam2 = new QAMModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
        return new PacketModulator(qam1, qam2);
    }

    private static ITransformer<Byte, Double> getUEF() {
        return new UEFModulator(DEFAULT_BASE_FREQUENCY, CYCLES_DEFAULT, DEFAULT_SAMPLE_RATE);
    }

    private static Double[] getSignal(String input, ITransformer<Byte, Double> transformer) {
        Byte[] data = Boxer.box(input.getBytes());
        return transformer.transform(data);
    }

    private static void writeAll(String fileName, Double[][] data) throws IOException, WavFileException {
        int length = Arrays.stream(data)
                .reduce(0,
                        (sub, signal)-> Math.toIntExact(signal.length + 1 * DEFAULT_SAMPLE_RATE + sub),
                        Integer::sum);

        WavWriter writer = WavWriter.getWriter(fileName, length, DEFAULT_SAMPLE_RATE);
        for(Double[] signal : data) {
            writeTransmission(signal, writer);
        }
    }

    private static void writeTransmission(Double[] signal, WavWriter writer) {
        try {
            writer.writeFrames(Boxer.unBox(signal), signal.length);
            writer.writeNothing((int) (2*DEFAULT_SAMPLE_RATE));
        } catch (IOException | WavFileException e) {
            e.printStackTrace();
        }
    }



}
