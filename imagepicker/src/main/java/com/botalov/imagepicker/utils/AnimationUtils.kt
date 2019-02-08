package com.botalov.imagepicker.utils

import android.animation.Animator
import android.graphics.Point
import android.view.View
import android.view.ViewAnimationUtils

class AnimationUtils {
    companion object {
        fun circleAnimationView(view: View, isShow: Boolean, duration: Long, x: Int, y: Int, sizeWindow: Point) {
            view.post {
                if (!isShow) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val startRadius = Math.hypot(sizeWindow.x.toDouble(), sizeWindow.y.toDouble()).toInt()
                        val endRadius = 0
                        val animator = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(), endRadius.toFloat()
                        )

                        animator.duration = duration

                        animator.start()
                        animator.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                view.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator) {

                            }

                            override fun onAnimationRepeat(animation: Animator) {

                            }
                        })
                    } else {
                        view.animate().alpha(0.0f).setDuration(duration)
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}

                                override fun onAnimationEnd(animation: Animator) {
                                    view.visibility = View.GONE
                                }

                                override fun onAnimationCancel(animation: Animator) {}

                                override fun onAnimationRepeat(animation: Animator) {}
                            })
                    }
                } else {
                    val startRadius = 0
                    val endRadius = Math.hypot(sizeWindow.x.toDouble(), sizeWindow.y.toDouble()).toInt()
                    // val endRadius = 600
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val animator = ViewAnimationUtils.createCircularReveal(
                            view,
                            x,
                            y,
                            startRadius.toFloat(), endRadius.toFloat()
                        )
                        animator.duration = duration

                        view.visibility = View.VISIBLE
                        animator.start()
                    } else {
                        view.animate().alphaBy(0.0f).alpha(1.0f).setDuration(duration)
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {
                                    view.alpha = 0.0f
                                    view.visibility = View.VISIBLE
                                }

                                override fun onAnimationEnd(animation: Animator) {}

                                override fun onAnimationCancel(animation: Animator) {}

                                override fun onAnimationRepeat(animation: Animator) {}
                            })
                    }
                }
            }
        }
    }
}