package com.huynhngoctai.vpn_ict.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val icon: Drawable,
    var isSelected: Boolean = false
)
