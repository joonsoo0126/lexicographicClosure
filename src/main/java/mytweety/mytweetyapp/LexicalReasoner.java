package mytweety.mytweetyapp;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;


public class LexicalReasoner {

    
    static ArrayList<Integer> bin = new ArrayList<>();
    static ArrayList<Integer> ter = new ArrayList<>();

    static void reasoner() throws IOException {

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
        Scanner scanner = new Scanner(System.in);
        PlFormula formulaWereCheckingFor = (PlFormula) parser.parseFormula("a");

        while (true) {
            System.out.println("Enter type of algorithm: ");
            String entailmentCheckingAlgorithm = scanner.next();
            if (entailmentCheckingAlgorithm.equals("stop")) {
                break;
            }
            System.out.println("Enter formula to check for: ");
            String formulaToCheckFor = scanner.next();
            if (formulaToCheckFor.contains("¬")) {
                formulaToCheckFor = formulaToCheckFor.replaceAll("¬", "!");
            }
            formulaWereCheckingFor = (PlFormula) parser.parseFormula(fileWriter.reformatDefeasibleImplication(formulaToCheckFor));

            if (entailmentCheckingAlgorithm.equals("naive")){

                System.out.println(NaiveLex.checkEntailment(rankedKnowledgeBase, formulaWereCheckingFor));}
      
            else if (entailmentCheckingAlgorithm.equals("powerset")){
  
                System.out.println(PowerSetLex.checkEntailment(rankedKnowledgeBase, formulaWereCheckingFor));}
       
            else if (entailmentCheckingAlgorithm.equals("binary")) {
                System.out.println(BinaryLex.checkEntailmentBinarySearch(rankedKBArray, formulaWereCheckingFor, 0,
                        rankedKnowledgeBase.size()));
             
            } else if (entailmentCheckingAlgorithm.equals("ternary")) {

                System.out.println(TernaryLex.checkEntailmentTernarySearch(rankedKBArray, formulaWereCheckingFor, 0,
                        rankedKnowledgeBase.size()));
    
            }
            else{
                System.out.println("Try again");
            }

        }
    }

}