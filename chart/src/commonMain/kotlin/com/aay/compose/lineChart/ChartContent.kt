package com.aay.compose.lineChart


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.aay.compose.lineChart.components.chartContainer
import com.aay.compose.lineChart.lines.drawDefaultLineWithShadow
import com.aay.compose.lineChart.lines.drawQuarticLineWithShadow
import com.aay.compose.lineChart.model.BackGroundGrid
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun ChartContent(
    modifier: Modifier,
    linesParameters: List<LineParameters>,
    backGroundColor: Color,
    xAxisData: List<String>,
    showBackgroundGrid: BackGroundGrid,
    barWidthPx: Dp,
    animateChart: Boolean,
    pathEffect: PathEffect
) {
    val spacing = 100.dp
    val textMeasure = rememberTextMeasurer()

    val animatedProgress = remember {
        if (animateChart) Animatable(0f) else Animatable(1f)
    }
    val upperValue = remember {
        linesParameters.flatMap { it.data }.maxOrNull()?.plus(1.0) ?: 0.0
    }
    val lowerValue = remember {
        linesParameters.flatMap { it.data }.minOrNull() ?: 0.0
    }


    Canvas(modifier = modifier.fillMaxSize().clipToBounds()) {
        chartContainer(
            xAxisData = xAxisData,
            spacing = spacing,
            textMeasure = textMeasure,
            upperValue = upperValue.toFloat(),
            lowerValue = lowerValue.toFloat(),
            isShowBackgroundLines = showBackgroundGrid,
            backgroundLineWidth = barWidthPx.toPx(),
            backGroundLineColor = backGroundColor,
            pathEffect = pathEffect,
        )


        val spaceBetweenXes = (size.width.toDp() - spacing) / xAxisData.size

        linesParameters.forEach { line ->
            if (line.lineType == LineType.DEFAULT_LINE) {

                drawDefaultLineWithShadow(
                    line = line,
                    lowerValue = lowerValue.toFloat(),
                    upperValue = upperValue.toFloat(),
                    spacing = spacing,
                    spaceBetweenXes = spaceBetweenXes,
                    animatedProgress = animatedProgress,
                    xAxisSize = xAxisData.size
                )

            } else {

                drawQuarticLineWithShadow(
                    line = line,
                    lowerValue = lowerValue.toFloat(),
                    upperValue = upperValue.toFloat(),
                    spacing = spacing,
                    spaceBetweenXes = spaceBetweenXes,
                    animatedProgress = animatedProgress,
                    xAxisSize = xAxisData.size
                )

            }
        }
    }

    LaunchedEffect(linesParameters, animateChart) {
        if (animateChart) {
            animatedProgress.animateTo(
                targetValue = 0f,
            )
            delay(400)
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }
}