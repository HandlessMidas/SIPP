import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Timer {
    companion object {
        private var startTime = LocalDateTime.now()

        fun start() {
            startTime = LocalDateTime.now()
        }

        fun print() {
            println(ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()))
        }

        fun get(): Long {
            return ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
        }
    }
}