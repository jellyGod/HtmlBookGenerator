package com.jellyGod.htmlToPdf.base;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * the api is a fake implementation of htmlToPdf
 * it use a website to do the work
 * Created by jelly on 2017/2/18.
 */
public class WebHtmlToPdf {

    public Result<Boolean,String> getPdf(String url){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            //add some parameter
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cURL",url));
            params.addAll(Utils.getGetParams());
            String mstr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpGet httpget = new HttpGet("https://www.web2pdfconvert.com/engine.aspx?"+mstr);
            //just add some normal header
            httpget.setHeader("Host","www.web2pdfconvert.com");
            httpget.setHeader("Referer","https://www.web2pdfconvert.com/");
            httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            //analyze the responseBody to get pdf url
            System.out.print(responseBody);
            String pdfUrl = Utils.getPatternWord(responseBody,"http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?pdf");
            if(!pdfUrl.equals(""))
            {
                res.res = true;
                res.ct = pdfUrl;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public Result<Boolean,String> downLoadPdf(String dir,String filename,String url){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            int stateCode = response.getStatusLine().getStatusCode();
            if(stateCode>=200 && stateCode<=300){
                HttpEntity he = response.getEntity();
                InputStream is = he.getContent();
                FileOutputStream fs = new FileOutputStream(new File(dir+"/"+filename+".pdf"));
                byte[] buff = new byte[1024];
                int count = 0;
                while((count=is.read(buff))>0){
                    fs.write(buff,0,count);
                }
                fs.flush();
                fs.close();
                is.close();
                res.res = true;
                res.ct = "download success";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /*
    url (the html url)
    dir and filename (the save path)
     */
    public Result<Boolean,String> htmlToPdf(String url,String dir,String filename){
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "fail";
        Result<Boolean,String> urlGet = getPdf(url);
        if(urlGet.res == true){
            Result<Boolean,String> download = downLoadPdf(dir,filename,urlGet.ct);
            res.res = download.res;
            res.ct = download.ct;
        }
        return res;
    }

    public static void main(String[] args){
        WebHtmlToPdf hp = new WebHtmlToPdf();
        hp.htmlToPdf("http://www.baidu.com","I:/downPdf","jellytest");
    }
}
