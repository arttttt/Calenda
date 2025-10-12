package com.arttttt.calenda.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.metro.appGraph
import com.arttttt.calenda.widget.di.WidgetGraph
import com.arttttt.calenda.widget.ui.AgendaWidgetDaySection
import dev.zacsweers.metro.asContribution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AgendaWidget : GlanceAppWidget() {

    private data class WidgetData(
        val today: DayData,
        val tomorrow: DayData,
    )

    private data class DayData(
        val date: LocalDate,
        val events: List<CalendarEvent>,
    )

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val widgetData = loadWidgetData(context)

        provideContent {
            GlanceTheme {
                Content(widgetData)
            }
        }
    }

    @Composable
    private fun Content(data: WidgetData) {
        Scaffold(
            modifier = GlanceModifier
                .cornerRadius(24.dp)
                .padding(8.dp),
            horizontalPadding = 0.dp,
            titleBar = {
                TitleBar(
                    title = "Agenda",
                )
            },
        ) {
            LazyColumn(
                modifier = GlanceModifier.padding(
                    top = 8.dp,
                )
            ) {
                item {
                    AgendaWidgetDaySection(
                        dayLabel = "Today",
                        date = data.today.date,
                        events = data.today.events,
                    )
                }

                item {
                    Spacer(
                        modifier = GlanceModifier.height(16.dp),
                    )
                }

                item {
                    AgendaWidgetDaySection(
                        dayLabel = "Tomorrow",
                        date = data.tomorrow.date,
                        events = data.tomorrow.events,
                    )
                }
            }
        }
    }

    @Composable
    private fun TitleBar(
        title: String,
        textColor: ColorProvider = GlanceTheme.colors.onSurface,
        modifier: GlanceModifier = GlanceModifier,
        fontFamily: FontFamily? = null,
        actions: @Composable RowScope.() -> Unit = {},
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = title,
                style = TextStyle(
                    color = textColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                ),
                maxLines = 1,
            )

            actions()
        }
    }

    private suspend fun loadWidgetData(context: Context): WidgetData {
        return withContext(Dispatchers.IO) {
            val widgetGraph = context.appGraph.asContribution<WidgetGraph.Factory>().create()
            val agendaStore = widgetGraph.agendaStore

            try {
                val state = agendaStore.states
                    .filter { !it.isLoading }
                    .first()

                val days = state.days

                val today = days.getOrNull(0)
                val tomorrow = days.getOrNull(1)

                WidgetData(
                    today = DayData(
                        date = today?.date ?: getTodayDate(),
                        events = today?.events ?: emptyList(),
                    ),
                    tomorrow = DayData(
                        date = tomorrow?.date ?: getTomorrowDate(),
                        events = tomorrow?.events ?: emptyList(),
                    ),
                )
            } finally {
                agendaStore.destroy()
            }
        }
    }

    private fun getTodayDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    private fun getTomorrowDate(): LocalDate {
        return getTodayDate().plus(DatePeriod(days = 1))
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    private fun ContentPreview() {
        GlanceTheme {
            Content(
                WidgetData(
                    today = DayData(
                        date = getTodayDate(),
                        events = emptyList(),
                    ),
                    tomorrow = DayData(
                        date = getTomorrowDate(),
                        events = emptyList(),
                    ),
                ),
            )
        }
    }
}