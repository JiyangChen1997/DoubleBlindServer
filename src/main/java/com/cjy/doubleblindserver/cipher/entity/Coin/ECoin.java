package com.cjy.doubleblindserver.cipher.entity.Coin;

import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import it.unisa.dia.gas.jpbc.Element;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:34
 */
public class ECoin {
    public Signature userSignature;
    public Element S;
    public Element R;
    public Element T;
    public ZKPOK zkpok;
    public Element J;
    public int type;
}
