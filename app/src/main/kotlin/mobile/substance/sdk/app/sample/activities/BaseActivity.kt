/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobile.substance.sdk.app.sample.activities

import android.os.Bundle
import com.afollestad.assent.Assent
import com.afollestad.assent.AssentActivity
import com.afollestad.assent.AssentCallback
import com.afollestad.assent.PermissionResultSet
import java.util.*

abstract class BaseActivity : AssentActivity(), AssentCallback {

    var savedInstanceState: Bundle? = null

    override fun onPermissionResult(result: PermissionResultSet?) {
        if (result!!.allPermissionsGranted())
            init(savedInstanceState)
    }

    companion object {
        val UNIQUE_ASSENT_REQUEST_CODE: Int by lazy {
            Random().nextInt(100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        setContentView(layoutResId)
        Thread() {
            run {
                if (!Assent.isPermissionGranted(Assent.READ_EXTERNAL_STORAGE) || !Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    Assent.requestPermissions(this, UNIQUE_ASSENT_REQUEST_CODE, Assent.READ_EXTERNAL_STORAGE, Assent.WRITE_EXTERNAL_STORAGE)
                } else runOnUiThread { init(savedInstanceState) }
            }
        }.start()
    }

    abstract fun init(savedInstanceState: Bundle?)

    abstract val layoutResId: Int

}