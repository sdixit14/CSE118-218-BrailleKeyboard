package com.elle.watchclient

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedRow
import androidx.wear.compose.material.*
import androidx.wear.compose.material.TimeTextDefaults.timeCurvedTextStyle
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.DeviceWearableButtonsProvider
import androidx.wear.input.WearableButtons
import androidx.wear.input.WearableButtonsProvider
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    val contentModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp, top = 15.dp)
    val iconModifier = Modifier
        .size(24.dp)
        .wrapContentSize(align = Alignment.Center)

    var insideBrailleKeyboard = false;

    @Composable
    fun ButtonExample(
        modifier: Modifier = Modifier,
        iconModifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            // Button
            Button(
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
                onClick = { /* ... */ },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Phone,
                    contentDescription = "triggers phone action",
                    modifier = iconModifier
                )
            }
        }
    }

    @ExperimentalWearMaterialApi
    @Composable
    fun SimpleTimeText(
        contentPadding: PaddingValues = PaddingValues(4.dp)
    ) {
        if (LocalConfiguration.current.isScreenRound) {
            CurvedRow(Modifier.padding(contentPadding)) {
                CurvedText(
                    text = "elle",
                    style = timeCurvedTextStyle()
                )
            }
        }
    }

    @Composable
    fun CustomButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        content: @Composable BoxScope.() -> Unit,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.DefaultButtonSize,
                    minHeight = ButtonDefaults.DefaultButtonSize
                )
                .clip(RoundedCornerShape(30))
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button,
                )
                .background(
                    color = colors.backgroundColor(enabled = enabled).value,
                    shape = RoundedCornerShape(20)
                )
        ) {
            val contentColor = colors.contentColor(enabled = enabled).value
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalContentAlpha provides contentColor.alpha,
                LocalTextStyle provides MaterialTheme.typography.button
            ) {
                content()
            }
        }
    }

    @Composable
    private fun SampleRow(anchor: Float, modifier: Modifier, vararg textBits: String) {
        CurvedRow(
            modifier = modifier.padding(4.dp),
            anchor = anchor
        ) {
            textBits.forEach { CurvedText(it, modifier = Modifier.padding(end = 8.dp)) }
        }
    }


    @ExperimentalWearMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyboardModifier = Modifier
            .padding(all = 0.5.dp)
            .width(85.dp)
        val middleModifier = Modifier
            .width(85.dp)
            .padding(all = 0.5.dp)

        val toast: Toast = Toast.makeText(this, "Hello", Toast.LENGTH_SHORT)
        val MyRequestQueue: RequestQueue = Volley.newRequestQueue(this)


        fun sendNoteMessage(message: String) {
            val URL = "https://balajimt.pythonanywhere.com/addnotepublic"
            val params = mutableMapOf("username" to "elle_admin",
                "licensekey" to "SEUSSGEISEL",
                "note" to message
                )
            val request_json = JsonObjectRequest(URL, JSONObject(params as Map<*, *>?),
                { response ->
                    try {
                        println(response)
                        val text: CharSequence = response.getString("message")
                        toast.setText(text)
                        toast.show()
                    } catch (e: Exception) {
                        val text: CharSequence? = e.message
                        toast.setText(text)
                        toast.show()
                        e.printStackTrace()
                    }
                }
            ) { error -> VolleyLog.e("Error: ", error.message) }

            // add the request object to the queue to be executed

            // add the request object to the queue to be executed
            MyRequestQueue.add(request_json)
        }
        setContent {
            MaterialTheme {
                val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
                val swipeDismissableNavController = rememberSwipeDismissableNavController()

                // Physical button information
                val sButtonsProvider: WearableButtonsProvider = DeviceWearableButtonsProvider()
                val buttonCodes = sButtonsProvider.getAvailableButtonKeyCodes(this)
                println(buttonCodes?.get(0) ?: 23)
                println(buttonCodes?.get(1) ?: 24)
                println(WearableButtons.getButtonCount(this))
                var buttonInfo = WearableButtons.getButtonInfo(this, KeyEvent.KEYCODE_POWER)

                if (buttonInfo == null) {
                    println("KEYCODE_POWER not present")
                } else {
                    println(buttonInfo.locationZone)
                }

                buttonInfo = WearableButtons.getButtonInfo(this, KeyEvent.KEYCODE_STEM_PRIMARY)

                if (buttonInfo == null) {
                    println("KEYCODE_STEM_PRIMARY not present")
                } else {
                    println(buttonInfo.locationZone)
                }

                SwipeDismissableNavHost(
                    navController = swipeDismissableNavController,
                    startDestination = "Landing",
                    modifier = Modifier.background(MaterialTheme.colors.background)
                ) {
                    composable("Landing") {
                        insideBrailleKeyboard = false
                        ScalingLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 28.dp,
                                start = 10.dp,
                                end = 10.dp,
                                bottom = 40.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            state = scalingLazyListState
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = { /* Do something */ },
                                        enabled = true,
                                        modifier = Modifier.size(35.dp),
                                        colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5))
                                    ) {
                                        Text(text = "elle", color = Color.Black)
                                    }
                                }
                            }
                            item {
                                TitleCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    onClick = { swipeDismissableNavController.navigate("Another list") },
                                    title = { Text("Type notes") },
                                    backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                                        backgroundImagePainter = painterResource(id = R.drawable.vangogh)
                                    ),
                                    contentColor = MaterialTheme.colors.onSurface,
                                    titleColor = MaterialTheme.colors.onSurface
                                ) { }
                            }
                            item {
                                TitleCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    onClick = { swipeDismissableNavController.navigate("braille") },
                                    title = { Text("Read notes") },
                                    backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                                        backgroundImagePainter = painterResource(id = R.drawable.vangogh2)
                                    ),
                                    contentColor = MaterialTheme.colors.onSurface,
                                    titleColor = MaterialTheme.colors.onSurface
                                ) { }
                            }
                        }
                    }

                    composable("Detail") {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 60.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                text = "Hello from Details Screen"
                            )
                        }
                    }

                    composable("Another list") {
                        Scaffold(
                            timeText = {
                                TimeText()
                            },
                            vignette = {
                                Vignette(vignettePosition = VignettePosition.TopAndBottom)
                            },
                            positionIndicator = {
                                PositionIndicator(
                                    scalingLazyListState = ScalingLazyListState()
                                )

                            }
                        ) {
                            ScalingLazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = 28.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom = 40.dp
                                ),
                                verticalArrangement = Arrangement.Center,
                                state = scalingLazyListState
                            ) {
                                items(10) { index ->
                                    Chip(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.vangogh),
                                                contentDescription = "Star",
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .wrapContentSize(align = Alignment.Center),
                                            )
                                        },
                                        label = {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colors.onError,
                                                text = "Item ${index + 1}"
                                            )
                                        },
                                        onClick = {
                                        }
                                    )
                                }
                            }
                        }
                    }

                    composable("braille") {
                        var currentCombination = ""
                        var sentence = ""
                        insideBrailleKeyboard = true
                        var count2 = 0
                        var count5 = 0


                        ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "1"
                                            println(currentCombination)
                                        },
                                        enabled = true,
                                        modifier = keyboardModifier,

                                        ) {
                                        Text(text = "1", color = Color.Black)
                                    }
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "4"
                                            println(currentCombination)
                                        },
                                        enabled = true,
                                        modifier = keyboardModifier,
                                        colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5))
                                    ) {
                                        Text(text = "4", color = Color.Black)
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "2"
                                            if (currentCombination.length > 2) {
                                                if (currentCombination[currentCombination.length - 2] == '2') {
                                                    // END OF CHARACTER
                                                    toast.setText("Sending: $sentence")
                                                    toast.show()
                                                    sendNoteMessage(sentence)
                                                    currentCombination = ""
                                                    sentence = ""
                                                }
                                            }
                                        },
                                        enabled = true,
                                        modifier = middleModifier,
                                        colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5))
                                    ) {
                                        Text(text = "2", color = Color.Black)
                                    }
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "5"
                                            println(currentCombination)
                                        },
                                        enabled = true,
                                        modifier = middleModifier,

                                        ) {
                                        Text(text = "5", color = Color.Black)
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "3"
                                            if (currentCombination.length > 2) {
                                                if (currentCombination[currentCombination.length - 2] == '3') {
                                                    // END OF CHARACTER
                                                    sentence += " "
                                                    toast.setText(sentence)
                                                    toast.show()
                                                    currentCombination = ""
                                                }
                                            }

                                            println(currentCombination)
                                        },
                                        enabled = true,
                                        modifier = keyboardModifier,

                                        ) {
                                        Text(text = "3", color = Color.Black)
                                    }
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "6"
                                            if (currentCombination.length > 2) {
                                                if (currentCombination[currentCombination.length - 2] == '6') {
                                                    // END OF CHARACTER
                                                    sentence += BrailleMapping().getAlphabetFromNumberString(
                                                        currentCombination.substring(
                                                            0,
                                                            currentCombination.length - 2
                                                        )
                                                    )
                                                    toast.setText(sentence)
                                                    toast.show()
                                                    currentCombination = ""
                                                }
                                            }

                                            println(currentCombination)
                                        },
                                        enabled = true,
                                        modifier = keyboardModifier,
                                        colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5)),

                                        ) {
                                        Text(text = "6", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }

                    composable("Keyboard") {
                        Scaffold(
                            timeText = {
                                TimeText()
                            },
                            vignette = {
                                Vignette(vignettePosition = VignettePosition.TopAndBottom)
                            },
                            positionIndicator = {
                                PositionIndicator(
                                    scalingLazyListState = ScalingLazyListState()
                                )

                            }
                        ) {
                            ScalingLazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = 28.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom = 40.dp
                                ),
                                verticalArrangement = Arrangement.Center,
                                state = scalingLazyListState
                            ) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle", color = Color.Black)
                                        }
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle 2", color = Color.Black)
                                        }
                                    }
                                }
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle", color = Color.Black)
                                        }
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle 2", color = Color.Black)
                                        }
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle 2", color = Color.Black)
                                        }
                                    }
                                }
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle", color = Color.Black)
                                        }
                                        Button(
                                            onClick = { /* Do something */ },
                                            enabled = true,
                                            modifier = keyboardModifier,
                                            colors = ButtonDefaults.primaryButtonColors(
                                                Color(
                                                    0xFFC5DED5
                                                )
                                            )
                                        ) {
                                            Text(text = "elle 2", color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

