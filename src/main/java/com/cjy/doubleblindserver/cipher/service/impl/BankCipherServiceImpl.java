package com.cjy.doubleblindserver.cipher.service.impl;

import com.cjy.doubleblindserver.cipher.entity.Coin.ECoin;
import com.cjy.doubleblindserver.cipher.entity.Coin.ZKPOK;
import com.cjy.doubleblindserver.cipher.entity.data.BankPubKey;
import com.cjy.doubleblindserver.cipher.entity.data.BankSecKey;
import com.cjy.doubleblindserver.cipher.entity.data.SystemData;
import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import com.cjy.doubleblindserver.cipher.tools.MD5;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:44
 */
@Service
public class BankCipherServiceImpl implements BankCipherService {


    @Override
    public Pairing generatePairing() {
        return  PairingFactory.getPairing("a.properties");
    }

    @Override
    public Pairing generatePairing(int rBits, int qBits) {
        TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
        PairingParameters pp = pg.generate();
        return PairingFactory.getPairing(pp);
    }

    @Override
    public SystemData generateSystemData(Pairing bp) {

        Field G1 = bp.getG1();
        Field Zr = bp.getZr();
        Field GT = bp.getGT();
        BigInteger r = bp.getGT().getOrder();
        Element g = G1.newRandomElement().getImmutable();
        BigInteger l = new BigInteger("1023");



        SystemData systemData = new SystemData();
        systemData.r = r;
        systemData.G1 = G1;
        systemData.Zr = Zr;
        systemData.GT = GT;
        systemData.l = l;
        systemData.g = g;
        systemData.bp = bp;
        return systemData;
    }



    @Override
    public BankSecKey generateBankSecKey(SystemData systemData) {
        BankSecKey bankSecKey = new BankSecKey();
        Field Zr = systemData.Zr;
        Element x = Zr.newRandomElement().getImmutable();
        Element y = Zr.newRandomElement().getImmutable();
        List<Element> z = new ArrayList<>();
        z.add(Zr.newRandomElement().getImmutable());
        z.add(Zr.newRandomElement().getImmutable());
        z.add(Zr.newRandomElement().getImmutable());
        bankSecKey.x = x;
        bankSecKey.y = y;
        bankSecKey.z = z;
        return bankSecKey;
    }

    @Override
    public BankPubKey generateBankPubKey(SystemData systemData, BankSecKey bankSecKey) {
        BankPubKey bankPubKey = new BankPubKey();
        Element g = systemData.g;
        Element x = bankSecKey.x;
        Element y = bankSecKey.y;
        List<Element> z = bankSecKey.z;
        Element X = g.powZn(x).getImmutable();
        Element Y = g.powZn(y).getImmutable();
        List<Element> Z = new ArrayList<>();
        for (int i = 0; i < z.size(); i++) {
            Z.add(g.powZn(z.get(i)).getImmutable());
        }
        bankPubKey.X = X;
        bankPubKey.Y = Y;
        bankPubKey.Z = Z;
        return bankPubKey;
    }

