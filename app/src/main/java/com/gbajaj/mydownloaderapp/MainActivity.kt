package com.gbajaj.mydownloaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gbajaj.mydownloaderapp.ui.theme.MyDownloaderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDownloaderAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val application: MyApplication = this.applicationContext as MyApplication
                    val progress = remember { mutableIntStateOf(0) }
                    val state = remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        state.value = "Started"
                        val downloader = application.downloader
                        val request =
                            downloader.newReqBuilder("someUrl", "someDirPath", "someFileName")
                                .readTimeOut(10000).connectTimeOut(10000).tag("someTag").build()
                        val id = downloader.enqueue(
                            request = request,
                            onStart = {
                                state.value = "started"
                            },
                            onProgress = { value ->
                                state.value = "Downloading"
                                progress.intValue = value
                            },
                            onPause = {
                                state.value = "Paused"
                            },
                            onError = {
                                state.value = "Error"
                            },
                            onCompleted = {
                                state.value = "Completed"
                            })
                    }

                    Greeting(
                        state,
                        progress,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    state: MutableState<String>,
    progress: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(text = "${progress.value}%")
            Text(
                text = "${state.value}!",
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyDownloaderAppTheme {
        val state = mutableStateOf("Downloading")
        val progress = mutableIntStateOf(50)
        Greeting(state, progress)
    }
}
