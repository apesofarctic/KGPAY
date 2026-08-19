package com.kgpay.app.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val inrFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

fun formatInr(amount: Double): String = inrFormatter.format(amount)

fun formatInr(amount: String): String = amount.toDoubleOrNull()?.let { formatInr(it) } ?: "₹--"

private val dateTimeFormatter = SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())

fun formatDateTime(millis: Long): String = dateTimeFormatter.format(Date(millis))
