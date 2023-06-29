package com.crazymakercircle.keystore;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
@SuppressWarnings("all")
public class CertHelper {
    /**
     * 指定证书拥有者信息。
     * 例如："CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码"
     */
    String dname = "C=CN,ST=Province,L=city,O=crazymaker,OU=crazymaker.com,CN=user";
    String CA_SHA = "SHA256WithRSAEncryption";

    public CertHelper(String dname) {
        this.dname = dname;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    protected KeyPairGenerator kpg = null;
    /**
     * 公钥和私钥对
     */
    protected KeyPair keyPair = null;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * 生成 X509 证书
     *
     * @param user
     * @return
     */
    public X509Certificate genCert() {
        X509Certificate cert = null;
        try {
            // 采用 RSA 非对称算法加密
            kpg = KeyPairGenerator.getInstance("RSA");
            // 初始化为 2048 位，这个长度的密钥目前可认为无法被暴力破解
            kpg.initialize(2048);
            keyPair = kpg.generateKeyPair();
            // 私钥
            PrivateKey privateKey = this.keyPair.getPrivate();

            KeyPair keyPair = this.keyPair;
            // 公钥
            PublicKey pubKey = keyPair.getPublic();
            X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
            // 设置序列号
            certGen.setSerialNumber(BigInteger.valueOf(1));
            // 颁发者证书
            X500Principal issuerDN = new X500Principal(dname);
            // 使用者证书,在自签证书中，颁发者证书和使用者证书两者一样
            X500Principal subjectDN = issuerDN;
            // 设置颁发者
            certGen.setIssuerDN(issuerDN);
            // 设置有效期
            certGen.setNotBefore(new Date());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            // 1年过期
            calendar.add(1, 1);
            certGen.setNotAfter(calendar.getTime());
            // 设置使用者
            certGen.setSubjectDN(subjectDN);
            // 公钥
            certGen.setPublicKey(pubKey);
            // 签名算法
            certGen.setSignatureAlgorithm(CA_SHA);
            cert = certGen.generateX509Certificate(privateKey, "BC");
        } catch (Exception e) {
            log.error(e.getClass() + e.getMessage());
            e.printStackTrace();
        }
        return cert;
    }
}
