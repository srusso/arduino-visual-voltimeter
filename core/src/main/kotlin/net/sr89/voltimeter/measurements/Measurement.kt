package net.sr89.voltimeter.measurements

import java.time.Instant

data class Measurement(val voltage: Float, val timestamp: Instant)