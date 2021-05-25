import multiple.AgentsComparator
import multiple.MultiAgentCase
import multiple.MultipleAgentSolver
import single.SIPP
import java.io.File

fun main() {
    val testsRoot = "tests"

    /*
    val files = listOf(
        "Moscow_2_256",
        "Berlin_2_256",
        "Boston_2_256",
        "Denver_2_256",
        "London_2_256",
        "Milan_2_256",
        "NewYork_2_256"
    )

     */

    val files = File("$testsRoot/ma").listFiles()!!.map { it.name }

    println("Number of files: ${files.size}")

    val testCases = listOf (
        Pair("Quickest_first", MultipleAgentSolver(SIPP(), AgentsComparator.QUICKEST_FIRST)),
        Pair("Slowest_first", MultipleAgentSolver(SIPP(), AgentsComparator.SLOWEST_FIRST)),
        Pair("Start_cliques_first", MultipleAgentSolver(SIPP(), AgentsComparator.START_CLIQUES_FIRST)),
        Pair("End_cliques_first", MultipleAgentSolver(SIPP(), AgentsComparator.END_CLIQUES_FIRST)),
        Pair("Max_sum_squares", MultipleAgentSolver(SIPP(), AgentsComparator.MAX_SQUARED_FIRST)),
        Pair("Min_sum_squares", MultipleAgentSolver(SIPP(), AgentsComparator.MIN_SQUARED_FIRST))
    )

    testCases.forEach { (name, _) ->
        File("ma$name.csv").writeText("map_name,cnt_agents,sum_length,max_length,avg_length\n")
    }

    files.forEachIndexed { fi, file ->
        println(fi)
        val test = MultiAgentCase.fromFile("$testsRoot/$file.map","$testsRoot/ma/$file", "$testsRoot/obs/$file")
        var shouldSave = true
        val toSave = mutableListOf<Pair<String, String>>()
        for ((name, solver) in testCases) {
            val res = solver.solve(test)
            if (res.paths == null) {
                shouldSave = false
                break
            }
            toSave.add(Pair("ma$name.csv", "$file,${res.nagents},${res.sumLength},${res.maxLength},${res.avgLength}\n"))
        }
        if (shouldSave) {
            toSave.forEach { (name, text) ->
                File(name).appendText(text)
            }
        }
    }
}