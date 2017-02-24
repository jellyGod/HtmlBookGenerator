package com.jellyGod.htmlToPdf.base;

/**
 * Created by jelly on 2017/2/21.
 */
public interface HtmlToPdf {

    public Result<Boolean,String>  htmlToPdf(String url,String dir,String filename);
}
