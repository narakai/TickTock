package com.martin.ads.ticktock.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Ads on 2017/3/16.
 */

public class FileUtils {
    public static final String EMPTY_FILE_STR="[]";

    public static String loadStringFromFile(File file){
        try {
            if(!file.exists()) return EMPTY_FILE_STR;
            String result = getStringFromStream(new FileInputStream(file));
            if(result==null || result.isEmpty()) return EMPTY_FILE_STR;
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return EMPTY_FILE_STR;
    }

    private static String getStringFromStream(InputStream inputStream){
        if(inputStream==null) return null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveStringToFile(File file,String content){
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter=new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file)));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
