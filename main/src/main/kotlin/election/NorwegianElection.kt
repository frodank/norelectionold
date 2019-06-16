package election

class NorwegianElection(private val populationPerCounty: Map<Int, Int>, private val seats: Int) {
    init {
        require(seats>0 && populationPerCounty.size>0)
    }

    fun results(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>): Map<String, Int> {
        val seatsPerCounty = (0..seats)
                .map { emptyMap<Int,Int>() }
                .reduce{ acc, _ -> acc+mapOf(populationPerCounty.mapValues { (it.value / (acc.mapValues { it.value.toDouble()*2+1 }.getOrDefault(it.key, 1.0))).toInt() }.maxBy { it.value }!!.toPair()).mapValues { acc.getOrDefault(it.key, 0) + 1 }}
        val countyseatsPerParty = seatsPerCounty
                .map {(0 until it.value).map { emptyMap<String,Int>() }.reduce{ acc, _ ->acc+ mapOf(votesPerCandidatePerCounty.get(it.key)!!.mapValues { (it.value / (acc.mapValues { it.value.toDouble()*2+1 }.getOrDefault(it.key, 1.4))).toInt() }.maxBy { it.value }!!.toPair()).mapValues { acc.getOrDefault(it.key, 0) + 1 }} }
                .flatMap { it.entries }
                .groupBy({it.key},{it.value})
                .mapValues { it.value.sum() }
        val votesPerParty = votesPerCandidatePerCounty.map { it.value }
                .flatMap { it.entries }
                .groupBy({it.key},{it.value})
                .mapValues { it.value.sum() }
                .filter { it.value>=Math.ceil(votesPerCandidatePerCounty.flatMap { it.value.values }.sum()*4.0/100).toInt() }
        val equalizingSeatsPerParty = (countyseatsPerParty.map { it.value }.sum()..seats).map { countyseatsPerParty }.reduce{ acc, _ ->acc+ mapOf(votesPerParty.mapValues { it.value/(acc.getOrDefault(it.key,0)*2+1) }.maxBy { it.value }!!.toPair()).mapValues { acc.getOrDefault(it.key, 0) + 1 }}

        return equalizingSeatsPerParty
    }

    private fun results(seats: Int, votesPerParty: Map<String, Int>) =
            (0..seats).map { emptyMap<String,Int>() }.reduce{ acc, _ ->acc+ mapOf(votesPerParty.mapValues { (it.value / (acc.mapValues { it.value.toDouble()*2+1 }.getOrDefault(it.key, 1.4))).toInt() }.maxBy { it.value }!!.toPair()).mapValues { acc.getOrDefault(it.key, 0) + 1 }}

}
private fun <T>nextSeatCandidateEntry(seatsSoFar: Map<T, Int>, votesPerParty: Map<T, Int>) =
        votesPerParty.mapValues(toSaintLague(seatsSoFar)).maxBy { it.value }!!.toPair()

private fun <T>toSaintLague(seatsSoFar: Map<T, Int>, specialCaseFirst:Double=1.0): (Map.Entry<T, Int>) -> Int =
        { (it.value / (seatsSoFar.mapValues { it.value.toDouble()*2+1 }.getOrDefault(it.key, specialCaseFirst))).toInt() }

private fun Map<Int, Int>.increaseSeatsSoFarByOne(seatsSoFar: Map<Int,Int>) =
        mapValues { seatsSoFar.getOrDefault(it.key, 0) + 1 }

private fun tresholdLimit(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>) =
        Math.ceil(votesPerCandidatePerCounty.flatMap { it.value.values }.sum()*4.0/100).toInt()