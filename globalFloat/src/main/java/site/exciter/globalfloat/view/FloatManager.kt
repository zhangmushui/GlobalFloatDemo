package site.exciter.globalfloat.view

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import site.exciter.globalfloat.R
import site.exciter.globalfloat.utils.Context
import java.lang.ref.WeakReference

class FloatManager : IFloatManager {

    private var mFloatView: MagnetFloatView? = null
    private var mContainer: WeakReference<FrameLayout>? = null
    private var mLayoutId = R.layout.defalut_float_view
    private var mIconRes = R.drawable.ic_add
    private var mLayoutParams: ViewGroup.LayoutParams? = getParams()

    companion object {
        private var mInstance: FloatManager? = null
        fun get(): FloatManager? {
            if (null == mInstance) {
                synchronized(FloatManager::class.java) {
                    mInstance = FloatManager()
                }
            }
            return mInstance
        }
    }


    override fun add(): IFloatManager {
        ensureFloatView()
        return this
    }

    override fun remove(): IFloatManager {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            if (null == mFloatView) {
                return@Runnable
            }
            if (ViewCompat.isAttachedToWindow(mFloatView!!)) {
                getContainer()?.removeView(mFloatView)
            }
            mFloatView = null
        })
        return this
    }

    override fun attach(activity: Activity?): IFloatManager {
        attach(getActivityRoot(activity))
        return this
    }

    override fun attach(container: FrameLayout?): IFloatManager {
        if (container == null || null == mFloatView) {
            mContainer = WeakReference<FrameLayout>(container)
            return this
        }
        if (mFloatView?.parent == container) {
            return this
        }
        if (null != mFloatView?.parent) {
            (mFloatView?.parent as ViewGroup).removeView(mFloatView)
        }
        mContainer = WeakReference(container)
        container.addView(mFloatView)
        return this
    }

    override fun detach(activity: Activity?): IFloatManager {
        detach(getActivityRoot(activity))
        return this
    }

    override fun detach(container: FrameLayout?): IFloatManager {
        if (null != mFloatView && null != container && ViewCompat.isAttachedToWindow(mFloatView!!)) {
            container.removeView(mFloatView)
        }
        if (getContainer() == container) {
            mContainer = null
        }
        return this
    }

    override fun getView(): MagnetFloatView? {
        return mFloatView
    }

    override fun autoHoverEdge(autoHoverEdge: Boolean): IFloatManager {
        mFloatView?.setAutoHoverEdge(autoHoverEdge)
        return this
    }

    override fun icon(resId: Int): IFloatManager {
        mIconRes = resId
        return this
    }

    override fun customView(viewGroup: MagnetFloatView?): IFloatManager {
        mFloatView = viewGroup
        return this
    }

    override fun customView(resource: Int): IFloatManager {
        mLayoutId = resource
        return this
    }

    override fun layoutParams(params: ViewGroup.LayoutParams?): IFloatManager {
        mLayoutParams = params
        mFloatView?.layoutParams = params
        return this
    }

    override fun listener(listener: MagnetFloatListener?): IFloatManager {
        mFloatView?.setMagnetFloatListener(listener!!)
        return this
    }

    private fun ensureFloatView() {
        synchronized(this) {
            if (null != mFloatView) {
                return
            }
            val floatView = FloatView(Context.get()!!, mLayoutId)
            mFloatView = floatView
            floatView.layoutParams = mLayoutParams
            floatView.setImageIcon(mIconRes)
            addViewToWindow(floatView)
        }
    }

    private fun addViewToWindow(view: View) {
        getContainer()?.addView(view)
    }

    private fun getContainer(): FrameLayout? {
        return mContainer?.get()
    }

    private fun getParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.setMargins(13, params.topMargin, params.rightMargin, 500)
        return params
    }

    private fun getActivityRoot(activity: Activity?): FrameLayout? {
        try {
            return activity?.window?.decorView?.findViewById(android.R.id.content) as FrameLayout
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}