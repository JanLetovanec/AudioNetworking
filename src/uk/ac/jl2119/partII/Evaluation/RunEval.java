package uk.ac.jl2119.partII.Evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RunEval {
    public static void main(String[] args) throws IOException{
        PremadeEvaluators.numberOfSamples = 10;

        FileWriter myWriter = new FileWriter("./output/Eval/eval0.json");
        myWriter.write("{\n");

        // Basic eval
        evaluatePowerVsError(myWriter);
        evaluatePowerVsUsefulRate(myWriter);
        evaluateBurstMeanTimeVsError(myWriter);

        //RS eval
        evaluateCorrectionVsError(myWriter);
        evaluatePowerVsErrorRS(myWriter);
        evaluatePowerVsUsefulRS(myWriter);
        evaluateBurstMeanTimeVsErrorRS(myWriter);

        myWriter.write("\"number_of_samples\" :" + PremadeEvaluators.numberOfSamples);
        myWriter.write("\n}");
        myWriter.close();
    }

    private static void evaluatePowerVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating POWER vs ERROR RATE");
        for(SchemeModulatorMap.CodingScheme scheme : SchemeModulatorMap.CodingScheme.values()) {
            String evalName = scheme.toString() + "powerVsError";
            Evaluator eval = PremadeEvaluators.defaultPowerVsErrorRate(scheme);
            String json = eval.stringFromMap(evalName, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(scheme.toString() + " Done!");
        }
    }

    private static void evaluatePowerVsUsefulRate(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating POWER vs USEFUL RATE");
        for(SchemeModulatorMap.CodingScheme scheme : SchemeModulatorMap.CodingScheme.values()) {
            String evalName = scheme.toString() + "powerVsUseful";
            Evaluator eval = PremadeEvaluators.defaultPowerVsUsefulRate(scheme);
            String json = eval.stringFromMap(evalName, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(scheme.toString() + " Done!");
        }
    }

    private static void evaluateCorrectionVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating CORRECTION RATE vs ERROR RATE");
        List<Double> noiseLevels = List.of(0.2,0.5,1.0,3.0);
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            for(double noiseLvl : noiseLevels) {
                String name = scheme.toString() + "|" + noiseLvl + "|CorrectionRSVsError";
                Evaluator eval = PremadeEvaluators.RSCorrectionRateVsErrorRate(scheme, noiseLvl);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluatePowerVsErrorRS(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating POWER RATE vs ERROR RATE for RS enabled");
        List<Integer> correctionSymbols = List.of(5,32,64,100);
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            for(int correctionSymbolCount : correctionSymbols) {
                String name = scheme.toString() + "|" + correctionSymbolCount + "|PowerRSVsError";

                Evaluator eval = PremadeEvaluators.RSPowerRVsErrorRate(scheme, correctionSymbolCount);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluatePowerVsUsefulRS(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating POWER RATE vs USEFUL RATE for RS enabled");
        List<Integer> correctionSymbols = List.of(5,32,64,100);
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            for(int correctionSymbolCount : correctionSymbols) {
                String name = scheme.toString() + "|" + correctionSymbolCount + "|PowerRSVsUseful";

                Evaluator eval = PremadeEvaluators.RSPowerVsUsefulRate(scheme, correctionSymbolCount);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluateBurstMeanTimeVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating BURST RATE vs ERROR RATE");
        List<Integer> burstLengths = List.of(50,100,300,600);
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            for(int burstLength : burstLengths) {
                String name = scheme.toString() + "|" + burstLength + "|BurstVsError";

                Evaluator eval = PremadeEvaluators.burstMeanTimeVsErrorRate(scheme, 1, burstLength);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluateBurstMeanTimeVsErrorRS(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating BURST RATE vs ERROR RATE");
        List<Integer> correctionCounts = List.of(5,32,64,100);
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            for(int correctionCount : correctionCounts) {
                String name = scheme.toString() + "|" + correctionCount + "|BurstRSVsError";

                Evaluator eval = PremadeEvaluators.RSburstMeanTimeVsErrorRate(scheme,
                        1, 200,
                        correctionCount);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluateClockDriftVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating BURST RATE vs ERROR RATE");
        for (SchemeModulatorMap.CodingScheme scheme :SchemeModulatorMap.CodingScheme.values()) {
            String name = scheme.toString() + "|ClockDriftVsError";

            Evaluator eval = PremadeEvaluators.clockDriftVsErrorRate(scheme);
            String json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");
        }
    }

}
