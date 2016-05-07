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

package mobile.substance.sdk.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import mobile.substance.sdk.R

/**
 * Created by Julian Os on 03.05.2016.
 */
class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    var subtitle: TextView? = null
    var image: ImageView? = null

    init {
        title = itemView.findViewById(R.id.list_item_title) as TextView
        subtitle = itemView.findViewById(R.id.list_item_subtitle) as TextView
        image = itemView.findViewById(R.id.list_item_icon) as ImageView
    }

}