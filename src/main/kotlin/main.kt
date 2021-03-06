import single.DijkstraWithTimeDimension
import single.SingleBotCase

fun main() {
    val map2 = """
        ............#######......
        ............#######......
        ....####.................
        .............#####.......
        .........................
        ....#####################
        .........................
        .........................
    """.trimIndent()

    val test2 = SingleBotCase(CaseMap(map2), Point(24, 0), Point(0, 7), emptyList())

    val res = DijkstraWithTimeDimension().findPath(test2).path

    test2.printWithPath(res!!)


}