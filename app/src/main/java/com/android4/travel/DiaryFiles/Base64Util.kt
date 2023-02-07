package com.android4.travel.DiaryFiles

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.NO_WRAP
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
     }
}