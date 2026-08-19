package com.kgpay.app.ui.theme

import com.kgpay.app.data.repository.AppLanguage

/**
 * In-app language switch (Settings > Language) independent of the device locale — deliberately
 * simple (a plain data class with two instances) rather than Android string-resource
 * configuration, since the whole point is a language toggle the user controls from inside KGPay.
 */
data class Strings(
    val appTagline: String,
    val onboardingTitle1: String, val onboardingBody1: String,
    val onboardingTitle2: String, val onboardingBody2: String,
    val onboardingTitle3: String, val onboardingBody3: String,
    val getStarted: String, val skip: String, val next: String,
    val grantPermissions: String, val permissionsBody: String,
    val enableAccessibility: String, val accessibilityBody: String,
    val allowRuntimePerms: String,
    val continueLabel: String,

    val homeLabel: String,
    val homeGreeting: String,
    val yourBalance: String, val tapToReveal: String, val checkBalance: String,
    val quickActions: String, val send: String, val scanAndPay: String, val history: String, val insights: String,
    val favorites: String, val recentActivity: String, val seeAll: String, val noTransactionsYet: String,
    val networkPoorBanner: String, val networkStableBanner: String,

    val sendMoney: String, val payTo: String, val enterUpiId: String, val orChooseFavorite: String,
    val amount: String, val remarksOptional: String, val reviewAndConfirm: String,

    val confirmTitle: String, val confirmBody: String, val confirmAndSend: String, val editDetails: String,
    val enterUpiPin: String, val pinNeverStored: String, val submit: String,

    val paymentSuccessful: String, val paymentFailed: String, val referenceId: String, val paidTo: String,
    val saveAsFavorite: String, val backToHome: String, val tryAgain: String,
    val wrongPin: String, val connectionError: String,

    val transactionHistory: String, val searchTransactions: String, val filterAll: String,
    val filterSuccess: String, val filterFailed: String, val fromUssd: String,

    val spendInsights: String, val totalSpend: String, val topPayees: String, val byCategory: String, val thisMonth: String,

    val contacts: String, val addContact: String, val nickname: String, val save: String,

    val settings: String, val language: String, val darkMode: String, val appLock: String,
    val appLockBody: String, val lowBalanceAlerts: String, val simCard: String, val sim1: String, val sim2: String,
    val helpAndSupport: String, val about: String, val version: String,

    val scanQr: String, val pointCameraAtQr: String,
)

val EnglishStrings = Strings(
    appTagline = "UPI payments that work even without the internet",
    onboardingTitle1 = "Pay over USSD, no data needed",
    onboardingBody1 = "KGPay dials your bank's *99# UPI menu for you and reads the reply — works on any network, even 2G.",
    onboardingTitle2 = "You confirm every payment",
    onboardingBody2 = "Nothing is sent until you review the amount and payee and tap Confirm & Send.",
    onboardingTitle3 = "Your PIN is never stored",
    onboardingBody3 = "You enter your UPI PIN fresh for each payment. KGPay never saves it on the device.",
    getStarted = "Get started", skip = "Skip", next = "Next",
    grantPermissions = "A few permissions to continue",
    permissionsBody = "KGPay needs phone-call and accessibility access to dial *99# and read the bank's replies on your behalf.",
    enableAccessibility = "Enable accessibility service",
    accessibilityBody = "Required so KGPay can read and fill the bank's USSD dialog.",
    allowRuntimePerms = "Allow phone permissions",
    continueLabel = "Continue",

    homeLabel = "Home",
    homeGreeting = "Hi there",
    yourBalance = "Your balance", tapToReveal = "Tap to check", checkBalance = "Check balance",
    quickActions = "Quick actions", send = "Send", scanAndPay = "Scan & Pay", history = "History", insights = "Insights",
    favorites = "Favorites", recentActivity = "Recent activity", seeAll = "See all", noTransactionsYet = "No transactions yet",
    networkPoorBanner = "Weak network detected — offline USSD payment recommended.",
    networkStableBanner = "Network looks good.",

    sendMoney = "Send money", payTo = "Pay to", enterUpiId = "Enter UPI ID", orChooseFavorite = "or choose a favorite",
    amount = "Amount", remarksOptional = "Remarks (optional)", reviewAndConfirm = "Review & confirm",

    confirmTitle = "Confirm this payment", confirmBody = "Please double check before sending — this cannot be undone.",
    confirmAndSend = "Confirm & Send", editDetails = "Edit details",
    enterUpiPin = "Enter your UPI PIN", pinNeverStored = "Never stored on this device.", submit = "Submit",

    paymentSuccessful = "Payment successful", paymentFailed = "Payment failed", referenceId = "Reference ID", paidTo = "Paid to",
    saveAsFavorite = "Save as favorite", backToHome = "Back to home", tryAgain = "Try again",
    wrongPin = "Wrong UPI PIN, please try again.", connectionError = "Connection problem, please try again.",

    transactionHistory = "Transaction history", searchTransactions = "Search transactions", filterAll = "All",
    filterSuccess = "Success", filterFailed = "Failed", fromUssd = "Synced from *99# on this SIM",

    spendInsights = "Spend insights", totalSpend = "Total spend", topPayees = "Top payees", byCategory = "By category", thisMonth = "This month",

    contacts = "Contacts", addContact = "Add contact", nickname = "Nickname", save = "Save",

    settings = "Settings", language = "Language", darkMode = "Dark mode", appLock = "App lock",
    appLockBody = "Require your device screen lock to open KGPay.", lowBalanceAlerts = "Low balance alerts",
    simCard = "SIM for USSD calls", sim1 = "SIM 1", sim2 = "SIM 2",
    helpAndSupport = "Help & support", about = "About", version = "Version",

    scanQr = "Scan QR", pointCameraAtQr = "Point your camera at a UPI QR code",
)

