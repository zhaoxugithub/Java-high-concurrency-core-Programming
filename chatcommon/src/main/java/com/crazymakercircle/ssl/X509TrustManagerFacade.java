package com.crazymakercircle.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 定制的信任管理器
 */
@Slf4j
public final class X509TrustManagerFacade implements X509TrustManager {
    /**
     * 内部的x509TrustManager委托成员
     */
    private X509TrustManager x509TrustManager;

    /**
     * 使用密钥仓库初始化信任管理器
     *
     * @param keyStore 密钥仓库
     */
    public void init(KeyStore keyStore) throws Exception {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // 使用密钥仓库初始化信任管理器工厂
        factory.init(keyStore);
        TrustManager[] trustManagers = factory.getTrustManagers();
        // 从信任管理器工厂的信任库中，筛选出X509格式的证书库
        for (int i = 0; i < trustManagers.length; i++) {
            TrustManager trustManager = trustManagers[i];
            if (trustManager instanceof X509TrustManager) {
                this.x509TrustManager = (X509TrustManager) trustManager;
            }
        }
        if (this.x509TrustManager == null) {
            throw new Exception("Couldn't find X509TrustManager");
        }
    }


    // 客户端证书检验
    // 该方法检查客户端的证书，若不信任该证书则抛出异常。如果不需要对客户端进行认证，
    // 只需要执行默认的信任管理器的这个方法。JSSE中，默认的信任管理器类为TrustManager。
    public final void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.info("checkClient {}, type is {}", chain, authType);
        X509TrustManager x509TrustManager = this.x509TrustManager;
        if (x509TrustManager != null) {
            x509TrustManager.checkClientTrusted(chain, authType);
        }
    }

    // 验服务端证书的校验
    // 客户端通过该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的任何证书。
    // 在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
    public final void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.info("checkServer {}, type is {}", chain, authType);
        if (this.x509TrustManager != null) {
            this.x509TrustManager.checkServerTrusted(chain, authType);
        }
    }

    // 返回受信任的X509证书数组
    public final X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] issuers = null;
        if (this.x509TrustManager != null) {
            issuers = x509TrustManager.getAcceptedIssuers();
        }
        if (null == issuers) {
            log.error("信任的X509证书数组 is null");
        }
        return issuers;
    }
}