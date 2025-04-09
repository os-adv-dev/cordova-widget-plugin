var exec = require('cordova/exec');

exports.updateWidget = function (text, success, error) {
    exec(success, error, 'WidgetPlugin', 'updateWidget', [text]);
};

exports.startAutoUpdate = function (success, error) {
    exec(success, error, 'WidgetPlugin', 'startAutoUpdate', []);
};

exports.stopAutoUpdate = function (success, error) {
    exec(success, error, 'WidgetPlugin', 'stopAutoUpdate', []);
};