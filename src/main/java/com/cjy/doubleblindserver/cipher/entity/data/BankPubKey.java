package com.cjy.doubleblindserver.cipher.entity.data;

import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 13:24
 */
public class BankPubKey { //银行的公钥
    public Element X;
    public Element Y;
    public List<Element> Z;
}
