package com.cjy.doubleblindserver.cipher.entity.data;

import it.unisa.dia.gas.jpbc.Element;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:59
 */
public class BankSecKey {
   //银行私钥，用于完成对于商家和用户的CL签名
    public Element x;
    public Element y;
    public List<Element> z;
}
