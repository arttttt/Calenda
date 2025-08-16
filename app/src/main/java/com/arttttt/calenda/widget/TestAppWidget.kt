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
import com.arttttt.calenda.widget.ui.CalendarDayLayout

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
            horizontalPadding = 0.dp,
            titleBar = {
                TitleBar(
                    title = "August",
                )
            },
        ) {
            LazyColumn(
                modifier = GlanceModifier.padding(
                    top = 8.dp,
                )
            ) {
                repeat(10) { index ->
                    item { CalendarDayLayout() }

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

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    private fun ContentPreview() {
        GlanceTheme {
            Content()
        }
    }
}