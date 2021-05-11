package site.exciter.globalfloat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import site.exciter.globalfloat.view.FloatManager
import site.exciter.globalfloat.view.FloatView
import site.exciter.globalfloat.view.MagnetFloatListener

object GlobalFloat : Application.ActivityLifecycleCallbacks {
    private var mLayoutParams = getFloatLayoutParams()
    private val mBlackList = mutableListOf<Class<*>>()
    private var mLayout: Int = 0
    private var mListener: MagnetFloatListener? = null
    private var isAutoHoverEdge = true

    /**
     * 自定义布局
     */
    fun layout(layout: Int): GlobalFloat {
        mLayout = layout
        return this
    }

    /**
     * 参数
     */
    fun layoutParams(layoutParams: FrameLayout.LayoutParams): GlobalFloat {
        mLayoutParams = layoutParams
        return this
    }

    /**
     * 是否自动悬停到侧边
     */
    fun autoHoverEdge(autoHoverEdge: Boolean): GlobalFloat {
        isAutoHoverEdge = autoHoverEdge
        return this
    }

    /**
     * 不显示悬浮窗的页面
     */
    fun blackList(blackList: MutableList<Class<*>>): GlobalFloat {
        mBlackList.addAll(blackList)
        return this
    }

    /**
     * 悬浮窗监听，包括点击和移除
     */
    fun listener(listener: MagnetFloatListener): GlobalFloat {
        mListener = listener
        return this
    }

    /**
     * 显示悬浮窗
     */
    fun show(activity: Activity?) {
        initShow(activity)
        activity?.application?.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 隐藏悬浮窗
     */
    fun dismiss(activity: Activity?) {
        FloatManager.get()?.remove()
        FloatManager.get()?.detach(activity)
        activity?.application?.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
        initShow(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if (isActivityInValid(activity)) {
            return
        }
        FloatManager.get()?.detach(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    private fun getFloatLayoutParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.setMargins(0, params.topMargin, params.rightMargin, 500)
        return params
    }

    private fun initShow(activity: Activity?) {
        activity?.let {
            if (FloatManager.get()?.getView() == null) {
                FloatManager.get()?.customView(
                    FloatView(activity, mLayout)
                )
            }
            FloatManager.get()?.autoHoverEdge(isAutoHoverEdge)
            FloatManager.get()?.layoutParams(mLayoutParams)
            FloatManager.get()?.attach(it)
            FloatManager.get()?.listener(mListener)
        }
    }

    private fun isActivityInValid(activity: Activity?): Boolean {
        return mBlackList.contains(activity!!::class.java)
    }
}