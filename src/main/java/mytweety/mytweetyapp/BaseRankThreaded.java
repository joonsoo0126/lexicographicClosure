package mytweety.mytweetyapp;

import org.tweetyproject.logics.pl.syntax.*;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.tweetyproject.logics.pl.sat.Sat4jSolver;

import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;

public class BaseRankThreaded extends RecursiveTask<PlBeliefSet> {

    private PlBeliefSet kb;
    private PlBeliefSet antecedants;
    private SatReasoner reasoner;
    private int start;
    private int end;
    private int threshold;

    public BaseRankThreaded(int start, int end, int threshold, PlBeliefSet antecedants, PlBeliefSet kb) {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        reasoner = new SatReasoner();
        this.antecedants = antecedants;
        this.kb = kb;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
    }

    @Override
    protected PlBeliefSet compute() {
        if ((end - start) <= threshold) {
            PlBeliefSet exceptionals = new PlBeliefSet();
            List<PlFormula> list = antecedants.getCanonicalOrdering();
            for (int i = start; i < end; i++) {
                PlFormula antecedant = list.get(i);
                if (reasoner.query(kb, new Negation(antecedant))) {
                    exceptionals.add(list.get(i));
                }
            }
            return exceptionals;
        }
        int middle = (start + end) / 2;
        BaseRankThreaded lower = new BaseRankThreaded(start, middle, threshold, antecedants, kb);
        BaseRankThreaded upper = new BaseRankThreaded(middle, end, threshold, antecedants, kb);
        lower.fork();
        upper.fork();
        PlBeliefSet result = new PlBeliefSet();
        result.addAll(lower.join());
        result.addAll(upper.join());
        return new PlBeliefSet(result);
    }

    private static PlBeliefSet getExceptionals(PlBeliefSet antecedants, PlBeliefSet kb, int threshold) {
        BaseRankThreaded brt = new BaseRankThreaded(0, antecedants.size(), threshold, antecedants, kb);
        ForkJoinPool pool = new ForkJoinPool();
        PlBeliefSet result = pool.invoke(brt);
        return result;
    }

    public static ArrayList<PlBeliefSet> rank(PlBeliefSet dkb, PlBeliefSet ckb) {
        ArrayList<PlBeliefSet> rankedKB = new ArrayList<PlBeliefSet>();
        PlBeliefSet currentMaterialisation = new PlBeliefSet(dkb);
        PlBeliefSet prevMaterialisation = new PlBeliefSet();

        while (true) {
            prevMaterialisation = currentMaterialisation;
            currentMaterialisation = new PlBeliefSet();
            PlBeliefSet temp = new PlBeliefSet(prevMaterialisation);
            temp.addAll(ckb);

            PlBeliefSet antecedants = new PlBeliefSet();

            for (PlFormula f : prevMaterialisation) {
                if (f.toString().contains("=>")) {
                    antecedants.add(((Implication) f).getFormulas().getFirst());
                }
            }

            PlBeliefSet exceptionals = getExceptionals(antecedants, temp, Math.max(antecedants.size() / 6, 1));
            for (PlFormula f : prevMaterialisation) {
                if (f.toString().contains("=>")) {
                    PlFormula ante = ((Implication) f).getFormulas().getFirst();
                    if (exceptionals.contains(ante))
                        currentMaterialisation.add(f);
                }
            }

            prevMaterialisation.removeAll(currentMaterialisation);
            if (currentMaterialisation.size() == 0) {
                rankedKB.add(prevMaterialisation);
                System.out.println("Added rank " + Integer.toString(rankedKB.size() - 1));
                currentMaterialisation.addAll(ckb);
                rankedKB.add(currentMaterialisation);
                System.out.println("Added infinite rank");
                break;
            }
            if (prevMaterialisation.size() == 0) {
                currentMaterialisation.addAll(ckb);
                rankedKB.add(currentMaterialisation);
                System.out.println("Added infinite rank");
                break;
            }
            rankedKB.add(prevMaterialisation);
            System.out.println("Added rank " + Integer.toString(rankedKB.size() - 1));
        }

        System.out.println("Base Ranking of Knowledge Base:");
        for (PlBeliefSet rank : rankedKB) {
            if (rankedKB.indexOf(rank) == rankedKB.size() - 1) {
                System.out.println("Infinite Rank:" + rank.toString());
            } else {
                System.out.println("Rank " + Integer.toString(rankedKB.indexOf(rank)) + ":" + rank.toString());
            }
        }
        return rankedKB;
    }
}
