package com.jellyGod.htmlToPdf.base;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.page.PageType;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;

import javax.swing.text.html.HTML;

/**
 * Created by jelly on 2017/2/21.
 */
public class LocalHtmlToPdf implements HtmlToPdf{

    public Result<Boolean, String> htmlToPdf(String url, String dir, String filename) {
        Result<Boolean,String> res = new Result<Boolean,String>();
        res.res = false;
        res.ct = "fail";
        try {
            Pdf pdf = new Pdf();
            pdf.addPage(url, PageType.url);
            pdf.addParam(new Param("--enable-javascript"));
            pdf.saveAs(dir + filename + ".pdf");
            res.res = true;
            res.ct = "success";
        }
        catch(Exception e){
            res.res = false;
            res.ct = "fail";
            e.printStackTrace();
        }
        return res;
    }

    public static void main(String[] args){
        LocalHtmlToPdf lp = new LocalHtmlToPdf();
        lp.htmlToPdf("http://www.bilibili.com","I:/downPdf/","wktest");
    }
}
