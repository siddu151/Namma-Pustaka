package com.example.nammapustaka.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun format(d: Date): String = fmt.format(d)
}
