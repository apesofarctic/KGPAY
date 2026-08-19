package com.kgpay.app.data.ussd

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView

/**
 * Watches the OS dialer's `*99#` USSD AlertDialog (shown by com.android.phone) and drives it:
 * fills in the UPI PIN / amount / UPI ID / remarks and taps Send / OK, so the user doesn't have
 * to manually step through each bank prompt. This is a straight Kotlin port of the original
 * `UssdService.java` — same detection heuristics, same dialog codes.
 *
 * Security note: the UPI PIN arrives via the launching [Intent] each time (see
 * [com.kgpay.app.data.ussd.UssdLauncher]) and is held only in memory for the life of this one
 * USSD session — it is never written to Room/DataStore/logs. The caller is expected to have
 * already shown the user an explicit "Confirm & Send" summary before starting this service;
 * this service does not add its own extra confirmation, it only automates the OS dialog.
 */
class UssdAccessibilityService : AccessibilityService() {

    private var serviceCode: Int = -1
    private var upiPin: String = ""
    private var upiId: String = ""
    private var amount: String = ""
    private var remarks: String = ""

    private var currentDialogBox: Int = -1
    private var dialogText: String = ""
    private var sendButton: AccessibilityNodeInfo? = null
    private var okButton: AccessibilityNodeInfo? = null
    private var cancelButton: AccessibilityNodeInfo? = null
    private var editText: AccessibilityNodeInfo? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        upiPin = intent?.getStringExtra(EXTRA_UPI_PIN).orEmpty()
        upiId = intent?.getStringExtra(EXTRA_UPI_ID).orEmpty()
        amount = intent?.getStringExtra(EXTRA_AMOUNT).orEmpty()
        remarks = intent?.getStringExtra(EXTRA_REMARKS).orEmpty()
        serviceCode = intent?.getIntExtra(EXTRA_SERVICE_CODE, -1) ?: -1
        if (transactionHistoryBuffer == null) transactionHistoryBuffer = StringBuilder()
        return START_STICKY
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        currentDialogBox = -1
        sendButton = null
        okButton = null
        cancelButton = null
        editText = null
        dialogText = ""

        if (event.className != "android.app.AlertDialog") return
        val root = event.source ?: return
        runCatching { collectDialog(root) }

        // Invalid PIN can surface for both balance-check and transfer flows.
        if (containsAll(dialogText, "Invalid", "UPI", "PIN,")) {
            okButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            emit(UssdResult.InvalidPin)
            return
        }

        when (serviceCode) {
            Codes.SERVICE_CHECK_BALANCE -> handleCheckBalance()
            Codes.SERVICE_TRANSACTIONS_HISTORY -> handleTransactionHistory()
            Codes.SERVICE_TRANSFER_UPI -> handleTransferUpi()
        }

