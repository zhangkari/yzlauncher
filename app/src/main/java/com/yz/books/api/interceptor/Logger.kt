package com.yz.books.api.interceptor

import android.text.TextUtils
import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 日志拦截器中的日志输出
 *
 * @author lilin
 * @time on 2018/7/25 14:02
 */
class Logger {

    companion object {

        private const val JSON_INDENT = 3
        private const val MAX_LONG_SIZE = 120
        private const val N = "\n"
        private const val T = "\t"

        private const val TOP_LEFT_CORNER = '╔'
        private const val BOTTOM_LEFT_CORNER = '╚'
        private const val DOUBLE_DIVIDER = "═════════════════════════════════════════════════"
        private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
        private val LINE_SEPARATOR = System.getProperty("line.separator")

        private fun String.isLineEmpty() = isEmpty() || N == this || T == this || this.trim { it <= ' ' }.isEmpty()

        private fun getDoubleSeparator(hideVerticalLine:Boolean=false):String =
            if (hideVerticalLine) "$LINE_SEPARATOR $LINE_SEPARATOR" else "${LINE_SEPARATOR}║ $LINE_SEPARATOR"

        /**
         * 支持超长日志的打印
         */
        private fun printLog(tag:String,logString: String) {

            if (logString.length > 4000) {

                var i = 0

                while (i < logString.length) {

                    if (i + 4000 < logString.length)
                        Log.i(tag, logString.substring(i, i + 4000))
                    else
                        Log.i(tag, logString.substring(i, logString.length))
                    i += 4000
                }
            } else
                Log.i(tag, logString)
        }

        @JvmStatic
        fun printJsonRequest(builder: LoggingInterceptor.Builder, request: Request) {

            val tag = builder.getTag(true)
            val hideVerticalLine = builder.hideVerticalLineFlag

            val sb = StringBuilder()
            sb.append("  ").append(LINE_SEPARATOR).append(TOP_BORDER).append(LINE_SEPARATOR)
            sb.append(getRequest(request, hideVerticalLine))

            if (request.method != "GET") { // get请求不需要body

                val requestBody = if (hideVerticalLine) {
                    " $LINE_SEPARATOR Body:$LINE_SEPARATOR"
                } else {
                    "║ ${LINE_SEPARATOR}║ Body:$LINE_SEPARATOR"
                }

                val bodyString = LINE_SEPARATOR?.toRegex()?.let { bodyToString(request).split(it).dropLastWhile { it.isEmpty() }.toTypedArray() }

                sb.append(requestBody+ bodyString?.let { logLines(it, hideVerticalLine) })

            } else {

                val header = request.headers.toString()

                if (header.isLineEmpty()) {

                    sb.append(LINE_SEPARATOR)
                }
            }

            sb.append(BOTTOM_BORDER)

            Log.i(tag, sb.toString())
        }

        @JvmStatic
        fun printFileRequest(builder: LoggingInterceptor.Builder, request: Request) {

            val tag = builder.getTag(true)

            val sb = StringBuilder()
            sb.append("  ").append(LINE_SEPARATOR).append(TOP_BORDER).append(LINE_SEPARATOR)
            sb.append(getRequest(request))

            val requestBody = "║ $LINE_SEPARATOR"

            val binaryBodyString =
                LINE_SEPARATOR?.toRegex()?.let { binaryBodyToString(request).split(it).dropLastWhile { it.isEmpty() }.toTypedArray() }

            sb.append(requestBody+ binaryBodyString?.let { logLines(it) })
            sb.append(BOTTOM_BORDER)

            Log.i(tag, sb.toString())
        }

        @JvmStatic
        fun printJsonResponse(builder: LoggingInterceptor.Builder, chainMs: Long, isSuccessful: Boolean,
                              code: Int, headers: String, bodyString: String, segments: List<String>) {

            val tag = builder.getTag(false)
            val hideVerticalLine = builder.hideVerticalLineFlag

            val sb = StringBuilder()
            sb.append("  ").append(LINE_SEPARATOR).append(TOP_BORDER).append(LINE_SEPARATOR)
            sb.append(getResponse(headers, chainMs, code, isSuccessful, segments, hideVerticalLine))

            val responseBody = if (hideVerticalLine) {
                " $LINE_SEPARATOR Body:$LINE_SEPARATOR"
            } else {
                "║ ${LINE_SEPARATOR}║ Body:$LINE_SEPARATOR"
            }

            val bodyString = LINE_SEPARATOR?.toRegex()?.let { getJsonString(bodyString).split(it).dropLastWhile { it.isEmpty() }.toTypedArray() }

            sb.append(responseBody+ bodyString?.let { logLines(it, hideVerticalLine) })

            sb.append(BOTTOM_BORDER)

            printLog(tag, sb.toString())
        }

        @JvmStatic
        fun printFileResponse(builder: LoggingInterceptor.Builder, chainMs: Long, isSuccessful: Boolean,
                              code: Int, headers: String, segments: List<String>) {

            val tag = builder.getTag(false)

            val sb = StringBuilder()
            sb.append("  ").append(LINE_SEPARATOR).append(TOP_BORDER).append(LINE_SEPARATOR)
            sb.append(getResponse(headers, chainMs, code, isSuccessful, segments))
            sb.append(BOTTOM_BORDER)

            Log.i(tag, sb.toString())
        }

        private fun getRequest(request: Request, hideVerticalLine:Boolean=false): String {

            val header = request.headers.toString()

            return if (hideVerticalLine) {

                " URL: " + request.url + getDoubleSeparator(hideVerticalLine) + " Method: @" + request.method + getDoubleSeparator(hideVerticalLine) +
                        if (header.isLineEmpty())  " " else " Headers:" + LINE_SEPARATOR + dotHeaders(header, hideVerticalLine)
            } else {

                "║ URL: " + request.url + getDoubleSeparator() + "║ Method: @" + request.method + getDoubleSeparator() +
                        if (header.isLineEmpty()) "║ " else "║ Headers:" + LINE_SEPARATOR + dotHeaders(header)
            }
        }

        private fun getResponse(header: String, tookMs: Long, code: Int, isSuccessful: Boolean,
                                segments: List<String>, hideVerticalLine:Boolean=false): String {

            val segmentString:String?

            return if (hideVerticalLine) {

                segmentString = " " + slashSegments(segments)

                (if (!TextUtils.isEmpty(segmentString)) "$segmentString - " else "") + "is success : " + isSuccessful + " - " + "Received in: " + tookMs + "ms" + getDoubleSeparator(hideVerticalLine) + " Status Code: " +
                        code + getDoubleSeparator(hideVerticalLine) +
                        if (header.isLineEmpty()) " " else " Headers:" + LINE_SEPARATOR + dotHeaders(header, hideVerticalLine)
            } else {

                segmentString = "║ " + slashSegments(segments)

                (if (!TextUtils.isEmpty(segmentString)) "$segmentString - " else "") + "is success : " + isSuccessful + " - " + "Received in: " + tookMs + "ms" + getDoubleSeparator() + "║ Status Code: " +
                        code + getDoubleSeparator() +
                        if (header.isLineEmpty()) "║ " else "║ Headers:" + LINE_SEPARATOR + dotHeaders(header)
            }
        }

        private fun slashSegments(segments: List<String>): String {
            val segmentString = StringBuilder()
            for (segment in segments) {
                segmentString.append("/").append(segment)
            }
            return segmentString.toString()
        }

        private fun dotHeaders(header: String, hideVerticalLine:Boolean=false): String {
            val headers =
                LINE_SEPARATOR?.toRegex()?.let { header.split(it).dropLastWhile { it.isEmpty() }.toTypedArray() }
            val builder = StringBuilder()

            if (headers != null) {
                if (headers.isNotEmpty()) {
                    for (item in headers) {
                        if (hideVerticalLine) {
                            builder.append(" - ").append(item).append("\n")
                        } else {
                            builder.append("║ - ").append(item).append("\n")
                        }
                    }
                } else {
                    builder.append(LINE_SEPARATOR)
                }
            }

            return builder.toString()
        }

        private fun logLines(lines: Array<String>,hideVerticalLine:Boolean=false): String {
            val sb = StringBuilder()
            for (line in lines) {
                val lineLength = line.length
                for (i in 0..lineLength / MAX_LONG_SIZE) {
                    val start = i * MAX_LONG_SIZE
                    var end = (i + 1) * MAX_LONG_SIZE
                    end = if (end > line.length) line.length else end

                    if (hideVerticalLine) {
                        sb.append(" " + line.substring(start, end)).append(LINE_SEPARATOR)
                    } else {
                        sb.append("║ " + line.substring(start, end)).append(LINE_SEPARATOR)
                    }

                }
            }

            return sb.toString()
        }

        private fun bodyToString(request: Request): String {
            try {
                val copy = request.newBuilder().build()
                val buffer = Buffer()
                if (copy.body == null) return ""

                copy.body!!.writeTo(buffer)
                return getJsonString(buffer.readUtf8())
            } catch (e: IOException) {
                return "{\"err\": \"" + e.message + "\"}"
            }
        }

        private fun binaryBodyToString(request: Request): String {

            val copy = request.newBuilder().build()
            val requestBody = copy.body ?: return ""

            var buffer:String?
            buffer = if (requestBody.contentType()!=null) {
                "Content-Type: "+requestBody.contentType().toString()
            } else {
                ""
            }

            if (requestBody.contentLength() > 0) {
                buffer += LINE_SEPARATOR + "Content-Length: "+requestBody.contentLength()
            }

            val method = request.method
            if ("POST" == method) {
                val sb = java.lang.StringBuilder()
                if (request.body is FormBody) {
                    val body = request.body as FormBody?
                    if (body != null) {
                        for (i in 0 until body.size) {
                            sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",")
                        }
                    }
                    sb.delete(sb.length - 1, sb.length)

                    buffer += LINE_SEPARATOR + "Body: "+sb.toString()
                }
            } else {
                buffer += LINE_SEPARATOR + "Body: "+ request.url
            }

            return buffer
        }

        @JvmStatic
        fun getJsonString(msg: String): String {

            return try {
                when {
                    msg.startsWith("{") -> {
                        val jsonObject = JSONObject(msg)
                        jsonObject.toString(JSON_INDENT)
                    }
                    msg.startsWith("[") -> {
                        val jsonArray = JSONArray(msg)
                        jsonArray.toString(JSON_INDENT)
                    }
                    else -> msg
                }
            } catch (e: JSONException) {
                msg
            }
        }
    }

}