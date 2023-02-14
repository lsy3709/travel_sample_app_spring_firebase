package com.android4.travel.DiaryFiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

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

         fun mp4ToBase64(filePath: String):String? {
             var inputStream: FileInputStream? = null
             return try {
                 inputStream = FileInputStream(filePath)
                 val videoBytes = ByteArray(inputStream.available())
                 inputStream.read(videoBytes)
                 Base64.encodeToString(videoBytes, Base64.DEFAULT)
             } catch (e: Exception) {
                 null
             } finally {
                 try {
                     inputStream?.close()
                 } catch (e: Exception) { }
             }
         }

         fun Base64Tomp4(base64Encoded: String,filePath: String):Boolean {
             var outputStream: FileOutputStream? = null
             return try {
                 val videoBytes = Base64.decode(base64Encoded, Base64.DEFAULT)
                 outputStream = FileOutputStream(filePath)
                 outputStream.write(videoBytes)
                 true
             } catch (e: Exception) {
                 false
             } finally {
                 try {
                     outputStream?.close()
                 } catch (e: Exception) { }
             }
         }


     }
}