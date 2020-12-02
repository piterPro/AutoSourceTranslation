package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static int translatedWords = 0;
    public static int notTranslatedWords = 0;

    /* Checks if a String is empty ("") or null. */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }


    /* Counts how many times the substring appears in the larger string. */
    public static int countMatches(String text, String str) {
        if (isEmpty(text) || isEmpty(str)) {
            return 0;
        }

        int index = 0, count = 0;
        while (true) {
            index = text.indexOf(str, index);
            if (index != -1) {
                count ++;
                index += str.length();
            } else {
                break;
            }
        }

        return count;
    }

    public static List<String> getAllFilesFromFolder(String folderPath){
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<String> value = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String name = file.getPath();
                Boolean b = file.getName().endsWith(".po");
                if(b)
                    value.add(name);
            }
            if(file.isDirectory()){
                value.addAll(getAllFilesFromFolder(file.getPath()));
            }
        }
        return value;
    }

}
