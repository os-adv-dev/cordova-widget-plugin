package com.outsystems.experts.widget.plugin.data

import android.util.Log
import org.json.JSONObject
import androidx.core.graphics.toColorInt

enum class WidgetType {
    TEXT,
    IMAGE,
    TEXT_IMAGE,
    LIST
}

data class WidgetItem(
    val title: String? = null,
    val description: String? = null
)

data class WidgetImage(
    val type: String,
    val value: String
)

data class WidgetData(
    val type: WidgetType,
    val text: String? = null,
    val image: WidgetImage? = null,
    val items: List<WidgetItem>? = null,
    val textColor: Int? = null,
    val backgroundColor: Int? = null,
    val fontSize: Int? = null,
    val alignment: String? = null,
    val clickAction: String? = null
) {
    companion object {
        fun fromJson(json: JSONObject): WidgetData {
            val type = try {
                WidgetType.valueOf(json.optString("type", "TEXT"))
            } catch (e: IllegalArgumentException) {
                Log.v("WidgetData", "Invalid widget type, defaulting to TEXT: "+e.message)
                WidgetType.TEXT
            }

            val text = json.optString("text", "")
            val fontSize = if (json.has("fontSize")) json.optInt("fontSize") else null
            val alignment = json.optString("alignment", "0")
            val clickAction = json.optString("clickAction", "")

            val items = if (json.has("items")) {
                val jsonArray = json.getJSONArray("items")
                List(jsonArray.length()) { i ->
                    val obj = jsonArray.getJSONObject(i)
                    WidgetItem(
                        title = obj.optString("title", ""),
                        description = obj.optString("description", "")
                    )
                }
            } else null

            val textColor = parseColorSafely(json.optString("textColor"))
            val backgroundColor = parseColorSafely(json.optString("backgroundColor"))

            val imageObj = if (json.has("image") || json.has("Image")) {
                val obj = json.getJSONObject("image") ?:  json.getJSONObject("Image")
                WidgetImage(
                    type = obj.optString("type", "APP_ICON"),
                    value = obj.optString("value", "")
                )
            } else null

            return WidgetData(
                type = type,
                text = text,
                image = imageObj,
                items = items,
                textColor = textColor,
                backgroundColor = backgroundColor,
                fontSize = fontSize,
                alignment = alignment,
                clickAction = clickAction
            )
        }

        private fun parseColorSafely(colorString: String?): Int? {
            return try {
                if (!colorString.isNullOrEmpty()) colorString.toColorInt() else null
            } catch (e: IllegalArgumentException) {
                Log.v("WidgetData", "parseColorSafely: ${e.message}")
                null
            }
        }
    }
}