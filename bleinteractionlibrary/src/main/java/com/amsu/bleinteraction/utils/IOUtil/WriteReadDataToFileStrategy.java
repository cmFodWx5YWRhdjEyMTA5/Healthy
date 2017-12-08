package com.amsu.bleinteraction.utils.IOUtil;

import java.util.List;

/**
 * Created by HP on 2017/4/27.
 */

public interface WriteReadDataToFileStrategy {
    boolean writeDataToFile(List<Integer> integerList, String fileName);
    List<Integer> readDataFromFile(String fileName);

    boolean writeByteDataToFile(byte[] bytes, String fileName);


    void writeArrayDataToBinaryFile(final int[] ints);
    void closeArrayDataStreamResource();


}
