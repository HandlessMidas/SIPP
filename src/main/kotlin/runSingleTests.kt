import single.DijkstraWithTimeDimension
import single.SIPP
import single.SingleBotCase
import java.io.File
import kotlin.random.Random

fun main() {
    val testsRoot = "tests"
    val files = listOf(
        "Moscow_2_256",
        "Berlin_2_256",
        "Boston_2_256",
        "Denver_2_256",
        "London_2_256",
        "Milan_2_256",
        "NewYork_2_256"
    )
    val CNT_RUNS = 20

    val dijFile = File("dijkstraSingle.csv")
    val sippFile = File("sippSingle.csv")

    dijFile.writeText("")
    sippFile.writeText("")
    dijFile.writeText("test_name,path_length,open_cnt,closed_cnt,time_ms,obs_cnt\n")
    sippFile.writeText("test_name,path_length,open_cnt,closed_cnt,time_ms,obs_cnt\n")

    val rand = Random(42)

    val dij = DijkstraWithTimeDimension()
    val sipp = SIPP()

    files.forEach { file ->
        println("Processing $file")
        val points = File("$testsRoot/$file.map.scen").readLines().drop(100).dropLast(400).shuffled(rand).take(CNT_RUNS)

        points.forEachIndexed {ii, pstr ->
            println("Processing $ii")
            val t = pstr.split("\\s+".toRegex())
            val start = Point(t[4].toInt(), t[5].toInt())
            val finish = Point(t[6].toInt(), t[7].toInt())

            val test = SingleBotCase.fromFile("$testsRoot/$file.map", "tests/$file.obs", start, finish)

            val ress = sipp.findPath(test)

            if (ress.path != null) {
                val resd = dij.findPath(test)

                dijFile.appendText("$file,${resd.path!!.size},${resd.openCnt},${resd.closedCnt},${resd.timeMs},${test.obstacles.size}\n")
                sippFile.appendText("$file,${ress.path!!.size},${ress.openCnt},${ress.closedCnt},${ress.timeMs},${test.obstacles.size}\n")
            }
        }
    }
}