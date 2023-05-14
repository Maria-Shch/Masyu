package ru.shcherbatykh.masyu;

import org.apache.commons.cli.CommandLine;
import ru.shcherbatykh.masyu.logic.Resolver;
import ru.shcherbatykh.masyu.models.MasyuMap;
import ru.shcherbatykh.masyu.utils.CommandUtils;

import java.io.File;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
//        CommandLine cmd = CommandUtils.getCommandLine(args);
//        String fileName = cmd.getOptionValue('f');
//        String[] rangeArr = cmd.getOptionValues('r');
//        BigInteger startNumber =  new BigInteger(rangeArr[0]);
//        BigInteger finishNumber = new BigInteger(rangeArr[1]);
//        MasyuMap masyuMap = CommandUtils.readFile(fileName);
//
//        Resolver.resolve(startNumber, finishNumber, masyuMap);

        MasyuMap masyuMap = CommandUtils.readFile("C:\\Users\\maria\\Documents\\8_sem\\GridComputing\\Masyu\\masyu.txt");
//        Resolver.resolve(masyuMap.getStartNumber(), BigInteger.valueOf(7).pow(masyuMap.getM() * masyuMap.getN()).subtract(BigInteger.ONE), masyuMap);

        Resolver.resolve("0000045000132501001032260", masyuMap);
    }
}
