package io.github.tomgarden.lib.img.img_crop.util

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

/**
 * describe : null
 *
 *
 * author : tom
 *
 * time : 18-10-9 17:41
 *
 * email : tom.work@foxmail.com
 *
 * CSDN : https://blog.csdn.net/u014587769
 *
 * Git : https://github.com/TomGarden
 */
object FileUtil {
    //*****************************************************************************************************
    //                             文件操作
    //*****************************************************************************************************
    fun md5(filePath: String): String = md5(FileInputStream(filePath))
    fun md5(file: File): String = md5(FileInputStream(file))

    fun md5(fileInputStream: FileInputStream): String {
        val md = MessageDigest.getInstance("MD5")
        val byteArray: ByteArray = try {
            val fis: InputStream = fileInputStream
            val buffer = ByteArray(1024)
            var numRead: Int
            do {
                numRead = fis.read(buffer)
                if (numRead > 0) {
                    md.update(buffer, 0, numRead)
                }
            } while (numRead != -1)
            fis.close()
            md.digest()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            md.digest(System.currentTimeMillis().toString().toByteArray())
        }

        return BigInteger(1, byteArray).toString(16).padStart(32, '0')
    }
}