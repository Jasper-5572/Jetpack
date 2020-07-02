package com.android.jasper.framework.network.cookie

import com.android.jasper.framework.util.StringUtils
import okhttp3.Cookie
import java.io.*

/**
 *@author   Jasper
 *@create   2020/6/4 18:01
 *@describe
 *@update
 */
class SerializableCookie : Serializable {

    companion object {
        private const val NON_VALID_EXPIRES_AT = -1L
        private const val serialVersionUID = -8594045714036645534L
    }

    private var mCookie: Cookie? = null
    fun encodeCookie(cookie: Cookie): String {
        mCookie = cookie
        var result: String
        val byteArrayOutputStream = ByteArrayOutputStream()
        var outputStream: ObjectOutputStream? = null
        try {
            outputStream = ObjectOutputStream(byteArrayOutputStream)
            outputStream.writeObject(this)
            result = StringUtils.byteArrayToHexString(byteArrayOutputStream.toByteArray()) ?: ""
        } catch (e: IOException) {
            e.printStackTrace()
            result = ""
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    /**
     *
     * @param encodeCookie
     * @return
     */
    fun decodeCookie(encodeCookie: String): Cookie? {
        var cookie: Cookie?
        val cookieBytes: ByteArray? = StringUtils.hexStringToByteArray(encodeCookie)
        val byteArrayInputStream =
            ByteArrayInputStream(cookieBytes)
        var inputStream: ObjectInputStream? = null
        try {
            inputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (inputStream.readObject() as SerializableCookie).mCookie
        } catch (e: IOException) {
            e.printStackTrace()
            cookie = null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            cookie = null
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return cookie
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(mCookie?.name?:"")
        out.writeObject(mCookie?.value?:"")
        out.writeLong(mCookie?.let { if (it.persistent) it.expiresAt else NON_VALID_EXPIRES_AT }
            ?: NON_VALID_EXPIRES_AT)
        out.writeObject(mCookie?.domain?:"")
        out.writeObject(mCookie?.path?:"")
        out.writeBoolean(mCookie?.secure ?: false)
        out.writeBoolean(mCookie?.httpOnly ?: false)
        out.writeBoolean(mCookie?.hostOnly ?: false)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val builder = Cookie.Builder()
        builder.name(`in`.readObject() as String)
        builder.value(`in`.readObject() as String)
        val expiresAt = `in`.readLong()
        if (expiresAt != NON_VALID_EXPIRES_AT) {
            builder.expiresAt(expiresAt)
        }
        val domain = `in`.readObject() as String
        builder.domain(domain)
        builder.path(`in`.readObject() as String)
        if (`in`.readBoolean()) {
            builder.secure()
        }
        if (`in`.readBoolean()) {
            builder.httpOnly()
        }
        if (`in`.readBoolean()) {
            builder.hostOnlyDomain(domain)
        }
        mCookie = builder.build()
    }

}