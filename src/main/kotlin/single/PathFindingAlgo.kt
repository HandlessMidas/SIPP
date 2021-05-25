package single

import TimePoint

interface PathFindingAlgo {
    fun findPath(case: SingleBotCase): AlgoRes

    data class AlgoRes(
        val path: List<TimePoint>?,
        val closedCnt: Int,
        val openCnt: Int,
        val timeMs: Long
    )
}