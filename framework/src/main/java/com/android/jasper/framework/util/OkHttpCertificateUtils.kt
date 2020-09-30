package com.android.jasper.framework.util

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 *@author   Jasper
 *@create   2020/7/8 11:40
 *@describe
 *@update
 */
object OkHttpCertificateUtils {
    /**
     * 信任所有证书
     * @param okHttpBuilder Builder
     */
    @JvmStatic
    fun trustAllCertificate(okHttpBuilder: OkHttpClient.Builder) {
        try {
            val trustAllManager = object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)

            }
            SSLContext.getInstance("TLS")?.apply {
                init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
                okHttpBuilder.sslSocketFactory(socketFactory, trustAllManager)
            }
            okHttpBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun trustCertificateString(okHttpBuilder: OkHttpClient.Builder, certificateString: String) {
        try {
            val byteArrayInputStream = ByteArrayInputStream(certificateString.toByteArray())
            val certificate =
                CertificateFactory.getInstance("X.509").generateCertificate(byteArrayInputStream)

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())?.apply {
                load(null, null)
                setCertificateEntry("ca", certificate)
                byteArrayInputStream.close()
            }

            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            SSLContext.getInstance("TLS")?.apply {
                init(null, trustManagerFactory.trustManagers, SecureRandom())
                okHttpBuilder.sslSocketFactory(
                    socketFactory,
                    trustManagerFactory.trustManagers[0] as X509TrustManager
                )
            }
            okHttpBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })
        } catch (e: Exception) {
        }
    }


//    //只信任指定证书（传入raw资源ID）
//    public static void setCertificate(Context context, OkHttpClient.Builder okHttpClientBuilder, int cerResID)
//    {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory . getInstance ("X.509");
//            InputStream inputStream = context . getResources ().openRawResource(cerResID);
//            Certificate ca = certificateFactory . generateCertificate (inputStream);
//
//            KeyStore keyStore = KeyStore . getInstance (KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//            inputStream.close();
//
//            TrustManagerFactory tmf = TrustManagerFactory . getInstance (TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(keyStore);
//
//            SSLContext sslContext = SSLContext . getInstance ("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom ());
//            okHttpClientBuilder.sslSocketFactory(
//                sslContext.getSocketFactory(),
//                (X509TrustManager) tmf . getTrustManagers ()[0]
//            );
//            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier () {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //批量信任证书
//    public static void setCertificates(Context context, OkHttpClient.Builder okHttpClientBuilder, int... cerResIDs)
//    {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory . getInstance ("X.509");
//
//            KeyStore keyStore = KeyStore . getInstance (KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            for (int i = 0; i < cerResIDs.length; i++) {
//                Certificate ca = certificateFactory . generateCertificate (context.getResources()
//                    .openRawResource(cerResIDs[i]));
//                keyStore.setCertificateEntry("ca" + i, ca);
//            }
//
//            TrustManagerFactory tmf = TrustManagerFactory . getInstance (TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(keyStore);
//
//            SSLContext sslContext = SSLContext . getInstance ("TLS");
//            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom ());
//            okHttpClientBuilder.sslSocketFactory(
//                sslContext.getSocketFactory(),
//                (X509TrustManager) tmf . getTrustManagers ()[0]
//            );
//            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier () {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}