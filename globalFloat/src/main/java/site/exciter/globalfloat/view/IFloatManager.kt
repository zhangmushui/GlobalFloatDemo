package site.exciter.globalfloat.view

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout

interface IFloatManager {
    fun add(): IFloatManager
    fun remove(): IFloatManager
    fun attach(activity: Activity?): IFloatManager
    fun attach(container: FrameLayout?): IFloatManager
    fun detach(activity: Activity?): IFloatManager
    fun detach(container: FrameLayout?): IFloatManager
    fun getView(): MagnetFloatView?
    fun autoHoverEdge(autoHoverEdge: Boolean): IFloatManager
    fun icon(resId: Int): IFloatManager;
    fun customView(viewGroup: MagnetFloatView?): IFloatManager
    fun customView(resource: Int): IFloatManager
    fun layoutParams(params: ViewGroup.LayoutParams?): IFloatManager
    fun listener(listener: MagnetFloatListener?): IFloatManager
}