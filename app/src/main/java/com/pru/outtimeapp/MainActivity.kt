package com.pru.outtimeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pru.outtimeapp.ui.theme.OutTimeAppTheme
import com.pru.outtimeapp.ui.theme.WarningColor
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            OutTimeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(modifier = Modifier) {
                        OutTime(
                            fromNotification = intent.getBooleanExtra(
                                "FromNotification",
                                false
                            )
                        )
                        AnimatedBG()
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBG() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.progress)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    LottieAnimation(
        composition = composition,
        progress = progress,
        alignment = Alignment.BottomCenter
    )
}

@Composable
fun OutTime(
    viewModel: OutTimeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    fromNotification: Boolean
) {
    var openNotification by remember {
        mutableStateOf(fromNotification)
    }
    val time = viewModel.currentTime.collectAsState()
    val differenceTracker = viewModel.differTracker.collectAsState()
    val timeState = remember {
        derivedStateOf {
            SimpleDateFormat("HH:mmss", Locale.getDefault()).format(time.value.time)
        }
    }
    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = timeState.value.dropLast(2),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = timeState.value.drop(timeState.value.length - 2),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
            ).format(time.value.time),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (viewModel.signInTime == null) {
            FloatingActionButton(onClick = {
                viewModel.signIn()
            }, containerColor = MaterialTheme.colorScheme.secondary) {
                Icon(imageVector = Icons.Default.Login, contentDescription = null)
            }
        } else {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sign in Time :",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = viewModel.getTime(viewModel.signInTime!!),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = differenceTracker.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WarningColor,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sign out Time :",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = viewModel.getTime(viewModel.getEndTime(viewModel.signInTime!!)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            FloatingActionButton(onClick = {
                viewModel.signOut()
            }, containerColor = MaterialTheme.colorScheme.secondary) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null)
            }
        }
        if (openNotification) {
            Dialog(onDismissRequest = {
                openNotification = false
            }) {
                Surface(color = MaterialTheme.colorScheme.primary) {
                    Text(
                        text = "Time's up!!",
                        fontSize = 28.sp,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(30.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun Prev() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        OutTime(fromNotification = true)
    }
}

