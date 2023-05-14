package ru.shcherbatykh.masyu.logic;

import ru.shcherbatykh.masyu.models.Bead;
import ru.shcherbatykh.masyu.models.Color;
import ru.shcherbatykh.masyu.models.MasyuMap;
import ru.shcherbatykh.masyu.models.SolutionMap;

import java.math.BigInteger;
import java.util.stream.LongStream;

public class Resolver {
    private static final BigInteger RADIX = BigInteger.valueOf(7);

    public static void resolve(String solution, MasyuMap masyuMap){
        SolutionMap solutionMap = new SolutionMap(masyuMap, solution.toString());
        if (SolutionMapChecker.checkSolutionMap(solutionMap, masyuMap)) {
            System.out.println("SUCCESS");
            System.out.println(solution);
            solutionMap.printMap();
        } else {
            System.out.println("No");
        }
    }

    public static void resolve(BigInteger start, BigInteger end, MasyuMap masyuMap){
        long startTime = System.currentTimeMillis();
        StringBuilder code;
        SolutionMap solutionMap;
        BigInteger i = start;
        while (i.compareTo(end) <= 0) {
            code = new StringBuilder(i.toString(7));
            while (code.length() < masyuMap.getM() * masyuMap.getN()) {
                code.insert(0, '0');
            }
            solutionMap = new SolutionMap(masyuMap, code.toString());

            int badIdx = SolutionMapChecker.checkCells(solutionMap);
            if (badIdx < code.length()) {
                int degree = code.length() - badIdx - 1;
                BigInteger inc = RADIX.pow(degree);
                BigInteger codeBI = i.add(inc);
                BigInteger tail = codeBI.mod(inc);
                i = codeBI.subtract(tail);
                continue;
            }

            if (SolutionMapChecker.checkSolutionMap(solutionMap, masyuMap)) {
                System.out.println("SUCCESS");
                System.out.println(code);
                solutionMap.printMap();


//                long endTime = System.currentTimeMillis();
//                System.out.println("===" + (endTime-startTime)  + "====");
                return;
            }

            i = i.add(BigInteger.ONE);
        }

        System.out.println("NO_SOLUTION");
//        long endTime = System.currentTimeMillis();
//        System.out.println("===" + (endTime-startTime)  + "====");
    }
}