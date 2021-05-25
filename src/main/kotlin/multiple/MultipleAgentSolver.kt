package multiple

import Obstacle
import TimePoint
import single.PathFindingAlgo
import single.SingleBotCase

class MultipleAgentSolver(
    private val pathFindingAlgo: PathFindingAlgo,
    private val agentsComparator: AgentsComparator
) {
    fun solve(case: MultiAgentCase): MultiRes {
        val agents = case.startFinishPoints.sortedWith { a, b -> agentsComparator.compare(a, b, case) }

        val currObstacles = case.obstacles.toMutableList()
        val res = mutableListOf<List<TimePoint>>()

        agents.forEach {
            val newRes = pathFindingAlgo.findPath(SingleBotCase(case.map, it.first, it.second, currObstacles)).path ?: return MultiRes(null)
            res.add(newRes)
            currObstacles.add(Obstacle(newRes, 0.5))
        }

        return MultiRes(res)
    }

    data class MultiRes(
        val paths: List<List<TimePoint>>?
    ) {
        val sumLength = paths?.sumBy { it.size }
        val maxLength = paths?.maxOf { it.size }
        val nagents = paths?.size
        val avgLength = nagents?.let { sumLength?.div(it) }
    }
}