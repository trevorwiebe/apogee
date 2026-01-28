package com.trevorwiebe.apogee.schedule.presentation

import androidx.compose.ui.graphics.Color
import com.trevorwiebe.apogee.schedule.data.ScheduleCould
import com.trevorwiebe.apogee.schedule.data.ScheduleShould
import kotlin.math.max
import kotlin.math.min

object ScheduleVMUtils {

    fun lightenColor(color: Color, targetLightness: Float = 0.85f): Color {
        val r = color.red
        val g = color.green
        val b = color.blue

        val maxC = max(r, max(g, b))
        val minC = min(r, min(g, b))
        val l = (maxC + minC) / 2f

        if (l >= targetLightness) {
            return color
        }

        val h: Float
        val s: Float

        if (maxC == minC) {
            h = 0f
            s = 0f
        } else {
            val d = maxC - minC
            s = if (l > 0.5f) d / (2f - maxC - minC) else d / (maxC + minC)
            h = when {
                maxC == r -> ((g - b) / d + (if (g < b) 6f else 0f)) / 6f
                maxC == g -> ((b - r) / d + 2f) / 6f
                else -> ((r - g) / d + 4f) / 6f
            }
        }

        return hslToColor(h, s, targetLightness, color.alpha)
    }

    private fun hslToColor(h: Float, s: Float, l: Float, alpha: Float): Color {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs((h * 6f) % 2f - 1f))
        val m = l - c / 2f

        val (r, g, b) = when {
            h < 1f / 6f -> Triple(c, x, 0f)
            h < 2f / 6f -> Triple(x, c, 0f)
            h < 3f / 6f -> Triple(0f, c, x)
            h < 4f / 6f -> Triple(0f, x, c)
            h < 5f / 6f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(r + m, g + m, b + m, alpha)
    }

    internal fun mapTaskToTime(
        timeList: List<UiTimeSlot>,
        taskList: List<ScheduleShould>,
        scheduleCouldList: List<ScheduleCould>
    ): List<UiTimeSlot>{
        return timeList.map { time ->
            val task = taskList.find {
                it.startTime <= time.slot.startTime &&
                it.endTime >= time.slot.endTime &&
                it.dayOfWeek == time.slot.dayOfTheWeek
            }
            val taskName = task?.let { t ->
                scheduleCouldList.find { it.id == t.scheduleCouldId }
            }?.name
            time.copy(taskName = taskName)
        }
    }
}