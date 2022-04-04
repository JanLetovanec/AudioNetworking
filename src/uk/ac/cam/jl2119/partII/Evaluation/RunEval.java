package uk.ac.cam.jl2119.partII.Evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.CodingScheme;
import static uk.ac.cam.jl2119.partII.Evaluation.SchemeModulatorMap.DEFAULT_BASE_FREQUENCY;

public class RunEval {
    private static final CodingScheme[] fastSchemes = {
            CodingScheme.FSK,
            CodingScheme.PSK,
            CodingScheme.UEF,
            CodingScheme.QAM
    };

    public static void main(String[] args) throws IOException{
        PremadeEvaluators.numberOfSamples = 10;

        FileWriter myWriter = new FileWriter("./output/Eval/eval1.json");
        myWriter.write("{\n");

//        // Basic eval
//        evaluatePowerVsError(myWriter);
//        evaluatePowerVsUsefulRate(myWriter);
//        evaluateLengthVsError(myWriter);
//        evaluateBurstMeanTimeVsError(myWriter);
//        evaluateSmallPowerVsError(myWriter);
//        evaluateClockDriftVsError(myWriter);
//
//        //RS eval
//        evaluateCorrectionVsError(myWriter);
//        evaluatePowerVsErrorRS(myWriter);
//        evaluatePowerVsUsefulRS(myWriter);
//        evaluateBurstMeanTimeVsErrorRS(myWriter);
//        evaluateSmallPowerVsErrorRS(myWriter);
//
//        //Scheme specific
//        evaluatePSK(myWriter);
//        evaluateFSK(myWriter);


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
        for(SchemeModulatorMap.CodingScheme scheme : fastSchemes) {
            String evalName = scheme.toString() + "powerVsUseful";
            Evaluator eval = PremadeEvaluators.defaultPowerVsUsefulRate(scheme);
            String json = eval.stringFromMap(evalName, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(scheme.toString() + " Done!");
        }
    }

    private static void evaluateLengthVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating RAYLEIGH LENGTH vs ERROR RATE");
        for(SchemeModulatorMap.CodingScheme scheme : SchemeModulatorMap.CodingScheme.values()) {
            String evalName = scheme.toString() + "RayleighVsError";
            Evaluator eval = PremadeEvaluators.defaultRayleighVsErrorRate(scheme);
            String json = eval.stringFromMap(evalName, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(scheme.toString() + " Done!");
        }
    }

    private static void evaluateCorrectionVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating CORRECTION RATE vs ERROR RATE");
        List<Double> noiseLevels = List.of(0.2,0.5,1.0,3.0);
        for (SchemeModulatorMap.CodingScheme scheme :fastSchemes) {
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
        for (SchemeModulatorMap.CodingScheme scheme :fastSchemes) {
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
        for (SchemeModulatorMap.CodingScheme scheme :fastSchemes) {
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
        for (SchemeModulatorMap.CodingScheme scheme :fastSchemes) {
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
        for (SchemeModulatorMap.CodingScheme scheme :fastSchemes) {
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
        System.out.println("Evaluating DRIFT vs ERROR RATE");
        List<Double> noiseLevels = List.of(0.0,0.2,1.0);
        for (CodingScheme scheme :CodingScheme.values()) {
            for(double noiseLvl : noiseLevels) {
                String name = scheme.toString() + "|" + noiseLvl + "|ClockDriftVsError";

                Evaluator eval = PremadeEvaluators.clockDriftVsErrorRate(scheme, noiseLvl);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluateSmallPowerVsError(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating SMALL POWER vs ERROR RATE");
        List<CodingScheme> schemes = List.of(
                CodingScheme.FSK,
                CodingScheme.UEF
        );
        for (SchemeModulatorMap.CodingScheme scheme :schemes) {
            String name = scheme.toString() + "|PowerSmallVsError";

            Evaluator eval = PremadeEvaluators.defaultPowerVsErrorRate(scheme, 0, 0.5);
            String json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");
        }
    }

    private static void evaluateSmallPowerVsErrorRS(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating SMALL POWER vs ERROR RATE for RS enabled");
        List<Integer> correctionSymbols = List.of(5,32,64,100);
        List<CodingScheme> schemes = List.of(
                CodingScheme.FSK,
                CodingScheme.UEF
        );
        for (SchemeModulatorMap.CodingScheme scheme :schemes) {
            for(int correctionSymbolCount : correctionSymbols) {
                String name = scheme.toString() + "|" + correctionSymbolCount + "|PowerSmallRSVsError";

                Evaluator eval = PremadeEvaluators.RSPowerRVsErrorRate(scheme, correctionSymbolCount, 0 , 0.5);
                String json = eval.stringFromMap(name, eval.evaluate());
                myWriter.write(json);
                myWriter.write(", \n");
                System.out.println(name + " Done!");
            }
        }
    }

    private static void evaluatePSK(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating PSK");
        List<Double> noiseLvls = List.of(0.0,0.5,1.0,2.0);
        for(double noiseLvl : noiseLvls) {
            String name = "PSK|" + noiseLvl + "|CycleVsError";
            Evaluator eval = PremadeEvaluators.PSKCyclesVsError(noiseLvl, 20);
            String json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");

            name = "PSK|" + noiseLvl + "|CycleVsUseful";
            eval = PremadeEvaluators.PSKCyclesVsUseful(noiseLvl, 20);
            json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");
        }
    }

    private static void evaluateFSK(FileWriter myWriter) throws IOException {
        System.out.println("Evaluating FSK");
        List<Double> noiseLvls = List.of(0.0,0.5,1.0,2.0);
        for(double noiseLvl : noiseLvls) {
            String name = "FSK|" + noiseLvl + "|SymboltimeVsError";
            double min = 0.5 / DEFAULT_BASE_FREQUENCY;
            double max = 20 / DEFAULT_BASE_FREQUENCY;
            Evaluator eval = PremadeEvaluators.FSKTimeVsError(noiseLvl, 20, min, max);
            String json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");

            name = "FSK|" + noiseLvl + "|SymboltimeVsUseful";
            eval = PremadeEvaluators.FSKTimeVsUseful(noiseLvl, 20, min, max);
            json = eval.stringFromMap(name, eval.evaluate());
            myWriter.write(json);
            myWriter.write(", \n");
            System.out.println(name + " Done!");
        }
    }
}
