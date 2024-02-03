package com.cjy.doubleblindserver.cipher.entity.Coin;

import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:41
 */
public class ZKPOK {
    public List<Element> randomValue;
    public List<Element> value;
    public List<Element> s;
    public Element c;
    public Element h;
}
