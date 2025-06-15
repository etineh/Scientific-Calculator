package ng.com.topstar.topcalculator.utilities

import ng.com.topstar.topcalculator.CalculatorUtils
import ng.com.topstar.topcalculator.CalculatorUtils.formatWithCommas
import ng.com.topstar.topcalculator.CalculatorUtils.isOperator
import ng.com.topstar.topcalculator.KVal
import ng.com.topstar.topcalculator.removeComma
import org.junit.Assert.*

import org.junit.Test

class CalculatorUtilsTest {

    @Test
    fun evaluateSimpleAddition() {
        val expression = "2+3"
        val mode = KVal.DEG // "DEG"
        val result = CalculatorUtils.evaluateExpression(expression, mode)
        assertEquals("5", result)
    }

    @Test
    fun evaluateWithParenthesesAndMultiply() {
        val expression = "(2+3)*4"
        val result = CalculatorUtils.evaluateExpression(expression, null) // default mode is "RAD"
        assertEquals("20", result)
    }

    @Test
    fun evaluateSineInDegMode() {
        val expression = "sin(90)"
        val result = CalculatorUtils.evaluateExpression(expression, KVal.DEG)
        assertEquals("1", result)
    }

    @Test
    fun evaluateSineInRadMode() {
        val expression = "sin(1.57079632679)" // ≈ π/2 radians
        val result = CalculatorUtils.evaluateExpression(expression, KVal.RAD)
        assertEquals("1", result)
    }

    @Test
    fun evaluate_ComplexSineInRadMode() {
        val expression = "sin(${Math.PI / 2})"
        val result = CalculatorUtils.evaluateExpression(expression, null) // null or "RAD"
        assertEquals("1", result)
    }


    @Test
    fun evaluateLargeNumberScientific() {
        val expression = "1e16"
        val result = CalculatorUtils.evaluateExpression(expression, null)
        assertEquals("10000000000000000", result)
    }

    @Test
    fun evaluateInvalidExpressionReturnsNull() {
        val expression = "2+*5"
        val result = CalculatorUtils.evaluateExpression(expression, null)
        assertNull(result)
    }

    @Test
    fun formatWithCommas() {
        val value = "20500".formatWithCommas()
        assertEquals("20,500", value)
    }

    @Test
    fun formatLargeNumberWithCommas() {
        val value = "1000000000".formatWithCommas()
        assertEquals("1,000,000,000", value)
    }

    @Test
    fun formatDecimalWithCommas() {
        val value = "1234567.89".formatWithCommas()
        assertEquals("1,234,567.89", value)
    }

    @Test
    fun formatNegativeNumberWithCommas() {
        val value = "-9876543".formatWithCommas()
        assertEquals("-9,876,543", value)
    }

    @Test
    fun formatDecimalAndNegativeWithCommas() {
        val value = "-1234567.8910".formatWithCommas()
        assertEquals("-1,234,567.8910", value)
    }

    @Test
    fun isOperator() {
        val sign = '+'.isOperator()
        assertEquals(true, sign)
    }

    @Test
    fun isOperator_TestNumber() {
        val sign = '6'.isOperator()
        assertEquals(false, sign)
    }

    @Test
    fun removeComma() {
        val value = "20,02,1,20,1,2,".removeComma
        assertEquals("The result is: ", "200212012", value)
    }

}






