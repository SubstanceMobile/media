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