package org.stanzamusic.stanza.presentation.saveCollection.coverCreator.colorPicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trevorwiebe.apogee.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker(
    color: MutableState<Color>
) {
    // Derive HSV from the color for internal use
    val hsv = remember(color.value) {
        val hsvArray = floatArrayOf(0f, 0f, 0f)
        android.graphics.Color.colorToHSV(color.value.toArgb(), hsvArray)
        mutableStateOf(Triple(hsvArray[0], hsvArray[1], hsvArray[2]))
    }

    val colors100 = listOf(
        Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFD1C4E9), Color(0xFFC5CAE9), Color(0xFFBBDEFB), Color(0xFFB3E5FC), Color(0xFFB2DFDB), Color(0xFFC8E6C9), Color(0xFFDCEDC8), Color(0xFFFFF9C4), Color(0xFFFFE0B2), Color(0xFFFFCCBC), Color(0xFFD7CCC8), Color(0xFFF5F5F5)
    )
    val colors200 = listOf(
        Color(0xFFEF9A9A), Color(0xFFF48FB1), Color(0xFFCE93D8), Color(0xFFB39DDB), Color(0xFF9FA8DA), Color(0xFF90CAF9), Color(0xFF81D4FA), Color(0xFF80CBC4), Color(0xFFA5D6A7), Color(0xFFC5E1A5), Color(0xFFFFF59D), Color(0xFFFFCC80), Color(0xFFFFAB91), Color(0xFFBCAAA4), Color(0xFFEEEEEE)
    )
    val colors300 = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD), Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DB6AC), Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFF176), Color(0xFFFFB74D), Color(0xFFFF8A65), Color(0xFFA1887F), Color(0xFFE0E0E0)
    )
    val colors400 = listOf(
        Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2), Color(0xFF5C6BC0), Color(0xFF42A5F5), Color(0xFF29B6F6), Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFFFEE58), Color(0xFFFFA726), Color(0xFFFF7043), Color(0xFF8D6E63), Color(0xFFBDBDBD)
    )
    val colors500 = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9E9E9E)
    )
    val colors600 = listOf(
        Color(0xFFE53935), Color(0xFFD81B60), Color(0xFF8E24AA), Color(0xFF5E35B1), Color(0xFF3949AB), Color(0xFF1E88E5), Color(0xFF039BE5), Color(0xFF00897B), Color(0xFF43A047), Color(0xFF7CB342), Color(0xFFFDD835), Color(0xFFFB8C00), Color(0xFFF4511E), Color(0xFF6D4C41), Color(0xFF757575)
    )
    val colors700 = listOf(
        Color(0xFFD32F2F), Color(0xFFC2185B), Color(0xFF7B1FA2), Color(0xFF512DA8), Color(0xFF303F9F), Color(0xFF1976D2), Color(0xFF0288D1), Color(0xFF00796B), Color(0xFF388E3C), Color(0xFF689F38), Color(0xFFFBC02D), Color(0xFFF57C00), Color(0xFFE64A19), Color(0xFF5D4037), Color(0xFF616161)
    )
    val colors800 = listOf(
        Color(0xFFC62828), Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFF4527A0), Color(0xFF283593), Color(0xFF1565C0), Color(0xFF0277BD), Color(0xFF00695C), Color(0xFF2E7D32), Color(0xFF558B2F), Color(0xFFF9A825), Color(0xFFEF6C00), Color(0xFFD84315), Color(0xFF4E342E), Color(0xFF424242)
    )
    val colors900 = listOf(
        Color(0xFFB71C1C), Color(0xFF880E4F), Color(0xFF4A148C), Color(0xFF311B92), Color(0xFF1A237E), Color(0xFF0D47A1), Color(0xFF01579B), Color(0xFF004D40), Color(0xFF1B5E20), Color(0xFF33691E), Color(0xFFF57F17), Color(0xFFE65100), Color(0xFFBF360C), Color(0xFF3E2723), Color(0xFF212121)
    )

    val pagerState = rememberPagerState { 2 }
    var selectedTab by remember { mutableIntStateOf(0) }
    val animationScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedTab = page
        }
    }

    val totalColorList = listOf(
        colors900,
        colors800,
        colors700,
        colors600,
        colors500,
        colors400,
        colors300,
        colors200,
        colors100
    )

    Column(modifier = Modifier.fillMaxWidth()){

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Choose a Color",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Selected Color",
            color = MaterialTheme.colorScheme.onSurface
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = color.value
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(200.dp)
                .height(50.dp)
        ) {}

        PrimaryTabRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            selectedTabIndex = selectedTab
        ) {
            Tab(
                selected = 0 == selectedTab,
                onClick = {
                    selectedTab = 0
                    animationScope.launch { pagerState.animateScrollToPage(0) }
                },
                text = { Text(text = "Grid") }
            )
            Tab(
                selected = 1 == selectedTab,
                onClick = {
                    selectedTab = 1
                    animationScope.launch { pagerState.animateScrollToPage(1) }
                },
                text = { Text(text = "Spectrum") }
            )
        }

        HorizontalPager(state = pagerState) {
            when (it) {
                0 -> {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            totalColorList.forEach {
                                BoxWithConstraints(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    val cardSize = maxWidth / 15

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        it.forEach { gridColor ->
                                            Card(
                                                colors = CardColors(
                                                    containerColor = gridColor,
                                                    contentColor = gridColor,
                                                    disabledContainerColor = gridColor,
                                                    disabledContentColor = gridColor
                                                ),
                                                shape = RoundedCornerShape(0.dp),
                                                border = BorderStroke(
                                                    3.dp,
                                                    if (gridColor == color.value) Color.White else Color.Transparent
                                                ),
                                                modifier = Modifier
                                                    .size(cardSize)
                                                    .clickable {
                                                        val newHsvArray =
                                                            floatArrayOf(0f, 0f, 0f)
                                                        android.graphics.Color.colorToHSV(
                                                            gridColor.toArgb(),
                                                            newHsvArray
                                                        )
                                                        hsv.value = Triple(
                                                            newHsvArray[0],
                                                            newHsvArray[1],
                                                            newHsvArray[2]
                                                        )
                                                        color.value = gridColor
                                                    }
                                            ) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ColorSpectrumBox(
                                hue = hsv.value.first,
                                setSatVal = { sat, value ->
                                    hsv.value = Triple(hsv.value.first, sat, value)
                                    color.value = Color.hsv(
                                        hsv.value.first,
                                        sat,
                                        value
                                    )
                                },
                                currentColor = color.value
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HueBar(
                                currentHue = color.value.toHue(),
                                setColor = { hue ->
                                    hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
                                    color.value = Color.hsv(
                                        hue,
                                        hsv.value.second,
                                        hsv.value.third
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedColorRow(
    modifier: Modifier = Modifier,
    colorList: List<Color>,
    selectedColor: Color,
    setColor: (Color) -> Unit,
    saveColor: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        colorList.forEach{
            Box(modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(it)
                .border(BorderStroke(
                    width = 3.dp,
                    color = if (it == selectedColor) Color.White else Color.Transparent),
                    shape = CircleShape
                )
                .clickable {
                    setColor(it)
                })
            Spacer(modifier = Modifier.width(8.dp))
        }
        IconButton(
            onClick = {
                saveColor()
            },
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Save Color",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

fun Color.toHue(): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv)
    return hsv[0]
}