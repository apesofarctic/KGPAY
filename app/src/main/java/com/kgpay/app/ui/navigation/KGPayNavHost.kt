package com.kgpay.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kgpay.app.KGPayApp
import com.kgpay.app.data.repository.AppSettings
import com.kgpay.app.ui.balance.CheckBalanceScreen
import com.kgpay.app.ui.contacts.ContactsScreen
import com.kgpay.app.ui.history.TransactionHistoryScreen
import com.kgpay.app.ui.home.HomeScreen
import com.kgpay.app.ui.insights.InsightsScreen
import com.kgpay.app.ui.onboarding.OnboardingScreen
import com.kgpay.app.ui.onboarding.PermissionsScreen
import com.kgpay.app.ui.scan.ScanQrScreen
import com.kgpay.app.ui.send.ConfirmSendScreen
import com.kgpay.app.ui.send.EnterPinScreen
import com.kgpay.app.ui.send.PaymentPurpose
import com.kgpay.app.ui.send.PaymentResultScreen
import com.kgpay.app.ui.send.PaymentViewModel
import com.kgpay.app.ui.send.SendMoneyScreen
import com.kgpay.app.ui.settings.SettingsScreen
import com.kgpay.app.ui.theme.Strings
import kotlinx.coroutines.launch

@Composable
fun KGPayNavHost(app: KGPayApp, settings: AppSettings, strings: Strings, deepLinkUpiId: String?) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = Dest.bottomNav.any { dest -> backStackEntry?.destination?.hierarchy?.any { it.route == dest.route } == true }
    val scope = rememberCoroutineScope()

    val paymentViewModel: PaymentViewModel = viewModel(factory = PaymentViewModel.factory(app))
    val startDestination = if (settings.onboardingComplete) Dest.Home.route else Dest.Onboarding.route

    Scaffold(
        bottomBar = { if (showBottomBar) KGPayBottomBar(navController, strings) },
    ) { padding ->
        NavHost(navController = navController, startDestination = startDestination, modifier = androidx.compose.ui.Modifier.padding(padding)) {
            composable(Dest.Onboarding.route) {
                OnboardingScreen(strings) { navController.navigate(Dest.Permissions.route) }
            }
            composable(Dest.Permissions.route) {
                PermissionsScreen(strings) {
                    scope.launch { app.settingsRepository.setOnboardingComplete(true) }
                    navController.navigate(Dest.Home.route) { popUpTo(0) }
                }
            }
            composable(Dest.Home.route) {
                HomeScreen(
                    strings = strings,
                    transactionRepository = app.transactionRepository,
                    payeeRepository = app.payeeRepository,
                    onSend = { navController.navigate(Dest.SendMoney.route(null)) },
                    onScanAndPay = { navController.navigate(Dest.ScanQr.route) },
                    onCheckBalance = { navController.navigate(Dest.CheckBalance.route) },
                    onHistory = { navController.navigate(Dest.History.route) },
                    onPayFavorite = { upiId, name ->
                        paymentViewModel.payeeUpiId = upiId
                        paymentViewModel.payeeName = name
                        navController.navigate(Dest.SendMoney.route(upiId))
                    },
                )
            }
            composable(
                Dest.SendMoney.route,
                arguments = listOf(navArgument("upiId") { defaultValue = "" }),
            ) { backStack ->
                val upiId = backStack.arguments?.getString("upiId")?.takeIf { it.isNotBlank() } ?: deepLinkUpiId
                ScreenWithTopBar(strings.sendMoney, onBack = { navController.popBackStack() }) {
                    SendMoneyScreen(
                        strings = strings,
                        viewModel = paymentViewModel,
                        payeeRepository = app.payeeRepository,
                        prefillUpiId = upiId,
                        onScanQr = { navController.navigate(Dest.ScanQr.route) },
                        onReview = { navController.navigate(Dest.ConfirmSend.route) },
                    )
                }
            }
            composable(Dest.ScanQr.route) {
                ScreenWithTopBar(strings.scanQr, onBack = { navController.popBackStack() }) {
                    ScanQrScreen(strings) { upiId ->
                        paymentViewModel.payeeUpiId = upiId
                        navController.popBackStack()
                    }
                }
            }
            composable(Dest.ConfirmSend.route) {
                ScreenWithTopBar(strings.reviewAndConfirm, onBack = { navController.popBackStack() }) {
                    ConfirmSendScreen(
                        strings = strings,
                        viewModel = paymentViewModel,
                        onEditDetails = { navController.popBackStack() },
                        onConfirmed = { navController.navigate(Dest.EnterPin.route(PaymentPurpose.SEND_MONEY.name)) },
                    )
                }
            }
            composable(Dest.CheckBalance.route) {
                ScreenWithTopBar(strings.checkBalance, onBack = { navController.popBackStack() }) {
                    CheckBalanceScreen(strings, paymentViewModel) {
                        navController.navigate(Dest.EnterPin.route(PaymentPurpose.CHECK_BALANCE.name))
                    }
                }
            }
            composable(Dest.EnterPin.route) {
                ScreenWithTopBar(strings.enterUpiPin, onBack = { navController.popBackStack() }) {
                    EnterPinScreen(strings, paymentViewModel, settings.simSlot) {
                        navController.navigate(Dest.PaymentResult.route) { popUpTo(Dest.Home.route) }
                    }
                }
            }
            composable(Dest.PaymentResult.route) {
                PaymentResultScreen(
                    strings = strings,
                    viewModel = paymentViewModel,
                    onSaveFavorite = {
                        scope.launch { app.payeeRepository.addPayee(paymentViewModel.payeeName, paymentViewModel.payeeUpiId) }
                    },
                    onBackHome = {
                        paymentViewModel.resetForNewPayment()
                        navController.navigate(Dest.Home.route) { popUpTo(Dest.Home.route) { inclusive = true } }
                    },
                    onTryAgain = {
                        navController.navigate(
                            if (paymentViewModel.purpose == PaymentPurpose.SEND_MONEY) Dest.SendMoney.route(null) else Dest.CheckBalance.route,
                        ) { popUpTo(Dest.Home.route) }
                    },
                )
            }
            composable(Dest.History.route) { TransactionHistoryScreen(strings, app.transactionRepository, settings.simSlot) }
            composable(Dest.Insights.route) { InsightsScreen(strings, app.transactionRepository) }
            composable(Dest.Contacts.route) { ContactsScreen(strings, app.payeeRepository) }
            composable(Dest.Settings.route) { SettingsScreen(strings, settings, app.settingsRepository) }
        }
    }
}
