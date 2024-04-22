package com.sunueric.tableauapp

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sunueric.tableauapp.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun TableauReportView(authViewModel: AuthViewModel) {

    val density = LocalDensity.current
    val desiredWidthPx = remember(density) {
        density.run { 1280.dp.toPx() }
    }
    val desiredHeightPx = remember(density) {
        density.run { 720.dp.toPx() }
    }
    val webViewUrl = "https://public.tableau.com/views/TestBook_17133560147830/ExecutiveSummary?:embed=y&:tooltip=n&:toolbar=n&:showVizHome=no&:mobile=y&:showAppBanner=n"
    val reloadTrigger = remember { mutableStateOf(false) }

    val isAuthenticated = authViewModel.isAuthenticated.value
    val accessToken = authViewModel.accessToken.value

    // Example URL that needs to be dynamically set based on context
    //val webViewUrl = "https://public.tableau.com/views/publicViz"

    val urlToLoad = remember(isAuthenticated, accessToken) {
        if (isAuthenticated == true && !accessToken.isNullOrEmpty()) {
            "$webViewUrl?token=$accessToken" // Append the token for authenticated access
        } else {
            webViewUrl
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    desiredWidthPx.toInt(),
                    desiredHeightPx.toInt()
                )

                webViewClient = object : WebViewClient() {

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view?.loadUrl(url ?: "")
                        return true
                    }
                }
                @Suppress("SetJavaScriptEnabled") // This is safe because the URL is a known constant...
                settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                loadUrl(webViewUrl)
            }
        },
        update = { webView ->
            webView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    desiredWidthPx.toInt(),
                    desiredHeightPx.toInt()
                )

                if (reloadTrigger.value) {
                    webView.loadUrl(webViewUrl)
                    reloadTrigger.value = false
                }
            }
        }
    )

    // Periodically reload the WebView
    LaunchedEffect(key1 = Unit, urlToLoad) {
        authViewModel.authenticateIfNeeded(webViewUrl)
        while (true) {
            delay(1800000) // wait for 30mins and reload
            // Reload the WebView in a UI thread
            reloadTrigger.value = true
        }
    }
}

