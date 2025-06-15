package ng.com.topstar.topcalculator

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import ng.com.topstar.topcalculator.CalculatorUtils.formatWithCommas
import ng.com.topstar.topcalculator.CalculatorUtils.isOperator
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random


object CalculatorUtils {

    fun evaluateExpression(expression: String, mode: String?): String? {
        return try {
            var processedExpression = replaceFunc(expression)

            if (mode == KVal.DEG) processedExpression = onDEGMode(processedExpression)

            println("General log: Processed Expression: $processedExpression")

            val result = ExpressionBuilder(processedExpression)
                .function(degToRadians)
                .function(toDegreesFunc)
                .function(randFunc)
                .function(nCrFunc)
                .function(nPrFunc)
                .function(asinhFunc)
                .function(acoshFunc)
                .function(atanhFunc)
                .function(factorialFunc)
                .function(gcdFunc)
                .function(lcmFunc)
                .function(hypFunc)
                .build()
                .evaluate()

            println("General log: Raw Result: $result")

            val formattedResult = when {
                result % 1 == 0.0 && result <= Long.MAX_VALUE -> result.toLong().toString()
                result > 1e15 || result < -1e15 -> String.format("%.6e", result)
                else -> String.format("%.8f", result).trimEnd('0').trimEnd('.')
            }

            println("General log: Formatted Result: $formattedResult")
            formattedResult
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Place this outside evaluateExpression, e.g., near factorial
    private fun gcd(a: Long, b: Long): Long {
        var x = a
        var y = b
        if (x < 0 || y < 0) return 0L // Invalid input returns 0 (mapped to NaN in lcm)
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }

    // Ensure factorial is defined (if not already in your code)
    private fun factorial(n: Long): Double {
        if (n < 0) return Double.NaN
        var result = 1.0
        for (i in 1..n) result *= i.toDouble()
        return result
    }

    private fun replaceFunc(expression: String): String {
        var processedExpression = expression
            .replace('÷', '/')
            .replace('×', '*')
            .replace(Regex("(?<=\\d),(?=\\d)(?![^()]*\\))"), "") // Only remove commas in standalone numbers, not in log( , )
            .replace(Regex("(\\d+(\\.\\d+)?)%")) { matchResult ->   // for % calculation
                val number = matchResult.groupValues[1]
                "($number/100)"
            }
            .replace(Regex("(acos|asin|atan)\\(degToRadians\\(([^)]+)\\)\\)"), "$1($2)")
            .replace(Regex("\\blog₂\\(([^)]+)\\)?"), "log($1)/log(2)")  // for log₂(x)
            .replace(Regex("\\blog\\(y:\\s*([^,]+)\\s*,\\s*x:\\s*([^)]+)\\)"), "log($1)/log($2)")  // for log(y:, x:)
            .replace(Regex("\\blog\\(([^)]+)\\)?"), "log10($1)")  // for log(10)
            .replace(Regex("\\brand\\(x:\\s*([^,]+)\\s*,\\s*y:\\s*([^)]+)\\)"), "($1 + ($2 - $1) * rand())")  // for rand(x:, y:)
            .replace(Regex("e\\^\\(([^)]+)\\)"), "exp($1)") // for eˣ
            .replace(Regex("³√\\(([^)]+)\\)?"), "($1)^(1/3)") // ³√x
            .replace(Regex("√\\(([^)]+)\\)?"), "sqrt($1)")    // for √x
            .replace(Regex("([⁰¹²³⁴⁵⁶⁷⁸⁹·]+)√(\\d+)")) { matchResult ->    // for ʸ√x
                val exponent = toNormalDigits(matchResult.groupValues[1])
                val base = matchResult.groupValues[2]
                "($base)^(1.0 / $exponent)" // Space around / to ensure clarity
            }
            .replace(Regex("(\\d*\\.?\\d+)²")) { matchResult -> // for x²
                val base = matchResult.groupValues[1]
                "($base)^2"
            }
            .replace(Regex("(\\d*\\.?\\d+)³")) { matchResult -> // for x³
                val base = matchResult.groupValues[1]
                "($base)^3"
            }
            .replace(Regex("ln\\(([^)]+)\\)?"), "log($1)") // ln(x) → log(x) (natural log)
            .replace(Regex("(\\d+)C(\\d+)"), "nCr($1,$2)") // nCr as nCr
            .replace(Regex("(\\d+)P(\\d+)"), "nPr($1,$2)") // nPr as nPr
            .replace(Regex("(\\d*\\.?\\d+)\\s*mod\\s*(\\d*\\.?\\d+)"), "$1 % $2") // n mod m → n % m
            .replace(Regex("(\\d+)!")) { matchResult -> // x!
                val base = matchResult.groupValues[1]
                "factorial($base)"
            }
            .replace(Regex("lcm\\(x:\\s*(\\d*\\.?\\d+)\\s*,\\s*y:\\s*(\\d*\\.?\\d+)\\s*\\)")) { matchResult ->
                "lcm(${matchResult.groupValues[1]},${matchResult.groupValues[2]})" // More flexible spacing
            }
            .replace(Regex("gcd\\(x:\\s*(\\d*\\.?\\d+)\\s*,\\s*y:\\s*(\\d*\\.?\\d+)\\s*\\)")) { matchResult ->
                "gcd(${matchResult.groupValues[1]},${matchResult.groupValues[2]})" // gcd(x: , y: )
            }

        val openBrackets = processedExpression.count { it == '(' }
        val closeBrackets = processedExpression.count { it == ')' }
        if (openBrackets > closeBrackets) {
            processedExpression += ")".repeat(openBrackets - closeBrackets)
        }

        println("General log: Replaced Expression: $processedExpression")

        return processedExpression  // Return the modified expression
    }

    // Convert superscript back to normal digits for calculation
    private fun toNormalDigits(superscript: String): String {
        val superscriptMap = mapOf(
            "\u2070" to "0", "\u00B9" to "1", "\u00B2" to "2", "\u00B3" to "3", "\u2074" to "4",
            "\u2075" to "5", "\u2076" to "6", "\u2077" to "7", "\u2078" to "8", "\u2079" to "9", "·" to "."
        )
        return superscript.map { superscriptMap[it.toString()] ?: it.toString() }.joinToString("")
    }

    private fun onDEGMode(expression: String) : String {
        return expression
            .replace(Regex("\\bsin\\(([^)]+)\\)"), "sin(degToRadians($1))")
            .replace(Regex("\\bcos\\(([^)]+)\\)"), "cos(degToRadians($1))")
            .replace(Regex("\\btan\\(([^)]+)\\)"), "tan(degToRadians($1))")
            .replace(Regex("\\b(acos|asin|atan)\\(([^)]+)\\)"), "toDegrees($1($2))")
    }

    private  val toDegreesFunc = object : Function("toDegrees", 1) {
        override fun apply(vararg args: Double): Double {
            return toDegrees(args[0])
        }
    }
    private val degToRadians = object : Function("degToRadians", 1) {
        override fun apply(vararg args: Double): Double {
            return toRadians(args[0])
        }
    }
    private val randFunc = object : Function("rand", 0) {
        override fun apply(vararg args: Double): Double = Random.nextDouble(0.0, 1.0)
    }

    private val nCrFunc = object : Function("nCr", 2) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toLong()
            val r = args[1].toLong()
            if (n < 0 || r < 0 || r > n) return Double.NaN
            return factorial(n) / (factorial(r) * factorial(n - r))
        }
    }

