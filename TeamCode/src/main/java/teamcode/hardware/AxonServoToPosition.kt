package teamcode.hardware

import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.Servo
import com.rowanmcalpin.nextftc.core.Subsystem
import com.rowanmcalpin.nextftc.core.command.Command
import kotlin.math.abs

class AxonServoToPosition @JvmOverloads constructor(
    private val servo: Servo,
    private val targetPosition: Double,
    private val analogInput: AnalogInput,
    override val subsystems: Set<Subsystem>,
    private val tolerance: Double = DEFAULT_TOLERANCE
) : Command() {
    constructor(
        servo: Servo,
        targetPosition: Double,
        analogInput: AnalogInput,
        subsystem: Subsystem,
        tolerance: Double = DEFAULT_TOLERANCE
    ) :
            this(servo, targetPosition, analogInput, setOf(subsystem), tolerance)

    constructor(
        servo: Servo,
        targetPosition: Double,
        analogInput: AnalogInput,
        subsystem: Subsystem
    ) : this(servo, targetPosition, analogInput, subsystem, DEFAULT_TOLERANCE)

    companion object {
        private const val DEFAULT_TOLERANCE = 0.05
        private const val MAX_VOLTAGE = 3.3
    }

    override val isDone: Boolean
        get() {
            val currentPosition = analogInput.voltage / MAX_VOLTAGE
            val directDiff = abs(currentPosition - targetPosition)
            return directDiff <= tolerance
        }

    override fun start() {
        servo.position = targetPosition
    }
}