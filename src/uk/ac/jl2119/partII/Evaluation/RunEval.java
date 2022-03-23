package uk.ac.jl2119.partII.Evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RunEval {
    public static void main(String[] args) throws IOException{
        PremadeEvaluators.numberOfSamples = 10;

        FileWriter myWriter = new FileWriter("./output/Eval/eval1.json");
        myWriter.write("{\n");

        //evaluatePowerVsError(myWriter);
        //evaluatePowerVsUsefulRate(myWriter);

        //evaluateCorrectionVsError(myWriter);
        //evaluatePowerVsErrorRS(myWriter);
        evaluatePowerVsUsefulRS(myWriter);

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

                Evaluator eval = PremadeEvaluators.RSCorrectionRateVsErrorRate(scheme, correctionSymbolCount);
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

                Evaluator eval = PremadeEvaluators.RSCorrectionRateVsUsefulRate(scheme, correctionSymbolCount);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }
}
