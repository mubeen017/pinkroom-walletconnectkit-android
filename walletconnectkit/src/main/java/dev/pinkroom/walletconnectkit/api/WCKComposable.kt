package dev.pinkroom.walletconnectkit.api

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pinkroom.walletconnectkit.R
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.api.theme.WalletConnectIconBlue

@Preview
@Composable
fun WalletConnectButton(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier.defaultContentModifier(),
    walletConnectKit: WalletConnectKit? = null,
    colors: ButtonColors = defaultButtonColors(),
    shape: Shape = defaultButtonShape(),
    border: BorderStroke = defaultBorderStroke(),
    content: @Composable RowScope.() -> Unit = { WalletConnectImage(contentModifier) },
) = Button(
    colors = colors,
    onClick = { onWalletConnectButtonClicked(walletConnectKit) },
    modifier = modifier,
    shape = shape,
    border = border,
    content = content
)

@Composable
private fun defaultButtonColors() =
    ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)

@Composable
private fun defaultButtonShape() = RoundedCornerShape(32.dp)

@Composable
private fun defaultBorderStroke() = BorderStroke(1.5.dp, WalletConnectIconBlue)

@Composable
private fun WalletConnectImage(modifier: Modifier) = Image(
    modifier = modifier,
    painter = painterResource(id = R.drawable.ic_walletconnect),
    contentDescription = ""
)

private fun onWalletConnectButtonClicked(walletConnectKit: WalletConnectKit?) {
    if (walletConnectKit?.isSessionStored == true) {
        walletConnectKit.removeSession()
    }
    walletConnectKit?.createSession()
}

private fun Modifier.defaultContentModifier() = this
    .size(232.dp, 48.dp)
    .padding(12.dp)

