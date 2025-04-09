package com.outsystems.experts.widget.plugin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import org.json.JSONArray

class WidgetListService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetListFactory(applicationContext, intent)
    }

    class WidgetListFactory(private val context: Context, private val intent: Intent) : RemoteViewsFactory {

        private var items: List<Pair<String, String>> = emptyList()
        private var backgroundColor: Int? = null

        override fun onCreate() {}

        override fun onDataSetChanged() {
            val rawList = intent.getStringExtra(CordovaWidgetProvider.ARG_WIDGET_LIST_ITEMS) ?: return
            backgroundColor = intent.getIntExtra(CordovaWidgetProvider.ARG_WIDGET_LIST_BACKGROUND, 0)

            try {
                val jsonArray = JSONArray(rawList)
                items = (0 until jsonArray.length()).map { i ->
                    val obj = jsonArray.getJSONObject(i)
                    Pair(
                        obj.optString("title", ""),
                        obj.optString("description", "")
                    )
                }
            } catch (e: Exception) {
                Log.v("WidgetListFactory", "Exception list: ${e.message}")
                items = emptyList()
            }
        }

        override fun onDestroy() {
            items = emptyList()
        }

        override fun getCount(): Int = items.size

        override fun getViewAt(position: Int): RemoteViews {
            val item = items[position]
            val itemTitleId = context.resources.getIdentifier("item_title", "id", context.packageName)
            val itemDescriptionId = context.resources.getIdentifier("item_description", "id", context.packageName)
            val itemContainerId = context.resources.getIdentifier("item_container", "id", context.packageName)
            val widgetListItemLayout = context.resources.getIdentifier("widget_list_item", "layout", context.packageName)
            val views = RemoteViews(context.packageName, widgetListItemLayout)

            views.setTextViewText(itemTitleId, item.first)
            views.setTextViewText(itemDescriptionId, item.second)

            backgroundColor?.let { backgroundColor ->
                views.setInt(itemContainerId, "setBackgroundColor", backgroundColor)
            }

            val fillInIntent = Intent()
            views.setOnClickFillInIntent(itemContainerId, fillInIntent)
            return views
        }

        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true
    }
}