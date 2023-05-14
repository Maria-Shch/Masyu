package ru.shcherbatykh.shaper.utils;

import ru.shcherbatykh.shaper.model.Bead;
import ru.shcherbatykh.shaper.model.Color;
import ru.shcherbatykh.shaper.model.MasyuMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class CommandUtils {
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


    public static String decodeFromBase64(String s) throws UnsupportedEncodingException {
        return new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), "UTF-8");
    }
}
