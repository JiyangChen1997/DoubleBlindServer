package com.cjy.doubleblindserver.cipher.entity.data;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.math.BigInteger;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:46
 */
public class SystemData {
    public BigInteger r; //需要用到的群的阶
    public Field Zr;
    public Field G1;     //运算所需要用到的群
    public Field GT;
    public Element g;    //生成元
    public BigInteger l; //用于判别钱包种类
    public Pairing bp;
}