        if (currentDialogBox == Codes.DIALOG_CONNECTION_ERROR) {
            okButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            emit(UssdResult.ConnectionError)
        }
    }

    private fun handleCheckBalance() {
        if (currentDialogBox == Codes.DIALOG_ENTER_PIN) {
            fillAndSend(upiPin)
        }
        if (currentDialogBox == Codes.DIALOG_SUCCESS_CHECK_BALANCE) {
            okButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            emit(UssdResult.CheckBalanceSuccess(dialogText))
        }
    }

    private fun handleTransactionHistory() {
        val sentPattern = containsAll(dialogText, "Sent", "Rs.", "to", "on")
        val failedPattern = containsAll(dialogText, "Failed", "Rs.", "to", "send")
        if (sentPattern || failedPattern || dialogText.contains("- Next") || dialogText.contains("* More")) {
            currentDialogBox = Codes.DIALOG_TRANSACTIONS_HISTORY
            transactionHistoryBuffer?.append(dialogText)

            if (editText != null && sendButton != null) {
                when {
                    dialogText.contains("- Next") -> {
                        fillDataInTextField(editText, "-")
                        sendButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        return
                    }
                    dialogText.contains("* More") -> {
                        fillDataInTextField(editText, "*")
                        sendButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        return
                    }
                }
            }
            if (cancelButton != null && dialogText.contains("00. Back")) {
                cancelButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                val full = transactionHistoryBuffer?.toString().orEmpty()
                transactionHistoryBuffer = StringBuilder()
                emit(UssdResult.TransactionHistory(full))
            }
        }
    }

    private fun handleTransferUpi() {
        when (currentDialogBox) {
            Codes.DIALOG_ENTER_UPI_ID -> fillAndSend(upiId)
            Codes.DIALOG_ENTER_AMOUNT -> fillAndSend(amount)
            Codes.DIALOG_ENTER_REMARKS -> fillAndSend(remarks)
            Codes.DIALOG_ENTER_PIN -> {
                if (editText != null && sendButton != null) {
                    fillDataInTextField(editText, upiPin)
                    val detailsMatch = containsAll(dialogText, "UPI ID-$upiId", "Amount $amount")
                    if (detailsMatch) {
                        sendButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    } else {
                        cancelButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
            }
            Codes.DIALOG_SUCCESS_TRANSFER_UPI -> {
                cancelButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                emit(UssdResult.TransferSuccess(dialogText))
            }
        }
    }

    private fun fillAndSend(value: String) {
        if (editText != null && sendButton != null) {
            fillDataInTextField(editText, value)
            sendButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    /** Depth-first walk of the alert dialog tree, mirrors the original `dfs()`. */
    private fun collectDialog(node: AccessibilityNodeInfo) {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val txt = child.text
            val className = child.className?.toString()

            when {
                txt != null && className == Button::class.java.name -> {
                    when (txt.toString().trim().lowercase()) {
                        "send" -> sendButton = child
                        "ok" -> okButton = child
                        "cancel" -> cancelButton = child
                    }
                }
                txt != null -> {
                    dialogText = if (dialogText.isEmpty()) txt.toString() else "$dialogText ${txt}"
                    classifyDialog(txt.toString())
                }
                className == ScrollView::class.java.name -> collectDialog(child)
                className == EditText::class.java.name -> editText = child
            }
        }
    }

    private fun classifyDialog(txt: String) {
        if (containsAll(txt, "Enter", "UPI", "PIN")) currentDialogBox = Codes.DIALOG_ENTER_PIN
        if (containsAll(txt, "Enter", "Amount", "in", "Rs")) currentDialogBox = Codes.DIALOG_ENTER_AMOUNT
        if (containsAll(txt, "Enter", "a", "Remark", "to", "skip")) currentDialogBox = Codes.DIALOG_ENTER_REMARKS
        if (containsAll(txt, "Enter", "UPI", "ID")) currentDialogBox = Codes.DIALOG_ENTER_UPI_ID
        if (containsAll(txt, "Your", "account", "balance", "is", "Rs")) currentDialogBox = Codes.DIALOG_SUCCESS_CHECK_BALANCE
        if (containsAll(txt, "Your", "payment", "to", "is", "successful")) currentDialogBox = Codes.DIALOG_SUCCESS_TRANSFER_UPI
        if (containsAll(txt, "Connection", "Problem", "or", "Invalid", "MMI", "code")) currentDialogBox = Codes.DIALOG_CONNECTION_ERROR
    }

    private fun fillDataInTextField(edit: AccessibilityNodeInfo?, value: String?) {
        if (edit == null || value == null) return
        val bundle = Bundle().apply {
            putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value)
        }
        edit.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
    }

    /** True if every token in [tokens] appears as a whole word somewhere in [text] (case-insensitive). */
    private fun containsAll(text: String, vararg tokens: String): Boolean {
        val words = text.split(' ', '.', ',', '\n', '(', ')')
            .filter { it.isNotEmpty() }
            .map { it.lowercase() }
            .toHashSet()
        return tokens.all { words.contains(it.trim().lowercase()) }
    }

    private fun emit(result: UssdResult) {
        upiPin = "" // never held longer than one USSD session
        callback?.invoke(result)
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = AccessibilityServiceInfo().apply {
            flags = AccessibilityServiceInfo.DEFAULT
            packageNames = arrayOf("com.android.phone")
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        }
    }

    companion object {
        const val EXTRA_UPI_PIN = "upi_pin"
        const val EXTRA_UPI_ID = "upi_id"
        const val EXTRA_AMOUNT = "amount"
        const val EXTRA_REMARKS = "remarks"
        const val EXTRA_SERVICE_CODE = "service_code"

        /** Set by whichever screen just launched a USSD session; cleared once it reports back. */
        var callback: ((UssdResult) -> Unit)? = null

        private var transactionHistoryBuffer: StringBuilder? = null
    }
}
