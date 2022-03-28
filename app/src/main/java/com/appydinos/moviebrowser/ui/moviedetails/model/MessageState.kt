package com.appydinos.moviebrowser.ui.moviedetails.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageState(
    val showMessageView: Boolean,
    val messageText: String,
    val messageAnimation: Int,
    val animationAspectRatio: Float,
    val canRetry: Boolean
): Parcelable
