package com.cjy.doubleblindserver.cipher.service.impl;

import com.cjy.doubleblindserver.cipher.entity.Coin.ECoin;
import com.cjy.doubleblindserver.cipher.entity.Coin.ProofGuilt;
import com.cjy.doubleblindserver.cipher.entity.Coin.ZKPOK;
import com.cjy.doubleblindserver.cipher.entity.data.BankPubKey;
import com.cjy.doubleblindserver.cipher.entity.data.SystemData;
import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import com.cjy.doubleblindserver.cipher.entity.wallet.Certificate;
import com.cjy.doubleblindserver.cipher.entity.wallet.TheFirstWallet;
import com.cjy.doubleblindserver.cipher.entity.wallet.TheSecondWallet;
import com.cjy.doubleblindserver.cipher.tools.MD5;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/11 14:43
 */
public class UserCipherServiceImpl implements UserCipherService {
//    @Override
//    public void getSystemData(SystemData systemData, BankPubKey bankPubKey) {
//         this.systemData = systemData;
//         this.bp = systemData.bp;
//         this.Zr = systemData.Zr;
//         this.G1 = systemData.G1;
//         this.GT = systemData.GT;
//         this.g = systemData.g;
//         this.X = bankPubKey.X;
//         this.Y = bankPubKey.Y;
//         this.Z = bankPubKey.Z;
//         this.l = systemData.l;
//         this.bankPubKey = bankPubKey;
//    }

    @Override
    public Element generateUserSecKey(SystemData systemData) {
        Element userSecKey = systemData.Zr.newRandomElement().getImmutable();
        return userSecKey;
    }

    @Override
    public Element generateUserPubKey(SystemData systemData, Element userSecKey) {
        Element userPbuKey = systemData.g.powZn(userSecKey).getImmutable();
        return userPbuKey;
    }

