package com.kgpay.app.data.ussd

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import androidx.core.app.ActivityCompat

/**
 * Dials a USSD code (e.g. `*99*1*3#`) on the chosen SIM and starts [UssdAccessibilityService]
 * to drive the resulting dialog. Kotlin port of `PhoneCall.java` / the dialing half of
 * `MainActivity.java`. The result comes back asynchronously via [UssdAccessibilityService.callback].
 */
object UssdLauncher {

    private val SIM_SLOT_EXTRA_NAMES = arrayOf(
        "extra_asus_dial_use_dualsim", "com.android.phone.extra.slot", "slot", "simslot",
        "sim_slot", "subscription", "Subscription", "phone", "com.android.phone.DialingMode",
        "simSlot", "slot_id", "simId", "simnum", "phone_type", "slotId", "slotIdx"
    )

    data class Request(
        val ussdCode: String,
        val simSlot: Int,
        val serviceCode: Int,
        val upiPin: String = "",
        val upiId: String = "",
        val amount: String = "",
        val remarks: String = "",
    )

    fun dial(activity: Activity, request: Request, onResult: (UssdResult) -> Unit) {
        UssdAccessibilityService.callback = onResult

        val serviceIntent = Intent(activity, UssdAccessibilityService::class.java).apply {
            putExtra(UssdAccessibilityService.EXTRA_SERVICE_CODE, request.serviceCode)
            putExtra(UssdAccessibilityService.EXTRA_UPI_PIN, request.upiPin)
            putExtra(UssdAccessibilityService.EXTRA_UPI_ID, request.upiId)
            putExtra(UssdAccessibilityService.EXTRA_AMOUNT, request.amount)
            putExtra(UssdAccessibilityService.EXTRA_REMARKS, request.remarks)
        }
        activity.startService(serviceIntent)

        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = ussdUri(request.ussdCode)
            putExtra("com.android.phone.force.slot", true)
            putExtra("Cdma_Supp", true)
            for (name in SIM_SLOT_EXTRA_NAMES) putExtra(name, request.simSlot)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
            ) return
            val telecomManager = activity.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val handles = runCatching { telecomManager.callCapablePhoneAccounts }.getOrNull()
            if (handles != null && handles.size > request.simSlot) {
                callIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", handles[request.simSlot])
            }
        }

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) return
        activity.startActivity(callIntent)
    }

    fun stop(context: Context) {
        context.stopService(Intent(context, UssdAccessibilityService::class.java))
        UssdAccessibilityService.callback = null
    }

    private fun ussdUri(code: String): Uri {
        val trimmed = if (code.endsWith("#")) code.dropLast(1).trim() else code
        return Uri.parse("tel:$trimmed${Uri.encode("#")}")
    }
}
