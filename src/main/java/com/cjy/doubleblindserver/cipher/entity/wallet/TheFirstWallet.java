package com.cjy.doubleblindserver.cipher.entity.wallet;

import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:23
 */
public class TheFirstWallet {
    public Element secKey;
    public Element s;
    public Element t;
    public Element J;
    public Element r;
    public Signature sigma;
    public Element rr;
    public Element r2;
}
