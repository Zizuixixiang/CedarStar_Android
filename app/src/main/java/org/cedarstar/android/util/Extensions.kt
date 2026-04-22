package org.cedarstar.android.util

import java.util.Locale

fun Float.formatMoney(): String = String.format(Locale.getDefault(), "%.2f", this)
