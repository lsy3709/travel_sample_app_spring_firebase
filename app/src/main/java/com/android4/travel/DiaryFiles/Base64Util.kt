package com.android4.travel.DiaryFiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.*

class Base64Util {

     companion object {
         fun bitMapToBase64(bitmap: Bitmap?): String {

             val byteArrayOutputStream = ByteArrayOutputStream()
             bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
             val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
             return  Base64.encodeToString(byteArray,Base64.DEFAULT)
             // 여기까지 인코딩 끝
         }

         fun stringToBitMap(base64: String?): Bitmap {
             val encodeByte = Base64.decode(base64,Base64.DEFAULT)
             return BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)
         }

         fun videoToBase64(file : String) {
             // the base64 encoding - acceptable estimation of encoded size
             // the base64 encoding - acceptable estimation of encoded size
             val sb: StringBuilder = StringBuilder(file.length / 3 * 4)

             var fin: FileInputStream? = null
             var inputStream: InputStream? = null
             try {
                 fin = FileInputStream("some.file")
                 // Max size of buffer
                 val bSize = 3 * 512
                 // Buffer
                 val buf = ByteArray(bSize)
                 // Actual size of buffer
                 var len = 0
                 while (fin.read(buf).also { len = it } != -1) {
                     val encoded: ByteArray = Base64.encode(buf,0)

                     // Although you might want to write the encoded bytes to another
                     // stream, otherwise you'll run into the same problem again.
                     sb.append(String(encoded, 0, len))
                 }
             } catch (e: IOException) {
                 if (null != fin) {
                     fin.close()
                 }
             }

             val base64EncodedFile = sb.toString()
         }
     }
}