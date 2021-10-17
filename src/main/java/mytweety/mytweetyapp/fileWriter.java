package mytweety.mytweetyapp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;

import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.*;

import java.io.*;
import java.util.Scanner;

import org.tweetyproject.commons.ParserException;

public class fileWriter {

    static ArrayList<Integer> bin = new ArrayList<>();
    static ArrayList<Integer> ter = new ArrayList<>();

    static void write(String fileName) throws IOException, ParserException {

        PlBeliefSet beliefSet = new PlBeliefSet();
        PlParser parser = new PlParser();
        PlBeliefSet classicalSet = new PlBeliefSet();
        //Parser
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String stringFormula = reader.nextLine();
                if (stringFormula.contains("¬")) {
                    stringFormula = stringFormula.replaceAll("¬", "!");
                }
                if (stringFormula.contains("~>")) {
                    stringFormula = reformatDefeasibleImplication(stringFormula);
                    beliefSet.add((PlFormula) parser.parseFormula(stringFormula));
                } else {
                    classicalSet.add((PlFormula) parser.parseFormula(stringFormula));

                }

            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(
                    "Output not in correct format. Please ensure each formula is in a separate line, with the first line being the defeasible query, and the remainder being the knowledge base. All formulas must use the following syntax:");
            System.out.println("Implication symbol: =>");
            System.out.println("Defeasible Implication symbol: ~>");
            System.out.println("Conjunction symbol: && ");
            System.out.println("Disjunction symbol: ||");
            System.out.println("Equivalence symbol: <=>");
            System.out.println("Negation symbol: !");
        }
        ArrayList<PlBeliefSet> rankedKnowledgeBase = BaseRankThreaded.rank(beliefSet, classicalSet);
        JSONArray jsonRankedKB = new JSONArray(rankedKnowledgeBase);

        try (FileWriter file = new FileWriter("rankedKB.json")) {
            // We can write any JSONArray or JSONObject instance to the file
            file.write(jsonRankedKB.toString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String reformatDefeasibleImplication(String formula) {
        int index = formula.indexOf("~>");
        formula = "(" + formula.substring(0, index) + ") => (" + formula.substring(index + 2, formula.length()) + ")";
        return formula;
    }
}