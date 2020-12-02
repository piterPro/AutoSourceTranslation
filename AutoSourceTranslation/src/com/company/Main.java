package com.company;


import java.util.List;

public class Main {

    private static final String desktopFolderLocation = "C:\\Users\\Piter\\Desktop\\Translation project\\";

    public static void main(String[] args){
        Dictionary dictionary = new Dictionary();
        List<String> fileList = Utils.getAllFilesFromFolder(desktopFolderLocation);
        for (String fileName : fileList) {
            addToDictionaryAndTranslateFile(dictionary, fileName);
        }
        dictionary.writeTranslatedWordsToFile();
        dictionary.saveToFile();

    }

    private static void addToDictionaryAndTranslateFile(Dictionary dictionary, String fileName) {
        System.out.println("File "+fileName+ " opened!");

        FileManager file = new FileManager(fileName, dictionary);
        file.addToDictionary();
        file.translate();

        System.out.println("File " + fileName + " translated!");
        System.out.println("Total translated words = "+Utils.translatedWords+ "!");
        System.out.println("Total notTranslated words = "+Utils.notTranslatedWords+ "!");
        System.out.println();
    }


}
