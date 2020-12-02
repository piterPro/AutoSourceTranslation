package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Dictionary {

    private static final String desktopFolderLocation = "C:\\Users\\Piter\\Desktop\\Translation project\\";
    private static final String fileName = "sourceFile.txt";

    public String translatedWordsText = "";
    private String translatedWordsFile = "translatedWords.txt";

    Map<String, String> map = new HashMap<>();
    File file;

    public Dictionary() {
        System.out.println("Opening existing dictionary");
        file = new File(desktopFolderLocation + fileName);
        createNewFile();
        loadDictionary();
    }

    private void createNewFile(){
        try{
            file.createNewFile();
        }catch (Exception e){}
    }

    public void add(String value1, String value2){
        if(!map.containsKey(value1))
            safeAdd(value1, value2);
    }
    private void safeAdd(String value1, String value2){
        map.put(value1, value2);
    }

    public String getTranslation(String value1){
        return map.getOrDefault(value1,"");
    }

    public void saveToFile(){
        try{
            unsafeSaveToFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void unsafeSaveToFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        map.forEach((k,v)->{
            writer.print(k+"="+v+"\n");
        });
        writer.close();
    }

    public void loadDictionary(){
        try {
            unsafeOpenDictionaryAndLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void unsafeOpenDictionaryAndLoad() throws IOException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String value1 = getValue1(line);
            String value2 = getValue2(line);
            add(value1.toLowerCase(), value2);
        }
    }

    private String getValue1(String line) {
        String Value = "";
        int valueEndPosition = line.indexOf("=");
        if(valueEndPosition>0){
            Value = line.substring(0, valueEndPosition);
        }
        return Value;
    }

    private String getValue2(String line) {
        String Value = "";
        int valueStartPosition = line.indexOf("=");
        if(valueStartPosition>0){
            Value = line.substring(valueStartPosition+1);
        }
        return Value;
    }

    public void writeTranslatedWordsToFile(){
        try {
            unsafeWriteTranslatedWordsToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unsafeWriteTranslatedWordsToFile() throws IOException {
        System.out.println("Writing file "+ translatedWordsFile +"...");
        byte[] strToBytes = translatedWordsText.getBytes();
        FileOutputStream outputStream = new FileOutputStream( desktopFolderLocation + translatedWordsFile);
        outputStream.write(strToBytes);
        outputStream.close();
        System.out.println("File done!");
    }

}