    private val nPrFunc = object : Function("nPr", 2) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toLong()
            val r = args[1].toLong()
            if (n < 0 || r < 0 || r > n) return Double.NaN
            return factorial(n) / factorial(n - r)
        }
    }

    private val asinhFunc = object : Function("asinh", 1) {
        override fun apply(vararg args: Double): Double {
            val x = args[0]
            return ln(x + sqrt(x * x + 1.0)) // ln(x + √(x² + 1))
        }
    }

    private val acoshFunc = object : Function("acosh", 1) {
        override fun apply(vararg args: Double): Double {
            val x = args[0]
            if (x < 1.0) return Double.NaN // Domain: x ≥ 1
            return ln(x + sqrt(x * x - 1.0)) // ln(x + √(x² - 1))
        }
    }

    private val atanhFunc = object : Function("atanh", 1) {
        override fun apply(vararg args: Double): Double {
            val x = args[0]
            if (x <= -1.0 || x >= 1.0) return Double.NaN // Domain: -1 < x < 1
            return 0.5 * ln((1.0 + x) / (1.0 - x)) // 0.5 * ln((1 + x) / (1 - x))
        }
    }

    private val factorialFunc = object : Function("factorial", 1) {
        override fun apply(vararg args: Double): Double {
            val n = args[0].toLong()
            if (n < 0 || n.toDouble() != args[0]) return Double.NaN // Only non-negative integers
            return factorial(n)
        }
    }

    private val gcdFunc = object : Function("gcd", 2) {
        override fun apply(vararg args: Double): Double {
            val a = args[0]
            val b = args[1]
            if (a <= 0 || b <= 0 || a != a.toLong().toDouble() || b != b.toLong().toDouble()) return Double.NaN // Integer-only
            return gcd(a.toLong(), b.toLong()).toDouble() // Use standalone gcd
        }
    }

    private val lcmFunc = object : Function("lcm", 2) {
        override fun apply(vararg args: Double): Double {
            val x = args[0]
            val y = args[1]
            if (x <= 0 || y <= 0 || x != x.toLong().toDouble() || y != y.toLong().toDouble()) return Double.NaN // Integer-only
            val gcdValue = gcd(x.toLong(), y.toLong()) // Call standalone gcd
            if (gcdValue == 0L) return Double.NaN // Avoid division by zero
            return abs(x * y) / gcdValue // |x * y| / gcd(x,y)
        }
    }

    private val hypFunc = object : Function("hyp", 1) {
        override fun apply(vararg args: Double): Double = cosh(args[0])
    }

    private val listOfFunc = listOf(
        "asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "lcm(", "gcd(",
        "asinh(", "acosh(", "atanh(", "sinh(", "cosh(", "tanh(", "ln(", "mod",
        "log₂(", "log(", "ln(", "sqrt(", "rand(", "e^(", "^(", "³√(", "√(",
    )


    fun Context.insertDigit(value1: String, view: View, color: Int, inputsET: EditText?) {
        view.toggleRoundHighlight100(color, duration = 200, true)

        if (value1.isEmpty()) return // Prevent empty input crashes
        val recentChar = value1.first()
        var newText = value1

        if (shouldPreventStart(inputsET, recentChar)) return

        if (shouldClearText(inputsET, recentChar, this)) inputsET?.setText("")

        inputsET?.let { editText ->
            val cursorPos = editText.selectionStart
            val text = editText.text.toString()

            if (newText == "+/-") {
                togglePlusMinus(editText, this)
                return
            }

            newText = handlePercentageMultiplication(cursorPos, text, recentChar, newText)
            newText = handleConsecutiveOperators(cursorPos, text, recentChar, value1) ?: return

            // Handle special functions with preceding number
            when (newText) {
                "√", "C", "P", "mod", "!" -> { // Add 'mod' here
                    handleSpecialFunction(editText, text, cursorPos, newText)
                    return
                }
            }

            insertAndHighlight(editText, text, newText, cursorPos)
        }
    }

    // Helper to convert digits to superscript
    private fun charToSuperscript(char: Char): String = when (char) {
        '0' -> "\u2070" // ⁰
        '1' -> "\u00B9" // ¹
        '2' -> "\u00B2" // ²
        '3' -> "\u00B3" // ³
        '4' -> "\u2074" // ⁴
        '5' -> "\u2075" // ⁵
        '6' -> "\u2076" // ⁶
        '7' -> "\u2077" // ⁷
        '8' -> "\u2078" // ⁸
        '9' -> "\u2079" // ⁹
        '.' ->  "·"        //·
        else -> char.toString()
    }
    private fun shouldPreventStart(inputsET: EditText?, recentChar: Char): Boolean {
        return inputsET?.text.isNullOrEmpty() && recentChar in listOf('×', '÷', '%', '^', 'P', 'C', '²', '³', '!')
    }

    private fun shouldClearText(inputsET: EditText?, recentChar: Char, context: Context): Boolean {
        return inputsET?.text.toString() == context.getString(R.string.zero)
                && (!recentChar.isOperator() || recentChar == '(')
                && recentChar !in listOf('^', 'P', 'C', '²', '³')
    }

    private fun handlePercentageMultiplication(cursorPos: Int, text: String, recentChar: Char, newText: String): String {
        return if (cursorPos > 0 && text[cursorPos - 1] == '%' && !recentChar.isOperator()) {
            "×$newText"
        } else newText
    }

    private fun handleConsecutiveOperators(cursorPos: Int, text: String, recentChar: Char, value1: String): String? {
        if (cursorPos == 0) return value1

        val prevChar = text[cursorPos - 1]
        val lastTwo = text.takeLast(2)

        if (!recentChar.isOperator() && prevChar == '%') return "×$value1"
        return when {
            prevChar.isOperator() && value1 == "^(" ->  {
                if (prevChar == '.') "0$value1" // add 0 to . -> 5.0^(
                else null // ❌ Prevent `-^( or +^(  ...`
            }
            prevChar == '.' && recentChar == '.' -> null // ❌ Prevent `..`
            prevChar == '%' && recentChar in listOf('×', '÷', '+', '-', '.') -> {
                if (recentChar == '.') "×0." else value1
            }
            prevChar.isOperator() && recentChar == '%' -> null // ❌ Prevent `%%`
            recentChar in listOf('+', '-') && prevChar in listOf('×', '÷') -> value1 // ✅ Allow `5×+2`
            recentChar in listOf('+', '-') && prevChar in listOf('+', '-') -> {
                if (lastTwo in listOf("+-", "-+", "--", "++")) null else value1
            }
            recentChar == '.' && prevChar.isOperator() -> "0." // ✅ Allow `20+ .5`
            recentChar in listOf('×', '÷') && prevChar in listOf('×', '÷') -> null  // ❌ Prevent `×× or x÷`
            prevChar in listOf('+', '-') && recentChar in listOf('×', '÷') -> null  // ❌ Prevent `-× or +÷`
            else -> {
//                println("General log: value is $value1")
                value1
            }
        }
    }

    private fun handleSpecialFunction(editText: EditText, text: String, cursorPos: Int, suffix: String) {
        val beforeCursor = text.substring(0, cursorPos)
        val numberMatch = Regex("\\d*\\.?\\d+$").find(beforeCursor)
        val newText = if (numberMatch != null) {
            val number = numberMatch.value
            when (suffix) {
                "√" -> number.map { charToSuperscript(it) }.joinToString("") + suffix   // help to transcript the number behind the function
                "mod" -> "$number $suffix "
                else -> "$number$suffix" // C, P, !     // help to add only the number behind the function
            }
        } else {
            if (suffix == "mod") "$suffix " else suffix
        }
        val startPos = numberMatch?.range?.first ?: cursorPos
        insertAndHighlight(editText, text.substring(0, startPos), newText, startPos)
        editText.setSelection(startPos + newText.length) // Cursor after suffix
    }

    private fun insertAndHighlight(editText: EditText, text: String, newText: String, cursorPos: Int) {
        editText.context.vibrateOnClick()
        val rawText = text.substring(0, cursorPos) + newText + text.substring(cursorPos)
        val finalText = rawText.replace(Regex("(?<=\\d),(?=\\d)(?!.*[()])"), "")    // remove the previous ,
        val formattedText = finalText.formatWithCommas()
        editText.setText(formattedText)
        LocalStorageUtils.saveInput(editText.context, formattedText)

        val newCursorPos = formattedText.length - (finalText.length - (cursorPos + newText.length))
        editText.setSelection(newCursorPos.coerceIn(0, formattedText.length))
        editText.assignHighlight(editText.context)
    }

    fun String.formatWithCommas(): String {
        val regex = """\b\d+(?:\.\d+)?\b""".toRegex()
        var lastIndex = 0
        val result = StringBuilder()

        regex.findAll(this).forEach { matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last + 1
            val numberStr = matchResult.value

            // Add text before this match
            result.append(this.substring(lastIndex, start))

            // Check if this number is inside log(y: ... ) or rand(x: ... )
            val prefix = this.substring(0, start)
            val suffix = this.substring(end)
            val isInsideLog = prefix.contains(Regex("\\blog\\(y:\\s*[^)]*$")) && suffix.contains(")")
            val isInsideRand = prefix.contains(Regex("\\brand\\(x:\\s*[^)]*$")) && suffix.contains(")")
            val isInsideLcm = prefix.contains(Regex("\\blcm\\(x:\\s*[^)]*$")) && suffix.contains(")")
            val isInsideGcd = prefix.contains(Regex("\\bgcd\\(x:\\s*[^)]*$")) && suffix.contains(")")

            if (isInsideLog || isInsideRand || isInsideLcm || isInsideGcd) {
                result.append(numberStr) // Skip formatting
            } else {
                val decimalIndex = numberStr.indexOf('.')
                if (decimalIndex == -1) {
                    val number = numberStr.toLongOrNull()
                    result.append(
                        number?.let { String.format("%,d", it) } ?: numberStr
                    )
                } else {
                    val intPartStr = numberStr.substring(0, decimalIndex)
                    val decPart = numberStr.substring(decimalIndex)
                    val intPart = intPartStr.toLongOrNull()
                    result.append(
                        intPart?.let { String.format("%,d", it) + decPart } ?: numberStr
                    )
                }
            }

            lastIndex = end
        }

        // Append any remaining text
        result.append(this.substring(lastIndex))
        return result.toString()
    }

    private fun togglePlusMinus(editText: EditText, context: Context) {
        val text = editText.text.toString()
        val lastOperatorIndex = text.lastIndexOfAny(charArrayOf('+', '-', '×', '÷', '('))

        if (lastOperatorIndex == -1) { // No operator found, toggle the entire number
            editText.setText(
                if (text.startsWith("(-")) text.substring(1) else "(-$text"
            )
            editText.setSelection(editText.text.length)
            return
        }

        // Extract the number after the last operator
        val lastDigits = text.substring(lastOperatorIndex + 1)
        val lastSignWithDigits = text.substring(lastOperatorIndex)
        val last2digitBeforeLastSign = text.substring(
            if (lastOperatorIndex > 0) lastOperatorIndex-1 else lastOperatorIndex
        ).takeIf { lastOperatorIndex > 0 } ?: "-"

        val newText = if (lastDigits.isNotEmpty()) {
            val number = if (last2digitBeforeLastSign.contains("(-")) -1 else 1
            buildString {
                append(text.substring(0, lastOperatorIndex + number)) // Keep everything before the number
                if (lastSignWithDigits.startsWith("-") && last2digitBeforeLastSign.contains("(-")) {
                    append(lastSignWithDigits.substring(1)) // Remove the negative sign
                } else {
                    append("(-$lastDigits") // Wrap negative number in brackets
                }
            }

        } else { // Toggle "(-" at the end
            if (text.endsWith("(-")) text.dropLast(2) else "$text(-"
        }

        editText.setText(newText)
        editText.setSelection(newText.length)
        editText.assignHighlight(context) // ✅ Apply operator highlighting
    }


    private fun EditText.assignHighlight(context: Context) {
        val text = this.text.toString()
        val spannable = SpannableString(text)

        // Highlight operators and x:/y: separately
        highlightOperators(spannable, context)
        highlightXYLabels(spannable, context)

        val cursorPos = this.selectionStart
        this.setText(spannable)
        this.setSelection(cursorPos.coerceIn(0, text.length))
    }

    private fun highlightOperators(spannable: SpannableString, context: Context) {
        val text = spannable.toString()
        val blueColor = ContextCompat.getColor(context, R.color.appBlue)

        text.forEachIndexed { index, char ->
            if (char.isOperator()) {
                spannable.setSpan(
                    ForegroundColorSpan(blueColor),
                    index, index + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun highlightXYLabels(spannable: SpannableString, context: Context) {
        val text = spannable.toString()
        val greenColor = ContextCompat.getColor(context, R.color.greenColor)

        val pattern = Regex("\\b(?:log|rand|lcm|gcd)\\([^)]*\\)")
        pattern.findAll(text).forEach { match ->
            val insideText = match.value
            val startOffset = match.range.first

            val xPattern = Regex("x:\\s*")
            val yPattern = Regex("y:\\s*")
            xPattern.findAll(insideText).forEach { xMatch ->
                val start = startOffset + xMatch.range.first
                val end = startOffset + xMatch.range.last + 1
                spannable.setSpan(
                    ForegroundColorSpan(greenColor),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            yPattern.findAll(insideText).forEach { yMatch ->
                val start = startOffset + yMatch.range.first
                val end = startOffset + yMatch.range.last + 1
                spannable.setSpan(
                    ForegroundColorSpan(greenColor),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    // Helper function to check if a character is an operator
    fun Char.isOperator(): Boolean {
        return this in listOf('+', '-', '×', '÷', '/', '*', '%', '.', '(', ')')
    }

    fun EditText.eraseDigit(context: Context) {
        val text = this.text.toString()
        val cursorPos = this.selectionStart

        if (text.isNotEmpty() && cursorPos > 0) {
            var newText = text
            var newCursorPos = cursorPos - 1

            // Try erasing a function log(|)
            newText = eraseFunction(text, cursorPos)?.also { newCursorPos = it.first }?.second ?: newText

            // If no function, handle colon or single char erase rand(x: , y:| )
            if (newText == text) {
                val result = eraseColonOrChar(text, cursorPos)
                newText = result.second
                newCursorPos = result.first
            }

            // Format and set the result
            applyFormattingAndSetText(newText, newCursorPos, context)
        }
    }

    private fun eraseFunction(text: String, cursorPos: Int): Pair<Int, String>? {
        for (func in listOfFunc) {
            if (cursorPos >= func.length && text.substring(cursorPos - func.length, cursorPos) == func) {
                return if (cursorPos < text.length && text[cursorPos] == ')') {
                    Pair(cursorPos - func.length, text.removeRange(cursorPos - func.length, cursorPos + 1))
                } else {
                    Pair(cursorPos - func.length, text.removeRange(cursorPos - func.length, cursorPos))
                }
            }
        }
        return null
    }

    private fun eraseColonOrChar(text: String, cursorPos: Int): Pair<Int, String> {
        // wipe out the entire log(y: , x: ) (same for rand(x: , y: )), if any ':' is erase
        val insideLogOrRand = Regex("\\b(?:log|rand|lcm|gcd)\\([^)]*\\)").containsMatchIn(text)
        var newCursorPos = cursorPos - 1
        var newText = text

        if (insideLogOrRand && cursorPos > 0 && text[cursorPos - 1] == ':') {
            val match = Regex("\\b(?:log|rand|lcm|gcd)\\([^)]*\\)").find(text)?.range
            if (match != null && cursorPos > match.first && cursorPos <= match.last + 1) {
                newText = text.removeRange(match.first, match.last + 1)
                newCursorPos = match.first
            }
        } else if (!insideLogOrRand || text[cursorPos - 1] != ',') {
            newText = text.removeRange(cursorPos - 1, cursorPos)
        }

        return Pair(newCursorPos, newText)
    }

    private fun EditText.applyFormattingAndSetText(newText: String, newCursorPos: Int, context: Context) {
        val reverseText = newText.replace(Regex("(?<=\\d),(?=\\d)(?![^()]*\\))"), "")
        val formattedText = reverseText.formatWithCommas()
        this.setText(formattedText)
        LocalStorageUtils.saveInput(context, formattedText)

        val delta = formattedText.length - reverseText.length
        val adjustedCursorPos = newCursorPos + delta
        this.setSelection(adjustedCursorPos.coerceIn(0, formattedText.length))

        assignHighlight(context)
    }

    private var eraseHandler: Handler? = null
    private val eraseRunnable = object : Runnable {
        private var editText: EditText? = null
        private var context: Context? = null

        fun setTarget(editText: EditText, context: Context) {
            this.editText = editText
            this.context = context
        }

        override fun run() {
            editText?.eraseDigit(context!!) // Call erase function
            eraseHandler?.postDelayed(this, 200) // Repeat every 100ms
        }
    }

    fun startErasing(editText: EditText, context: Context) {
        if (eraseHandler == null) {
            eraseHandler = Handler(Looper.getMainLooper())
        }
        eraseRunnable.setTarget(editText, context)
        eraseHandler?.post(eraseRunnable)
    }

    fun stopErasing() {
        eraseHandler?.removeCallbacks(eraseRunnable)
    }

}

fun String.proceedToCalculator(): Boolean {
    return isNotEmpty()
            && !this.contains("log(,)")
            && (!this.matches(Regex("log\\(y:\\s*\\d*\\s*,\\s*x:\\s*\\d*\\)"))
            || this.matches(Regex("log\\(y:\\s*\\d+\\s*,\\s*x:\\s*\\d+\\)")))  // Valid log case
            && (!this.matches(Regex("rand\\(x:\\s*\\d*\\s*,\\s*y:\\s*\\d*\\)"))
            || this.matches(Regex("rand\\(x:\\s*\\d+\\s*,\\s*y:\\s*\\d+\\)")))  // Valid rand case
            && (!this.matches(Regex("lcm\\(x:\\s*\\d*\\s*,\\s*y:\\s*\\d*\\)"))
            || this.matches(Regex("lcm\\(x:\\s*\\d+\\s*,\\s*y:\\s*\\d+\\)")))  // Valid LCM case
            && (!this.matches(Regex("gcd\\(x:\\s*\\d*\\s*,\\s*y:\\s*\\d*\\)"))
            || this.matches(Regex("gcd\\(x:\\s*\\d+\\s*,\\s*y:\\s*\\d+\\)")))  // Valid GCD case
            && (!last().isOperator() || last() == '%' || last() == ')')
            && (last() != '√' && last() != 'P' && last() != 'C')
}



//fun String.proceedToCalculator() : Boolean {
//    return isNotEmpty() && !this.contains("log(,)")
//            && ( !this.contains("log(y:${1..10} , x: )") && !this.contains("log(y:, x:)") && !this.contains("log(y:, x: )"))
//            && ( !this.contains("rand(x:${1..10} , y: )") && !this.contains("rand(x:, y:)") && !this.contains("rand(x:, y: )"))
//            && !(this.matches(Regex("lcm\\(x:\\d+\\s*,\\s*y:\\s*\\)"))
//            && ( !this.contains("gcd(x:${1..10} , y: )") && !this.contains("gcd(x:, y:)") && !this.contains("gcd(x:, y: )"))
//            && (!last().isOperator() || last() == '%' || last() == ')')
//            && (last() != '√' && last() != 'P' && last() != 'C'))
//
//}

fun Double.roundToWithComma(maxDecimals: Int): String {
    return "%.${maxDecimals}f".format(this).trimEnd('0').trimEnd('.').formatWithCommas()
}

val String.removeComma: String
    get() = this.replace(",", "")

