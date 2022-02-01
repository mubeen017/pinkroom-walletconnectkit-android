package pro.moreira.composesample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig
import dev.pinkroom.walletconnectkit.api.WalletConnectButton
import kotlinx.coroutines.launch
import pro.moreira.composesample.ui.theme.WalletConnectKitTheme

class MainActivity : ComponentActivity() {

    private val config by lazy {
        WalletConnectKitConfig(
            bridgeUrl = "wss://bridge.walletconnect.org",
            appUrl = "walletconnectkit.com",
            appName = "WalletConnect Kit",
            appDescription = "This is the Swiss Army toolkit for WalletConnect!"
        )
    }
    private val walletConnectKit by lazy { WalletConnectKit.builder(this).config(config).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletConnectKitTheme {
                MainScreen(walletConnectKit)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreen(walletConnectKit: WalletConnectKit? = null) {
        Surface(color = MaterialTheme.colors.background) {
            var address: String? by remember { mutableStateOf(walletConnectKit?.address) }
            walletConnectKit?.onConnected = { address = it }
            walletConnectKit?.onDisconnected = { address = null }
            Scaffold(
                topBar = { TopAppBar(address != null) { walletConnectKit?.removeSession() } },
                content = { MainScreenContent(address, walletConnectKit) }
            )
        }
    }

    @Composable
    fun MainScreenContent(
        address: String? = null,
        walletConnectKit: WalletConnectKit? = null
    ) = if (address == null) {
        ConnectContent(walletConnectKit = walletConnectKit)
    } else {
        PerformTransactionContent(walletConnectKit = walletConnectKit)
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ConnectContent(walletConnectKit: WalletConnectKit? = null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { WalletConnectButton(walletConnectKit = walletConnectKit) }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun PerformTransactionContent(walletConnectKit: WalletConnectKit? = null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Connected with: ${walletConnectKit?.address}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            var toAddress by remember { mutableStateOf("") }
            NormalSpacer()
            OutlinedTextField(
                value = toAddress,
                onValueChange = { toAddress = it },
                placeholder = { Text("Address (0xAAA...AAA)") },
                modifier = Modifier.fillMaxWidth()
            )
            NormalSpacer()
            var value by remember { mutableStateOf("") }
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = { Text("Value (0.01 ETH") },
                modifier = Modifier.fillMaxWidth()
            )
            NormalSpacer()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composableScope = rememberCoroutineScope()
                OutlinedButton(onClick = {
                    composableScope.launch {
                        walletConnectKit?.performTransaction(toAddress, value)
                            ?.onSuccess { showMessage("Transaction done!") }
                            ?.onFailure { showMessage(it.message ?: it.toString()) }
                    }
                }) {
                    Text(
                        text = "Perform Transaction",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun TopAppBar(
        showDisconnect: Boolean = false,
        onDisconnected: () -> Unit = {},
    ) {
        var expanded by remember { mutableStateOf(false) }
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            actions = {
                if (showDisconnect) {
                    Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                onDisconnected()
                            }) {
                                Text(text = "Disconnect")
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    private fun NormalSpacer() = Spacer(modifier = Modifier.padding(12.dp))

    private fun Context.showMessage(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
