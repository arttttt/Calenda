package com.arttttt.calenda

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
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class TestAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        Scaffold(
            modifier = GlanceModifier
                .cornerRadius(24.dp)
                .padding(8.dp),
            titleBar = {
                TitleBar(
                    title = "August",
                )
            },
        ) {
            LazyColumn {
                repeat(10) { index ->
                    item {
                        CalendarEvent(
                            title = "Nothing scheduled",
                        )
                    }

                    if (index < 10) {
                        item {
                            Spacer(
                                modifier = GlanceModifier.height(8.dp),
                            )
                        }
                    }
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
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = title,
                style = TextStyle(
                    color = textColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily
                ),
                maxLines = 1,
            )

            actions()
        }
    }

    @Composable
    private fun CalendarEvent(
        title: String,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.onBackground)
                .cornerRadius(8.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
            )
        }
    }
}