    @Override
    public Element calculateExp(List<Element> base, List<Element> exp, SystemData systemData) {
        if (base.size() != exp.size()) System.out.println("长度不同，不能运算");
        Field G1 = systemData.G1;
        Element sum = G1.newOneElement();
        for (int i = 0; i < base.size(); i++) {
            Element temp = base.get(i).powZn(exp.get(i));
            sum.mul(temp);
        }
        return sum.getImmutable();
    }
    @Override
    public Element calculateExpGT(List<Element> base, List<Element> exp, SystemData systemData) {
        if (base.size() != exp.size()) System.out.println("长度不同，不能运算");
        Field GT = systemData.GT;
        Element sum = GT.newOneElement();
        for (int i = 0; i < base.size(); i++) {
            Element temp = base.get(i).powZn(exp.get(i));
            sum.mul(temp);
        }

        return sum.getImmutable();
    }
    @Override
    public ZKPOK generateWithdrawProof(Element A_, Element r, Element userSecKey, Element s_, Element t, Element userPbuKey, SystemData systemData, BankPubKey bankPubKey) {
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        ZKPOK proofOfWithdraw = new ZKPOK();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        base.add(g);
        base.addAll(Z);
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> R = new ArrayList<>();
        List<Element> s = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            R.add(Zr.newRandomElement().getImmutable());
        }
        exp.add(r);
        exp.add(userSecKey);
        exp.add(s_);
        exp.add(t);
        Element A_r = calculateExp(base,R,systemData);
        Element Pku_r  = g.powZn(R.get(1)).getImmutable();
        value.add(userPbuKey.getImmutable());
        value.add(A_.getImmutable());
        randomValue.add(Pku_r);
        randomValue.add(A_r);
        String c = "";
        MD5 md5 = new MD5();
        c = c + userPbuKey.toString() + A_ + Pku_r + A_r;
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c,16)).getImmutable();
        for (int i = 0; i < R.size(); i++) {
            s.add(R.get(i).sub(exp.get(i).mul(numc)).getImmutable());
        }
        proofOfWithdraw.randomValue = randomValue;
        proofOfWithdraw.value = value;
        proofOfWithdraw.s = s;
        proofOfWithdraw.c = numc;
        return proofOfWithdraw;
    }
    @Override
    public boolean verifyReturnedSignature(Signature bankSignatureUser, Element A, Element r, Element userSecKey, Element s, Element t, SystemData systemData, BankPubKey bankPubKey) {
        List<Element> secList = new ArrayList<>();
        secList.add(r);
        secList.add(userSecKey);
        secList.add(s);
        secList.add(t);
        if (!verifyA(A,secList,systemData,bankPubKey)) {
            System.out.println("A验证失败");
            return false;
        }
        if (!verifySignature(bankSignatureUser,secList,systemData,bankPubKey)) {
            System.out.println("银行签名验证失败");
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyA(Element A, List<Element> secList, SystemData systemData, BankPubKey bankPubKey) {
        Field G1 = systemData.G1;
        List<Element> Z = bankPubKey.Z;
        Element g = systemData.g;
        Element A_ = G1.newOneElement();
        A_.mul(g.powZn(secList.get(0))).mul(Z.get(0).powZn(secList.get(1))).mul(Z.get(1).powZn(secList.get(2))).mul(Z.get(2).powZn(secList.get(3)));
        return A.isEqual(A_);
    }

    @Override
    public boolean verifySignature(Signature bankSignature, List<Element> secList, SystemData systemData, BankPubKey bankPubKey) {
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Field GT = systemData.GT;
        Element a = bankSignature.a;
        Element b = bankSignature.b;
        Element c = bankSignature.c;
        List<Element> A = bankSignature.A;
        List<Element> B = bankSignature.B;
        if (!verifyegg(a,Y,g,b,bp)) return false;
        for (int i = 0; i < B.size(); i++) {
            if (!verifyegg(a,Z.get(i),g,A.get(i),bp)) return false;
            if (!verifyegg(A.get(i),Y,g,B.get(i),bp)) return false;
        }
        Element e_Xa = bp.pairing(X,a).getImmutable();
        Element e_Xbm = bp.pairing(X,b).powZn(secList.get(0)).getImmutable();
        List<Element> e_XB = new ArrayList<>();
        for (int i = 0; i < B.size(); i++) {
            e_XB.add(bp.pairing(X,B.get(i)).powZn(secList.get(i+1)).getImmutable());
        }
        Element e_gc  = bp.pairing(g,c);
        Element sum = GT.newOneElement();
        for (int i = 0; i < B.size(); i++) {
            sum.mul(e_XB.get(i));
        }
        sum.mul(e_Xa).mul(e_Xbm).getImmutable();
        if (!e_gc.isEqual(sum)) return false;
        return true;
    }

    @Override
    public boolean verifyMerchantCertifate(ZKPOK proofOfMerchantCertifate, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey) {
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Field GT = systemData.GT;
        Element a = bankSignatureMerchant.a;
        Element b = bankSignatureMerchant.b;
        Element c = bankSignatureMerchant.c;
        List<Element> A = bankSignatureMerchant.A;
        List<Element> B = bankSignatureMerchant.B;
        if (!verifyegg(a,Y,g,b,bp)) return false;
        for (int i = 0; i < B.size(); i++) {
            if (!verifyegg(a,Z.get(i),g,A.get(i),bp)) return false;
            if (!verifyegg(A.get(i),Y,g,B.get(i),bp)) return false;
        }
        Element v_x = bp.pairing(X,a).getImmutable();
        Element v_xy = bp.pairing(X,b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < B.size(); i++) {
            v_xyi.add(bp.pairing(X,B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,c).getImmutable();
        boolean verifyEqual = proofOfMerchantCertifate.value.get(0).invert().isEqual(v_x);
        if (!verifyEqual) return false;
        List<Element> s = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            s.add(proofOfMerchantCertifate.s.get(i));
        }
        List<Element> base = new ArrayList<>();
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        boolean zpk1 = verifyZKPOKGT(proofOfMerchantCertifate.value.get(0),proofOfMerchantCertifate.randomValue.get(0),s,base,proofOfMerchantCertifate.c,systemData);
        return zpk1;
    }
    @Override
    public boolean verifyZKPOK(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData) {
        Field G1 = systemData.G1;
        int n = s.size();
        Element sum = G1.newOneElement();
        for (int i = 0; i < n; i++) {
            Element temp = base.get(i).powZn(s.get(i)).getImmutable();
            sum.mul(temp);
        }
        sum.getImmutable();
        if (randomValue.isEqual(sum.mul(value.powZn(c)))) return true;
        return false;
    }
    @Override
    public boolean verifyZKPOKGT(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData) {
        Field GT = systemData.GT;
        int n = s.size();
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
    public Signature generateAnnoymousSignature(Signature signature, Element r, Element r2){
        Element a = signature.a;
        Element b = signature.b;
        Element c = signature.c;
        List<Element> A = signature.A;
        List<Element> B = signature.B;
        a = a.powZn(r).getImmutable();
        b = b.powZn(r).getImmutable();
        c = c.powZn(r).powZn(r2).getImmutable();
        for (int i = 0; i < A.size(); i++) {
            A.set(i,A.get(i).powZn(r).getImmutable());
            B.set(i,B.get(i).powZn(r).getImmutable());
        }
        Signature newSignature = new Signature();
        newSignature.a = a;
        newSignature.b = b;
        newSignature.c = c;
        newSignature.A = A;
        newSignature.B = B;
        return newSignature;
    }

    @Override
    public ECoin generateCoin(Signature MerchantSignature, Element userPbuKey, TheFirstWallet theFirstWallet, SystemData systemData, BankPubKey bankPubKey) {
        String c = "";
        Field G1 = systemData.G1;
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element J = theFirstWallet.J.getImmutable();
        Element r = theFirstWallet.r;
        Element s = theFirstWallet.s;
        Element t = theFirstWallet.t;
        Element userSecKey = theFirstWallet.secKey;
        Element h = G1.newRandomElement().getImmutable();
        Element rA = Zr.newRandomElement().getImmutable();
        Element rB = J.add(t).add(Zr.newOneElement()).mul(rA).negate().getImmutable();
        Element lam = userSecKey.mul(J.add(Zr.newOneElement().add(t))).getImmutable();
        Signature signature = theFirstWallet.sigma;
        c = c + MerchantSignature.a + MerchantSignature.b + MerchantSignature.c + MerchantSignature.A.get(0) + MerchantSignature.B.get(0);
        MD5 md5 = new MD5();
        c = md5.start(c);
        c=c.substring(0,16);
        Element R = Zr.newElement(new BigInteger(c,16)).getImmutable();
        Element S = g.powZn(J.add(s).add(Zr.newOneElement()).invert()).getImmutable();
        Element T = userPbuKey.mul(g.powZn(R.mul(J.add(t).add(Zr.newOneElement()).invert()))).getImmutable();

        Element a = signature.a;
        Element b = signature.b;
        Element cc = signature.c;
        List<Element> A = signature.A;
        List<Element> B = signature.B;
        Element v_x = bp.pairing(X,signature.a).getImmutable();
        Element v_xy = bp.pairing(X,signature.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < signature.B.size(); i++) {
            v_xyi.add(bp.pairing(X,signature.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,signature.c).getImmutable();
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> Rr = new ArrayList<>();
        List<Element> Ss = new ArrayList<>();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        List<Element> sec = new ArrayList<>();
        Element r2 = theFirstWallet.r2.invert().negate();
        sec.add(r);
        sec.add(userSecKey);
        sec.add(s);
        sec.add(t);
        sec.add(r2);
        sec.add(rA);
        sec.add(rB);
        sec.add(lam);
        for (int i = 0; i < 8; i++) {
            Rr.add(Zr.newRandomElement().getImmutable());
        }
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        for (int i = 0; i < 5; i++) {
            exp.add(Rr.get(i));
        }
        Element valueOne_r = calculateExpGT(base,exp,systemData);
        exp.clear();
        exp.add(r);
        exp.add(userSecKey);
        exp.add(s);
        exp.add(t);
        exp.add(r2);
        Element valueOne = calculateExpGT(base,exp,systemData);
        base.clear();
        exp.clear();
        base.add(g);
        base.add(h);
        exp.add(Rr.get(1));
        exp.add(Rr.get(5));
        Element valueTwo_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(userSecKey);
        exp.add(rA);
        Element valuetwo = calculateExp(base,exp,systemData);

        base.clear();
        exp.clear();
        base.add(S.getImmutable());
        exp.add(Rr.get(2));
        Element valueThree_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(s);
        Element valueThree = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        base.add(valuetwo.getImmutable());
        base.add(g.invert().getImmutable());
        base.add(h);
        exp.add(Rr.get(3));
        exp.add(Rr.get(7));
        exp.add(Rr.get(6));
        Element valueFour_r = calculateExp(base, exp,systemData);
        exp.clear();
        exp.add(t);
        exp.add(lam);
        exp.add(rB);
        Element valueFour = calculateExp(base, exp,systemData);
        exp.clear();
        base.clear();
        base.add(T.getImmutable());
        base.add(g.invert().getImmutable());
        exp.add(Rr.get(3));
        exp.add(Rr.get(7));
        Element valueFive_r = calculateExp(base, exp,systemData);
        exp.clear();
        exp.add(t);
        exp.add(lam);
        Element valueFive = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        value.add(valueOne.getImmutable());
        value.add(valuetwo.getImmutable());
        value.add(valueThree.getImmutable());
        value.add(valueFour.getImmutable());
        value.add(valueFive.getImmutable());
        randomValue.add(valueOne_r.getImmutable());
        randomValue.add(valueTwo_r.getImmutable());
        randomValue.add(valueThree_r.getImmutable());
        randomValue.add(valueFour_r.getImmutable());
        randomValue.add(valueFive_r.getImmutable());
        for (int i = 0; i < value.size(); i++) {
            c += value.get(i).toString();
        }
        for (int i = 0; i < randomValue.size(); i++) {
            c += randomValue.get(i).toString();
        }
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c, 16)).getImmutable();
        for (int i = 0; i < sec.size(); i++) {
            Ss.add(Rr.get(i).sub(numc.mul(sec.get(i))).getImmutable());
        }
        ZKPOK zkpok = new ZKPOK();
        zkpok.s = Ss;
        zkpok.c = numc;
        zkpok.value = value;
        zkpok.randomValue = randomValue;
        zkpok.h = h;
        ECoin coin = new ECoin();
        coin.type = 0;
        coin.zkpok = zkpok;
        coin.J = J;
        coin.R = R;
        coin.T = T;
        coin.userSignature = signature;
        coin.S = S;
        return coin;
    }

    @Override
    public ECoin generateCoin(Signature MerchantSignature, Element userPbuKey, TheSecondWallet theSecondWallet, SystemData systemData, BankPubKey bankPubKey) {
        String c = "";
        Field G1 = systemData.G1;
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element J = theSecondWallet.J.getImmutable();
        Element r = theSecondWallet.r;
        Element s = theSecondWallet.s;
        Element t = theSecondWallet.t;
        Element userSecKey = theSecondWallet.secKey;
        Element h = G1.newRandomElement().getImmutable();
        Element rA = Zr.newRandomElement().getImmutable();
        Element rB = J.add(t).add(Zr.newOneElement()).mul(rA).negate().getImmutable();
        Element lam = userSecKey.mul(J.add(Zr.newOneElement().add(t))).getImmutable();
        Signature signature = theSecondWallet.sigma;
        c = c + MerchantSignature.a + MerchantSignature.b + MerchantSignature.c + MerchantSignature.A.get(0) + MerchantSignature.B.get(0);
        MD5 md5 = new MD5();
        Element R = Zr.newElement(new BigInteger(c,16)).getImmutable();
        Element S = g.powZn(J.add(s).add(Zr.newOneElement()).invert()).getImmutable();
        Element T = userPbuKey.mul(g.powZn(R.mul(J.add(t).add(Zr.newOneElement()).invert()))).getImmutable();

        Element a = signature.a;
        Element b = signature.b;
        Element cc = signature.c;
        List<Element> A = signature.A;
        List<Element> B = signature.B;
        Element v_x = bp.pairing(X,signature.a).getImmutable();
        Element v_xy = bp.pairing(X,signature.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < signature.B.size(); i++) {
            v_xyi.add(bp.pairing(X,signature.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,signature.c).getImmutable();
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> Rr = new ArrayList<>();
        List<Element> Ss = new ArrayList<>();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        List<Element> sec = new ArrayList<>();
        Element r2 = theSecondWallet.r2.invert().negate();
        sec.add(r);
        sec.add(userSecKey);
        sec.add(s);
        sec.add(t);
        sec.add(r2);
        sec.add(rA);
        sec.add(rB);
        sec.add(lam);
        for (int i = 0; i < 8; i++) {
            Rr.add(Zr.newRandomElement().getImmutable());
        }
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        for (int i = 0; i < 5; i++) {
            exp.add(Rr.get(i));
        }
        Element valueOne_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(r);
        exp.add(userSecKey);
        exp.add(s);
        exp.add(t);
        exp.add(r2);
        Element valueOne = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        base.add(g);
        base.add(h);
        exp.add(Rr.get(1));
        exp.add(Rr.get(5));
        Element valueTwo_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(userSecKey);
        exp.add(rA);
        Element valuetwo = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        base.add(S);
        exp.add(Rr.get(2));
        Element valueThree_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(s);
        Element valueThree = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        base.add(valuetwo);
        base.add(g.invert().getImmutable());
        base.add(h);
        exp.add(Rr.get(3));
        exp.add(Rr.get(7));
        exp.add(Rr.get(6));
        Element valueFour_r = calculateExp(base, exp,systemData);
        exp.clear();
        exp.add(t);
        exp.add(lam);
        exp.add(rB);
        Element valueFour = calculateExp(base, exp,systemData);
        exp.clear();
        base.clear();
        base.add(T);
        base.add(g.invert().getImmutable());
        exp.add(Rr.get(3));
        exp.add(Rr.get(7));
        Element valueFive_r = calculateExp(base, exp,systemData);
        exp.clear();
        exp.add(t);
        exp.add(lam);
        Element valueFive = calculateExp(base,exp,systemData);
        base.clear();
        exp.clear();
        value.add(valueOne);
        value.add(valuetwo);
        value.add(valueThree);
        value.add(valueFour);
        value.add(valueFive);
        randomValue.add(valueOne_r);
        randomValue.add(valueTwo_r);
        randomValue.add(valueThree_r);
        randomValue.add(valueFour_r);
        randomValue.add(valueFive_r);
        for (int i = 0; i < value.size(); i++) {
            c += value.get(i).toString();
        }
        for (int i = 0; i < randomValue.size(); i++) {
            c += randomValue.get(i).toString();
        }
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c, 16)).getImmutable();
        for (int i = 0; i < sec.size(); i++) {
            Ss.add(Rr.get(i).sub(numc.mul(sec.get(i))).getImmutable());
        }
        ZKPOK zkpok = new ZKPOK();
        zkpok.s = Ss;
        zkpok.c = numc;
        zkpok.value = value;
        zkpok.randomValue = randomValue;
        zkpok.h = h;
        ECoin coin = new ECoin();
        coin.type = 0;
        coin.zkpok = zkpok;
        coin.J = J;
        coin.R = R;
        coin.T = T;
        coin.userSignature = signature;
        coin.S = S;
        return coin;
    }


    @Override
    public ZKPOK generateSpendProofOfUser() {
        return null;
    }


    @Override
    public boolean verifyegg(Element lefta, Element leftb, Element righta, Element rightb, Pairing bp) {
        Element left_egg = bp.pairing(lefta, leftb).getImmutable();
        Element right_egg = bp.pairing(righta, rightb).getImmutable();
        if (left_egg.isEqual(right_egg)) return true;
        else return false;
    }



    @Override
    public boolean verifyGuilt(ProofGuilt proofGuilt) {
        ECoin coin1 = proofGuilt.coin1;
        ECoin coin2 = proofGuilt.coin2;;
        Element S = coin1.S;
        Element Pku_Guilt = proofGuilt.PubKey;
        Element Pku = identify(coin1, coin2);
        return Pku.isEqual(Pku_Guilt);

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
    public ZKPOK generateSignProof(Element rm, SystemData systemData, BankPubKey bankPubKey, Element userSecKey, Element userPbuKey) {
        Field Zr = systemData.Zr;
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        ZKPOK proofOfSign = new ZKPOK();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> R = new ArrayList<>();
        List<Element> s = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            R.add(Zr.newRandomElement().getImmutable());
        }
        base.add(g);
        base.add(Z.get(0));
        exp.add(rm);
        exp.add(userSecKey);
        Element M = calculateExp(base,exp,systemData);

        Element M_r = calculateExp(base,R,systemData);
        Element Pku_r  = g.powZn(R.get(1)).getImmutable();
        value.add(userPbuKey);
        value.add(M.getImmutable());
        randomValue.add(Pku_r);
        randomValue.add(M_r);
        String c = "";
        MD5 md5 = new MD5();
        c = c + userPbuKey.toString() + M + Pku_r + M_r;
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c,16)).getImmutable();
        for (int i = 0; i < R.size(); i++) {
            s.add(R.get(i).sub(exp.get(i).mul(numc)).getImmutable());
        }
        proofOfSign.randomValue = randomValue;
        proofOfSign.value = value;
        proofOfSign.s = s;
        proofOfSign.c = numc;
        return proofOfSign;
    }


    @Override
    public boolean verifyReturned(Signature bankSignatureMerchant, Element M, Element rm, Element userSecKey, SystemData systemData, BankPubKey bankPubKey) {
//        List<Element> exp = new ArrayList<>();
//        List<Element> base = new ArrayList<>();
//        base.add(g);
//        base.addAll(Z);
//        exp.add(r);
//        exp.add(userSecKey);
//        exp.add(s);
//        exp.add(t);
//        A = calculateExp(base, exp);
        List<Element> secList = new ArrayList<>();
        secList.add(rm);
        secList.add(userSecKey);
        if (!verifyM(M,secList,systemData,bankPubKey)) {
            System.out.println("M验证失败");
            return false;
        }
        if (!verifySignature(bankSignatureMerchant,secList,systemData,bankPubKey)){
            System.out.println("商家验证银行签名失败");
            return false;
        }
        return true;
    }
    @Override
    public boolean verifyM(Element M, List<Element> secList, SystemData systemData, BankPubKey bankPubKey) {
        Field G1 = systemData.G1;
        Element g = systemData.g;
        List<Element> Z = bankPubKey.Z;
        Element M_ = G1.newOneElement();
        M_.mul(g.powZn(secList.get(0))).mul(Z.get(0).powZn(secList.get(1)));
        return M.isEqual(M_);
    }

    @Override
    public ZKPOK generateCertifateProof(Certificate certificate, SystemData systemData, BankPubKey bankPubKey){
        Signature signature = certificate.sigma;
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Field Zr = systemData.Zr;
        Field GT = systemData.GT;
        Element a = signature.a;
        Element b = signature.b;
        Element cc = signature.c;
        List<Element> A = signature.A;
        List<Element> B = signature.B;
        Element v_x = bp.pairing(X,signature.a).getImmutable();
        Element v_xy = bp.pairing(X,signature.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < signature.B.size(); i++) {
            v_xyi.add(bp.pairing(X,signature.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,signature.c).getImmutable();
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> R = new ArrayList<>();
        List<Element> S = new ArrayList<>();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        List<Element> sec = new ArrayList<>();
        Element r = certificate.r;
        Element u = certificate.secKey;
        Element r2 = certificate.r2.invert().negate();
        sec.add(r);
        sec.add(u);
        sec.add(r2);
        for (int i = 0; i < 3; i++) {
            R.add(Zr.newRandomElement().getImmutable());
        }
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        for (int i = 0; i < 3; i++) {
            exp.add(R.get(i));
        }
        Element value_r = calculateExpGT(base,exp,systemData);
        exp.clear();
        exp.add(r);
        exp.add(u);
        exp.add(r2);
        Element valuee = calculateExpGT(base,exp,systemData);
        String c = "";
        c = c + valuee + value_r;
        MD5 md5 = new MD5();
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c, 16)).getImmutable();
        for (int i = 0; i < sec.size(); i++) {
            S.add(R.get(i).sub(numc.mul(sec.get(i))).getImmutable());
        }
        value.add(valuee.getImmutable());
        randomValue.add(value_r.getImmutable());
        ZKPOK zkpok = new ZKPOK();
        zkpok.s = S;
        zkpok.c = numc;
        zkpok.value = value;
        zkpok.randomValue = randomValue;
        return zkpok;
    }

    @Override
    public boolean verifyUserCoin(ECoin coin, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey) {
        Field Zr = systemData.Zr;
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element g = systemData.g;
        BigInteger l = systemData.l;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;

        Element S = coin.S;
        Element J = coin.J;
        Element R = coin.R;
        Element T = coin.T;
        ZKPOK zpk = coin.zkpok;
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
    public boolean deposit(ECoin coin, Signature bankSignatureMerchant) {
        return false;
    }


    @Override
    public ZKPOK generateTransferProof(ECoin coin, Signature bankSignatureMerchant, Element rm, Element userSecKey, Element r2, Element rmu, Element s_, Element t, SystemData systemData, BankPubKey bankPubKey) {
        ZKPOK proofOfTransfer = new ZKPOK();
        Field Zr = systemData.Zr;
        Pairing bp = systemData.bp;
        Element X = bankPubKey.X;
        Element g = systemData.g;
        BigInteger l = systemData.l;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;
        Element rr = r2.invert().negate().getImmutable();
        Element a = bankSignatureMerchant.a;
        Element b = bankSignatureMerchant.b;
        Element cc = bankSignatureMerchant.c;
        List<Element> A = bankSignatureMerchant.A;
        List<Element> B = bankSignatureMerchant.B;
        Element v_x = bp.pairing(X,bankSignatureMerchant.a).getImmutable();
        Element v_xy = bp.pairing(X,bankSignatureMerchant.b).getImmutable();
        List<Element> v_xyi = new ArrayList<>();
        for (int i = 0; i < bankSignatureMerchant.B.size(); i++) {
            v_xyi.add(bp.pairing(X,bankSignatureMerchant.B.get(i)).getImmutable());
        }
        Element v_s = bp.pairing(g,bankSignatureMerchant.c).getImmutable();
        List<Element> exp = new ArrayList<>();
        List<Element> base = new ArrayList<>();
        List<Element> value = new ArrayList<>();
        List<Element> randomValue = new ArrayList<>();
        List<Element> R = new ArrayList<>();
        List<Element> Ss = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            R.add(Zr.newRandomElement().getImmutable());
        }
        base.add(v_xy);
        base.addAll(v_xyi);
        base.add(v_s);
        for (int i = 0; i < 3; i++) {
            exp.add(R.get(i));
        }
        Element valueOne_r = calculateExpGT(base,exp,systemData);
        exp.clear();
        exp.add(rm);
        exp.add(userSecKey);
        exp.add(rr);
        Element valueOne = calculateExpGT(base,exp,systemData);

        base.clear();
        exp.clear();
        base.add(g);
        base.addAll(Z);
        exp.add(R.get(3));
        exp.add(R.get(1));
        exp.add(R.get(4));
        exp.add(R.get(5));
        Element valueTwo_r = calculateExp(base,exp,systemData);
        exp.clear();
        exp.add(rmu);
        exp.add(userSecKey);
        exp.add(s_);
        exp.add(t);
        Element valueTwo = calculateExp(base,exp,systemData);


        value.add(valueOne);
        value.add(valueTwo);
        randomValue.add(valueOne_r);
        randomValue.add(valueTwo_r);
        exp.clear();
        exp.add(rm);
        exp.add(userSecKey);
        exp.add(rr);
        exp.add(rmu);
        exp.add(s_);
        exp.add(t);
        String c = "";
        MD5 md5 = new MD5();
        c = c + valueOne + valueTwo + valueOne_r + valueTwo_r;
        c = md5.start(c);
        c = c.substring(0,16);
        Element numc = Zr.newElement(new BigInteger(c,16)).getImmutable();
        for (int i = 0; i < R.size(); i++) {
            Ss.add(R.get(i).sub(exp.get(i).mul(numc)).getImmutable());
        }
        proofOfTransfer.randomValue = randomValue;
        proofOfTransfer.value = value;
        proofOfTransfer.c = numc;
        proofOfTransfer.s = Ss;
        return proofOfTransfer;
    }


    @Override
    public boolean verifyTransferReturnedSignature(Element r, Element userSecKey, Element s, Element t, Signature bankSignatureUser, Element MA, SystemData systemData, BankPubKey bankPubKey) {
        List<Element> secList = new ArrayList<>();
        secList.add(r);
        secList.add(userSecKey);
        secList.add(s);
        secList.add(t);
        if(!verifyA(MA,secList,systemData,bankPubKey)) {
            System.out.println("验证MA失败");
            return false;
        }
        if (!verifyTransferSignature(bankSignatureUser,secList,systemData,bankPubKey)){
            System.out.println("验证签名失败");
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyTransferSignature(Signature bankSignatureMu, List<Element> secList, SystemData systemData, BankPubKey bankPubKey) {
        Element X = bankPubKey.X;
        Element Y = bankPubKey.Y;
        List<Element> Z = bankPubKey.Z;
        Element g = systemData.g;
        Pairing bp = systemData.bp;
        Field GT = systemData.GT;
        Field Zr = systemData.Zr;
        BigInteger l = systemData.l;
        Element a = bankSignatureMu.a;
        Element b = bankSignatureMu.b;
        Element c = bankSignatureMu.c;
        List<Element> A = bankSignatureMu.A;
        List<Element> B = bankSignatureMu.B;
        if (!verifyegg(a,Y,g,b,bp)) return false;
        for (int i = 0; i < B.size(); i++) {
            if (!verifyegg(a,Z.get(i),g,A.get(i),bp)) return false;
            if (!verifyegg(A.get(i),Y,g,B.get(i),bp)) return false;
        }
        Element e_Xal = bp.pairing(X,a).powZn(Zr.newElement(l)).getImmutable();
        Element e_Xbm = bp.pairing(X,b).getImmutable().powZn(secList.get(0)).getImmutable();
        List<Element> e_XB = new ArrayList<>();
        for (int i = 0; i < B.size(); i++) {
            e_XB.add(bp.pairing(X,B.get(i)).getImmutable().powZn(secList.get(i+1)).getImmutable());
        }
        Element e_gc  = bp.pairing(g,c);
        Element sum = GT.newOneElement();
        for (int i = 0; i < B.size(); i++) {
            sum.mul(e_XB.get(i));
        }
        sum = sum.mul(e_Xal).mul(e_Xbm).getImmutable();
        System.out.println(sum);
        System.out.println(e_gc);
        if (!e_gc.isEqual(sum)) return false;
        return true;
    }
}
