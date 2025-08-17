/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.glance.tools.sample

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.android.glance.appwidget.host.AppWidgetHostPreview

class SampleAppWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetManager.updateAppWidget(appWidgetIds, SampleAppWidget.createWidget(context))
    }
}

object SampleAppWidget {
    fun createWidget(context: Context): RemoteViews = RemoteViews(
        context.packageName,
        R.layout.widget_sample,
    )
}

@Preview
@Composable
fun SampleAppWidgetPreview() {
    AppWidgetHostPreview(
        modifier = Modifier.fillMaxSize(),
        displaySize = DpSize(200.dp, 200.dp),
    ) { context ->
        SampleAppWidget.createWidget(context)
    }
}
