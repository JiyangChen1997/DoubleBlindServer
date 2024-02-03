package com.cjy.doubleblindserver.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cjy.doubleblindserver.Transfer.*;
import com.cjy.doubleblindserver.cipher.entity.Coin.ECoin;
import com.cjy.doubleblindserver.cipher.entity.Coin.ZKPOK;
import com.cjy.doubleblindserver.cipher.entity.Dto.CoinDto;
import com.cjy.doubleblindserver.cipher.entity.Dto.POKDto;
import com.cjy.doubleblindserver.cipher.entity.Dto.SigDto;
import com.cjy.doubleblindserver.cipher.entity.data.BankPubKey;
import com.cjy.doubleblindserver.cipher.entity.data.BankSecKey;
import com.cjy.doubleblindserver.cipher.entity.data.SystemData;
import com.cjy.doubleblindserver.cipher.entity.signature.Signature;
import com.cjy.doubleblindserver.cipher.service.impl.BankCipherService;
import com.cjy.doubleblindserver.dao.KnowledgeDao;
import com.cjy.doubleblindserver.dao.SignatureDao;
import com.cjy.doubleblindserver.dao.UserDao;
import com.cjy.doubleblindserver.dao.UsercoinDao;
import com.cjy.doubleblindserver.domain.Knowledge;
import com.cjy.doubleblindserver.domain.Sig;
import com.cjy.doubleblindserver.domain.User;
import com.cjy.doubleblindserver.domain.Usercoin;
import com.cjy.doubleblindserver.tool.StrEleUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/28 8:54
 */
@Service
public class UserServiceImpl  implements UserService {

    @Autowired
    private BankCipherService bankCipherService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UsercoinDao usercoinDao;
    @Autowired
    private SignatureDao signatureDao;
    @Autowired
    private KnowledgeDao knowledgeDao;

    private Pairing bp;
    private SystemData systemData;
    private BankPubKey bankPubKey;
    private BankSecKey bankSecKey;
    private SpendMtoU receivedCer;
    private SpendUtoM receivedCoin;
    long time1;
    long time2;
    @Override
    public BeginBtoU getSystemData() {
        if (bp == null) {
            bp = bankCipherService.generatePairing();
            systemData = bankCipherService.generateSystemData(bp);
            bankSecKey = bankCipherService.generateBankSecKey(systemData);
            bankPubKey = bankCipherService.generateBankPubKey(systemData, bankSecKey);
        }
        BeginBtoU beginBtoU = new BeginBtoU();
        beginBtoU.g = StrEleUtils.elementToString(systemData.g);
        beginBtoU.X = StrEleUtils.elementToString(bankPubKey.X);
        beginBtoU.Y = StrEleUtils.elementToString(bankPubKey.Y);
        beginBtoU.Z = StrEleUtils.ListEtoS(bankPubKey.Z);
        System.out.println(systemData.g);
        System.out.println(bankPubKey.X);
        System.out.println(bankPubKey.Y);
        System.out.println(bankPubKey.Z.get(0));
        System.out.println(bankPubKey.Z.get(1));
        System.out.println(bankPubKey.Z.get(2));
        return beginBtoU;
    }

    @Override
    public RegBtoU walletRegister(RegUtoB regUtoB) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",regUtoB.name);
        User user = userDao.selectOne(queryWrapper);
        RegBtoU regBtoU = null;
        System.out.println(user.getPassword());
        System.out.println(regUtoB.password);
        if (user.getPassword().equals(regUtoB.password)) {
            ZKPOK pokOfWithdraw = StrToPokG1(regUtoB.zkpok);
            time1 = System.currentTimeMillis();
            if (bankCipherService.verifyWithdrawZK(pokOfWithdraw,systemData,bankPubKey)) {
                regBtoU = new RegBtoU();
                Element rbank_ = systemData.Zr.newRandomElement();
                Element A = pokOfWithdraw.value.get(1).mul(bankPubKey.Z.get(1).powZn(rbank_)).getImmutable();
                Signature signature = bankCipherService.generateBankUserSignature(A,systemData,bankSecKey);
                time2 = System.currentTimeMillis();
                System.out.println("申请电子钱包第二阶段"+(time2 - time1));

                regBtoU.rbank_ = StrEleUtils.elementToString(rbank_);
                regBtoU.signature = SigDouToStr(signature);


                System.out.println(signature.a);
                System.out.println(signature.b);
                System.out.println(signature.c);
                System.out.println(signature.A.get(0));
                System.out.println(signature.A.get(1));
                System.out.println(signature.A.get(2));
                System.out.println(signature.B.get(0));
                System.out.println(signature.B.get(1));
                System.out.println(signature.B.get(2));
                System.out.println(rbank_);

                System.out.println("注册钱包大成功！！！！！！！！！！\n\n\n\n\n\n\n");
            }
            user.setRest(user.getRest()-1023);
            userDao.updateById(user);
        }


