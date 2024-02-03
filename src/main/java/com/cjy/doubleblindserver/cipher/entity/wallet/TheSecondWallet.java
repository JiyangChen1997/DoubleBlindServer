package com.cjy.doubleblindserver.cipher.entity.wallet;

import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:24
 */
public class TheSecondWallet {
    public Element secKey;
    public Element s;
    public Element t;
    public Element r;
    public Element J;
    public Element r2;
    public Signature sigma;
}
