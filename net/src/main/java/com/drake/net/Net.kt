/*
 * MIT License
 *
 * Copyright (c) 2023 劉強東 https://github.com/liangjingkanji
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("unused", "FunctionName") @file:JvmName("NetKt")

package com.drake.net

import android.util.Log
import com.drake.net.interfaces.ProgressListener
import com.drake.net.request.*
import com.drake.net.tag.NetTag

object Net {

    //<editor-fold desc="同步执行网络请求">
    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun get(
        path: String,
        tag: Any? = null,
        block: (UrlRequest.() -> Unit)? = null
    ) = UrlRequest().apply {
        setPath(path)
        method = Method.GET
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun post(
        path: String,
        tag: Any? = null,
        block: (BodyRequest.() -> Unit)? = null
    ) = BodyRequest().apply {
        setPath(path)
        method = Method.POST
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun head(
        path: String,
        tag: Any? = null,
        block: (UrlRequest.() -> Unit)? = null
    ) = UrlRequest().apply {
        setPath(path)
        method = Method.HEAD
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun options(
        path: String,
        tag: Any? = null,
        block: (UrlRequest.() -> Unit)? = null
    ) = UrlRequest().apply {
        setPath(path)
        method = Method.OPTIONS
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun trace(
        path: String,
        tag: Any? = null,
        block: (UrlRequest.() -> Unit)? = null
    ) = UrlRequest().apply {
        setPath(path)
        method = Method.TRACE
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun delete(
        path: String,
        tag: Any? = null,
        block: (BodyRequest.() -> Unit)? = null
    ) = BodyRequest().apply {
        setPath(path)
        method = Method.DELETE
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun put(
        path: String,
        tag: Any? = null,
        block: (BodyRequest.() -> Unit)? = null
    ) = BodyRequest().apply {
        setPath(path)
        method = Method.PUT
        tag(tag)
        block?.invoke(this)
    }

    /**
     * 同步网络请求
     *
     * @param path 请求路径, 如果其不包含http/https则会自动拼接[NetConfig.host]
     * @param tag 可以传递对象给Request请求, 一般用于在拦截器/转换器中进行针对某个接口行为判断
     * @param block 函数中可以配置请求参数
     */
    @JvmOverloads
    @JvmStatic
    fun patch(
        path: String,
        tag: Any? = null,
        block: (BodyRequest.() -> Unit)? = null
    ) = BodyRequest().apply {
        setPath(path)
        method = Method.PATCH
        tag(tag)
        block?.invoke(this)
    }
    //</editor-fold>

    //<editor-fold desc="取消网络请求">
    /**
     * 取消全部网络请求
     */
    @JvmStatic
    fun cancelAll() {
        NetConfig.runningCalls.forEach { it.get()?.cancel() }
        NetConfig.runningCalls.clear()
        NetConfig.okHttpClient.dispatcher.cancelAll()
    }

    /**
     * 取消指定的网络请求, Id理论上是唯一的, 所以该函数一次只能取消一个请求
     * @return 如果成功取消返回true
     */
    @JvmStatic
    fun cancelId(id: Any?): Boolean {
        if (id == null) return false
        val iterator = NetConfig.runningCalls.iterator()
        while (iterator.hasNext()) {
            val call = iterator.next().get() ?: continue
            if (id == call.request().tagOf<NetTag.RequestId>()?.value) {
                call.cancel()
                iterator.remove()
                return true
            }
        }
        return false
    }

    /**
     * 根据分组取消网络请求
     * @return 如果成功取消返回true, 无论取消个数
     */
    @JvmStatic
    fun cancelGroup(group: Any?): Boolean {
        if (group == null) return false
        val iterator = NetConfig.runningCalls.iterator()
        var hasCancel = false
        while (iterator.hasNext()) {
            val call = iterator.next().get() ?: continue
            val value = call.request().tagOf<NetTag.RequestGroup>()?.value
            if (group == value) {
                call.cancel()
                iterator.remove()
                hasCancel = true
            }
        }
        return hasCancel
    }
    //</editor-fold>

    //<editor-fold desc="监听请求进度">
    /**
     * 监听正在请求的上传进度
     * @param id 请求的Id
     * @see com.drake.net.request.BaseRequest.setId
     */
    @JvmStatic
    fun addUploadListener(id: Any, progressListener: ProgressListener) {
        NetConfig.runningCalls.forEach {
            val request = it.get()?.request() ?: return@forEach
            if (request.id == id) {
                request.uploadListeners().add(progressListener)
            }
        }
    }

    /**
     * 删除正在请求的上传进度监听器
     * @param id 请求的Id
     * @see com.drake.net.request.BaseRequest.setId
     */
    @JvmStatic
    fun removeUploadListener(id: Any, progressListener: ProgressListener) {
        NetConfig.runningCalls.forEach {
            val request = it.get()?.request() ?: return@forEach
            if (request.id == id) {
                request.uploadListeners().remove(progressListener)
            }
        }
    }

    /**
     * 监听正在请求的下载进度
     * @param id 请求的Id
     * @see com.drake.net.request.BaseRequest.setId
     */
    @JvmStatic
    fun addDownloadListener(id: Any, progressListener: ProgressListener) {
        NetConfig.runningCalls.forEach {
            val request = it.get()?.request() ?: return@forEach
            if (request.id == id) {
                request.downloadListeners().add(progressListener)
            }
        }
    }

    /**
     * 删除正在请求的下载进度监听器
     *
     * @param id 请求的Id
     * @see com.drake.net.request.BaseRequest.setId
     */
    @JvmStatic
    fun removeDownloadListener(id: Any, progressListener: ProgressListener) {
        NetConfig.runningCalls.forEach {
            val request = it.get()?.request() ?: return@forEach
            if (request.id == id) {
                request.downloadListeners().remove(progressListener)
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="日志">
    /**
     * 输出异常日志
     * @see NetConfig.debug
     */
    @Deprecated("命名变更, 后续版本将被删除", ReplaceWith("Net.debug(t)"))
    fun printStackTrace(t: Throwable) {
        debug(t)
    }

    /**
     * 输出异常日志
     * @param message 如果非[Throwable]则会自动追加代码位置(文件:行号)
     * @see NetConfig.debug
     */
    @JvmStatic
    fun debug(message: Any) {
        if (NetConfig.debug) {
            val adjustMessage = if (message is Throwable) {
                message.stackTraceToString()
            } else {
                val occurred = Throwable().stackTrace.getOrNull(1)?.run { " (${fileName}:${lineNumber})" } ?: ""
                message.toString() + occurred
            }
            Log.d(NetConfig.TAG, adjustMessage)
        }
    }
    //</editor-fold>
}
