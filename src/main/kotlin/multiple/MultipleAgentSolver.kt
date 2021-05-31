package multiple

import CaseMap
import Obstacle
import Point
import TimePoint
import single.PathFindingAlgo
import single.SingleBotCase

class MultipleAgentSolver(
    private val pathFindingAlgo: PathFindingAlgo,
    private val agentsComparator: AgentsComparator,
    private val adaptiveBlockFinalPoints: Boolean
) {
    fun solve(case: MultiAgentCase): MultiRes {
        return if (!adaptiveBlockFinalPoints) {
            solveSimple(case)
        } else {
            solveBlocking(case)
        }
    }

    private fun solveSimple(case: MultiAgentCase): MultiRes {
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

    private fun solveBlocking(case: MultiAgentCase): MultiRes {
        val fullRes = solveBlockingN(case, case.startFinishPoints.size)
        if (fullRes.paths != null) {
            return fullRes
        }

        var L = 0
        var R = case.startFinishPoints.size

        var ans = MultiRes(null)

        while (R - L != 1) {
            val M = (R + L) / 2
            val res = solveBlockingN(case, M)
            if (res.paths == null) {
                R = M;
            } else {
                L = M
                ans = res
            }
        }

        return ans
    }

    private fun solveBlockingN(case: MultiAgentCase, n: Int): MultiRes {
        val agents = case.startFinishPoints.sortedWith { a, b -> agentsComparator.compare(a, b, case) }

        val currObstacles = case.obstacles.toMutableList()
        val res = mutableListOf<List<TimePoint>>()

        agents.forEachIndexed {i, agent ->
            val map = case.map.copy()
            if (i < n) {
                (i + 1 until agents.size).forEach { j ->
                    map.blockMovingPoint(agents[j].second)
                }
            }
            val newRes = pathFindingAlgo.findPath(
                SingleBotCase(map, agent.first, agent.second, currObstacles)
            ).path ?: return MultiRes(null)
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