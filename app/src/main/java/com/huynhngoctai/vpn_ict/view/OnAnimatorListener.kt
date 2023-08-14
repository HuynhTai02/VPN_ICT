package com.huynhngoctai.vpn_ict.view

import android.animation.Animator
import android.animation.Animator.AnimatorListener

interface OnAnimatorListener : AnimatorListener {
    override fun onAnimationStart(animation: Animator) {
    }

    override fun onAnimationEnd(animation: Animator) {
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }
}