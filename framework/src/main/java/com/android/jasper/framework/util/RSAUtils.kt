package com.android.jasper.framework.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 *@author   Jasper
 *@create   2020/7/27 13:35
 *@describe
 *@update
 */
object RSAUtils {
    private const val RSA = "RSA"
    private const val RSA_ECB = "RSA/ECB/PKCS1Padding"


    /**
     * 随机生成RSA密钥对(默认密钥长度为1024)
     * @param keyLength Int 密钥长度，范围：512～2048  一般1024
     * @return KeyPair?
     */
    fun generateRSAKeyPair(keyLength: Int = 1024): KeyPair? {
        return try {
            val kpg = KeyPairGenerator.getInstance(RSA)
            kpg.initialize(keyLength)
            kpg.genKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }


    /**
     *  用公钥加密  每次加密的字节数，不能超过密钥的长度值减去11
     * @param data ByteArray? 需加密数据的byte数据
     * @param publicKey PublicKey? 公钥
     * @return ByteArray? 加密后的byte型数据
     */
    fun encryptData(data: ByteArray?, publicKey: PublicKey?): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(RSA_ECB)
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            // 传入编码数据并返回编码结果
            cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData 经过encryptedData()加密返回的byte数据
     * @param privateKey 私钥
     * @return
     */
    fun decryptData(encryptedData: ByteArray?, privateKey: PrivateKey?): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(RSA_ECB)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            null
        }
    }


    /**
     *  通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
     * @param keyBytes ByteArray?
     * @return PublicKey?
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(keyBytes: ByteArray?): PublicKey? {
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePublic(keySpec)
    }


    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     * @param keyBytes ByteArray?
     * @return PrivateKey?
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(keyBytes: ByteArray?): PrivateKey? {
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePrivate(keySpec)
    }


    /**
     * 使用N、e值还原公钥
     * @param modulus String
     * @param publicExponent String
     * @return PublicKey?
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(modulus: String, publicExponent: String): PublicKey? {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(publicExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 使用N、d值还原私钥
     * @param modulus String
     * @param privateExponent String
     * @return PrivateKey?
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(modulus: String, privateExponent: String): PrivateKey? {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(privateExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePrivate(keySpec)
    }


    /**
     * 从字符串中加载公钥
     * @param publicKeyStr String 公钥数据字符串
     * @return PublicKey?
     * @throws Exception 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(publicKeyStr: String): PublicKey? {
        return try {
            val buffer = StringUtils.decode(publicKeyStr)
            val keyFactory = KeyFactory.getInstance(RSA)
            val keySpec = X509EncodedKeySpec(buffer)
            keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("公钥非法")
        } catch (e: NullPointerException) {
            throw Exception("公钥数据为空")
        }
    }


    /**
     *  从字符串中加载私钥 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令
     * @param privateKeyStr String
     * @return PrivateKey?
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(privateKeyStr: String): PrivateKey? {
        return try {
            val buffer= StringUtils.decode(privateKeyStr)
            val keySpec = PKCS8EncodedKeySpec(buffer)
            val keyFactory = KeyFactory.getInstance(RSA)
            keyFactory.generatePrivate(keySpec) as? RSAPrivateKey
        } catch (e: NoSuchAlgorithmException) {
            throw Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw Exception("私钥非法")
        } catch (e: NullPointerException) {
            throw Exception("私钥数据为空")
        }
    }


    /**
     * 从文件中输入流中加载公钥
     * @param `in` InputStream 公钥输入流
     * @return PublicKey?
     * @throws Exception 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(`in`: InputStream): PublicKey? {
        return try {
            loadPublicKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("公钥数据流读取错误")
        } catch (e: NullPointerException) {
            throw Exception("公钥输入流为空")
        }
    }

    /**
     * 从文件中加载私钥
     * @param `in` InputStream
     * @return PrivateKey?
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(inputStream: InputStream): PrivateKey? {
        return try {
            loadPrivateKey(readKey(inputStream))
        } catch (e: IOException) {
            throw Exception("私钥数据读取错误")
        } catch (e: NullPointerException) {
            throw Exception("私钥输入流为空")
        }
    }


    /**
     * 读取密钥信息
     * @param `in` InputStream
     * @return String
     * @throws IOException
     */
    private fun readKey(inputStream: InputStream): String {
        val resultSb = StringBuilder()
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var readLine: String
            while (reader.readLine().also { readLine = it } != null) {
                if (readLine[0] == '-') {
                    continue
                } else {
                    resultSb.append(readLine)
                    resultSb.append('\r')
                }
            }
        }
        return resultSb.toString()
    }

    /**
     * 打印公钥信息
     *
     * @param publicKey
     */
    fun printPublicKeyInfo(publicKey: PublicKey) {
        val rsaPublicKey = publicKey as RSAPublicKey
        println("----------RSAPublicKey----------")
        println("Modulus.length=" + rsaPublicKey.modulus.bitLength())
        println("Modulus=" + rsaPublicKey.modulus.toString())
        println("PublicExponent.length=" + rsaPublicKey.publicExponent.bitLength())
        println("PublicExponent=" + rsaPublicKey.publicExponent.toString())
    }

    fun printPrivateKeyInfo(privateKey: PrivateKey) {
        val rsaPrivateKey = privateKey as RSAPrivateKey
        println("----------RSAPrivateKey ----------")
        println("Modulus.length=" + rsaPrivateKey.modulus.bitLength())
        println("Modulus=" + rsaPrivateKey.modulus.toString())
        println("PrivateExponent.length=" + rsaPrivateKey.privateExponent.bitLength())
        println("PrivatecExponent=" + rsaPrivateKey.privateExponent.toString())
    }
}