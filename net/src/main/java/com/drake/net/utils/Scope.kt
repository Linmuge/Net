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

package com.drake.net.utils

import android.app.Dialog
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.drake.brv.PageRefreshLayout
import com.drake.net.scope.*
import com.drake.statelayout.StateLayout
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


/**
 * 作用域内部全在主线程
 * 作用域全部属于异步
 * 作用域内部异常全部被捕获, 不会引起应用崩溃
 */

//<editor-fold desc="异步任务">
/**
 * 异步作用域
 *
 * 该作用域生命周期跟随整个应用, 注意内存泄漏
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun scope(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): AndroidScope {
    return AndroidScope(dispatcher = dispatcher).launch(block)
}

/**
 * 异步作用域
 *
 * 该作用域生命周期跟随[LifecycleOwner]
 * @param lifeEvent 生命周期事件, 默认为[Lifecycle.Event.ON_DESTROY]下取消协程作用域
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun LifecycleOwner.scopeLife(
    lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) = AndroidScope(this, lifeEvent, dispatcher).launch(block)

/**
 * 异步作用域
 *
 * 该作用域生命周期跟随[Fragment]
 * @param lifeEvent 生命周期事件, 默认为[Lifecycle.Event.ON_DESTROY]下取消协程作用域
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun Fragment.scopeLife(
    lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): AndroidScope {
    val coroutineScope = AndroidScope(dispatcher = dispatcher).launch(block)
    viewLifecycleOwnerLiveData.observe(this) {
        it?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (lifeEvent == event) coroutineScope.cancel()
            }
        })
    }
    return coroutineScope
}
//</editor-fold>

// <editor-fold desc="加载对话框">

/**
 * 作用域开始时自动显示加载对话框, 结束时自动关闭加载对话框
 * 可以设置全局对话框 [com.drake.net.NetConfig.dialogFactory]
 * 对话框被取消或者界面关闭作用域被取消
 *
 * @param dialog 仅该作用域使用的对话框
 * @param cancelable 对话框是否可取消
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun FragmentActivity.scopeDialog(
    dialog: Dialog? = null,
    cancelable: Boolean = true,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) = DialogCoroutineScope(this, dialog, cancelable, dispatcher).launch(block)

/**
 * 作用域开始时自动显示加载对话框, 结束时自动关闭加载对话框
 * 可以设置全局对话框 [com.drake.net.NetConfig.dialogFactory]
 * 对话框被取消或者界面关闭作用域被取消
 * @param dialog 仅该作用域使用的对话框
 * @param cancelable 对话框是否可取消
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun Fragment.scopeDialog(
    dialog: Dialog? = null,
    cancelable: Boolean = true,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) = DialogCoroutineScope(requireActivity(), dialog, cancelable, dispatcher).launch(block)

// </editor-fold>


//<editor-fold desc="视图">
/**
 * 自动处理缺省页的异步作用域
 * 作用域开始执行时显示加载中缺省页
 * 作用域正常结束时显示成功缺省页
 * 作用域抛出异常时显示错误缺省页
 * 并且自动吐司错误信息, 可配置 [com.drake.net.interfaces.NetErrorHandler.onStateError]
 * 自动打印异常日志
 * 布局被销毁或者界面关闭作用域被取消
 * @receiver 当前视图会被缺省页包裹
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun StateLayout.scope(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): NetCoroutineScope {
    val scope = StateCoroutineScope(this, dispatcher)
    scope.launch(block)
    return scope
}

/**
 * PageRefreshLayout的异步作用域
 *
 * 1. 下拉刷新自动结束
 * 2. 上拉加载自动结束
 * 3. 捕获异常
 * 4. 打印异常日志
 * 5. 吐司部分异常[com.drake.net.interfaces.NetErrorHandler.onStateError]
 * 6. 判断添加还是覆盖数据
 * 7. 自动显示缺省页
 *
 * 布局被销毁或者界面关闭作用域被取消
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun PageRefreshLayout.scope(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): PageCoroutineScope {
    val scope = PageCoroutineScope(this, dispatcher)
    scope.launch(block)
    return scope
}

/**
 * 视图作用域
 * 会在视图销毁时自动取消作用域
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun View.scopeNetLife(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): ViewCoroutineScope {
    val scope = ViewCoroutineScope(this, dispatcher)
    scope.launch(block)
    return scope
}
//</editor-fold>

//<editor-fold desc="网络">

/**
 * 该函数比[scope]多了以下功能
 * - 在作用域内抛出异常时会被回调的[com.drake.net.interfaces.NetErrorHandler.onError]函数中
 * - 自动显示错误信息吐司, 可以通过指定[com.drake.net.interfaces.NetErrorHandler.onError]来取消或者增加自己的处理
 *
 * 该作用域生命周期跟随整个应用, 注意内存泄漏
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun scopeNet(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) = NetCoroutineScope(dispatcher = dispatcher).launch(block)

/**
 * 该函数比scopeNet多了自动取消作用域功能
 *
 * 该作用域生命周期跟随LifecycleOwner. 比如传入Activity会默认在[FragmentActivity.onDestroy]时取消网络请求.
 * @receiver 可传入FragmentActivity/AppCompatActivity, 或者其他的实现了LifecycleOwner的类
 * @param lifeEvent 指定LifecycleOwner处于生命周期下取消网络请求/作用域
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun LifecycleOwner.scopeNetLife(
    lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
) = NetCoroutineScope(this, lifeEvent, dispatcher).launch(block)

/**
 * 和[scopeNetLife]功能相同, 只是接受者为Fragment
 *
 * @param lifeEvent 生命周期事件, 默认为[Lifecycle.Event.ON_DESTROY]下取消协程作用域
 * @param dispatcher 调度器, 默认运行在[Dispatchers.Main]即主线程下
 */
fun Fragment.scopeNetLife(
    lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): NetCoroutineScope {
    val coroutineScope = NetCoroutineScope(dispatcher = dispatcher).launch(block)
    viewLifecycleOwnerLiveData.observe(this) {
        it?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (lifeEvent == event) coroutineScope.cancel()
            }
        })
    }
    return coroutineScope
}


//</editor-fold>