package site.exciter.globalfloatdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import site.exciter.globalfloat.GlobalFloat
import site.exciter.globalfloat.view.MagnetFloatListener
import site.exciter.globalfloat.view.MagnetFloatView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.btn_show).setOnClickListener { show() }
        findViewById<View>(R.id.btn_dismiss).setOnClickListener { dismiss() }
        findViewById<View>(R.id.btn_next_01).setOnClickListener { toNext01() }
        findViewById<View>(R.id.btn_next_02).setOnClickListener { toNext02() }
    }

    private fun show() {
        GlobalFloat
            .layout(R.layout.layout_float_view)
            .blackList(mutableListOf(Next02Activity::class.java))
            .layoutParams(initLayoutParams())
            .autoHoverEdge(false)
            .listener(object : MagnetFloatListener {
                override fun onRemove(magnetFloatView: MagnetFloatView) {

                }

                override fun onClick(magnetFloatView: MagnetFloatView) {
                    Toast.makeText(this@MainActivity, "Clicked The Float View", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .show(this)
    }

    private fun dismiss() {
        GlobalFloat.dismiss(this)
    }

    private fun toNext01() {
        val intent = Intent(this, Next01Activity::class.java)
        startActivity(intent)
    }

    private fun toNext02() {
        val intent = Intent(this, Next02Activity::class.java)
        startActivity(intent)
    }

    private fun initLayoutParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.setMargins(0, params.topMargin, params.rightMargin, 500)
        return params
    }

}