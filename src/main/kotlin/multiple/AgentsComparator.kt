package multiple

import Point
import kotlin.random.Random

abstract class AgentsComparator private constructor() {

    abstract fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int

    companion object {
        val QUICKEST_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                return agent1.first.dist2(agent1.second).compareTo(agent2.first.dist2(agent2.second))
            }
        }

        val SLOWEST_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                return -agent1.first.dist2(agent1.second).compareTo(agent2.first.dist2(agent2.second))
            }
        }

        val START_CLIQUES_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                val a1 = case.startFinishPoints.minOf { agent1.first.dist2(it.first) }
                val a2 = case.startFinishPoints.minOf { agent2.first.dist2(it.first) }
                return a1.compareTo(a2)
            }
        }

        val END_CLIQUES_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                val a1 = case.startFinishPoints.minOf { agent1.second.dist2(it.second) }
                val a2 = case.startFinishPoints.minOf { agent2.second.dist2(it.second) }
                return -a1.compareTo(a2)
            }
        }

        val MIN_SQUARED_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                val a1 = case.startFinishPoints.sumOf { agent1.first.dist2(it.first) }
                val a2 = case.startFinishPoints.sumOf { agent2.first.dist2(it.first) }
                return a1.compareTo(a2)
            }
        }

        val MAX_SQUARED_FIRST = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                val a1 = case.startFinishPoints.sumOf { agent1.first.dist2(it.first) }
                val a2 = case.startFinishPoints.sumOf { agent2.first.dist2(it.first) }
                return -a1.compareTo(a2)
            }
        }

        val RANDOM = object : AgentsComparator() {
            override fun compare(agent1: Pair<Point, Point>, agent2: Pair<Point, Point>, case: MultiAgentCase): Int {
                return Random(agent1.hashCode()).nextLong().compareTo(Random(agent2.hashCode()).nextLong())
            }
        }
    }
}