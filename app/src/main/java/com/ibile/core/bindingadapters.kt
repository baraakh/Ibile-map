package com.ibile.core

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibile.core.Extensions.dp
import com.maltaisn.icondialog.pack.IconPack
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*

@BindingAdapter("app:isVisible")
fun isVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("imageButtonEnabled")
fun setImageButtonEnabled(imageButton: ImageButton, enabled: Boolean) {
    with(imageButton) {
        if (enabled == isEnabled) return
        isEnabled = enabled

        if (enabled) return setImageDrawable(tag as Drawable)
        if (tag == null) tag = drawable
        setImageDrawable(drawable.toGrayScale())
    }
}

@BindingAdapter("drawableBackgroundColor")
fun setDrawableBackgroundColor(view: View, color: Int) {
    view.background.mutate().setColor(color)
}

@BindingAdapter(value = ["imageUri", "requestOptions"], requireAll = false)
fun setImageUri(imageView: ImageView, uri: Uri?, requestOptions: RequestOptions?) {
    if (uri == null) return
    Glide.with(imageView).load(uri)
        .apply(requestOptions ?: RequestOptions().centerCrop())
        .into(imageView)
}

@BindingAdapter("dimension")
fun setDimension(view: View, dimension: Float) {
    val lp = view.layoutParams
    lp.height = dimension.dp.toInt()
    lp.width = dimension.dp.toInt()
    view.layoutParams = lp
}

@ExperimentalStdlibApi
@BindingAdapter("capitalize")
fun capitalize(view: TextView, capitalize: Boolean) {
    if (capitalize) view.text = view.text.toString().capitalize(Locale.getDefault())
}

object BindingAdapters : KoinComponent {
    @BindingAdapter("src_iconPackId")
    @JvmStatic
    fun setSrcFromIconPack(view: ImageView, iconPackId: Int) {
        val drawable = get<IconPack>().getIconDrawable(iconPackId)?.mutate()
        view.setImageDrawable(drawable)
    }
}
