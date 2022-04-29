package com.github.calo001.nigma.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun Bitmap.split(xCount: Int, yCount: Int): Array<Array<Bitmap?>> {
    // Allocate a two dimensional array to hold the individual images.
    val bitmaps = Array(xCount) {
        arrayOfNulls<Bitmap>(
            yCount
        )
    }
    // Divide the original bitmap width by the desired vertical column count
    val width: Int = this.width / xCount
    // Divide the original bitmap height by the desired horizontal row count
    val height: Int = this.height / yCount
    // Loop the array and create bitmaps for each coordinate
    for (x in 0 until xCount) {
        for (y in 0 until yCount) {
            // Create the sliced bitmap
            bitmaps[x][y] = Bitmap.createBitmap(this, x * width, y * height, width, height)
        }
    }
    // Return the array
    return bitmaps
}

fun Bitmap.toFile(fileNameToSave: String, context: Context): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        //Environment.getExternalStorageDirectory().toString()
        val externalUri = context.cacheDir
        file = File(externalUri.toString() + File.separator + fileNameToSave.replace(":", "_"))
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}