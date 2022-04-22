package com.github.calo001.nigma.ui.model

import android.graphics.Bitmap

/*
  0 -- 3 -- 6
  |    |    |
  1 -- 4 -- 7
  |    |    |
  2 -- 5 -- 8
 */

data class GridItem(val originalPosition: Int, val bitmap: Bitmap)