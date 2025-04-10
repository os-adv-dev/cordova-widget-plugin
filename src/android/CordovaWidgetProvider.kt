package com.outsystems.experts.widget.plugin

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import android.widget.RemoteViews
import com.outsystems.experts.widget.plugin.data.WidgetData
import org.json.JSONObject
import com.outsystems.experts.widget.plugin.data.WidgetType
import androidx.core.graphics.createBitmap
import org.json.JSONArray
import java.net.URL
import java.util.Locale

class CordovaWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val appName = context.resources.getIdentifier("app_name", "string", context.packageName)
        for (id in appWidgetIds) {
            val defaultData = WidgetData(type = WidgetType.TEXT, text = context.getString(appName))
            updateAppWidget(context, appWidgetManager, id, defaultData)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val json = intent.getStringExtra("widget_data") ?: return
            val data = WidgetData.fromJson(JSONObject(json))
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: appWidgetManager.getAppWidgetIds(ComponentName(context, CordovaWidgetProvider::class.java))

            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id, data)
            }
        }
    }

    companion object {

        const val ARG_WIDGET_DATA = "widget_data"
        const val ARG_WIDGET_LIST_ITEMS = "widget_list_items"
        const val ARG_WIDGET_LIST_BACKGROUND = "widget_list_background_color"

        fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int, data: WidgetData) {
            val layoutId = when (data.type) {
                WidgetType.TEXT -> context.resources.getIdentifier("widget_text", "layout", context.packageName)
                WidgetType.IMAGE -> context.resources.getIdentifier("widget_image", "layout", context.packageName)
                WidgetType.TEXT_IMAGE -> context.resources.getIdentifier("widget_text_image", "layout", context.packageName)
                WidgetType.LIST -> context.resources.getIdentifier("widget_list", "layout", context.packageName)
            }

            val views = RemoteViews(context.packageName, layoutId)

            // Apply text
            data.text?.let {
                views.setTextViewText(context.resources.getIdentifier("widget_text", "id", context.packageName), it)
                data.fontSize?.let { size ->
                    views.setFloat(context.resources.getIdentifier("widget_text", "id", context.packageName), "setTextSize", size.toFloat())
                }
            }

            // Apply title if LIST
            if (data.type == WidgetType.LIST && data.items != null) {
                val jsonArray = JSONArray()
                data.items.forEach { item ->
                    val obj = JSONObject()
                    obj.put("title", item.title)
                    obj.put("description", item.description)
                    jsonArray.put(obj)
                }

                val serviceIntent = Intent(context, WidgetListService::class.java).apply {
                    putExtra(ARG_WIDGET_LIST_ITEMS, jsonArray.toString())
                    putExtra(ARG_WIDGET_LIST_BACKGROUND, data.backgroundColor)
                }

                val widgetList = context.resources.getIdentifier("widget_list", "id", context.packageName)
                views.setRemoteAdapter(widgetList, serviceIntent)
                views.setEmptyView(widgetList, android.R.id.empty)

                // Title header list
                if (!data.text.isNullOrEmpty()) {
                    val id = context.resources.getIdentifier("widget_title", "id", context.packageName)
                    views.setTextViewText(id, data.text)
                }

                data.fontSize?.let { size ->
                    val id = context.resources.getIdentifier("widget_title", "id", context.packageName)
                    views.setFloat(id, "setTextSize", size.toFloat())
                }

                data.textColor?.let {
                    val id = context.resources.getIdentifier("widget_title", "id", context.packageName)
                    views.setTextColor(id, it)
                }

                val clickIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                clickIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    clickIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                views.setPendingIntentTemplate(widgetList, pendingIntent)
            }

            // Apply colors
            data.textColor?.let {
                val id = context.resources.getIdentifier("widget_text", "id", context.packageName)
                views.setTextColor(id, it)
            }
            data.backgroundColor?.let {
                val id = context.resources.getIdentifier("widget_root", "id", context.packageName)
                views.setInt(id, "setBackgroundColor", it)
            }

            // Apply image
            if (data.image != null) {
                val imageViewId = context.resources.getIdentifier("img", "id", context.packageName)
                val bitmap = when (data.image.type.uppercase(Locale.ROOT)) {
                    "BASE_64" -> {
                        try {
                            val decodedBytes = Base64.decode(data.image.value, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        } catch (e: Exception) {
                            Log.v("WidgetProvider", "Base64 decoding failed: ${e.message}")
                            null
                        }
                    }
                    "APP_ICON" -> {
                        val drawable = context.packageManager.getApplicationIcon(context.packageName)
                        when (drawable) {
                            is BitmapDrawable -> drawable.bitmap
                            is AdaptiveIconDrawable -> {
                                val temp =
                                    createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
                                val canvas = Canvas(temp)
                                drawable.setBounds(0, 0, canvas.width, canvas.height)
                                drawable.draw(canvas)
                                temp
                            }
                            else -> null
                        }
                    }
                    else -> null
                }

                bitmap?.let {
                    views.setImageViewBitmap(imageViewId, it)
                }
            }

            val clickIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            clickIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val id = context.resources.getIdentifier("widget_root", "id", context.packageName)
            views.setOnClickPendingIntent(id, pendingIntent)
            manager.updateAppWidget(widgetId, views)
        }


        fun downloadImageToBitmap(urlString: String): Bitmap? {
            return try {
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.getInputStream().use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } catch (e: Exception) {
                Log.e("WidgetProvider", "Image download failed: ${e.message}")
                null
            }
        }
    }

}