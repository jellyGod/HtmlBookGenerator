package com.jellyGod.htmlToPdf.base;

/**
 * Created by jelly on 2017/2/19.
 */
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MergePdf{

    //make a static method
    //just merge and generate all things
    public static Result<Boolean,String> merge(List<String> names,Map<String,String> files,String dest){
            Result<Boolean,String> res = new Result<Boolean,String>();
            res.res = false;
            res.ct = "fail";
            try {
                //read all pdfs into a map
                Map<String, PdfDocument> filesToMerge = new TreeMap<String, PdfDocument>();
                for (String name : names) {
                    filesToMerge.put(name, new PdfDocument(new PdfReader(files.get(name))));
                }

                //start the whole content
                PdfDocument destPdf = new PdfDocument(new PdfWriter(dest));
                destPdf.initializeOutlines();
                PdfOutline rootOutline = destPdf.getOutlines(false);
                Document doc = new Document(destPdf);

                //use size to generate the start of book
                int sizes =(names.size()-1) / 39+1;
                PdfDocument pdf = new PdfDocument(new PdfWriter("toc.pdf"));
                PageSize pagesize = PageSize.A4;
                Document document = new Document(pdf, pagesize);
                for (int i = 0; i < sizes; i++) {
                    pdf.addNewPage();
                }
                pdf.close();
                int pageNo = -1;
                PdfDocument toc = new PdfDocument(new PdfReader("toc.pdf"));
                pageNo = toc.getNumberOfPages();
                toc.copyPagesTo(1, toc.getNumberOfPages(), destPdf);
                toc.close();
                //add outlines
                Map<String, Integer> indexs = new HashMap<String, Integer>();
                int n;
                for (String name : names) {
                    PdfDocument temp = filesToMerge.get(name);
                    n = temp.getNumberOfPages();
                    indexs.put(name, pageNo + 1);
                    for (int i = 1; i <= n; i++) {
                        pageNo++;
                        temp.copyPagesTo(i, i, destPdf);
                        Text text = new Text(String.format("Page %d", pageNo));
                        if (i == 1) {
                            text.setDestination("p" + pageNo);
                            PdfOutline outline = rootOutline.addOutline(name);
                            outline.addDestination(PdfDestination.makeDestination(new PdfString("p" + pageNo)));
                        }
                        doc.add(new Paragraph(text).setFixedPosition(pageNo, 549, 810, 40));
                    }
                }
                //add table of content
                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i);
                    int rePage = indexs.get(name);
                    //add to toc page
                    //init a paragraph and align action
                    Paragraph p = new Paragraph();
                    p.addTabStops(new TabStop(500, TabAlignment.LEFT, new DashedLine()));
                    p.add(name);
                    p.add(new Tab());
                    p.add(String.valueOf(rePage));
                    p.setAction(PdfAction.createGoTo("p" + new Integer(rePage)));
                    //there are variable
                    //page x,y,width
                    int page = i / 39 + 1;
                    int y = 770 - i % 39 * 20;
                    doc.add(p.setFixedPosition(page, 36, y, 595 - 72));
                }

                for (PdfDocument srcDoc : filesToMerge.values()) {
                    srcDoc.close();
                }
                doc.close();
                res.res = true;
                res.ct = "success";
            }
            catch(Exception e){
                e.printStackTrace();
                res.res = false;
                res.ct = "fail";
            }
     return res;
    }

    public static void main(String[] args){
            //init some value
            //add some names
            List<String> names = new ArrayList<String>();
            //for(int i=0;i<5;i++)
            names.add("Introduction to Distributed Operating System");
            names.add("What is a distributed operating system");
            //add names and url
            Map<String,String> files = new HashMap<String,String>();
            //for(int i=0;i<5;i++)
            files.put("Introduction to Distributed Operating System","I:/Introduction.pdf");
            files.put("What is a distributed operating system","I:/What.pdf");
            Result<Boolean,String> res = MergePdf.merge(names,files,"dest1.pdf");
            if(res.res == true)
            {
                System.out.println("Yes i merge success!");
            }
    }
}
