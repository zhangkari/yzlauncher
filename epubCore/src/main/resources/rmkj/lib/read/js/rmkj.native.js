/**
 * Created by vkenchen on 13-12-27.
 */

(function ($) {
    $.Client = function () {

        var iosIframe;
        var platformType;

        this.platformTypeEnum = {
            IOS: "ios",
            ANDROID: "android",
            WINDOWS: "windows"
        };

        this.setPlatform = function (platform) {
            destroyIOS();

            platformType = platform;
            if (platformType == this.platformTypeEnum.IOS) {
                initIOS();
            }
        };

        this.sendMessage = function (operation, parameters) {
            if (!operation) {
                trace("sendMessage error,invalid operation");
                return null;
            }
            var jsonString = null;

            //if parameters valid
            if (parameters) {
                var jsonToClient = {"function": operation, "parameters": parameters};
                jsonString = JSON.stringify(jsonToClient);
            } else {
                var jsonToClient = {"function": operation};
                jsonString = JSON.stringify(jsonToClient);
            }

            if (this.platformTypeEnum.ANDROID == platformType) {
                return sendMessageToAndroid(jsonString);
            } else if (this.platformTypeEnum.IOS == platformType) {
                return sendMessageToIOS(jsonString);
            } else if (this.platformTypeEnum.WINDOWS == platformType) {
                return sendMessageToWindows(jsonString);
            } else {
                trace("sendMessage error,platform is invalid");
            }
            return;
        };

        function initIOS() {
            if (!iosIframe) {
                iosIframe = document.createElement('iframe');
                iosIframe.style.display = 'none';
                iosIframe.style.width = 0;
                iosIframe.style.height = 0;
                iosIframe.frameborder = 0;
                document.body.appendChild(iosIframe);
            }
        }

        function destroyIOS() {
            if (iosIframe) {
                iosIframe.parentNode.removeChild(iosIframe);
                iosIframe = null;
            }
        }

        // 私有函数：trace
        function trace(text) {
            var traceTag = "rmkj.native.js: ";
            if (window.console && window.console.log)
                window.console.log(traceTag + text);
        };

        function sendMessageToAndroid(json) {
            window.JSInterface.onJSCall(json);
        }

        //ios
        function sendMessageToIOS(json) {
            iosIframe.src = json;
            return;
        }

        function sendMessageToWindows(json) {
            return json;
        }
    };
})(jQuery);