        return regBtoU;
    }


    @Override
    public RegBtoM certificateRegister(RegMtoB regMtoB) {
        //申请匿名证书时，应该存在该用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",regMtoB.name);
        User user = userDao.selectOne(queryWrapper);
        RegBtoM regBtoM = null;
        if (user.getPassword().equals(regMtoB.password)) {
            ZKPOK pokOfSign = StrToPokG1(regMtoB.zkpok);
            time1 = System.currentTimeMillis();
            if (bankCipherService.verifySignZK(pokOfSign, systemData, bankPubKey)) {
                regBtoM = new RegBtoM();
                Element M = pokOfSign.value.get(1);
                Signature signature = bankCipherService.generateBankMerchantSignature(M, systemData, bankSecKey);
                time2 = System.currentTimeMillis();
                System.out.println("申请电子钱包第二阶段"+(time2 - time1));
                regBtoM.signature = SigDouToStr(signature);
                System.out.println("注册证书大成功！！！！！！！！！！\n\n\n\n\n\n\n");
            }
        }
        return regBtoM;
    }

    @Override
    public void getAnnoyCertificate(SpendMtoU spendMtoU) {
        receivedCer = spendMtoU;
    }

    @Override
    public SpendMtoU sendAnnoyCertificate() {
        return receivedCer;
    }

    @Override
    public void getElectronicCoin(SpendUtoM spendUtoM) {
        receivedCoin = spendUtoM;
    }

    @Override
    public SpendUtoM sendElectronicCoin() {
        return receivedCoin;
    }

    @Override
    public boolean Deposit(DepositMtoB depositMtoB) {
        Signature signature = SigStrToDou(depositMtoB.sig);

        CoinDto coinDto = depositMtoB.coin;
        POKDto pokDto = coinDto.zkpok;
        ECoin coin = new ECoin();
        Signature userSignature = SigStrToDou(coinDto.signature);
        SigDto sigDto = coinDto.signature;
        coin.userSignature = userSignature;
        coin.S = StrEleUtils.stringToElement(coinDto.S, systemData.G1);
        coin.R = StrEleUtils.stringToElement(coinDto.R, systemData.Zr);
        coin.T = StrEleUtils.stringToElement(coinDto.T, systemData.G1);
        coin.J = StrEleUtils.stringToElement(coinDto.J, systemData.Zr);
        coin.zkpok = StrToPokGT(pokDto);
        QueryWrapper<Usercoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("S",coinDto.S);
        Usercoin ucoin = usercoinDao.selectOne(queryWrapper);
        time1 = System.currentTimeMillis();
        if (ucoin != null) {
            //运行identify算法
            ECoin coin2 = new ECoin();
            coin2.R = StrEleUtils.stringToElement(ucoin.getR(), systemData.Zr);
            coin2.S = StrEleUtils.stringToElement(ucoin.getS(), systemData.G1);
            coin2.T = StrEleUtils.stringToElement(ucoin.getT(), systemData.G1);
            coin2.J = StrEleUtils.stringToElement(ucoin.getJ(), systemData.Zr);
            long cid = ucoin.getId();
            QueryWrapper<Sig> sigQueryWrapper = new QueryWrapper<>();
            sigQueryWrapper.eq("coinid",cid);
            Sig sig = signatureDao.selectOne(sigQueryWrapper);
            System.out.println(sig.getA());
            System.out.println(sig.getB());
            System.out.println(sig.getC());
            Signature coinsig = new Signature();
            coinsig.a = StrEleUtils.stringToElement(sig.getA(), systemData.G1);
            coinsig.b = StrEleUtils.stringToElement(sig.getB(), systemData.G1);
            coinsig.c = StrEleUtils.stringToElement(sig.getC(), systemData.G1);
            List<Element> listA = new ArrayList<>();
            List<Element> listB = new ArrayList<>();
            listA.add(StrEleUtils.stringToElement(sig.getA1(), systemData.G1));
            listA.add(StrEleUtils.stringToElement(sig.getA2(), systemData.G1));
            listA.add(StrEleUtils.stringToElement(sig.getA3(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB1(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB2(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB3(), systemData.G1));
            coinsig.A = listA;
            coinsig.B = listB;
            coin2.userSignature = coinsig;

            Element pku = bankCipherService.identify(coin,coin2);
            System.out.println("该用户存在双重支付行为!!!");



        } else if (bankCipherService.despoit(coin,signature,systemData,bankPubKey)) {
            time2 = System.currentTimeMillis();
            System.out.println("货币清算第二阶段"+(time2 - time1));
            //将收到的电子货币保存到已花费电子货币列表中
            Usercoin usercoin = new Usercoin(coinDto);
            int coinid = usercoinDao.insert(usercoin);
            System.out.println(coinid);
            System.out.println(usercoin.getId());
            Sig usersig = new Sig(sigDto,(int)usercoin.getId());
            signatureDao.insert(usersig);
            Knowledge knowledge = new Knowledge(pokDto,coinid);
            knowledgeDao.insert(knowledge);
            System.out.println("用户清算电子货币成功！！！");
            return true;
        }
        return false;
    }

    @Override
    public TransferBtoM transfer(TransferMtoB transferMtoB) {
        TransferBtoM transferBtoM = null;
        Signature signature = SigStrToDou(transferMtoB.sig);
        POKDto pokTran = transferMtoB.pokoftransfer;
        ZKPOK pokOfTransfer = StrToPokTrans(pokTran);
        CoinDto coinDto = transferMtoB.coin;
        POKDto pokDto = coinDto.zkpok;
        ECoin coin = new ECoin();
        Signature userSignature = SigStrToDou(coinDto.signature);
        SigDto sigDto = coinDto.signature;
        coin.userSignature = userSignature;
        coin.S = StrEleUtils.stringToElement(coinDto.S, systemData.G1);
        coin.R = StrEleUtils.stringToElement(coinDto.R, systemData.Zr);
        coin.T = StrEleUtils.stringToElement(coinDto.T, systemData.G1);
        coin.J = StrEleUtils.stringToElement(coinDto.J, systemData.Zr);
        coin.zkpok = StrToPokGT(pokDto);
        QueryWrapper<Usercoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("S",coinDto.S);
        Usercoin ucoin = usercoinDao.selectOne(queryWrapper);
        time1 = System.currentTimeMillis();
        if (ucoin != null) {
            //运行identify算法
            ECoin coin2 = new ECoin();
            coin2.R = StrEleUtils.stringToElement(ucoin.getR(), systemData.Zr);
            coin2.S = StrEleUtils.stringToElement(ucoin.getS(), systemData.G1);
            coin2.T = StrEleUtils.stringToElement(ucoin.getT(), systemData.G1);
            coin2.J = StrEleUtils.stringToElement(ucoin.getJ(), systemData.Zr);
            long cid = ucoin.getId();
            QueryWrapper<Sig> sigQueryWrapper = new QueryWrapper<>();
            sigQueryWrapper.eq("coinid",cid);
            Sig sig = signatureDao.selectOne(sigQueryWrapper);
            Signature coinsig = new Signature();
            coinsig.a = StrEleUtils.stringToElement(sig.getA(), systemData.G1);
            coinsig.b = StrEleUtils.stringToElement(sig.getB(), systemData.G1);
            coinsig.c = StrEleUtils.stringToElement(sig.getC(), systemData.G1);
            List<Element> listA = new ArrayList<>();
            List<Element> listB = new ArrayList<>();
            listA.add(StrEleUtils.stringToElement(sig.getA1(), systemData.G1));
            listA.add(StrEleUtils.stringToElement(sig.getA2(), systemData.G1));
            listA.add(StrEleUtils.stringToElement(sig.getA3(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB1(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB2(), systemData.G1));
            listB.add(StrEleUtils.stringToElement(sig.getB3(), systemData.G1));
            coinsig.A = listA;
            coinsig.B = listB;
            coin2.userSignature = coinsig;

            Element pku = bankCipherService.identify(coin,coin2);
            System.out.println("该用户存在双重支付行为!!!");
            return transferBtoM;

        }else if (bankCipherService.transfer(coin,signature,pokOfTransfer, systemData,bankPubKey)){

            transferBtoM = new TransferBtoM();
            Element rmu_ = systemData.Zr.newRandomElement();
            Element MA = pokOfTransfer.value.get(1).mul(bankPubKey.Z.get(1).powZn(rmu_)).getImmutable();
            Signature signatureMu = bankCipherService.generateBankMUSignature(MA,systemData,bankSecKey);
            time2 = System.currentTimeMillis();
            System.out.println("Transfer第二阶段"+(time2 - time1));
            transferBtoM.rmbank_ = StrEleUtils.elementToString(rmu_);
            transferBtoM.sig = SigDouToStr(signatureMu);

            System.out.println("转换电子货币大成功");
        }
        return transferBtoM;
    }
    public ZKPOK StrToPokG1(POKDto pokDto) {
        List<Element> randomValues = StrEleUtils.ListStoE(pokDto.randomValue,systemData.G1);
        List<Element> values = StrEleUtils.ListStoE(pokDto.value, systemData.G1);
        List<Element> s = StrEleUtils.ListStoE(pokDto.s, systemData.Zr);
        Element c = StrEleUtils.stringToElement(pokDto.c, systemData.Zr);
        ZKPOK zkpok = new ZKPOK();
        zkpok.randomValue = randomValues;
        zkpok.value = values;
        zkpok.s = s;
        zkpok.c = c;
        return zkpok;
    }
    public ZKPOK StrToPokGT(POKDto pokDto) {
        List<Element> randomValues = new ArrayList<>();
        List<Element> values = new ArrayList<>();
        values.add(StrEleUtils.stringToElement(pokDto.value.get(0),systemData.GT));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(0),systemData.GT));
        values.add(StrEleUtils.stringToElement(pokDto.value.get(1),systemData.G1));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(1),systemData.G1));
        values.add(StrEleUtils.stringToElement(pokDto.value.get(2),systemData.G1));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(2),systemData.G1));
        values.add(StrEleUtils.stringToElement(pokDto.value.get(3),systemData.G1));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(3),systemData.G1));
        values.add(StrEleUtils.stringToElement(pokDto.value.get(4),systemData.G1));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(4),systemData.G1));
        List<Element> s = StrEleUtils.ListStoE(pokDto.s, systemData.Zr);
        Element c = StrEleUtils.stringToElement(pokDto.c, systemData.Zr);
        Element h = StrEleUtils.stringToElement(pokDto.h, systemData.G1);
        ZKPOK zkpok = new ZKPOK();
        zkpok.randomValue = randomValues;
        zkpok.value = values;
        zkpok.s =s;
        zkpok.c = c;
        zkpok.h = h;
        return zkpok;

    }
    public ZKPOK StrToPokTrans(POKDto pokDto) {
        List<Element> randomValues = new ArrayList<>();
        List<Element> values = new ArrayList<>();
        values.add(StrEleUtils.stringToElement(pokDto.value.get(0),systemData.GT));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(0),systemData.GT));
        values.add(StrEleUtils.stringToElement(pokDto.value.get(1),systemData.G1));
        randomValues.add(StrEleUtils.stringToElement(pokDto.randomValue.get(1),systemData.G1));
        List<Element> s = StrEleUtils.ListStoE(pokDto.s, systemData.Zr);
        Element c = StrEleUtils.stringToElement(pokDto.c, systemData.Zr);
        ZKPOK zkpok = new ZKPOK();
        zkpok.randomValue = randomValues;
        zkpok.value = values;
        zkpok.s =s;
        zkpok.c = c;
        return zkpok;
    }
    public SigDto SigDouToStr(Signature signature) {
        SigDto sigDto = new SigDto();
        sigDto.a = StrEleUtils.elementToString(signature.a);
        sigDto.b = StrEleUtils.elementToString(signature.b);
        sigDto.c = StrEleUtils.elementToString(signature.c);
        sigDto.A = StrEleUtils.ListEtoS(signature.A);
        sigDto.B = StrEleUtils.ListEtoS(signature.B);
        return sigDto;
    }
    public Signature SigStrToDou(SigDto sigDto) {
        Signature signature = new Signature();
        signature.a = StrEleUtils.stringToElement(sigDto.a, systemData.G1);
        signature.b = StrEleUtils.stringToElement(sigDto.b, systemData.G1);
        signature.c = StrEleUtils.stringToElement(sigDto.c, systemData.G1);
        signature.A = StrEleUtils.ListStoE(sigDto.A, systemData.G1);
        signature.B = StrEleUtils.ListStoE(sigDto.B, systemData.G1);
        return signature;
    }

}
