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
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/23 16:02
 */
public interface UserCipherService {
    Element generateUserSecKey(SystemData systemData);

    Element generateUserPubKey(SystemData systemData, Element userSecKey);

    Element calculateExp(List<Element> base, List<Element> exp, SystemData systemData);

    Element calculateExpGT(List<Element> base, List<Element> exp, SystemData systemData);

    ZKPOK generateWithdrawProof(Element A_, Element r, Element userSecKey, Element s_, Element t, Element userPbuKey, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyReturnedSignature(Signature bankSignatureUser, Element A, Element r, Element userSecKey, Element s, Element t, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyA(Element A, List<Element> secList, SystemData systemData, BankPubKey bankPubKey);

    boolean verifySignature(Signature bankSignature, List<Element> secList, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyMerchantCertifate(ZKPOK proofOfMerchantCertifate, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyZKPOK(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData);

    boolean verifyZKPOKGT(Element value, Element randomValue, List<Element> s, List<Element> base, Element c, SystemData systemData);

    Signature generateAnnoymousSignature(Signature signature, Element r, Element r2);

    ECoin generateCoin(Signature MerchantSignature, Element userPbuKey, TheFirstWallet theFirstWallet, SystemData systemData, BankPubKey bankPubKey);

    ECoin generateCoin(Signature MerchantSignature, Element userPbuKey, TheSecondWallet theSecondWallet, SystemData systemData, BankPubKey bankPubKey);

    ZKPOK generateSpendProofOfUser();

    boolean verifyegg(Element lefta, Element leftb, Element righta, Element rightb, Pairing bp);

    boolean verifyGuilt(ProofGuilt proofGuilt);

    Element identify(ECoin coin1, ECoin coin2);

    ZKPOK generateSignProof(Element rm, SystemData systemData, BankPubKey bankPubKey, Element userSecKey, Element userPbuKey);

    boolean verifyReturned(Signature bankSignatureMerchant, Element M, Element rm, Element userSecKey, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyM(Element M, List<Element> secList, SystemData systemData, BankPubKey bankPubKey);

    ZKPOK generateCertifateProof(Certificate certificate, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyUserCoin(ECoin coin, Signature bankSignatureMerchant, SystemData systemData, BankPubKey bankPubKey);

    boolean deposit(ECoin coin, Signature bankSignatureMerchant);

    ZKPOK generateTransferProof(ECoin coin, Signature bankSignatureMerchant, Element rm, Element userSecKey, Element r2, Element rmu, Element s_, Element t, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyTransferReturnedSignature(Element r, Element userSecKey, Element s, Element t, Signature bankSignatureUser, Element MA, SystemData systemData, BankPubKey bankPubKey);

    boolean verifyTransferSignature(Signature bankSignatureMu, List<Element> secList, SystemData systemData, BankPubKey bankPubKey);
}
