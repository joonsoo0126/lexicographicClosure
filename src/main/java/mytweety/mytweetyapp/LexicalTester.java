package mytweety.mytweetyapp;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.*;

public class LexicalTester {
    PlBeliefSet knowledgeBase;
    static ArrayList<Integer> bin = new ArrayList<>();
    static ArrayList<Integer> ter = new ArrayList<>();
    static void test() throws IOException {
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
        PlBeliefSet[] rankedKBArray = rankedKnowledgeBase.toArray(rankedKnowledgeBaseArray);

       
        ArrayList<String> formulaToCheckFor = new ArrayList<>();
        try {
            File file = new File("input.txt");
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                formulaToCheckFor.add(reader.nextLine());
               

            }
            reader.close();
        } catch (FileNotFoundException e){}
    
        for(String s : formulaToCheckFor){
            System.out.println("Binary "+s);
            benchBinary(rankedKBArray, (PlFormula) parser.parseFormula(fileWriter.reformatDefeasibleImplication(s)));
            System.out.println("Ternary "+s);
            benchTernary(rankedKBArray, (PlFormula) parser.parseFormula(fileWriter.reformatDefeasibleImplication(s)));
        }
        /**
         * Testing the Binary and Ternary search
         */
        int min = 999;
        float average = 0;
        int max = -1;
        for (int i : bin){
            if (max < i) max = i;
            if (min > i) min = i;
            average+=i;
        }
        System.out.println("Binary - min: "+min+" avg: "+average+" max: "+max);
        max = -1;
        average=0;
        min = 999;
        for (int i : ter){
            if (max < i) max = i;
            if (min > i) min = i;
            average+=i;
        }
        System.out.println("Ternary - min: "+min+" avg: "+average+" max: "+max);

    }

    public static void benchNaive(ArrayList<PlBeliefSet> rankedKnowledgeBase, PlFormula formulaWereCheckingFor) throws IOException{
        System.out.println(NaiveLex.checkEntailment(rankedKnowledgeBase, formulaWereCheckingFor));
    }

    public static void benchPSet(ArrayList<PlBeliefSet> rankedKnowledgeBase, PlFormula formulaWereCheckingFor) throws IOException{
        System.out.println(PowerSetLex.checkEntailment(rankedKnowledgeBase, formulaWereCheckingFor));
    }
    
    public static void benchBinary(PlBeliefSet[] rankedKBArray, PlFormula formulaWereCheckingFor) throws IOException{
        BinaryLex b = new BinaryLex();
        System.out.println(b.checkEntailmentBinarySearch(rankedKBArray, formulaWereCheckingFor, 0,
                        rankedKBArray.length));
        System.out.println("Counter Comp "+b.counter);
        System.out.println("Counter Ref "+b.counterR);
        bin.add(b.counter);
        b.counter = 0;
        b.counterR=0;
    }
    public static void benchTernary(PlBeliefSet[] rankedKBArray, PlFormula formulaWereCheckingFor) throws IOException{
        TernaryLex t = new TernaryLex();
        System.out.println(t.checkEntailmentTernarySearch(rankedKBArray, formulaWereCheckingFor, 0,
        rankedKBArray.length));
        System.out.println("Counter Comp "+t.counter);
        System.out.println("Counter Ref "+t.counterR);
        ter.add(t.counter);
        t.counter = 0;
        t.counterR = 0;
    }
}