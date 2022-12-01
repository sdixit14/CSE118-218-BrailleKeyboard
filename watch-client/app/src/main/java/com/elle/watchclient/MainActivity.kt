package com.elle.watchclient

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
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
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Character.*


class MainActivity : ComponentActivity() {


    val contentModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp, top = 15.dp)
    val iconModifier = Modifier
        .size(24.dp)
        .wrapContentSize(align = Alignment.Center)

    var insideBrailleKeyboard = false;
    var messages = mutableListOf<String>()

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

    fun JSONArray.toArrayList(): ArrayList<String> {
        val list = arrayListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }

        return list
    }

    @ExperimentalWearMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var currentMessage:String = "HELLOWWORLD"
        val keyboardModifier = Modifier
            .padding(all = 0.5.dp)
            .width(85.dp)
        val middleModifier = Modifier
            .width(85.dp)
            .padding(all = 0.5.dp)

        val vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        val toast: Toast = Toast.makeText(this, "Hello", Toast.LENGTH_SHORT)
        val MyRequestQueue: RequestQueue = Volley.newRequestQueue(this)

        fun getNotes() {
            val URL = "https://balajimt.pythonanywhere.com/viewallnotepublic"
            val params = mutableMapOf(
                "username" to "elle_admin",
                "licensekey" to "SEUSSGEISEL"
            )



            toast.setText("Syncing messages from cloud")
            toast.show()

            val request_json = JsonObjectRequest(URL, JSONObject(params as Map<*, *>?),
                { response ->
                    try {
                        val jsonArray = response.getJSONArray("messages")
                        println(jsonArray)
                        val text: CharSequence = "Synced: " + jsonArray.length() + " messages"
                        toast.setText(text)
                        toast.show()
                        messages = jsonArray.toArrayList().reversed() as MutableList<String>
                        messages.removeAll(listOf("", null, " "))
                    } catch (e: Exception) {
                        val text: CharSequence? = e.message
                        toast.setText(text)
                        toast.show()
                        e.printStackTrace()
                        println(e)
                    }
                }
            ) { error -> println(error) }
            // add the request object to the queue to be executed
            MyRequestQueue.add(request_json)
        }

        fun sendNoteMessage(message: String) {
            val URL = "https://balajimt.pythonanywhere.com/addnotepublic"
            val params = mutableMapOf(
                "username" to "elle_admin",
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
                        println(e)
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
                        getNotes()
                        println("inside landing")
                        println(messages)
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
                                        .height(100.dp)
                                        .padding(top = 10.dp),
                                    onClick = { swipeDismissableNavController.navigate("braille") },
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
                                        .height(100.dp)
                                        .padding(top = 10.dp),
                                    onClick = { swipeDismissableNavController.navigate("reader") },
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

                    composable("reader") {
                        val maxPages = 9
                        var selectedPage by remember { mutableStateOf(0) }
                        var messageItem by remember { mutableStateOf(messages[0]) }
                        var finalValue by remember { mutableStateOf(0) }
                        var painterValue by remember { mutableStateOf(R.drawable.vangogh) }

                        val animatedSelectedPage by animateFloatAsState(
                            targetValue = selectedPage.toFloat(),
                        ) {
                            finalValue = it.toInt()
                        }

                        val pageIndicatorState: PageIndicatorState = remember {
                            object : PageIndicatorState {
                                override val pageOffset: Float
                                    get() = animatedSelectedPage - finalValue
                                override val selectedPage: Int
                                    get() = finalValue
                                override val pageCount: Int
                                    get() = maxPages
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        ) {
                            Button(
                                onClick = { /* Do something */ },
                                enabled = true,
                                modifier = Modifier
                                    .size(35.dp)
                                    .align(Alignment.TopCenter),
                                colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5))
                            ) {
                                Text(text = "elle", color = Color.Black)
                            }

                            InlineSlider(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .height(100.dp),
                                value = selectedPage,
                                increaseIcon = {
                                    Icon(
                                        InlineSliderDefaults.Increase,
                                        "Previous",
                                        modifier = Modifier.width(50.dp)
                                    )
                                },
                                decreaseIcon = {
                                    Icon(
                                        InlineSliderDefaults.Decrease,
                                        "Next",
                                        modifier = Modifier.width(50.dp)
                                    )
                                },
                                valueProgression = 0 until maxPages,
                                onValueChange = {
                                    selectedPage = it
                                    messageItem = messages[it]
                                    if (it % 2 == 1) {
                                        painterValue = R.drawable.vangogh2
                                    } else {
                                        painterValue = R.drawable.vangogh
                                    }
                                }
                            )
                            HorizontalPageIndicator(
                                pageIndicatorState = pageIndicatorState
                            )
                            TitleCard(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(90.dp)
                                    .align(Alignment.Center),
                                onClick = {
                                    currentMessage = messageItem
                                    if (currentMessage.length > 1) {
                                        swipeDismissableNavController.navigate("letterScreen")
                                    } else {
                                        println(currentMessage)
                                        if (currentMessage[0]=='_' || currentMessage[0]==' ') {
                                            println("Detected space")
                                        } else {
                                            vibrator.vibrate(
                                                VibrationEffect.createWaveform(
                                                    BrailleMapping().getVibrationSequence(
                                                        currentMessage[0].toString()
                                                    ), -1
                                                )
                                            )
                                            println("Finished vibration for $messageItem")
                                        }
                                    }
                                },
                                title = { Text(messageItem.toString()) },
                                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                                    backgroundImagePainter = painterResource(id = painterValue)
                                ),
                                contentColor = MaterialTheme.colors.onSurface,
                                titleColor = MaterialTheme.colors.onSurface
                            ) { }
                        }
                    }

                    composable("letterScreen") {
                        val maxPages = currentMessage.length
                        var selectedPage by remember { mutableStateOf(0) }
                        var messageItem by remember { mutableStateOf(currentMessage[0]) }
                        var finalValue by remember { mutableStateOf(0) }
                        var painterValue by remember { mutableStateOf(R.drawable.vangogh) }

                        val animatedSelectedPage by animateFloatAsState(
                            targetValue = selectedPage.toFloat(),
                        ) {
                            finalValue = it.toInt()
                        }

                        val pageIndicatorState: PageIndicatorState = remember {
                            object : PageIndicatorState {
                                override val pageOffset: Float
                                    get() = animatedSelectedPage - finalValue
                                override val selectedPage: Int
                                    get() = finalValue
                                override val pageCount: Int
                                    get() = maxPages
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        ) {
                            Button(
                                onClick = { /* Do something */ },
                                enabled = true,
                                modifier = Modifier
                                    .size(35.dp)
                                    .align(Alignment.TopCenter),
                                colors = ButtonDefaults.primaryButtonColors(Color(0xFFC5DED5))
                            ) {
                                Text(text = "elle", color = Color.Black)
                            }

                            InlineSlider(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .height(100.dp),
                                value = selectedPage,
                                increaseIcon = {
                                    Icon(
                                        InlineSliderDefaults.Increase,
                                        "Previous",
                                        modifier = Modifier.width(50.dp)
                                    )
                                },
                                decreaseIcon = {
                                    Icon(
                                        InlineSliderDefaults.Decrease,
                                        "Next",
                                        modifier = Modifier.width(50.dp)
                                    )
                                },
                                valueProgression = 0 until maxPages,
                                onValueChange = {
                                    selectedPage = it
                                    messageItem = currentMessage[it]
                                    if (it % 2 == 1) {
                                        painterValue = R.drawable.vangogh2
                                    } else {
                                        painterValue = R.drawable.vangogh
                                    }
                                }
                            )
                            HorizontalPageIndicator(
                                pageIndicatorState = pageIndicatorState
                            )
                            TitleCard(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(90.dp)
                                    .align(Alignment.Center),
                                onClick = {
                                    println(messageItem)
                                    if (messageItem=='_' || messageItem==' ') {
                                        println("Detected space")
                                    } else {
                                        vibrator.vibrate(
                                            VibrationEffect.createWaveform(
                                                BrailleMapping().getVibrationSequence(
                                                    messageItem.toString()
                                                ), -1
                                            )
                                        )
                                        println("Finished vibration for $messageItem")
                                    }
                                },
                                title = { Text(messageItem.toString()) },
                                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                                    backgroundImagePainter = painterResource(id = painterValue)
                                ),
                                contentColor = MaterialTheme.colors.onSurface,
                                titleColor = MaterialTheme.colors.onSurface
                            ) { }
                        }
                    }

                    composable("braille") {
                        var currentCombination = ""
                        var sentence = ""
                        insideBrailleKeyboard = true

                        ScalingLazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CustomButton(
                                        onClick = {
                                            currentCombination += "1"
//                                            tryVibrate(VibrationEffect.Composition.PRIMITIVE_THUD)
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
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
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
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
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
                                            if (currentCombination.length > 2) {
                                                if (currentCombination[currentCombination.length - 2] == '2') {
                                                    // END OF CHARACTER
                                                    vibrator.vibrate(
                                                        VibrationEffect.createOneShot(
                                                            150,
                                                            // The default vibration strength of the device.
                                                            VibrationEffect.EFFECT_DOUBLE_CLICK
                                                        )
                                                    )
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
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
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
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
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
                                            vibrator.vibrate(
                                                VibrationEffect.createOneShot(
                                                    100,
                                                    // The default vibration strength of the device.
                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                )
                                            )
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
                }
            }
        }
    }
}

