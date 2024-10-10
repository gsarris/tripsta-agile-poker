package com.sarris.poker.card

import android.animation.Animator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.sarris.models.Card
import com.sarris.poker.R
import io.codetail.animation.ViewAnimationUtils
import java.util.Random
import javax.inject.Inject
import kotlin.math.hypot
import kotlin.math.sqrt

class CardActivity : AppCompatActivity(), CardContract.View,
    NavigationView.OnNavigationItemSelectedListener,
    SensorEventListener {
    @JvmField
    @BindView(R.id.nav_view)
    var navigationView: NavigationView? = null

    @JvmField
    @BindView(R.id.visibleView)
    var visibleView: RelativeLayout? = null

    @JvmField
    @BindView(R.id.linearView)
    var revealView: LinearLayout? = null

    @JvmField
    @BindView(R.id.layoutButtons)
    var layoutButtons: LinearLayout? = null

    @JvmField
    @BindView(R.id.cardList)
    var cardList: RecyclerView? = null

    @JvmField
    @BindView(R.id.textToShow)
    var textToShow: TextView? = null

    @JvmField
    @BindView(R.id.imageToShow)
    var imageToShow: ImageView? = null
    var flag: Boolean = true
    var x: Int = 0
    var y: Int = 0
    var hypotenuse: Int = 0

    @JvmField
    @Inject
    var cardPresenter: CardPresenter? = null
    private val appCompatActivity: AppCompatActivity = this
    private var alphaAnimation: Animation? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        initializeListeners()
        DaggerCardComponent.builder().cardPresenterModule(CardPresenterModule(this)).build().inject(
            this
        )
        cardPresenter!!.start()
        val bundle = Bundle()
        bundle.putString("Screen", this.localClassName)
    }

    private fun initializeListeners() {
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        navigationView!!.setNavigationItemSelectedListener(this)
    }

    override fun initializeGrid(cards: List<Card>) {
        gridLayoutManager = GridLayoutManager(this, SPAN_COUNT)
        cardList!!.layoutManager = gridLayoutManager
        val cardListAdapter = CardListAdapter(this, cards)
        cardList!!.adapter = cardListAdapter
        cardList!!.addItemDecoration(GridDecoration(SPAN_COUNT, 0, false))
    }

    override fun animateCard(tag: Any?) {
        val view = visibleView!!.findViewWithTag<View>(tag)
        try {
            if (flag) {
                animateOpen(view)
            } else {
                animateClose()
            }
            flag = !flag
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(this)
        super.onPause()
    }

    fun animate(view: View) {
        cardPresenter!!.showCard(view.tag)
    }

    private fun animateOpen(view: View) {
        val originalPos = IntArray(2)
        view.getLocationInWindow(originalPos)
        x = originalPos[0] + 100
        y = originalPos[1]
        hypotenuse = hypot(visibleView!!.width.toDouble(), visibleView!!.height.toDouble()).toInt()

        val parameters = revealView!!.layoutParams as FrameLayout.LayoutParams
        parameters.height = visibleView!!.height
        revealView!!.layoutParams = parameters

        val anim =
            ViewAnimationUtils.createCircularReveal(revealView, x, y, 0f, hypotenuse.toFloat())
        anim.setDuration(OPEN_ANIMATION_DURATION.toLong())
        val statusBarColor: Int
        val background: Drawable?
        when (view.id) {
            R.id.skull, R.id.coffee -> {
                textToShow!!.visibility = View.GONE
                imageToShow!!.visibility = View.VISIBLE
                imageToShow!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        if (view.id == R.id.coffee) R.drawable.big_coffee else R.drawable.big_skull
                    )
                )
                background = ContextCompat.getDrawable(this, R.color.color_323232)
                statusBarColor = ContextCompat.getColor(applicationContext, R.color.color_323232)
            }

            else -> {
                imageToShow!!.visibility = View.GONE
                textToShow!!.visibility = View.VISIBLE
                val textView = view as TextView
                textToShow!!.text = textView.text
                background = textView.background
                statusBarColor = (textView.background as ColorDrawable).color
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            revealView!!.background = background
        } else {
            revealView!!.setBackgroundDrawable(background)
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                val window = appCompatActivity.window
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = statusBarColor
                }
                layoutButtons!!.visibility = View.VISIBLE
                layoutButtons!!.startAnimation(alphaAnimation)
            }

            override fun onAnimationEnd(animator: Animator) {
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        revealView!!.visibility = View.VISIBLE
        anim.start()
    }

    private fun animateClose() {
        val anim =
            ViewAnimationUtils.createCircularReveal(revealView, x, y, hypotenuse.toFloat(), 0f)
        anim.setDuration(CLOSE_ANIMATION_DURATION.toLong())

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window = appCompatActivity.window
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor =
                        ContextCompat.getColor(appCompatActivity, R.color.colorPrimary)
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                revealView!!.visibility = View.GONE
                layoutButtons!!.visibility = View.GONE
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })

        anim.start()
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (!flag) {
                animateClose()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_about) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSensorChanged(se: SensorEvent) {
        val x = se.values[0]
        val y = se.values[1]
        val z = se.values[2]
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt(x * x + y * y + z * z)
        val delta = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta
        if (acceleration > VIBRATION_TRIGGER) {
            acceleration = 0f
            val ran = Random()
            val randomNum = ran.nextInt(NO_OF_ROWS)
            when (randomNum) {
                16 -> animate(findViewById(R.id.coffee))
                17 -> animate(findViewById(R.id.skull))
                else -> {
                    val child = cardList!!.getChildAt(randomNum)
                    if (child != null) {
                        animate(child.findViewById(R.id.textView))
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    companion object {
        const val SPAN_COUNT: Int = 4
        const val OPEN_ANIMATION_DURATION: Int = 500
        const val CLOSE_ANIMATION_DURATION: Int = 400
        const val VIBRATION_TRIGGER: Int = 25
        const val NO_OF_ROWS: Int = 18
    }
}
