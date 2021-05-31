import multiple.AgentsComparator
import multiple.MultiAgentCase
import multiple.MultipleAgentSolver
import single.SIPP
import java.io.File

data class MTest(
    val name: String,
    val solver: MultipleAgentSolver,
    val nagents: List<Int>
)

fun main() {
    /**
     * 1) .map files in the testRoot
     * 2) files with start and finish of agents in the testRoot/ma folder
     */

    val testsRoot = "tests"

    //if more than actual number of agents, then max number of agents is used
    val cntAgent = listOf(10, 1000000)

    val testCases = listOf (
        MTest("Start_cliques_first", MultipleAgentSolver(SIPP(), AgentsComparator.START_CLIQUES_FIRST, false), cntAgent),
        MTest("Max_sum_squares", MultipleAgentSolver(SIPP(), AgentsComparator.MAX_SQUARED_FIRST, false), cntAgent),
        MTest("Min_sum_squares", MultipleAgentSolver(SIPP(), AgentsComparator.MIN_SQUARED_FIRST, false), cntAgent),
        MTest("Quickest_first", MultipleAgentSolver(SIPP(), AgentsComparator.QUICKEST_FIRST, false), cntAgent),
        MTest("Slowest_first", MultipleAgentSolver(SIPP(), AgentsComparator.SLOWEST_FIRST, false), cntAgent),
        MTest("Random", MultipleAgentSolver(SIPP(), AgentsComparator.RANDOM, false), cntAgent),
    )

    val files = File("$testsRoot/ma").listFiles()!!.map { it.name }

    println("Number of files: ${files.size}")

    testCases.forEach { (name, _) ->
        File("ma$name.csv").writeText("map_name,cnt_agents,sum_length,max_length,avg_length\n")
    }

    files.forEachIndexed { fi, file ->
        println("$fi $file")
        var shouldSave = true
        val toSave = mutableListOf<Pair<String, String>>()
        for ((name, solver, cnts) in testCases) {
            for (cntAg in cnts) {
                val test = MultiAgentCase.fromFile("$testsRoot/$file.map", "$testsRoot/ma/$file", cntAg)
                val res = solver.solve(test)
                if (res.paths == null) {
                    shouldSave = false
                    break
                }
                toSave.add(
                    Pair(
                        "ma$name.csv",
                        "$file,${res.nagents},${res.sumLength},${res.maxLength},${res.avgLength}\n"
                    )
                )
            }
            if (!shouldSave) {
                break
            }
        }
        if (shouldSave) {
            toSave.forEach { (name, text) ->
                File(name).appendText(text)
            }
        }
    }
}