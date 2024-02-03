package com.cjy.doubleblindserver.cipher.service.impl;

import com.cjy.doubleblindserver.cipher.entity.Coin.ECoin;
import com.cjy.doubleblindserver.cipher.entity.Coin.ZKPOK;
import com.cjy.doubleblindserver.cipher.entity.data.BankPubKey;
import com.cjy.doubleblindserver.cipher.entity.data.BankSecKey;
import com.cjy.doubleblindserver.cipher.entity.data.SystemData;
import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/23 16:02
 */
public interface BankCipherService {
    Pairing generatePairing();

    Pairing generatePairing(int rBits, int qBits);

    SystemData generateSystemData(Pairing bp);

    BankSecKey generateBankSecKey(SystemData systemData);

    BankPubKey generateBankPubKey(SystemData systemData, BankSecKey bankSecKey);

    boolean verifyWithdrawZK(ZKPOK proofOfWithdraw, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyZKPOK(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData);

    boolean verifyZKPOKGT(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData);

    Signature generateBankUserSignature(Element A, SystemData systemData, BankSecKey bankSecKey);

    boolean verifySignZK(ZKPOK proofOfSign, SystemData systemData, BankPubKey bankPubKey);

    Signature generateBankMerchantSignature(Element M, SystemData systemData, BankSecKey bankSecKey);

    boolean despoit(ECoin coin, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyegg(Element lefta, Element leftb, Element righta, Element rightb, Pairing bp);

    boolean transfer(ECoin coin, Signature bankSignatureMerchant, ZKPOK proofOfTransfer, SystemData systemData, BankPubKey bankPubKey);

    Signature generateBankMUSignature(Element Ma, SystemData systemData, BankSecKey bankSecKey);

    Element identify(ECoin coin1, ECoin coin2);

    boolean verifyTransferZK(ZKPOK proofTransfer, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey);
}
