package io.github.hiairman.monet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.ui.theme.MonetTheme
import io.github.hiairman.monet.ui.timetable.TimetableScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimetableScreen(
                        courses = sampleCourses,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
