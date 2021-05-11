package site.exciter.globalfloat.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import site.exciter.globalfloat.R

@SuppressLint("ViewConstructor")
class FloatView(context: Context, resource: Int) : MagnetFloatView(context, null) {

    private var mIcon: ImageView? = null

    init {
        View.inflate(context, resource, this)
        mIcon = findViewById(R.id.icon)
    }

    fun setImageIcon(resId: Int) {
        mIcon?.setImageResource(resId)
    }
}