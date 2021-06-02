import single.*
import java.io.File
import kotlin.random.Random

fun main() {
    /**
     * Three types of files in test root:
     * 1) .map (maps)
     * 2) .map.scen (points of start and finish)
     * 3) .obs files (obstacles)
     */

    val testsRoot = "tests"
    val files = listOf(
        "arena",
        "brc000d",
        "brc200d",
        "brc300d",
        "brc502d",
        "brc999d",
        "combat",
        "den001d",
        "den011d",
        "den101d",
        "den201d",
        "den203d",
        "den207d"
    )

    val cntssOfObstacles = listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 1000000)
    val CNT_RUNS = 10

    val sippFile = File("sippSingle.csv")
    val astarFile = File("astarSingle.csv")

    sippFile.writeText("")
    astarFile.writeText("");
    sippFile.writeText("test_name,obs_cnt,path_length,open_cnt,closed_cnt,time_ms\n")
    astarFile.writeText("test_name,obs_cnt,path_length,open_cnt,closed_cnt,time_ms\n")

    val rand = Random(42)

    val sipp = SIPP()
    val astar = AStarWithTimeDimension()

    files.forEach { file ->
        println("Processing $file")

        //choosing the start and the finish points
        val points = File("$testsRoot/scenes/$file.map.scen").readLines().drop(100).dropLast(0).shuffled(rand).take(CNT_RUNS)

        points.forEachIndexed {ii, pstr ->
            cntssOfObstacles.forEach { obsCnt ->
                println("Processing point $ii for $obsCnt obstacles")
                val t = pstr.split("\\s+".toRegex())
                val start = Point(t[4].toInt(), t[5].toInt())
                val finish = Point(t[6].toInt(), t[7].toInt())

                val test =
                    SingleBotCase.fromFile("$testsRoot/$file.map", "tests/obs/$file", start, finish, obsCnt)

                val ress = sipp.findPath(test)

                //if solution exists
                if (ress.path != null) {
                    val resa = astar.findPath(test)

                    sippFile.appendText("$file,${test.obstacles.size},${ress.path!!.size},${ress.openCnt},${ress.closedCnt},${ress.timeMs}\n")
                    astarFile.appendText("$file,${test.obstacles.size},${resa.path!!.size},${resa.openCnt},${resa.closedCnt},${resa.timeMs}\n")
                }
            }
        }
    }
}