    @Override
    public boolean verifyWithdrawZK(ZKPOK proofOfWithdraw, SystemData systemData, BankPubKey bankPubKey) {
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        List<Element> s = new ArrayList<>();
        s.add(proofOfWithdraw.s.get(1));
        List<Element> base = new ArrayList<>();
        base.add(g);
        boolean zpk1 = verifyZKPOK(proofOfWithdraw.value.get(0),proofOfWithdraw.randomValue.get(0),s,base,proofOfWithdraw.c,systemData);
        base.clear();
        s.clear();
        s = proofOfWithdraw.s;
        base.add(g);
        for (int i = 0; i < Z.size(); i++) {
            base.add(Z.get(i));
        }
        System.out.println(proofOfWithdraw.value.get(1));
        System.out.println(proofOfWithdraw.randomValue.get(1));
        boolean zpk2 = verifyZKPOK(proofOfWithdraw.value.get(1),proofOfWithdraw.randomValue.get(1),s,base,proofOfWithdraw.c,systemData);

        if (!zpk1){
            System.out.println("公钥校验出错");
            return false;
        }
        if (!zpk2) {
            System.out.println("A校验出错");
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyZKPOK(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData) {
        int n = s.size();
        Field G1 = systemData.G1;
        Element sum = G1.newOneElement();
        System.out.println(value);
        System.out.println(randomValue);
        for (int i = 0; i < n; i++) {
            Element temp = base.get(i).powZn(s.get(i)).getImmutable();
            sum.mul(temp);
        }
        if (randomValue.isEqual(sum.mul(value.powZn(c)))) return true;
        System.out.println(value);
        System.out.println(randomValue);
        return false;
    }
    @Override
    public boolean verifyZKPOKGT(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData) {
        int n = s.size();
        Field GT = systemData.GT;
        Element sum = GT.newOneElement();
        for (int i = 0; i < n; i++) {
            Element temp = base.get(i).powZn(s.get(i)).getImmutable();
            sum.mul(temp);
        }
        sum.getImmutable();

        if (randomValue.isEqual(sum.mul(value.powZn(c)))) return true;
        return false;
    }
    @Override
    public Signature generateBankUserSignature(Element A, SystemData systemData, BankSecKey bankSecKey) {
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        Element x = bankSecKey.x;
        Element y = bankSecKey.y;
        List<Element> z = bankSecKey.z;
        Element alpha = Zr.newRandomElement().getImmutable();
        Element a = g.powZn(alpha).getImmutable();
        Element b = a.powZn(y);
        Element c = a.powZn(x).mul(A.powZn(alpha.mul(x).mul(y)));
        List<Element> Aa = new ArrayList<>();
        for (int i = 0; i < z.size(); i++) {
            Aa.add(a.powZn(z.get(i)).getImmutable());
        }
        List<Element> B = new ArrayList<>();
        for (int i = 0; i < Aa.size(); i++) {
            B.add(Aa.get(i).powZn(y).getImmutable());
        }
        Signature bankSignatureUser = new Signature();
        bankSignatureUser.a = a;
        bankSignatureUser.b = b;
        bankSignatureUser.c = c;
        bankSignatureUser.A = Aa;
        bankSignatureUser.B = B;
        return bankSignatureUser;

    }


    @Override
    public boolean verifySignZK(ZKPOK proofOfSign, SystemData systemData, BankPubKey bankPubKey) {
        List<Element> s = new ArrayList<>();
        s.add(proofOfSign.s.get(1));
        List<Element> base = new ArrayList<>();
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        base.add(g);
        boolean zpk1 = verifyZKPOK(proofOfSign.value.get(0),proofOfSign.randomValue.get(0),s,base,proofOfSign.c,systemData);
        base.clear();
        s.clear();
        s = proofOfSign.s;
        base.add(g);
        for (int i = 0; i < 1; i++) {
            base.add(Z.get(i));
        }
        boolean zpk2 = verifyZKPOK(proofOfSign.value.get(1),proofOfSign.randomValue.get(1),s,base,proofOfSign.c,systemData);
        if (!zpk2) {
            System.out.println("M校验出错");
            return false;
        }
        if (!zpk1){
            System.out.println("公钥校验出错");
            return false;
        }
        return true;
    }


    @Override
    public Signature generateBankMerchantSignature(Element M, SystemData systemData, BankSecKey bankSecKey) {
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        Element x = bankSecKey.x;
        Element y = bankSecKey.y;
        List<Element> z = bankSecKey.z;
        Element alpha = Zr.newRandomElement().getImmutable();
        Element a = g.powZn(alpha).getImmutable();
        Element b = a.powZn(y);
        Element c = a.powZn(x).mul(M.powZn(alpha.mul(x).mul(y)));
        List<Element> A = new ArrayList<>();
        A.add(a.powZn(z.get(0)).getImmutable());
        List<Element> B = new ArrayList<>();
        B.add(A.get(0).powZn(y).getImmutable());

        Signature bankSignatureMerchant = new Signature();
        bankSignatureMerchant.a = a;
        bankSignatureMerchant.b = b;
        bankSignatureMerchant.c = c;
        bankSignatureMerchant.A = A;
        bankSignatureMerchant.B = B;
        return bankSignatureMerchant;
    }


    @Override
    public boolean despoit(ECoin coin, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey) {
        Element S = coin.S;
        Element J = coin.J;
        Element R = coin.R;
        Element T = coin.T;
        ZKPOK zpk = coin.zkpok;
        Field Zr = systemData.Zr;
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        Element g = systemData.g;
        BigInteger l = systemData.l;
        List<Element> Z = bankPubKey.Z;
        if (coin.type == 1 && !J.isEqual(Zr.newZeroElement())) return false;
        System.out.println("第一步");
        Signature signature = coin.userSignature;
        String c = "";
        c = c + bankSignatureMerchant.a + bankSignatureMerchant.b + bankSignatureMerchant.c + bankSignatureMerchant.A.get(0) + bankSignatureMerchant.B.get(0);
        MD5 md5 = new MD5();
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c,16)).getImmutable();
        if(!numc.isEqual(R)) return false;
        System.out.println("第二步");
        Element v_x = bp.pairing(X,signature.a).getImmutable();
        Element v_xy = bp.pairing(X,signature.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < signature.B.size(); i++) {
            v_xyi.add(bp.pairing(X,signature.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,signature.c).getImmutable();
        List<Element> s = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            s.add(zpk.s.get(i));
        }
        List<Element> base = new ArrayList<>();
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        if (coin.type == 1) v_x = v_x.powZn(Zr.newElement(l)).getImmutable();
        Element vx_inv  = v_x.invert().getImmutable();
        if (!vx_inv.isEqual(zpk.value.get(0))) return false;
        boolean verifySignature = verifyZKPOKGT(zpk.value.get(0),zpk.randomValue.get(0),s,base,zpk.c,systemData);
        if (!verifySignature) return false;
        System.out.println("第三步");

        if (!verifyegg(signature.a, Y,g, signature.b, bp)) return false;
        System.out.println("第三步");
        for (int i = 0; i < Z.size(); i++) {
            if (!verifyegg(signature.a, Z.get(i),g,signature.A.get(i),bp)) return false;
            if (!verifyegg(signature.A.get(i), Y, g, signature.B.get(i),bp)) return false;
        }
        System.out.println("第三步");
        s.clear();
        base.clear();
        Element h = zpk.h;
        base.add(g);
        base.add(h);
        s.add(zpk.s.get(1));
        s.add(zpk.s.get(5));
        System.out.println("=========");
        boolean verifyOne = verifyZKPOK(zpk.value.get(1),zpk.randomValue.get(1),s,base,zpk.c,systemData);
        if (!verifyOne) return false;
        System.out.println("第四步");
        s.clear();
        base.clear();
        base.add(S.getImmutable());
        s.add(zpk.s.get(2));
        boolean verifyTwo = verifyZKPOK(zpk.value.get(2),zpk.randomValue.get(2),s,base,zpk.c,systemData);
        if (!verifyTwo) return false;
        System.out.println("第五步");
        if (!g.mul(S.powZn(J.add(Zr.newOneElement())).invert()).isEqual(zpk.value.get(2)))return false;
        System.out.println("第五步");
        s.clear();
        base.clear();
        Element A = zpk.value.get(1);
        base.add(A);
        base.add(g.invert().getImmutable());
        base.add(h);
        s.add(zpk.s.get(3));
        s.add(zpk.s.get(7));
        s.add(zpk.s.get(6));
        boolean verifyThree = verifyZKPOK(zpk.value.get(3),zpk.randomValue.get(3),s,base,zpk.c,systemData) && A.powZn(J.add(Zr.newOneElement())).invert().isEqual(zpk.value.get(3));
        if (!verifyThree) return false;
        System.out.println("第六步");
        s.clear();
        base.clear();
        s.add(zpk.s.get(3));
        s.add(zpk.s.get(7));
        base.add(T);
        base.add(g.invert().getImmutable());
        boolean verifyFour = verifyZKPOK(zpk.value.get(4),zpk.randomValue.get(4),s,base,zpk.c,systemData) && g.powZn(R).mul(T.powZn(J.add(Zr.newOneElement())).invert()).isEqual(zpk.value.get(4));
        if (!verifyFour) return false;
        System.out.println("第七步");
        return true;
    }

    @Override
    public boolean verifyegg(Element lefta, Element leftb, Element righta, Element rightb, Pairing bp) {
        Element left_egg = bp.pairing(lefta, leftb).getImmutable();
        Element right_egg = bp.pairing(righta, rightb).getImmutable();
        if (left_egg.isEqual(right_egg)) return true;
        else return false;
    }

    @Override
    public boolean transfer(ECoin coin, Signature bankSignatureMerchant, ZKPOK proofOfTransfer, SystemData systemData, BankPubKey bankPubKey) {
        //在transfer中检验的不是公钥，而是商家所收到的电子货币
        //需要构建全新的零知识证明协议
        //需要对于用户钱包、商家签名和电子货币进行验证
        //银行不生成随机数返回了
        boolean de = despoit(coin,bankSignatureMerchant,systemData,bankPubKey);
        boolean ve = verifyTransferZK(proofOfTransfer,bankSignatureMerchant,systemData,bankPubKey);
        return de && ve;
    }

    @Override
    public Signature generateBankMUSignature(Element Ma, SystemData systemData, BankSecKey bankSecKey) {
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        Element x = bankSecKey.x;
        Element y = bankSecKey.y;
        BigInteger l = systemData.l;
        Pairing bp = systemData.bp;
        List<Element> z = bankSecKey.z;
        Element alpha = Zr.newRandomElement().getImmutable();
        Element a = g.powZn(alpha).getImmutable();
        Element b = a.powZn(y).getImmutable();
        Element c = a.powZn(x.mul(Zr.newElement(l))).mul(Ma.powZn(alpha.mul(x).mul(y)));
        System.out.println("bbbbbbbbbbbb" + bp.pairing(g,c));
        List<Element> Aa = new ArrayList<>();
        for (int i = 0; i < z.size(); i++) {
            Aa.add(a.powZn(z.get(i)).getImmutable());
        }
        List<Element> B = new ArrayList<>();
        for (int i = 0; i < Aa.size(); i++) {
            B.add(Aa.get(i).powZn(y).getImmutable());
        }

        Signature bankSignatureMerchant = new Signature();
        bankSignatureMerchant.a = a;
        bankSignatureMerchant.b = b;
        bankSignatureMerchant.c = c;
        bankSignatureMerchant.A = Aa;
        bankSignatureMerchant.B = B;
        return bankSignatureMerchant;
    }

    @Override
    public Element identify(ECoin coin1, ECoin coin2) {
        Element T1 = coin1.T;
        Element T2 = coin2.T;
        Element R1 = coin1.R;
        Element R2 = coin2.R;
        Element TR21 = T2.powZn(R1).getImmutable();
        Element TR12 = T1.powZn(R2).getImmutable();
        Element R_inv = R1.sub(R2).getImmutable();
        Element Pku = TR21.div(TR12).powZn(R_inv);
        return Pku;
    }

    @Override
    public boolean verifyTransferZK(ZKPOK proofTransfer, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey) {
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        Element v_x = bp.pairing(X,bankSignatureMerchant.a).getImmutable();
        Element v_xy = bp.pairing(X,bankSignatureMerchant.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < bankSignatureMerchant.B.size(); i++) {
            v_xyi.add(bp.pairing(X,bankSignatureMerchant.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,bankSignatureMerchant.c).getImmutable();
        boolean verifyEqual = proofTransfer.value.get(0).invert().isEqual(v_x);
        if (!verifyEqual) return false;
        List<Element> s = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            s.add(proofTransfer.s.get(i));
        }
        List<Element> base = new ArrayList<>();
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        boolean zpk1 = verifyZKPOKGT(proofTransfer.value.get(0),proofTransfer.randomValue.get(0),s,base,proofTransfer.c,systemData);
        base.clear();
        s.clear();
        s.add(proofTransfer.s.get(3));
        s.add(proofTransfer.s.get(1));
        s.add(proofTransfer.s.get(4));
        s.add(proofTransfer.s.get(5));
        base.add(g);
        for (int i = 0; i < Z.size(); i++) {
            base.add(Z.get(i));
        }
        boolean zpk2 = verifyZKPOK(proofTransfer.value.get(1),proofTransfer.randomValue.get(1),s,base,proofTransfer.c,systemData);
        if (!zpk2) {
            System.out.println("A校验出错");
            return false;
        }
        if (!zpk1){
            System.out.println("公钥校验出错");
            return false;
        }
        return true;
    }

}
