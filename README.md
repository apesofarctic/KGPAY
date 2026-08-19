# KGPay

UPI payments over `*99#` USSD — works without a data connection. KGPay dials your bank's USSD
menu, reads its replies via an accessibility service, and fills in the PIN/amount/UPI ID for you,
so paying someone doesn't require a stable internet connection.

## What changed in this rewrite

**Stack:** Java + XML/AppCompat → **Kotlin + Jetpack Compose (Material 3)**, MVVM, Room, DataStore,
CameraX + ML Kit for QR scanning (replacing the deprecated `play-services-vision` barcode reader).
Package renamed `com.example.ussdtest3` → `com.kgpay.app`.

**Product additions** (the PM-lens pass):
- Onboarding + permissions flow explaining *why* KGPay needs accessibility/call permissions
- Home dashboard: balance, quick actions, favorites, recent activity, live network-quality banner
- Explicit **Confirm & Send** review screen before any USSD automation fires
- Favorites/contacts with nicknames, quick-pay chips
- Transaction history with search/filter, plus on-demand sync of the bank's own USSD history
- Spend insights: monthly total, category breakdown, top payees
- Settings: in-app language switch (English/Hindi), dark mode, biometric app-lock, SIM selection,
  low-balance alert toggle

**Security choices, carried through deliberately:**
- The UPI PIN is **never persisted** — it lives only in `PaymentViewModel` for the duration of one
  USSD session and is wiped the instant that session reports back, success or failure.
- Every payment requires an explicit in-app confirmation screen before the accessibility service
  is allowed to touch the bank's own USSD dialog — nothing is auto-submitted on your behalf.
- Optional device-credential/biometric app lock gates the whole app.

See `data/ussd/UssdAccessibilityService.kt` for why this app needs an `AccessibilityService` at
all: it's what lets KGPay read and drive the OS dialer's `*99#` AlertDialog without the user
tapping through each bank prompt by hand.

## Structure

```
data/local        Room entities/DAOs + database
data/repository   Transaction / Payee / Settings repositories
data/ussd         The actual USSD automation: Codes, UssdAccessibilityService, UssdLauncher,
                  UssdHistoryParser, NetworkQualityChecker, PermissionsHelper
domain/model      Plain Kotlin domain models
ui/…              One package per screen/flow, Compose Navigation ties them together in
                  ui/navigation/KGPayNavHost.kt
```

## Building

Requires a full Android SDK + emulator/device, neither of which were available in the environment
this rewrite was done in — the code was written and reviewed carefully but **not compiled**.
`./gradlew assembleDebug` from Android Studio (Giraffe+ / AGP 8.3) is the fastest way to surface
anything that doesn't build cleanly.
