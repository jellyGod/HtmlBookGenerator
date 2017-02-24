package com.jellyGod.htmlToPdf.example;

import com.jellyGod.htmlToPdf.base.Utils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * Created by jelly on 2017/2/18.
 */
public class GetUrlAndScratch {

    public static void main(String[] args) throws Exception{
       Parser ps = new Parser("Temp.txt");
        NodeFilter nf = new TagNameFilter("A");
        NodeList list = ps.extractAllNodesThatMatch(nf);
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("UrlFile.txt")));
        for(Node temp:list.toNodeArray()){
            LinkTag lt = (LinkTag)temp;
            System.out.println(lt.getLink()+" "+lt.getLinkText());
            bw.write(lt.getLinkText()+"----"+lt.getLink()+"\n");
        }
        bw.flush();
        bw.close();
    }
}
