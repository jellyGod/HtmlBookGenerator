package com.jellyGod.htmlToPdf.base;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jelly on 2017/2/18.
 */
public class Utils {

    public static List<NameValuePair> getGetParams(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("GetParams.txt")));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                //analyze the temp
                String[] temps = temp.split(":");
                if(temps.length>=2)
                     params.add(new BasicNameValuePair(temps[0],temps[1]));
                else
                     params.add(new BasicNameValuePair(temps[0],""));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return params;
    }


    public static String getPatternWord(String buff,String pattern){
        String word = "";
        Pattern pn = Pattern.compile(pattern);
        Matcher matcher = pn.matcher(buff);
        if(matcher.find()){
            word = matcher.group();
        }
        System.out.println("the matcher word is:"+word);
        return word;
    }

    //read record
    public static Result<Boolean,String> getRecord(String key){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "";
        BufferedReader br = null;
        try{
            File file = new File("Record.txt");
            if(!file.exists()){
                return res;
            }
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while((temp=br.readLine())!=null){
                String[] temps = temp.split(" ");
                if(temps.length>=2){
                    if(temps[0].equals(key)){
                        res.res = true;
                        res.ct = temps[1];
                    }
                }
            }

        }
        catch(Exception e){
            if(br != null){
                try {
                    br.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return res;
    }

    //read all records
    public static Result<Boolean,LinkedHashMap<String,String>> getAllRecords(){
        Result<Boolean,LinkedHashMap<String,String>> res = new Result<Boolean,LinkedHashMap<String,String>>();
        res.res = false;
        res.ct = new LinkedHashMap<String, String>();
        BufferedReader br = null;
        try{
            File file = new File("Record.txt");
            if(!file.exists()){
                return res;
            }
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while((temp=br.readLine())!=null){
                String[] temps = temp.split(" ");
                if(temps.length>=2){
                   res.ct.put(temps[0],temps[1]);
                }
            }
            res.res = true;
        }
        catch(Exception e){
            if(br != null){
                try {
                    br.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return res;
    }

    //first read then write the record
    public static Result<Boolean,String> writeRecord(String key,String value){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "fail";
        Result<Boolean,LinkedHashMap<String,String>> allRecords = getAllRecords();
        if(allRecords.res == true){
            System.out.println("yes hello man!");
            LinkedHashMap<String,String> maps = allRecords.ct;
            maps.put(key,value);
            BufferedWriter bw = null;
            try{
                bw = new BufferedWriter(new FileWriter(new File("Record.txt")));
                for(String mkey:maps.keySet())
                {
                    bw.write(mkey+" "+maps.get(mkey)+"\n");
                }
                bw.flush();
                res.res = true;
                res.ct = "success";
            }
            catch(Exception e){
                if(bw != null){
                    try {
                        bw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        return res;
    }


    public static void main(String[] args){
        Utils.getGetParams();
        System.out.println("Yes i do it success!");
        Utils.getPatternWord("<a href=\"https://www.web2pdfconvert.com/download?path=1e53bee4-0153-4fae-b2da-4700921a915dwww-e-reading-club.pdf\"","http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?.pdf");
    }

}
