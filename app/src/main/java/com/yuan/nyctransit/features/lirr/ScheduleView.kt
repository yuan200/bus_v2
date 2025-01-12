package com.yuan.nyctransit.features.lirr

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.widget.textChangeEvents
import com.yuan.nyctransit.R
import com.yuan.nyctransit.ui.textView.RoundedBgTextView
import com.yuan.nyctransit.ui.textView.ScheduledTextView
import com.yuan.nyctransit.utils.dpToPx
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ScheduleView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val firstRowTopMargin = 10

    private val rightMargin = 10

    private val composeDisposable = CompositeDisposable()


    var tripHeadSignTV: RoundedBgTextView

    var scheduleTime: ScheduledTextView

    private var realTimeUpdateTV: TextView

    private var minTV: TextView

    var stopTV: TextView

    private var topExtendView: View

    var firstView = false
    set(value) {
        if (value) topExtendView.visibility = View.VISIBLE
        else topExtendView.visibility = View.GONE
    }

    var routeColor: String = "000000"
        set(value) {
            var intColor = Color.parseColor("#$value")
            field = value
            tripHeadSignTV.setTextColor(intColor)
            tripHeadSignTV.setBackgroundColor(intColor)
            setBackgroundColor(intColor)
        }

    var vehicleImageView = ImageView(context).apply {
        id = View.generateViewId()
        setImageResource(R.drawable.ic_train_24px)
        addView(this)
    }

    init {

        setBackgroundColor(ContextCompat.getColor(context, R.color.scheduleViewPrimaryColor))

        topExtendView = View(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(context, 20).toInt())
            visibility = View.GONE
        }
        addView(topExtendView)

        val lineBorder = View(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1)
            setBackgroundColor(Color.BLACK)
        }

        addView(lineBorder)

        id = generateViewId()
        tripHeadSignTV = RoundedBgTextView(context).apply {
            id = generateViewId()
        }
        addView(tripHeadSignTV)

        scheduleTime = ScheduledTextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.WHITE)
        }
        addView(scheduleTime)

        realTimeUpdateTV = TextView(context).apply {
            id = generateViewId()
            setTextColor(Color.WHITE)
            textSize = 20f
        }
        addView(realTimeUpdateTV)

        minTV = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.WHITE)
        }
        addView(minTV)

        stopTV = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.WHITE)
        }
        addView(stopTV)

        val set = ConstraintSet().also {
            it.clone(this)
        }

        //topExtendView
        set.connect(
            topExtendView.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP
        )

        //vehicleImageView
        set.connect(
            vehicleImageView.id,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT,
            10
        )
        set.connect(
            vehicleImageView.id,
            ConstraintSet.TOP,
            topExtendView.id,
            ConstraintSet.BOTTOM,
            firstRowTopMargin
        )

        //tripHeadSignTV
        set.connect(
            tripHeadSignTV.id,
            ConstraintSet.LEFT,
            vehicleImageView.id,
            ConstraintSet.RIGHT,
            10
        )
        set.connect(
            tripHeadSignTV.id,
            ConstraintSet.TOP,
            topExtendView.id,
            ConstraintSet.BOTTOM,
            firstRowTopMargin
        )

        //realTimeUpdateTV
        set.connect(
            realTimeUpdateTV.id,
            ConstraintSet.RIGHT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.RIGHT,
            30
        )
        set.connect(
            realTimeUpdateTV.id,
            ConstraintSet.TOP,
            topExtendView.id,
            ConstraintSet.BOTTOM,
            firstRowTopMargin
        )

        set.connect(scheduleTime.id, ConstraintSet.TOP, tripHeadSignTV.id, ConstraintSet.BOTTOM, 10)
        set.connect(
            scheduleTime.id,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT,
            rightMargin
        )

        set.connect(stopTV.id, ConstraintSet.TOP, scheduleTime.id, ConstraintSet.BOTTOM, 10)
        set.connect(
            stopTV.id,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT,
            rightMargin
        )


        set.connect(minTV.id, ConstraintSet.TOP, realTimeUpdateTV.id, ConstraintSet.BOTTOM)
        set.connect(minTV.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 30)
        set.applyTo(this)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val disposable = realTimeUpdateTV.textChangeEvents()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                Consumer {
                    if (it.text.toString().toInt() <= 1) minTV.text = "minute"
                    else minTV.text = "minutes"
                }
            )

        composeDisposable.add(disposable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        composeDisposable.clear()
    }

    fun setRealTimeMuinutes(minute: String) {
        realTimeUpdateTV.text = minute
        val minuteObserver = Observable.interval(1, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                val minuteNum = minute.toInt() - it
                realTimeUpdateTV.text = minuteNum.toString()
            })
        composeDisposable.add(minuteObserver)
    }

}