package ng.com.topstar.topcalculator.utilities

import ng.com.topstar.topcalculator.roundToWithComma
import org.junit.Assert.assertEquals
import org.junit.Test

class RoundToTest {

    @Test
    fun testRoundToWithNoDecimals() {
        val value = 1234567.0.roundToWithComma(0)
        assertEquals("1,234,567", value)
    }

    @Test
    fun testRoundToWithTwoDecimals() {
        val value = 1234567.8999.roundToWithComma(2)
        assertEquals("1,234,567.9", value)  // .8999 rounds to .90 then trimmed
    }

    @Test
    fun testRoundToWithManyDecimals() {
        val value = 1234.567886.roundToWithComma(5)
        assertEquals("1,234.56789", value)
    }

    @Test
    fun testRoundToWithTrailingZeroTrimming() {
        val value = 1000000.5000.roundToWithComma(4)
        assertEquals("1,000,000.5", value)
    }

    @Test
    fun testNegativeNumberRoundTo() {
        val value = (-9876543.2160).roundToWithComma(2)
        assertEquals("-9,876,543.22", value)
    }

    @Test
    fun testLargeNumberScientificCutoff() {
        val value = 1000000000.0.roundToWithComma(2)
        assertEquals("1,000,000,000", value)
    }
}
