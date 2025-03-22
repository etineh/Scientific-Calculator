package ng.com.topstar.topcalculator

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewStub
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ng.com.topstar.topcalculator.CalculatorUtils.formatWithCommas
import ng.com.topstar.topcalculator.CalculatorUtils.insertDigit

class MainActivity : ComponentActivity() {

    private var digitView : View? = null

    private lateinit var headingInclude : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
//        setAppearanceMode()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headingInclude = findViewById(R.id.headingInclude)

        setDelay{
            if (isPortrait()) {
                digitViewStub(R.id.digitViewStub)
            } else {
                digitViewStub(R.id.landscapeViewStub)
                headingInclude.gone()
            }
        }

//        setupThemeSwitch()
    }

    //  ==========  method

//    private fun setupThemeSwitch() {
//        val switch = findViewById<Switch>(R.id.switch1)
//
//        // Set switch to correct state
//        switch.isChecked = isNightMode()
//
//        switch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                LocalStorageUtils.saveDarkMode(this)
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                toastShort("Switched to dark mode")
//            } else {
//                LocalStorageUtils.saveLightMode(this)
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                toastShort("Switched to light mode")
//            }
//            recreate() // Recreate activity to apply the new theme
//        }
//    }


    private var equalTo : TextView? = null
    private var totalAns : TextView? = null
    private var inputsET : EditText? = null
    private var mode : String = KVal.DEG
    private lateinit var recyclerHistory : RecyclerView
    private lateinit var clearAllTV : TextView
    private lateinit var historyIV: ImageView

    private val historyAdapter by lazy {
        CalculatorHistoryAdapter(historyMutList) {
            inputsET?.setText(it.input)
            totalAns?.text = it.totalAns
            inputsET?.setSelection(inputsET?.length()?.minus(0) ?: 0)
            toggleRecyclerHistory()
        }
    }
    private var historyMutList: MutableList<CalculatorHistoryM> = mutableListOf()

    private fun digitViewStub(int: Int) {
        if (digitView != null) {
            digitView?.visible()
            return
        }

        val vs = findViewById<ViewStub>(int)
        digitView = vs.inflate()

        digitView?.let {
            // science cal 1
//            val backArrowIV = findViewById<ImageView>(R.id.backArrowIV_)
            val scienceLinear1 = findViewById<LinearLayout>(R.id.scienceLinear1)
            val second1TV = findViewById<TextView>(R.id.second1TV)
            val bracketOpen = findViewById<TextView>(R.id.bracketOpen)
            val bracketClose = findViewById<TextView>(R.id.bracketClose)
            val sinTV = findViewById<TextView>(R.id.sinTV)
            val cosTV = findViewById<TextView>(R.id.cosTV)
            val tanTV = findViewById<TextView>(R.id.tanTV)
            val log10TV = findViewById<TextView>(R.id.log1TV)
            val logyTV = findViewById<TextView>(R.id.logyTV)
            val log2TV = findViewById<TextView>(R.id.log2TV)
            val sinh = findViewById<TextView>(R.id.sinh)
            val cosh = findViewById<TextView>(R.id.cosh)
            val tanh = findViewById<TextView>(R.id.tanh)
            val rand = findViewById<TextView>(R.id.rand)
            val xPowY = findViewById<TextView>(R.id.xPowY)
            val ePowX = findViewById<TextView>(R.id.ePowX)
            val oneOverX = findViewById<TextView>(R.id.oneOverX)
            val sqRoot = findViewById<TextView>(R.id.sqr)
            val cubeRoot = findViewById<TextView>(R.id.cubeRoot)
            val yRootX = findViewById<TextView>(R.id.yRootX)
            val square = findViewById<TextView>(R.id.square)
            val cube = findViewById<TextView>(R.id.cube)
            val ln = findViewById<TextView>(R.id.ln)
            val pi = findViewById<TextView>(R.id.pi)
            val rad1 = findViewById<TextView>(R.id.rad1)

            // science cal 2
            val scienceLinear2 = findViewById<LinearLayout>(R.id.scienceLinear2)
            val second2TV = findViewById<TextView>(R.id.secondTV)
            val asin = findViewById<TextView>(R.id.asin)
            val acos = findViewById<TextView>(R.id.acos)
            val atan = findViewById<TextView>(R.id.atan)
            val nCr = findViewById<TextView>(R.id.nCr)
            val abs = findViewById<TextView>(R.id.abs)
            val asinh = findViewById<TextView>(R.id.asinh)
            val acosh = findViewById<TextView>(R.id.acosh)
            val atanh = findViewById<TextView>(R.id.atanh)
            val xFract = findViewById<TextView>(R.id.xFractorial)
            val mod = findViewById<TextView>(R.id.mod)
            val twoPowXTV = findViewById<TextView>(R.id.two_pow_xTV)
            val tenPowXTV = findViewById<TextView>(R.id.ten_pow_x)
            val lcmTV = findViewById<TextView>(R.id.lcm)
            val gcdTV = findViewById<TextView>(R.id.gcd)
            val nPr = findViewById<TextView>(R.id.permutationTV)
            val bracketOpen2 = findViewById<TextView>(R.id.bracketOpen2)
            val bracketClose2 = findViewById<TextView>(R.id.bracketClose2)
            val eTV = findViewById<TextView>(R.id.eTV)
            val rad2 = findViewById<TextView>(R.id.rad2)

            // numbers
            val plusMinusIV = findViewById<ImageView>(R.id.plus_minusIV)
            val oneTV = findViewById<TextView>(R.id.tv1)
            val twoTV = findViewById<TextView>(R.id.tv2)
            val threeTV = findViewById<TextView>(R.id.tv3)
            val fourTV = findViewById<TextView>(R.id.tv4)
            val fiveTV = findViewById<TextView>(R.id.tv5)
            val sixTV = findViewById<TextView>(R.id.tv6)
            val sevenTV = findViewById<TextView>(R.id.tv7)
            val eightTV = findViewById<TextView>(R.id.tv8)
            val nineTV = findViewById<TextView>(R.id.tv9)
            val zeroTV = findViewById<TextView>(R.id.tv0)
            val divisionTV = findViewById<TextView>(R.id.divisionTV)
            val multipleTV = findViewById<TextView>(R.id.multipleTV)
            val minusTV = findViewById<TextView>(R.id.minusTV)
            val additionTV = findViewById<TextView>(R.id.additionTV)
            val percent = findViewById<TextView>(R.id.tvPercent)
            val reset = findViewById<TextView>(R.id.tvReset)
            val dotTV = findViewById<TextView>(R.id.tvDot)
            equalTo = findViewById(R.id.equalToTV)
            totalAns = findViewById(R.id.totalAns)
            inputsET = findViewById(R.id.inputs)

            // option buttons - currency, unit convert, history
            val optionButtons = findViewById<LinearLayout>(R.id.optionButtons)
            val scientificIV = findViewById<ImageView>(R.id.scientificIV)
            val eraseIV = findViewById<ImageView>(R.id.eraseIV_)
//            val convertIV = findViewById<ImageView>(R.id.convertIV)
//            val currencyConvertIV = findViewById<ImageView>(R.id.currencyIV)
            historyIV = findViewById(R.id.historyIV)

            recyclerHistory = findViewById(R.id.recyclerHistory)
            clearAllTV = findViewById(R.id.clearAllTV)
            recyclerHistory.layoutManager = LinearLayoutManager(this)

//            convertIV.setOnClickListener {
//                toastShort("in progress")
//            }
//            currencyConvertIV.setOnClickListener {
//                toastShort("in progress")
//            }

            // set history recyclerView
            historyMutList = LocalStorageUtils.getCalculatorHistory(this)
            recyclerHistory.adapter = historyAdapter
            historyIV.setOnClickListener {  toggleRecyclerHistory() }

            clearAllTV.setOnClickListener {
                historyAdapter.notifyDataSetChanged()
                LocalStorageUtils.clearCalculatorHistory(this)
                toggleRecyclerHistory()
            }

            inputsET?.setText(LocalStorageUtils.getInput(this))
            if (inputsET?.isNullExt() == false) calculateInputs(inputsET?.text.toString())

            if (isPortrait()) {
                if (getScreenHeightDp() < 750) {
                    scienceLinear1.gone()
                    scientificIV.setImageTintExt(R.color.iconColor)
                }
                if (getScreenHeightDp() < 550) optionButtons.gone()

                scientificIV.setOnClickListener {
                    vibrateOnClick()
                    if (getScreenHeightDp() > 750) {
                        if (scienceLinear1.visibility == View.VISIBLE || scienceLinear2.visibility == View.VISIBLE){
                            scienceLinear1.slideOutToLeft(300)
                            scienceLinear2.slideOutToLeft(300)
                            scientificIV.setImageTintExt(R.color.iconColor)
                            return@setOnClickListener
                        }
                        scienceLinear1.slideInFromRight(300)
                        scienceLinear2.slideOutToLeft(300)
                        scientificIV.setImageTintExt(R.color.greenColor)
                    } else {
                        // turn to landscape
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

            } else {
//                backArrowIV.setOnClickListener {v ->
//                    v.rotateView360Ext()
//                    onBackPressedDispatcher.onBackPressed()
//                }
                scientificIV.setOnClickListener {
                    vibrateOnClick()
                    if (scienceLinear1.visibility == View.VISIBLE || scienceLinear2.visibility == View.VISIBLE){
                        scienceLinear1.gone()
                        scienceLinear2.gone()
                        scientificIV.setImageTintExt(R.color.iconColor)
                        return@setOnClickListener
                    }
                    scienceLinear1.visible()
                    scienceLinear2.gone()
                    scientificIV.setImageTintExt(R.color.greenColor)
                }
            }

            second1TV.setOnClickListener {
                vibrateOnClick()
                if(isPortrait()) {
                    scienceLinear1.slideOutToLeft(300)
                    scienceLinear2.slideInFromRight(300)
                } else {
                    scienceLinear1.gone()
                    scienceLinear2.visible()
                }
            }

            second2TV.setOnClickListener {
                vibrateOnClick()
                if(isPortrait()) {
                    scienceLinear2.slideOutToLeft(300)
                    scienceLinear1.slideInFromRight(300)
                } else {
                    scienceLinear2.gone()
                    scienceLinear1.visible()
                }
            }

            eraseIV.setOnTouchListener { _, event ->
                eraseDigitOnTouch(event)
            }

            reset.setOnClickListener {
                vibrateOnClick()
                LocalStorageUtils.clearInput(this)
                inputsET?.setText("")
                totalAns?.text = ""
            }

            oneTV.setOnClickListener { insertDigit("1", it, R.drawable.radius_card_color100, inputsET) }
            twoTV.setOnClickListener { insertDigit("2", it, R.drawable.radius_card_color100, inputsET) }
            threeTV.setOnClickListener { insertDigit("3", it, R.drawable.radius_card_color100, inputsET) }
            fourTV.setOnClickListener { insertDigit("4", it, R.drawable.radius_card_color100, inputsET) }
            fiveTV.setOnClickListener { insertDigit("5", it, R.drawable.radius_card_color100, inputsET) }
            sixTV.setOnClickListener { insertDigit("6", it, R.drawable.radius_card_color100, inputsET) }
            sevenTV.setOnClickListener { insertDigit("7", it, R.drawable.radius_card_color100, inputsET) }
            eightTV.setOnClickListener { insertDigit("8", it, R.drawable.radius_card_color100, inputsET) }
            nineTV.setOnClickListener { insertDigit("9", it, R.drawable.radius_card_color100, inputsET) }
            zeroTV.setOnClickListener { insertDigit("0", it, R.drawable.radius_card_color100, inputsET) }
            dotTV.setOnClickListener { insertDigit(".", it, R.drawable.radius_card_color100, inputsET) }
            additionTV.setOnClickListener { insertDigit("+", it, R.drawable.round_radius_blue100, inputsET) }
            minusTV.setOnClickListener { insertDigit("-", it, R.drawable.round_radius_blue100, inputsET) }
            multipleTV.setOnClickListener { insertDigit("×", it, R.drawable.round_radius_blue100, inputsET) }
            divisionTV.setOnClickListener { insertDigit("÷", it, R.drawable.round_radius_blue100, inputsET) }
            percent.setOnClickListener { insertDigit("%", it, R.drawable.round_radius_blue100, inputsET) }
            plusMinusIV.setOnClickListener { insertDigit("+/-", it, R.drawable.radius_card_color100, inputsET) }

            // science cal 1
            bracketOpen.setOnClickListener { insertDigit("(", it, R.drawable.radius_black_n_card15, inputsET) }
            bracketClose.setOnClickListener { insertDigit(")", it, R.drawable.radius_black_n_card15, inputsET) }
            sinTV.setOnClickListener { insertDigit("sin(", it, R.drawable.radius_black_n_card15, inputsET) }
            cosTV.setOnClickListener { insertDigit("cos(", it, R.drawable.radius_black_n_card15, inputsET) }
            tanTV.setOnClickListener { insertDigit("tan(", it, R.drawable.radius_black_n_card15, inputsET) }
            log10TV.setOnClickListener { insertDigit("log(", it, R.drawable.radius_black_n_card15, inputsET) }
            logyTV.setOnClickListener {
                insertDigit("log(y: , x: )", it, R.drawable.radius_black_n_card15, inputsET)
                inputsET?.setSelection(inputsET?.selectionStart?.minus(7) ?: 0) // After "y: "
            }
            log2TV.setOnClickListener { insertDigit("log₂(", it, R.drawable.radius_black_n_card15, inputsET) }
            sinh.setOnClickListener { insertDigit("sinh(", it, R.drawable.radius_black_n_card15, inputsET) }
            cosh.setOnClickListener { insertDigit("cosh(", it, R.drawable.radius_black_n_card15, inputsET) }
            tanh.setOnClickListener { insertDigit("tanh(", it, R.drawable.radius_black_n_card15, inputsET) }
            rand.setOnClickListener {
                insertDigit("rand(x: , y: )", it, R.drawable.radius_black_n_card15, inputsET)
                inputsET?.setSelection(inputsET?.selectionStart?.minus(7) ?: 0) // Cursor after "x: "
            }
            xPowY.setOnClickListener { insertDigit("^(", it, R.drawable.radius_black_n_card15, inputsET) }
            ePowX.setOnClickListener { insertDigit("e^(", it, R.drawable.radius_black_n_card15, inputsET) }
            oneOverX.setOnClickListener { insertDigit("(1÷", it, R.drawable.radius_black_n_card15, inputsET) }
            sqRoot.setOnClickListener { insertDigit("√(", it, R.drawable.radius_black_n_card15, inputsET) }
            cubeRoot.setOnClickListener { insertDigit("³√(", it, R.drawable.radius_black_n_card15, inputsET) }
            yRootX.setOnClickListener { insertDigit("√", it, R.drawable.radius_black_n_card15, inputsET) }
            square.setOnClickListener { insertDigit("²", it, R.drawable.radius_black_n_card15, inputsET) }
            cube.setOnClickListener { insertDigit("³", it, R.drawable.radius_black_n_card15, inputsET) }
            ln.setOnClickListener { insertDigit("ln(", it, R.drawable.radius_black_n_card15, inputsET) }
            pi.setOnClickListener { insertDigit("π", it, R.drawable.radius_black_n_card15, inputsET) }
            rad1.setOnClickListener { view -> toggleRadAndDeg(rad1, rad2, view, inputsET) }

            // science cal 2
            asin.setOnClickListener { insertDigit("asin(", it, R.drawable.radius_black_n_card15, inputsET) }
            acos.setOnClickListener { insertDigit("acos(", it, R.drawable.radius_black_n_card15, inputsET) }
            atan.setOnClickListener { insertDigit("atan(", it, R.drawable.radius_black_n_card15, inputsET) }
            asinh.setOnClickListener { insertDigit("asinh(", it, R.drawable.radius_black_n_card15, inputsET) }
            acosh.setOnClickListener { insertDigit("acosh(", it, R.drawable.radius_black_n_card15, inputsET) }
            atanh.setOnClickListener { insertDigit("atanh(", it, R.drawable.radius_black_n_card15, inputsET) }
            nCr.setOnClickListener { insertDigit("C", it, R.drawable.radius_black_n_card15, inputsET) }
            abs.setOnClickListener { insertDigit("abs(", it, R.drawable.radius_black_n_card15, inputsET) }
            xFract.setOnClickListener { insertDigit("!", it, R.drawable.radius_black_n_card15, inputsET) }
            mod.setOnClickListener { insertDigit("mod", it, R.drawable.radius_black_n_card15, inputsET) }
            nPr.setOnClickListener { insertDigit("P", it, R.drawable.radius_black_n_card15, inputsET) }
            eTV.setOnClickListener { insertDigit("e", it, R.drawable.radius_black_n_card15, inputsET) }
            tenPowXTV.setOnClickListener { insertDigit("10^(", it, R.drawable.radius_black_n_card15, inputsET) }
            twoPowXTV.setOnClickListener { insertDigit("2^(", it, R.drawable.radius_black_n_card15, inputsET) }
            lcmTV.setOnClickListener {
                insertDigit("lcm(x: , y: )", it, R.drawable.radius_black_n_card15, inputsET)
                inputsET?.setSelection(inputsET?.selectionStart?.minus(7) ?: 0) //
            }
            gcdTV.setOnClickListener {
                insertDigit("gcd(x: , y: )", it, R.drawable.radius_black_n_card15, inputsET)
                inputsET?.setSelection(inputsET?.selectionStart?.minus(7) ?: 0) // After "x: "
            }
            bracketOpen2.setOnClickListener { insertDigit("(", it, R.drawable.radius_black_n_card15, inputsET) }
            bracketClose2.setOnClickListener { insertDigit(")", it, R.drawable.radius_black_n_card15, inputsET) }
            rad2.setOnClickListener { view -> toggleRadAndDeg(rad1, rad2, view, inputsET) }

            equalTo?.setOnClickListener { view->
                view.toggleRoundHighlight100(R.drawable.round_radius_blue100, duration = 200, true)
                val getTotal = totalAns?.text.toString()
                if(!getTotal.isNullExt() && getTotal != getString(R.string.error) && getTotal != "NaN") {
                    vibrateOnClick()
                    val input = inputsET?.text.toString()
                    val historyM = CalculatorHistoryM( input, getTotal, System.currentTimeMillis() )
                    historyAdapter.updateHistory(historyM)
                    LocalStorageUtils.saveCalculatorHistory(this, historyM)
                    inputsET?.setText(getTotal)
                    totalAns?.text = ""
                    inputsET?.setSelection(inputsET?.text?.length!!)
                } else if (getTotal == getString(R.string.error)) toastShort(getString(R.string.input_error))
            }

            totalAns?.setOnLongClickListener {
                it.toggleHighlight10()
                totalAns?.text.toString().copyTextExt(this)
                true
            }

            inputsET?.showSoftInputOnFocus = false
            inputsET?.requestFocus()

            inputsET?.addTextChangedListener { expression -> calculateInputs(expression.toString()) }
        }
    }


    private fun eraseDigitOnTouch(event: MotionEvent): Boolean { // Add `Boolean` return type
//        vibrateOnClick()
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                CalculatorUtils.startErasing(inputsET!!, this)
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                CalculatorUtils.stopErasing()
                true
            }
            else -> false
        }
    }

    private fun toggleRecyclerHistory() {
        recyclerHistory.toggleVisibility()
        clearAllTV.toggleVisibility()
        recyclerHistory.scrollToPosition(historyMutList.size - 1)
        if (recyclerHistory.visibility == View.VISIBLE) historyIV.setImageTintExt(R.color.greenColor)
        else historyIV.setImageTintExt(R.color.iconColor)
    }

    private fun toggleRadAndDeg(rad1TV: TextView, rad2TV: TextView, view: View, inputsET1: EditText?) {
        vibrateOnClick()
        view.toggleHighlight10(R.drawable.round_radius_blue10, 100, true)
        if (mode == KVal.DEG) {
            mode = KVal.RAD
            rad1TV.text = getString(R.string.deg)
            rad2TV.text = getString(R.string.deg)
        } else if (mode == KVal.RAD) {
            mode = KVal.DEG
            rad1TV.text = getString(R.string.rad)
            rad2TV.text = getString(R.string.rad)
        }
        // refresh the inputsET to reload ans
        val expression = inputsET1?.text.toString()
        calculateInputs(expression)
    }

    private fun calculateInputs(expression: String) {
        if (expression.proceedToCalculator()) {
            val result = CalculatorUtils.evaluateExpression(expression, mode)
            totalAns?.text = result?.formatWithCommas() ?: getString(R.string.error)
        } else {
            totalAns?.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        setDelay { inputsET?.hideKeyboard() }
    }
}
