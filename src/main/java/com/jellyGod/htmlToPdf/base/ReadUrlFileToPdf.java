package com.jellyGod.htmlToPdf.base;

import java.io.*;
import java.util.*;

/**
 * use the following website
 * there is many problem about it
 * https://www.web2pdfconvert.com/
 * Created by jelly on 2017/2/18.
 */
public class ReadUrlFileToPdf {

    //read name and url
    public Result<Boolean,LinkedHashMap> getNameAndUrl(String fileName) {
        Result<Boolean,LinkedHashMap> res = new Result<Boolean,LinkedHashMap>();
        res.res = false;
        res.ct = null;
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(new File(fileName)));
            LinkedHashMap<String,String> orderedMap = new LinkedHashMap<String,String>();
            String temp = null;
            while((temp = br.readLine())!=null){
                String[] temps = temp.split("----");
                if(temps.length >= 2)
                {
                    orderedMap.put(temps[0],temps[1]);
                }
            }
            res.res = true;
            res.ct = orderedMap;
        }
        catch(Exception e){
            e.printStackTrace();;
        }
        finally{
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    //read a url file and download all data to a dir
    public Result<Boolean,String> downloadAndSave(String urlFile,String dstDir){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "fail";
        try {
            File file = new File(dstDir);
            if(!file.exists())
                file.mkdirs();
           Result<Boolean,LinkedHashMap> getUrl = getNameAndUrl("UrlFile.txt");
           if(getUrl.res == true){
               LinkedHashMap<String,String> orderedMap = getUrl.ct;
               Set<String> set = orderedMap.keySet();
               //read records
               Result<Boolean,String> download = Utils.getRecord("download");
               if(download.res == true){
                   int downloadNum = Integer.valueOf(download.ct);
                   if(downloadNum<set.size()){
                       //start to download
                       int i = 0;
                       for(String name:set){
                           i++;
                           if(i>downloadNum){
                               //just download and save in record
                               HtmlToPdf hp = new LocalHtmlToPdf();
                               System.out.println(orderedMap.get(name)+" "+dstDir+" "+i);
                               Result<Boolean,String> htmlToPdf = hp.htmlToPdf(orderedMap.get(name),dstDir,""+i);
                               if(htmlToPdf.res == true){
                                   downloadNum = i;
                                   System.out.println("complete"+(int)(i*1.0/set.size()*100)+"%");
                                   Thread.sleep(1000);
                                   //write to the record
                                   Utils.writeRecord("download", "" + downloadNum);
                               }
                               else{
                                   System.out.println("download pdf fail!");
                                   return res;
                               }
                           }
                       }
                   }
                   //start to merge and generate to a book
                   //It's very interesting
                   List<String> names = new ArrayList<String>();
                   for(String name:set){
                       names.add(name);
                   }
                   Map<String,String> fileUrls = new HashMap<String,String>();
                   int i = 0;
                   for(String name:set){
                       i++;
                       String fileUrl = dstDir+i+".pdf";
                       fileUrls.put(name,fileUrl);
                   }
                   MergePdf.merge(names,fileUrls,"book.pdf");
                   res.res = true;
                   res.ct = "success";
               }
           }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }


    public static void main(String[] args){
         ReadUrlFileToPdf rp = new ReadUrlFileToPdf();
         Result<Boolean,String> res = rp.downloadAndSave("UrlFile.txt","I:/downPdf/");
         if(res.res == true){
             System.out.print("yes success!");
         }
    }
}
