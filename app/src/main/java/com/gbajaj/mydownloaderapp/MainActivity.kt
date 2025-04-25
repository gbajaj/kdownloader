package com.gbajaj.mydownloaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbajaj.mydownloaderapp.ui.theme.MyDownloaderAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDownloaderAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DownloadList(items = generateFakeItems())
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

data class DownloadItem(
    val name: String,
    val status: String,
    val progress: Int
)

@Composable
fun DownloadItemView(item: DownloadItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val scope = rememberCoroutineScope()
        val progress = remember { mutableIntStateOf(0) }
        val state = remember { mutableStateOf("") }
        val downloadId = remember { mutableIntStateOf(0) }
        val enableButton = remember { mutableStateOf(true) }
        val showCancelButton = remember { mutableStateOf(false) }

        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Status: ${state.value}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress.value / 100f },
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
        val context = LocalContext.current
        if (!showCancelButton.value) {
            Button(enabled = enableButton.value && !showCancelButton.value, onClick = {
                scope.launch {  // Using Scope to launch a suspend function
                    val application = context.applicationContext as MyApplication
                    val downloader = application.downloader

                    val request =
                        downloader.newReqBuilder(item.name, "someDirPath", "someFileName")
                            .readTimeOut(10000).connectTimeOut(10000).tag("someTag").build()
                    showCancelButton.value = true
                    downloadId.intValue = downloader.enqueue(
                        request = request,
                        onStart = {
                            state.value = "started"
                            showCancelButton.value = true
                        },
                        onProgress = { value ->
                            state.value = "Downloading"
                            progress.intValue = value
                        },
                        onPause = {
                            state.value = "Paused"
                        },
                        onCancel = {
                            state.value = "Canceled"
                            showCancelButton.value = false
                            progress.intValue = 0
                        },
                        onError = {
                            state.value = "Error"
                            showCancelButton.value = false
                        },
                        onCompleted = {
                            state.value = "Completed"
                            showCancelButton.value = false
                        })
                }
            }) {
                Text("Download", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Button(enabled = showCancelButton.value, onClick = {
                scope.launch {  // Using Scope to launch a suspend function
                    val application = context.applicationContext as MyApplication
                    val downloader = application.downloader

                    downloader.cancel(downloadId.intValue)
                }
            }) {
                Text("Cancel", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

fun generateFakeItems(): List<DownloadItem> {
    return List(10) { index ->
        DownloadItem(
            name = "File ${index + 1}",
            status = listOf("Downloading", "Paused", "Completed", "Error").random(),
            progress = (0..100).random()
        )
    }
}

@Composable
fun DownloadList(items: List<DownloadItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            DownloadItemView(item)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadListPreview() {
    DownloadList(generateFakeItems())
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
