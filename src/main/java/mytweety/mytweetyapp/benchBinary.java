package mytweety.mytweetyapp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;
import org.tweetyproject.logics.pl.syntax.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.commons.ParserException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
/**
 * Benchmarking using JMH
 */
public class benchBinary {
    
    @State(Scope.Benchmark)
    public static class MyState {
        PlBeliefSet[] rankedKBArray;
        PlFormula formulaWereCheckingFor;

        @Setup(Level.Trial) // Setup of the data before executing the benchmark. Time taken for this part will not be considered.
        public void setup() throws IOException{
            PlParser parser = new PlParser();
            JSONParser jsonParser = new JSONParser();
            ArrayList<PlBeliefSet> rankedKnowledgeBase = new ArrayList<>();
            try (FileReader reader = new FileReader("rankedKB.json"))
            {
                //Read JSON file
                Object obj = jsonParser.parse(reader);
     
                JSONArray rankedKB = (JSONArray) obj;
            
                for(int i =0; i < rankedKB.size(); i++){
                    PlBeliefSet plBeliefSet = new PlBeliefSet();
                    Object[] statements = ((JSONArray) rankedKB.get(i)).toArray();
                    for (Object statement : statements){
                        plBeliefSet.add((PlFormula) parser.parseFormula(statement.toString()));
                    }
                    rankedKnowledgeBase.add(plBeliefSet);
                }
                
     
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
           
            PlBeliefSet[] rankedKnowledgeBaseArray = new PlBeliefSet[rankedKnowledgeBase.size()];
            this.rankedKBArray = rankedKnowledgeBase.toArray(rankedKnowledgeBaseArray);
    
            this.formulaWereCheckingFor = (PlFormula) parser.parseFormula("a"); // Initial setting of the PlFormula
    
            String formulaToCheckFor = "";
            try {
                File file = new File("input.txt");
                Scanner reader = new Scanner(file);
    
                while (reader.hasNextLine()) {
                    formulaToCheckFor = reader.nextLine();
                   
    
                }
                reader.close();
            } catch (FileNotFoundException e){}
            if (formulaToCheckFor.contains("¬")) {
                formulaToCheckFor = formulaToCheckFor.replaceAll("¬", "!");
            }
            this.formulaWereCheckingFor = (PlFormula) parser.parseFormula(reformatDefeasibleImplication(formulaToCheckFor));
          
        }
    }
    @Benchmark
    @Fork(value = 2) // 2 trails in total
    @Measurement(iterations = 10, time = 1) // 10 iterations
    @Warmup(iterations = 5, time = 1) // 5 iterations of warmup
    public void binary(MyState mystate) throws IOException {
        LexicalTester.benchBinary(mystate.rankedKBArray, mystate.formulaWereCheckingFor);
    }
    public static void main(String[] args) throws IOException, ParserException, RunnerException {
        
        Options opt = new OptionsBuilder().include(benchBinary.class.getSimpleName()).forks(2).build();

        new Runner(opt).run(); // Execution of the benchmark
    

    }

    static String reformatDefeasibleImplication(String formula) {
        int index = formula.indexOf("~>");
        formula = "(" + formula.substring(0, index) + ") => (" + formula.substring(index + 2, formula.length()) + ")";
        return formula;
    }

}
