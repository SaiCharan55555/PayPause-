package com.example.paypause

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


data class PaymentRecord(
    val recipient: String,
    val amount: String,
    val status: String
)


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var loggedIn by remember { mutableStateOf(false) }

            var showCreateAccount by remember {
                mutableStateOf(false)
            }

            var showPaymentScreen by remember {
                mutableStateOf(false)
            }

            var showRiskCheck by remember {
                mutableStateOf(false)
            }

            var showRiskDetails by remember {
                mutableStateOf(false)
            }

            var showReviewScreen by remember {
                mutableStateOf(false)
            }

            var showSecurityDashboard by remember {
                mutableStateOf(false)
            }

            var paymentRecipient by remember {
                mutableStateOf("")
            }

            var paymentAmount by remember {
                mutableStateOf("")
            }

            var balanceVisible by remember {
                mutableStateOf(false)
            }

            var paymentHistory by remember {
                mutableStateOf(listOf<PaymentRecord>())
            }

            MaterialTheme {

                when {

                    // -------------------------
                    // LOGIN
                    // -------------------------

                    !loggedIn && !showCreateAccount -> {

                        LoginScreen(

                            onLoginSuccess = {
                                loggedIn = true
                            },

                            onCreateAccount = {
                                showCreateAccount = true
                            }
                        )
                    }


                    // -------------------------
                    // CREATE ACCOUNT
                    // -------------------------

                    !loggedIn && showCreateAccount -> {

                        CreateAccountScreen(

                            onAccountCreated = {

                                showCreateAccount = false
                                loggedIn = true
                            },

                            onBackToLogin = {

                                showCreateAccount = false
                            }
                        )
                    }


                    // -------------------------
                    // SECURITY DASHBOARD
                    // -------------------------

                    loggedIn && showSecurityDashboard -> {

                        SecurityDashboardScreen(

                            paymentHistory = paymentHistory,

                            onBack = {
                                showSecurityDashboard = false
                            }
                        )
                    }


                    // -------------------------
                    // REVIEW PAYMENT
                    // -------------------------

                    loggedIn && showReviewScreen -> {

                        ReviewPaymentScreen(

                            recipient = paymentRecipient,

                            amount = paymentAmount,

                            onConfirm = {

                                paymentHistory = listOf(

                                    PaymentRecord(
                                        paymentRecipient,
                                        paymentAmount,
                                        "✓ Confirmed"
                                    )

                                ) + paymentHistory

                                showReviewScreen = false
                                showPaymentScreen = false
                                showRiskCheck = false
                                showRiskDetails = false

                                Toast.makeText(
                                    this@MainActivity,
                                    "Payment confirmed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onCancel = {

                                paymentHistory = listOf(

                                    PaymentRecord(
                                        paymentRecipient,
                                        paymentAmount,
                                        "✕ Cancelled"
                                    )

                                ) + paymentHistory

                                showReviewScreen = false
                                showPaymentScreen = false
                                showRiskCheck = false
                                showRiskDetails = false

                                Toast.makeText(
                                    this@MainActivity,
                                    "Payment cancelled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }


                    // -------------------------
                    // RISK DETAILS
                    // -------------------------

                    loggedIn && showRiskDetails -> {

                        RiskDetailsScreen(

                            recipient = paymentRecipient,

                            amount = paymentAmount,

                            onPausePayment = {

                                showRiskDetails = false
                                showReviewScreen = true
                            },

                            onGoBack = {

                                showRiskDetails = false
                                showRiskCheck = true
                            }
                        )
                    }


                    // -------------------------
                    // RISK CHECK
                    // -------------------------

                    loggedIn && showRiskCheck -> {

                        RiskCheckScreen(

                            recipient = paymentRecipient,

                            amount = paymentAmount,

                            onViewRiskDetails = {

                                showRiskCheck = false
                                showRiskDetails = true
                            },

                            onAllowPayment = {

                                paymentHistory = listOf(

                                    PaymentRecord(
                                        paymentRecipient,
                                        paymentAmount,
                                        "✓ Allowed"
                                    )

                                ) + paymentHistory

                                showRiskCheck = false
                                showPaymentScreen = false

                                Toast.makeText(
                                    this@MainActivity,
                                    "Payment allowed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onPausePayment = {

                                showRiskCheck = false
                                showReviewScreen = true
                            }
                        )
                    }


                    // -------------------------
                    // MAKE PAYMENT
                    // -------------------------

                    loggedIn && showPaymentScreen -> {

                        MakePaymentScreen(

                            recipient = paymentRecipient,

                            amount = paymentAmount,

                            onRecipientChange = {
                                paymentRecipient = it
                            },

                            onAmountChange = {
                                paymentAmount = it
                            },

                            onContinue = {

                                if (
                                    paymentRecipient.isNotBlank() &&
                                    paymentAmount.isNotBlank()
                                ) {

                                    showPaymentScreen = false
                                    showRiskCheck = true

                                } else {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Please enter recipient and amount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            onBack = {

                                showPaymentScreen = false
                            }
                        )
                    }


                    // -------------------------
                    // MAIN DASHBOARD
                    // -------------------------

                    else -> {

                        DashboardScreen(

                            balanceVisible = balanceVisible,

                            onToggleBalance = {

                                balanceVisible =
                                    !balanceVisible
                            },

                            paymentHistory = paymentHistory,

                            onMakePayment = {

                                paymentRecipient = ""
                                paymentAmount = ""

                                showPaymentScreen = true
                            },

                            onSecurityDashboard = {

                                showSecurityDashboard = true
                            },

                            onLogout = {

                                loggedIn = false

                                showPaymentScreen = false
                                showRiskCheck = false
                                showRiskDetails = false
                                showReviewScreen = false
                                showSecurityDashboard = false
                            }
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// LOGIN SCREEN
// ============================================================

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit
) {

    val context = LocalContext.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Image(

            painter = painterResource(
                id = R.drawable.paypause_logo
            ),

            contentDescription = "PayPause Logo",

            modifier = Modifier.size(180.dp)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(

            text = "A breath before it's gone",

            fontSize = 18.sp,

            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(

            onClick = {

                if (
                    email.isNotBlank() &&
                    password.isNotBlank()
                ) {

                    onLoginSuccess()

                } else {

                    Toast.makeText(
                        context,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text(
                text = "Login",
                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(

            onClick = {

                val biometricManager =
                    BiometricManager.from(context)

                if (
                    biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                    ) ==
                    BiometricManager.BIOMETRIC_SUCCESS
                ) {

                    val executor =
                        ContextCompat.getMainExecutor(context)

                    val biometricPrompt =
                        BiometricPrompt(

                            context as FragmentActivity,

                            executor,

                            object :
                                BiometricPrompt.AuthenticationCallback() {

                                override fun
                                        onAuthenticationSucceeded(
                                    result: BiometricPrompt.AuthenticationResult
                                ) {

                                    super.onAuthenticationSucceeded(
                                        result
                                    )

                                    onLoginSuccess()
                                }

                                override fun
                                        onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence
                                ) {

                                    super.onAuthenticationError(
                                        errorCode,
                                        errString
                                    )

                                    Toast.makeText(
                                        context,
                                        errString,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )

                    val promptInfo =
                        BiometricPrompt.PromptInfo.Builder()

                            .setTitle(
                                "PayPause Login"
                            )

                            .setSubtitle(
                                "Use fingerprint or face recognition"
                            )

                            .setNegativeButtonText(
                                "Cancel"
                            )

                            .build()

                    biometricPrompt.authenticate(
                        promptInfo
                    )

                } else {

                    Toast.makeText(
                        context,
                        "Biometric authentication is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            )
        ) {

            Text(
                "🔐 Login with Biometrics"
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onCreateAccount
        ) {

            Text(

                text = "Create Account",

                color = orange,

                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ============================================================
// CREATE ACCOUNT
// ============================================================

@Composable
fun CreateAccountScreen(
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {

    val context = LocalContext.current

    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(

            text = "Create Your PayPause Account",

            fontSize = 25.sp,

            fontWeight = FontWeight.Bold,

            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        OutlinedTextField(

            value = fullName,

            onValueChange = {
                fullName = it
            },

            label = {
                Text("Full Name")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(

            value = confirmPassword,

            onValueChange = {
                confirmPassword = it
            },

            label = {
                Text("Confirm Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(

            onClick = {

                when {

                    fullName.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Please enter your full name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    email.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Please enter your email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password.isBlank() -> {

                        Toast.makeText(
                            context,
                            "Please enter a password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password != confirmPassword -> {

                        Toast.makeText(
                            context,
                            "Passwords do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                        Toast.makeText(
                            context,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        onAccountCreated()
                    }
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text("Create Account")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextButton(
            onClick = onBackToLogin
        ) {

            Text("Back to Login")
        }
    }
}


// ============================================================
// MAIN DASHBOARD
// ============================================================

@Composable
fun DashboardScreen(

    balanceVisible: Boolean,

    onToggleBalance: () -> Unit,

    paymentHistory: List<PaymentRecord>,

    onMakePayment: () -> Unit,

    onSecurityDashboard: () -> Unit,

    onLogout: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(

                text = "PayPause",

                fontSize = 28.sp,

                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onLogout
            ) {

                Text(

                    text = "Logout",

                    color = orange
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(

            text = "Welcome back!",

            fontSize = 23.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // BALANCE CARD

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Available Balance",
                        fontSize = 15.sp
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(

                        text =
                            if (balanceVisible)
                                "$2,450.00"
                            else
                                "$****.**",

                        fontSize = 28.sp,

                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onToggleBalance
                ) {

                    Text(

                        text =
                            if (balanceVisible)
                                "🙈"
                            else
                                "👁",

                        fontSize = 25.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // PROTECTION CARD

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "🛡 PayPause Protection",

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(

                    text = "● ACTIVE",

                    color = Color(0xFF2E7D32),

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Your payments are being monitored for unusual activity."
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(

                    text =
                        "Last security check: Just now",

                    fontSize = 13.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // SECURITY DASHBOARD BUTTON

        Button(

            onClick = onSecurityDashboard,

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            )
        ) {

            Text(
                text = "🛡 View Security Dashboard",
                fontSize = 16.sp
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // PAYMENT BUTTON

        Button(

            onClick = onMakePayment,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text(

                text = "Make a Payment",

                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(

            text = "Sample Account Activity",

            fontSize = 20.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ActivityRow(
            name = "Amazon",
            amount = "-$84.99"
        )

        ActivityRow(
            name = "Walmart",
            amount = "-$52.30"
        )

        ActivityRow(
            name = "Direct Deposit",
            amount = "+$1,500.00"
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(

            text = "Payment History",

            fontSize = 20.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (paymentHistory.isEmpty()) {

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Text(

                    text =
                        "No PayPause payments yet.\nYour payment decisions will appear here.",

                    modifier = Modifier.padding(18.dp),

                    textAlign = TextAlign.Center
                )
            }

        } else {

            paymentHistory.forEach { payment ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(

                            text = payment.recipient,

                            fontWeight = FontWeight.Bold,

                            fontSize = 17.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = payment.amount,
                            fontSize = 16.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(

                            text = payment.status,

                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// SECURITY DASHBOARD
// ============================================================

@Composable
fun SecurityDashboardScreen(

    paymentHistory: List<PaymentRecord>,

    onBack: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TextButton(
            onClick = onBack
        ) {

            Text(
                text = "← Back",
                color = orange,
                fontSize = 16.sp
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(

            text = "Security Dashboard",

            fontSize = 28.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                "Your PayPause security status at a glance.",
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // OVERALL SECURITY STATUS

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "🛡 PayPause Protection",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(

                    text = "● ACTIVE",

                    color = Color(0xFF2E7D32),

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Your payment activity is being monitored."
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // AUTHENTICATION

        SecurityStatusCard(

            title = "🔐 Authentication",

            status = "VERIFIED",

            description =
                "Your current login session has been authenticated."
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // DEVICE

        SecurityStatusCard(

            title = "📱 Current Device",

            status = "TRUSTED",

            description =
                "This device is currently associated with your PayPause session."
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // SECURITY CHECK

        SecurityStatusCard(

            title = "✓ Security Check",

            status = "PASSED",

            description =
                "No additional security action is required right now."
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // LAST CHECK

        SecurityStatusCard(

            title = "🕐 Last Security Check",

            status = "JUST NOW",

            description =
                "PayPause most recently checked this session."
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(

            text = "Recent Security Activity",

            fontSize = 21.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (paymentHistory.isEmpty()) {

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Text(

                    text =
                        "No security decisions yet.\nPayment risk decisions will appear here.",

                    modifier = Modifier.padding(18.dp),

                    textAlign = TextAlign.Center
                )
            }

        } else {

            paymentHistory.take(5).forEach { payment ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(

                            text = payment.status,

                            fontWeight = FontWeight.Bold,

                            fontSize = 16.sp
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Payment to ${payment.recipient}"
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "Amount: ${payment.amount}"
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "How PayPause Helps",

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text =
                        "PayPause adds a review step when a payment appears unusual. Instead of immediately completing the payment, the user can pause, review the details, and decide whether to confirm or cancel."
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}


@Composable
fun SecurityStatusCard(

    title: String,

    status: String,

    description: String
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(

                text = title,

                fontSize = 18.sp,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(

                text = status,

                color = Color(0xFF2E7D32),

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = description
            )
        }
    }
}


// ============================================================
// ACTIVITY ROW
// ============================================================

@Composable
fun ActivityRow(
    name: String,
    amount: String
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(

            text = name,

            fontSize = 16.sp
        )

        Text(

            text = amount,

            fontWeight = FontWeight.Bold
        )
    }
}


// ============================================================
// MAKE PAYMENT
// ============================================================

@Composable
fun MakePaymentScreen(

    recipient: String,

    amount: String,

    onRecipientChange: (String) -> Unit,

    onAmountChange: (String) -> Unit,

    onContinue: () -> Unit,

    onBack: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(

            text = "Make a Payment",

            fontSize = 27.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(

            value = recipient,

            onValueChange =
                onRecipientChange,

            label = {
                Text("Recipient")
            },

            placeholder = {
                Text("Example: John Smith")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        OutlinedTextField(

            value = amount,

            onValueChange =
                onAmountChange,

            label = {
                Text("Amount")
            },

            placeholder = {
                Text("Example: 250")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Button(

            onClick = onContinue,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text(

                text = "Continue",

                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextButton(
            onClick = onBack
        ) {

            Text("Back")
        }
    }
}


// ============================================================
// RISK CHECK
// ============================================================

@Composable
fun RiskCheckScreen(

    recipient: String,

    amount: String,

    onViewRiskDetails: () -> Unit,

    onAllowPayment: () -> Unit,

    onPausePayment: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    val numericAmount =
        amount
            .replace("$", "")
            .replace(",", "")
            .toDoubleOrNull()
            ?: 0.0

    val unusual =
        numericAmount > 500.0

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(35.dp)
        )

        Text(

            text =
                if (unusual)
                    "⚠ Unusual Activity Detected"
                else
                    "✓ Payment Looks Normal",

            fontSize = 25.sp,

            fontWeight = FontWeight.Bold,

            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "Payment",

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Recipient: $recipient"
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Amount: $$amount"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (unusual) {

            Text(

                text =
                    "This payment is outside the normal demo threshold. PayPause recommends pausing the payment so you can review it.",

                textAlign = TextAlign.Center,

                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(

                onClick = onViewRiskDetails,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = orange
                )
            ) {

                Text("View Risk Details")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = onPausePayment
            ) {

                Text("Pause Payment")
            }

        } else {

            Text(

                text =
                    "No unusual activity was detected for this demo payment.",

                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(

                onClick = onAllowPayment,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = orange
                )
            ) {

                Text("Allow Payment")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TextButton(
                onClick = onPausePayment
            ) {

                Text("Pause Instead")
            }
        }
    }
}


// ============================================================
// RISK DETAILS
// ============================================================

@Composable
fun RiskDetailsScreen(

    recipient: String,

    amount: String,

    onPausePayment: () -> Unit,

    onGoBack: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    val numericAmount =
        amount
            .replace("$", "")
            .replace(",", "")
            .toDoubleOrNull()
            ?: 0.0

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(

            text = "Risk Details",

            fontSize = 28.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(

            text =
                "Why PayPause flagged this payment",

            fontSize = 16.sp,

            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "Payment Information",

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "Recipient",
                    fontWeight = FontWeight.Bold
                )

                Text(recipient)

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Payment Amount",
                    fontWeight = FontWeight.Bold
                )

                Text(

                    text = "$$amount",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "Risk Analysis",

                    fontSize = 19.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "Demo Threshold",
                    fontWeight = FontWeight.Bold
                )

                Text("$500.00")

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Amount Check",
                    fontWeight = FontWeight.Bold
                )

                Text(

                    text =
                        if (numericAmount > 500)
                            "⚠ Above threshold"
                        else
                            "✓ Within threshold"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Risk Reason",
                    fontWeight = FontWeight.Bold
                )

                Text(

                    text =
                        if (numericAmount > 500)
                            "Payment amount exceeds the demo risk threshold."
                        else
                            "Payment amount is within the demo threshold."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "PayPause Action",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "PAUSED FOR REVIEW"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(

                    text = "Recommended Action",

                    fontSize = 18.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Review the payment before confirming it."
                )
            }
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Button(

            onClick = onPausePayment,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text(

                text = "Pause Payment",

                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onGoBack
        ) {

            Text("Go Back")
        }
    }
}


// ============================================================
// REVIEW PAYMENT
// ============================================================

@Composable
fun ReviewPaymentScreen(

    recipient: String,

    amount: String,

    onConfirm: () -> Unit,

    onCancel: () -> Unit
) {

    val orange = Color(0xFFFF8C42)

    val lightOrange = Color(0xFFFFF3E8)

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(lightOrange)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(

            text = "Payment Paused",

            fontSize = 28.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(

            text =
                "Take a breath before it's gone.",

            fontSize = 17.sp,

            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Card(

            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(

                    text = "Payment Details",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = "Recipient",
                    fontWeight = FontWeight.Bold
                )

                Text(recipient)

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Amount",
                    fontWeight = FontWeight.Bold
                )

                Text(

                    text = "$$amount",

                    fontSize = 22.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "PAUSED FOR REVIEW"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Button(

            onClick = onConfirm,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = orange
            )
        ) {

            Text(

                text = "Confirm Payment",

                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextButton(
            onClick = onCancel
        ) {

            Text(

                text = "Cancel Payment",

                color = Color.Red
            )
        }
    }
}