package ru.shcherbatykh.masyu.utils;

import org.apache.commons.cli.*;
import ru.shcherbatykh.masyu.models.Bead;
import ru.shcherbatykh.masyu.models.Color;
import ru.shcherbatykh.masyu.models.MasyuMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandUtils {
    public static CommandLine getCommandLine(String[] args) {
        Options options = new Options();

        options.addOption(Option.builder()
                .required()
                .hasArg()
                .option("f")
                .longOpt("file")
                .desc("Name of file with input map")
                .build());

        options.addOption(Option.builder()
                .required()
                .hasArg()
                .numberOfArgs(2)
                .option("r")
                .longOpt("range")
                .desc("Range of enumeration")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("FAILED");
            System.out.println(e.getMessage());
            formatter.printHelp("solver", options);
            System.exit(1);
        }
        return cmd;
    }

    public static MasyuMap readFile(String fileName) {
        Path path = Paths.get(fileName);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            MasyuMap masyuMap = new MasyuMap();
            masyuMap.setM(Integer.parseInt(reader.readLine()));
            masyuMap.setN(Integer.parseInt(reader.readLine()));
            String s;
            String[] beadCode;
            while ((s = reader.readLine()) != null) {
                beadCode = s.split(" ");
                masyuMap.addBead(new Bead(
                        Integer.parseInt(beadCode[0]) - 1,
                        Integer.parseInt(beadCode[1]) - 1,
                        Color.getColorByCode(beadCode[2].charAt(0))
                ));
            }
            return masyuMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOneOf(Object source, Object... target) {
        for (Object obj : target) {
            if (obj.equals(source)) {
                return true;
            }
        }
        return false;
    }
}
