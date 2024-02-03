package com.cjy.doubleblindserver.tool;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StrEleUtils {
    public static String elementToString(Element e) {
        return new String(e.toBytes(), StandardCharsets.ISO_8859_1);
    }

    public static Element stringToElement(String str, Field G) {
        return G.newElementFromBytes(str.getBytes(StandardCharsets.ISO_8859_1)).getImmutable();
    }
    public static List<String> ListEtoS(List<Element> listE) {
        List<String> listS = new ArrayList<>();
        for (Element e : listE) {
            listS.add(elementToString(e));
        }
        return listS;
    }
    public static List<Element> ListStoE(List<String> listS, Field G) {
        List<Element> listE = new ArrayList<>();
        for (String s:listS) {
            listE.add(stringToElement(s,G));
        }
        return listE;
    }
}
