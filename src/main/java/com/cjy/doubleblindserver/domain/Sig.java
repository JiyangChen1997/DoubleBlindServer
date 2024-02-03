package com.cjy.doubleblindserver.domain;

import com.cjy.doubleblindserver.cipher.entity.Dto.SigDto;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/12/11 23:52
 */
public class Sig {

    private long id;

    private long coinid;

    private String a;

    private String b;

    private String c;

    private String A1;

    private String A2;

    private String A3;

    private String B1;

    private String B2;

    private String B3;

    public Sig() {

    }
    public Sig(SigDto sigDto, int coinid) {
        this.setA(sigDto.a);
        this.setB(sigDto.b);
        this.setC(sigDto.c);
        this.setA1(sigDto.A.get(0));
        this.setA2(sigDto.A.get(1));
        this.setA3(sigDto.A.get(2));
        this.setB1(sigDto.B.get(0));
        this.setB2(sigDto.B.get(1));
        this.setB3(sigDto.B.get(2));
        this.setCoinid(coinid);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCoinid() {
        return coinid;
    }

    public void setCoinid(long coinid) {
        this.coinid = coinid;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getA1() {
        return A1;
    }

    public void setA1(String a1) {
        A1 = a1;
    }

    public String getA2() {
        return A2;
    }

    public void setA2(String a2) {
        A2 = a2;
    }

    public String getA3() {
        return A3;
    }

    public void setA3(String a3) {
        A3 = a3;
    }

    public String getB1() {
        return B1;
    }

    public void setB1(String b1) {
        B1 = b1;
    }

    public String getB2() {
        return B2;
    }

    public void setB2(String b2) {
        B2 = b2;
    }

    public String getB3() {
        return B3;
    }

    public void setB3(String b3) {
        B3 = b3;
    }
}
