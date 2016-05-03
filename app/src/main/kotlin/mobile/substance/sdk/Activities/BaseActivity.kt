package mobile.substance.sdk.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        initViews()
        init()
    }

    open fun init() {
    }

    open fun initViews() {
    }

    open fun getLayoutResId(): Int {
        return 0
    }

}