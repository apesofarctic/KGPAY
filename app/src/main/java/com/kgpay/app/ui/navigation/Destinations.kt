package com.kgpay.app.ui.navigation

sealed class Dest(val route: String) {
    object Onboarding : Dest("onboarding")
    object Permissions : Dest("permissions")

    object Home : Dest("home")
    object SendMoney : Dest("send_money?upiId={upiId}") {
        fun route(upiId: String? = null) = "send_money?upiId=${upiId ?: ""}"
    }
    object ConfirmSend : Dest("confirm_send")
    object EnterPin : Dest("enter_pin/{purpose}") {
        fun route(purpose: String) = "enter_pin/$purpose"
    }
    object PaymentResult : Dest("payment_result")
    object CheckBalance : Dest("check_balance")
    object ScanQr : Dest("scan_qr")
    object History : Dest("history")
    object Insights : Dest("insights")
    object Contacts : Dest("contacts")
    object Settings : Dest("settings")

    companion object {
        /** Bottom nav destinations, in display order. */
        val bottomNav = listOf(Home, History, Insights, Contacts, Settings)
    }
}
