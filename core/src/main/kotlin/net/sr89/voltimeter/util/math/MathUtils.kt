package net.sr89.voltimeter.util.math

/**
 * Assumes that:
 * value / total = missingValue / otherTotal
 *
 * @return missingValue
 */
fun missingRatioValue(value: Float, total: Float, otherTotal: Float): Float {
    return (value * otherTotal) / total
}
