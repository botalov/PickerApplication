package com.botalov.imagepicker.utils

import android.animation.Animator
import android.graphics.Point
import android.view.View
import android.view.ViewAnimationUtils
import io.reactivex.Observer

class AnimationUtils {
    companion object {
        fun circleShowAnimationView(view: View, duration: Long, point: Point, sizeWindow: Point, endObserver:  Observer<Boolean>?) {
            val startRadius = 0
            val endRadius = Math.hypot(sizeWindow.x.toDouble(), sizeWindow.y.toDouble()).toInt()
            // val endRadius = 600
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    point.x,
                    point.y,
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
                            endObserver?.onNext(true)
                        }

                        override fun onAnimationEnd(animation: Animator) {}

                        override fun onAnimationCancel(animation: Animator) {}

                        override fun onAnimationRepeat(animation: Animator) {}
                    })
            }
        }

        fun circleHideAnimationView(view: View, duration: Long, point: Point, sizeWindow: Point, endObserver:  Observer<Boolean>?) {
            view.post {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val startRadius = Math.hypot(sizeWindow.x.toDouble(), sizeWindow.y.toDouble()).toInt()
                    val endRadius = 0
                    val animator = ViewAnimationUtils.createCircularReveal(
                        view,
                        point.x,
                        point.y,
                        startRadius.toFloat(), endRadius.toFloat()
                    )

                    animator.duration = duration

                    animator.start()
                    animator.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            view.visibility = View.GONE
                            endObserver?.onNext(true)
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

            }
        }
    }
}