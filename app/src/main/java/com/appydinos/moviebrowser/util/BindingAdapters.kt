package com.appydinos.moviebrowser.util

import android.view.View
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import java.lang.Exception

@BindingAdapter("visibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("lottie_rawRes_binding")
fun setLottieAnimationRawResAsset(view: LottieAnimationView, animation: Int) {
    try {
        view.setAnimation(animation)
    } catch (ex: Exception) {}
}