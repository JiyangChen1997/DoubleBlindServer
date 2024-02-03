package com.cjy.doubleblindserver.cipher.entity.wallet;

import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:50
 */
public class Certificate {
    public Element secKey;
    public Element r;
    public Element rr;
    public Element r2;
    public Signature sigma;
}
