package com.github.calo001.nigma.util

import android.graphics.Bitmap

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