package io.github.hiairman.monet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.hiairman.monet.ui.MonetApp
import io.github.hiairman.monet.ui.theme.MonetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonetTheme {
                MonetApp()
            }
        }
    }
}
