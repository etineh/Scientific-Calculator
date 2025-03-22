package ng.com.topstar.topcalculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun setDelay(duration: Long = 200, onComplete : () -> Unit){
    Handler(Looper.getMainLooper()).postDelayed({
        onComplete()
    }, duration)
}

fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun View.toggleRoundHighlight100(defaultColor: Int = 0, duration: Long = 1_000, isDrawable: Boolean? = false) {
    this.setBackgroundDrawableExt(R.drawable.radius_highlight_trans100)

    setDelay (duration) {
        if (isDrawable == false) this.setBackgroundColorExt(defaultColor)
        else this.setBackgroundDrawableExt(defaultColor)
    }
}

fun View.setBackgroundDrawableExt(drawableResId: Int) {
    val drawable = if (drawableResId != 0) {
        ContextCompat.getDrawable(context, drawableResId)
    } else {
        null
    }
    this.background = drawable
}

fun View.setBackgroundColorExt(colorResId: Int) {
    if (colorResId != 0) {
        try {
            val color = ContextCompat.getColor(context, colorResId)
            this.setBackgroundColor(color)
        } catch (e: Resources.NotFoundException) {
            // Log error or silently handle if needed
        }
    } else {
        this.setBackgroundColor(Color.TRANSPARENT) // Default to transparent
    }
}

fun Context.vibrateOnClick() {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) { // Check if device supports vibration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26+ (Android 8.0+)
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // For older APIs
            @Suppress("DEPRECATION")
            vibrator.vibrate(50) // 50ms vibration
        }
    }
}

fun Long.toDateExt(): String {
    val date = Date(this)
    val dayFormat = SimpleDateFormat("d", Locale.US)
    val monthFormat = SimpleDateFormat("MMM", Locale.US)
    val day = dayFormat.format(date).toInt()
    val month = monthFormat.format(date)

    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }

    return "$month $day$suffix"
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Any?.isNullExt(): Boolean {
    return this == null || (this is String && (this.isEmpty() || this == "null" || this == ""))
}

fun ImageView.setImageTintExt(@ColorRes colorResId: Int) {
    val color = ContextCompat.getColor(context, colorResId)
    this.setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun getScreenHeightDp(): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return (displayMetrics.heightPixels / displayMetrics.density).toInt()
}

fun View.slideInFromBottom(duration: Long = 500) {
    this.translationY = this.height.toFloat()
    this.visibility = View.VISIBLE
    ObjectAnimator.ofFloat(this, "translationY", this.height.toFloat(), 0f).apply {
        this.duration = duration
        start()
    }
}

fun View.slideInFromTop(duration: Long = 500) {
    this.translationY = -this.height.toFloat()
    this.visibility = View.VISIBLE
    ObjectAnimator.ofFloat(this, "translationY", -this.height.toFloat(), 0f).apply {
        this.duration = duration
        start()
    }
}

fun View.slideInFromRight(duration: Long = 500) {
    this.translationX = this.width.toFloat()
    this.visibility = View.VISIBLE
    ObjectAnimator.ofFloat(this, "translationX", this.width.toFloat(), 0f).apply {
        this.duration = duration
        start()
    }
}

fun View.slideInFromLeft(duration: Long = 500) {
    this.translationX = -this.width.toFloat()
    this.visibility = View.VISIBLE
    ObjectAnimator.ofFloat(this, "translationX", -this.width.toFloat(), 0f).apply {
        this.duration = duration
        start()
    }
}

fun View.slideOutToLeft(duration: Long = 500) {
    ObjectAnimator.ofFloat(this, "translationX", 0f, -this.width.toFloat()).apply {
        this.duration = duration
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@slideOutToLeft.visibility = View.GONE
            }
        })
        start()
    }
}

fun View.slideOutToRight(duration: Long = 500) {
    ObjectAnimator.ofFloat(this, "translationX", 0f, this.width.toFloat()).apply {
        this.duration = duration
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@slideOutToRight.visibility = View.GONE
            }
        })
        start()
    }
}

fun View.slideOutToBottom(duration: Long = 500) {
    ObjectAnimator.ofFloat(this, "translationY", 0f, this.height.toFloat()).apply {
        this.duration = duration
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@slideOutToBottom.visibility = View.GONE
            }
        })
        start()
    }
}

fun View.slideOutToTop(duration: Long = 500) {
    ObjectAnimator.ofFloat(this, "translationY", 0f, -this.height.toFloat()).apply {
        this.duration = duration
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@slideOutToTop.visibility = View.GONE
            }
        })
        start()
    }
}

fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.copyTextExt(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("label", this)
    clipboard?.setPrimaryClip(clip)
}
fun View.toggleHighlight10(defaultColor: Int = 0, duration: Long = 1_000, isDrawableDefault: Boolean? = false) {
    this.setBackgroundDrawableExt(R.drawable.radius_highlight_trans10)

    setDelay (duration) {
        if (isDrawableDefault == false) this.setBackgroundColorExt(defaultColor)
        else this.setBackgroundDrawableExt(defaultColor)
    }
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

fun Context.setAppearanceMode() {
    val sharedPreferences = this.getSharedPreferences(KVal.THEME_REF, AppCompatActivity.MODE_PRIVATE)
    val isNightModeEnabled = sharedPreferences.getString(KVal.NIGHT_MODE, null)

    if (isNightModeEnabled != null) {
        if (isNightModeEnabled == KVal.DARK){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toastShort("I am dark")
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toastShort("I am light")
        }
    }
}

fun Context.isNightMode(): Boolean {
    val sharedPreferences = getSharedPreferences(KVal.THEME_REF, AppCompatActivity.MODE_PRIVATE)
    val modeStatus = sharedPreferences.getString(KVal.NIGHT_MODE, null)

    return if (modeStatus == null) {
        // Get the default device mode
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    } else {
        // Check if the stored preference indicates dark mode
        modeStatus == KVal.DARK
    }
}