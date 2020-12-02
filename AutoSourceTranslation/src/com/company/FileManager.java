package com.company;

import java.io.*;
import java.util.Scanner;

public class FileManager {
    private static final int msg1CharacterNumber = 7;
    private static final int msg2CharacterNumber = 8;
    private String fileName = "";
    private String newText = "";

    private int currentLineNum = 0;
    private int foundCouples = 0;
    private int foundSingleWords = 0;
    private int foundNotTranslated = 0;

    private Dictionary dictionary;
    boolean shouldIWriteFile = false;

    public FileManager(String fileName, Dictionary dictionary) {
        this.fileName = fileName;
        this.dictionary = dictionary;
    }

    public void addToDictionary() {
        try {
            unsafeAddToDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unsafeAddToDictionary() throws FileNotFoundException {
        nullTheStatistics();
        File file = new File(fileName);

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine(); currentLineNum++;
            String value1 = getValue1(line);

            if (!needToTranslate(value1) && scanner.hasNextLine()) {

                String line2 = scanner.nextLine(); currentLineNum++;
                String value2 = getValue2(line2);
//                printCouplesValues(currentLineNum, value1, value2);
                if(!needToTranslate(value2)){
                    dictionary.add(value1.toLowerCase(), value2);
                }
                checkStatistics(value1, value2);
            }
        }
//        printStatistics();
    }

    private void checkStatistics(String value1, String value2) {
        checkForSingleWord(value1);
        foundCouples++;
        checkForNotTranslated(value2);
    }

    private void checkForNotTranslated(String value2) {
        if(needToTranslate(value2))
            foundNotTranslated++;
    }

    private void checkForSingleWord(String value1) {
        int numberOfSpaces = Utils.countMatches(value1," ");
        if(numberOfSpaces == 0)
            foundSingleWords++;
    }

    private void nullTheStatistics() {
        currentLineNum = 0;
        foundCouples = 0;
        foundSingleWords = 0;
        foundNotTranslated = 0;
    }

    private void printStatistics() {
        System.out.println("foundCouples = "+foundCouples);
        System.out.println("foundNotTranslated = "+foundNotTranslated);
        System.out.println("foundSingleWords = "+foundSingleWords);
    }

    private void printCouplesValues(int lineNum, String value1, String value2) {
        System.out.println("Value [" + (lineNum -1) + "] = " + value1);
        System.out.println("Value2 [" + lineNum + "] = " + value2);
        System.out.println("");
    }

    private String getValue1(String line) {
        String Value = "";
        int valueStartPosition = line.indexOf("msgid \"");
        if(valueStartPosition>=0){
            int valueEndPosition = line.indexOf("\"",valueStartPosition + msg1CharacterNumber);
            Value = line.substring(valueStartPosition+msg1CharacterNumber, valueEndPosition);
        }
        return Value;
    }

    private String getValue2(String line) {
        String Value = "-1";
        int valueStartPosition = line.indexOf("msgstr \"");
        if(valueStartPosition>=0){
            int valueEndPosition = line.indexOf("\"",valueStartPosition + msg2CharacterNumber);
            Value = line.substring(valueStartPosition+msg2CharacterNumber, valueEndPosition);
        }
        return Value;
    }

    public void translate(){
        try {
            translateV3();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void translateV1() throws FileNotFoundException {
        //we have the open file
        //find every msgtxt and msgvalue empty and translate the
        //value if possible
        nullTheStatistics();
        File file = new File(fileName);

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine(); currentLineNum++;
            String value1 = getValue1(line);

            if (!needToTranslate(value1) && scanner.hasNextLine()) {

                String line2 = scanner.nextLine(); currentLineNum++;
                String value2 = getValue2(line2);
//                printCouplesValues(currentLineNum, value1, value2);
                if(needToTranslate(value2)){
                    String translatedValue2 = dictionary.getTranslation(value1.toLowerCase());
                    if(!needToTranslate(translatedValue2))
                        System.out.println("Found word to translate ["+currentLineNum+"]; "+ value1 +" = "+ translatedValue2);
                }
                checkStatistics(value1, value2);
                //here we should write to the same file and just replace the empty string with found string

            }
        }
//        printStatistics();

    }

    private void translateV2() throws IOException {
        //we have the open file
        //find every msgtxt and msgvalue empty and translate the
        //value if possible
        nullTheStatistics();
        File file = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
//        raf.seek(0);
        Scanner scanner = new Scanner(file);
        String line;
        while ((line = raf.readLine())!=null) {
            currentLineNum++;
            String value1 = getValue1(line);

            if (!needToTranslate(value1)) {
                long previousPointer = raf.getFilePointer();
                String line2 = raf.readLine();
                currentLineNum++;
                String value2 = getValue2(line2);
//                printCouplesValues(currentLineNum, value1, value2);

                if(needToTranslate(value2)){

                    int pointerOffset = line2.indexOf("msgstr \"")*2;
                    String translatedValue2 = dictionary.getTranslation(value1.toLowerCase());
                    if(!needToTranslate(translatedValue2)){
                        raf.seek(previousPointer);
                        String newLine2 = line2.replace("\"\"", "\""+translatedValue2+"\"");
                        raf.writeBytes(newLine2);
                        raf.writeBytes(System.getProperty("line.separator"));

                        System.out.println("Replaced");
                        System.out.println(line2);
                        System.out.println("with");
                        System.out.println(newLine2);
                        System.out.println("Found word to translate ["+currentLineNum+"]; "+ value1 +" = "+ translatedValue2);
                    }
                }
                checkStatistics(value1, value2);
                //here we should write to the same file and just replace the empty string with found string

            }
        }
//        printStatistics();

    }

    private void translateV3() throws IOException {

        nullTheStatistics();
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()){
            findCouplesAndTranslate(scanner);
        }
        if(shouldIWriteFile){
            writeToFile(fileName, newText);
            printStatistics();
        }
    }
    
    private void findCouplesAndTranslate(Scanner scanner){
        String line = scanner.nextLine();            currentLineNum++;
        newText += line + System.getProperty("line.separator");
        String value1 = getValue1(line);

        if (!needToTranslate(value1) && scanner.hasNextLine()) {
            String line2 = scanner.nextLine();                currentLineNum++;
            String value2 = getValue2(line2);

            if(needToTranslate(value2)){
                String translatedValue2 = dictionary.getTranslation(value1.toLowerCase());
                if(!needToTranslate(translatedValue2))
                {
                    String newLine2 = line2.replace("\"\"", "\""+translatedValue2+"\"");
                    newText += newLine2 + System.getProperty("line.separator");
                    shouldIWriteFile = true;
                    Utils.translatedWords++;
                    dictionary.translatedWordsText += "" + value1 +"=" + translatedValue2 + System.getProperty("line.separator");
//                        System.out.println("Found word to translate ["+currentLineNum+"]; "+ value1 +" = "+ translatedValue2);
                }
                else{
                    Utils.notTranslatedWords ++;
                    newText += line2 + System.getProperty("line.separator");
                }
            }
            else
                newText += line2 + System.getProperty("line.separator");
            checkStatistics(value1, value2);
        }
    }

    private boolean needToTranslate(String value2) {
        return value2.equals("");
    }

    private void writeToFile(String fileName, String text) throws IOException {
        System.out.println("Writing file "+ fileName +" ...");
        byte[] strToBytes = text.getBytes();
        FileOutputStream outputStream = new FileOutputStream(fileName);
        outputStream.write(strToBytes);
        outputStream.close();
        System.out.println("File done!");
    }
}
