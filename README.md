# 📱 Cordova Widget Plugin

**Cordova plugin to dynamically render Android (only) home screen widgets with support for Text, Image, Text + Image, and List layouts.**  
Perfect for use in enterprise applications, integrations with OutSystems, or customizable product experiences.

---

## ✨ Features

- 📋 Dynamic layouts (Text, Image, Text+Image, List)
- 🎨 Custom font size, colors, and styling
- 🔄 Resizable and responsive (supports widget resizing)
- ✅ Clickable widget area and list items (launches your app)
- 🔧 Fully customizable via a single JSON `payload`

---

## 📦 Installation

```bash
cordova plugin add cordova-widget-plugin
```

---

## 🛠 Usage

```js
cordova.plugins.WidgetPlugin.updateWidget(payload, successCallback, errorCallback);
```

---

## 📨 Payload Examples

The widget is fully customizable via a single `payload` object.  
Each layout type supports different fields as described below:

---

### 🧾 Common Parameters

| Field             | Type     | Description                                                                 |
|------------------|----------|-----------------------------------------------------------------------------|
| `type`           | `string` | Widget layout type. Options: `TEXT`, `IMAGE`, `TEXT_IMAGE`, `LIST`         |
| `text`           | `string` | Main text to display                                                        |
| `textColor`      | `string` | Text color in hex (e.g. `#FFFFFF`)                                          |
| `backgroundColor`| `string` | Widget background color                                                     |
| `fontSize`       | `number` | Font size in `sp` (scale-independent pixels)                                |
| `image`          | `object` | Object with `type` and `value`. Type can be: `"BASE_64"`, or `"APP_ICON"` |
| `items`          | `array`  | List of items for `LIST` layout (each item must have `title` and `description`) |

---

### 🔹 Type: `TEXT`

A minimal layout that displays a single text.

```json
{
  "type": "TEXT",
  "text": "🚀 This is a text-only widget!",
  "textColor": "#FFFFFF",
  "fontSize": 22,
  "backgroundColor": "#007BFF"
}
```

✅ Use this to show status, headlines, or any message.

---

### 🖼️ Type: `IMAGE`

Shows only an image in the widget (app icon or custom drawable).

```json
{
  "type": "IMAGE",
  "image":  {
    "type": "APP_ICON",
    "value": "..." // Depends on type
  },
  "backgroundColor": "#EEEEEE"
}
```

### 🖼️ Image Object Format

You can define an image using the following object format:

```json
"image": {
  "type": "BASE_64" | "APP_ICON",
  "value": "..." // Depends on type
}
```

✅ Perfect for brand widgets or showing app logo or status icon.

---

### 🔸 Type: `TEXT_IMAGE`

Displays a block of text followed by an image.

```json
{
  "type": "TEXT_IMAGE",
  "text": "🔥 Text with image below",
  "image":  {
    "type": "APP_ICON",
    "value": "..." // Depends on type
  },
  "textColor": "#000000",
  "backgroundColor": "#F5F5F5"
}
```

✅ Great for promotional widgets or dynamic tips.

---

### 📋 Type: `LIST`

Displays a list of items, each with a title and description.  
This layout is scrollable and resizable.

```json
{
  "type": "LIST",
  "text": "📋 Dynamic List",
  "textColor": "#007BFF",
  "backgroundColor": "#FFFFFF",
  "fontSize": 18,
  "items": [
    { "title": "Item One", "description": "Description for item one" },
    { "title": "Item Two", "description": "Details for item two" },
    { "title": "Item Three", "description": "Details for item three" },
    { "title": "Item Four", "description": "Details for item four" }
  ]
}
```

✅ Ideal for showing recent updates, reminders, or short news feeds.

---

### 🚀 Triggering from JavaScript

```js
cordova.plugins.WidgetPlugin.updateWidget(payload, function(success) {
  console.log("✅ Widget updated:", success);
}, function(error) {
  console.error("❌ Widget update failed:", error);
});
```

You can dynamically switch layouts and test them one by one using different `payload` objects.

---

## 📲 Compatibility

- ✅ Cordova Android 9+
- ✅ Android 7.0+ (API 24+ recommended)
- ✅ Compatible with OutSystems via custom plugin bridge

---

## 🛡 License

Apache 2.0  
Developed by Paulo Camilo @ OutSystems for professional widget integrations on Android.