package ru.shcherbatykh.shaper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.shaper.model.MasyuMap;
import ru.shcherbatykh.shaper.service.RangeDivider;
import ru.shcherbatykh.shaper.utils.CommandUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RangeDividerImpl implements RangeDivider {
    @Override
    public List<String> getPartsOfTask(String filename) {
        MasyuMap masyuMap = CommandUtils.readFile(filename);
        BigInteger startNumber = masyuMap.getStartNumber();
        BigInteger endNumber = BigInteger.valueOf(7).pow(masyuMap.getM() * masyuMap.getN()).subtract(BigInteger.ONE);
        BigInteger range = endNumber.subtract(startNumber);
        BigInteger taskPartSize = getTaskPartSize(range, masyuMap);
        List<String> ranges = new ArrayList<>();
        BigInteger curr = new BigInteger(String.valueOf(startNumber));
        while (curr.compareTo(endNumber) < 0) {
            if (curr.add(taskPartSize).compareTo(endNumber) < 0) {
                String str = curr + " " + curr.add(taskPartSize);
                ranges.add(str);
                curr = curr.add(taskPartSize).add(BigInteger.valueOf(1));
            } else {
                String str = curr + " " + endNumber;
                ranges.add(str);
                break;
            }
        }
        return ranges;
    }

    private BigInteger getTaskPartSize(BigInteger range, MasyuMap masyuMap) {
        int matrixSize = masyuMap.getM()* masyuMap.getN();
        long countOfSubTasks = Math.round(matrixSize*0.3725);
        log.info("TASK will be divided into {} subtasks", countOfSubTasks);
        return range.divide(BigInteger.valueOf(countOfSubTasks));
    }

//    private BigInteger getTaskPartSize(BigInteger range) {
//        return range.divide(BigInteger.valueOf(100));
//    }

}
