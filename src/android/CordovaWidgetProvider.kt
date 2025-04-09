package com.outsystems.experts.widget.plugin

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.cordova.hellocordova.R

class CordovaWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id, "Texto inicial")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val text = intent.getStringExtra("widget_text") ?: "Texto padrão"
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(ComponentName(context, CordovaWidgetProvider::class.java))

            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id, text)
            }
        }
    }

    companion object {

        fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int, text: String) {
            val views = RemoteViews(context.packageName, R.layout.cordova_widget)
            views.setTextViewText(R.id.widget_text, text)
            manager.updateAppWidget(widgetId, views)
        }
    }
}