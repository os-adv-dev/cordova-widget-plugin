package com.outsystems.experts.widget.plugin

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.outsystems.experts.widget.plugin.data.WidgetData
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.json.JSONArray
import org.json.JSONObject

class WidgetPlugin : CordovaPlugin() {

    override fun execute(action: String?, args: JSONArray?, callbackContext: CallbackContext?): Boolean {
        val context = cordova.activity.applicationContext

        return when (action) {
            ACTION_UPDATE_WIDGET -> {
                val json = args?.optJSONObject(0)
                if (json != null) {
                    val data = WidgetData.fromJson(json)
                    sendWidgetUpdate(context, data)
                    callbackContext?.success("Widget updated")
                    true
                } else {
                    callbackContext?.error("Invalid JSON")
                    false
                }
                true
            }
            else -> false
        }
    }

    private fun sendWidgetUpdate(context: Context, data: WidgetData) {
        val intent = Intent(context, CordovaWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(CordovaWidgetProvider.ARG_WIDGET_DATA, dataToJson(data).toString())
        }
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, CordovaWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    private fun dataToJson(data: WidgetData): JSONObject {
        val json = JSONObject()
        json.put("type", data.type.name)
        data.text?.let { json.put("text", it) }
        data.image?.let {
            val imageJson = JSONObject().apply {
                put("type", it.type)
                put("value", it.value)
            }
            json.put("image", imageJson)
        }
        data.items?.let {
            val itemsArray = JSONArray()
            for (item in it) {
                val itemObj = JSONObject()
                item.title?.let { t -> itemObj.put("title", t) }
                item.description?.let { d -> itemObj.put("description", d) }
                itemsArray.put(itemObj)
            }
            json.put("items", itemsArray)
        }
        data.textColor?.let { json.put("textColor", String.format("#%06X", 0xFFFFFF and it)) }
        data.backgroundColor?.let { json.put("backgroundColor", String.format("#%06X", 0xFFFFFF and it)) }
        data.fontSize?.let { json.put("fontSize", it) }
        data.alignment?.let { json.put("alignment", it) }
        data.clickAction?.let { json.put("clickAction", it) }
        return json
    }

    companion object {
        const val ACTION_UPDATE_WIDGET = "updateWidget"
    }
}