val HindiStrings = Strings(
    appTagline = "बिना इंटरनेट के भी यूपीआई भुगतान",
    onboardingTitle1 = "USSD से भुगतान करें, डेटा की ज़रूरत नहीं",
    onboardingBody1 = "KGPay आपकी बैंक की *99# यूपीआई मेन्यू खुद डायल करता है — किसी भी नेटवर्क पर काम करता है।",
    onboardingTitle2 = "हर भुगतान आप ही कन्फर्म करते हैं",
    onboardingBody2 = "जब तक आप राशि और प्राप्तकर्ता देखकर Confirm & Send नहीं दबाते, कुछ नहीं भेजा जाता।",
    onboardingTitle3 = "आपका पिन कभी सेव नहीं होता",
    onboardingBody3 = "हर भुगतान के लिए पिन नए सिरे से डालें। KGPay इसे डिवाइस पर सेव नहीं करता।",
    getStarted = "शुरू करें", skip = "छोड़ें", next = "आगे",
    grantPermissions = "जारी रखने के लिए अनुमतियाँ चाहिए",
    permissionsBody = "*99# डायल करने और बैंक के जवाब पढ़ने के लिए फ़ोन-कॉल और एक्सेसिबिलिटी अनुमति चाहिए।",
    enableAccessibility = "एक्सेसिबिलिटी सेवा चालू करें",
    accessibilityBody = "बैंक के USSD डायलॉग को पढ़ने के लिए ज़रूरी।",
    allowRuntimePerms = "फ़ोन अनुमतियाँ दें",
    continueLabel = "जारी रखें",

    homeLabel = "होम",
    homeGreeting = "नमस्ते",
    yourBalance = "आपका बैलेंस", tapToReveal = "देखने के लिए टैप करें", checkBalance = "बैलेंस जांचें",
    quickActions = "त्वरित कार्य", send = "भेजें", scanAndPay = "स्कैन और भुगतान", history = "इतिहास", insights = "जानकारी",
    favorites = "पसंदीदा", recentActivity = "हाल की गतिविधि", seeAll = "सभी देखें", noTransactionsYet = "अभी कोई लेनदेन नहीं",
    networkPoorBanner = "कमज़ोर नेटवर्क — ऑफ़लाइन USSD भुगतान बेहतर रहेगा।",
    networkStableBanner = "नेटवर्क ठीक है।",

    sendMoney = "पैसे भेजें", payTo = "किसे भेजें", enterUpiId = "यूपीआई आईडी डालें", orChooseFavorite = "या पसंदीदा चुनें",
    amount = "राशि", remarksOptional = "टिप्पणी (वैकल्पिक)", reviewAndConfirm = "समीक्षा करें और भेजें",

    confirmTitle = "यह भुगतान कन्फर्म करें", confirmBody = "भेजने से पहले कृपया जांच लें — इसे वापस नहीं लिया जा सकता।",
    confirmAndSend = "कन्फर्म और भेजें", editDetails = "विवरण बदलें",
    enterUpiPin = "अपना यूपीआई पिन डालें", pinNeverStored = "इस डिवाइस पर कभी सेव नहीं होता।", submit = "सबमिट करें",

    paymentSuccessful = "भुगतान सफल", paymentFailed = "भुगतान असफल", referenceId = "संदर्भ आईडी", paidTo = "भुगतान किया",
    saveAsFavorite = "पसंदीदा में सेव करें", backToHome = "होम पर जाएं", tryAgain = "फिर कोशिश करें",
    wrongPin = "गलत यूपीआई पिन, फिर कोशिश करें।", connectionError = "कनेक्शन समस्या, फिर कोशिश करें।",

    transactionHistory = "लेनदेन इतिहास", searchTransactions = "लेनदेन खोजें", filterAll = "सभी",
    filterSuccess = "सफल", filterFailed = "असफल", fromUssd = "इस सिम पर *99# से सिंक किया गया",

    spendInsights = "खर्च की जानकारी", totalSpend = "कुल खर्च", topPayees = "प्रमुख प्राप्तकर्ता", byCategory = "श्रेणी अनुसार", thisMonth = "इस महीने",

    contacts = "संपर्क", addContact = "संपर्क जोड़ें", nickname = "उपनाम", save = "सेव करें",

    settings = "सेटिंग्स", language = "भाषा", darkMode = "डार्क मोड", appLock = "ऐप लॉक",
    appLockBody = "KGPay खोलने के लिए डिवाइस स्क्रीन लॉक ज़रूरी करें।", lowBalanceAlerts = "कम बैलेंस अलर्ट",
    simCard = "USSD कॉल के लिए सिम", sim1 = "सिम 1", sim2 = "सिम 2",
    helpAndSupport = "सहायता", about = "ऐप के बारे में", version = "वर्शन",

    scanQr = "क्यूआर स्कैन करें", pointCameraAtQr = "यूपीआई क्यूआर कोड पर कैमरा रखें",
)

fun stringsFor(language: AppLanguage): Strings = when (language) {
    AppLanguage.ENGLISH -> EnglishStrings
    AppLanguage.HINDI -> HindiStrings
}
