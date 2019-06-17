package election

import java.lang.Math.ceil

class NorwegianElection(befolkningPerFylke: Map<Int, Int>, arealPerFylke: Map<Int, Int>, private val seats: Int) {
    private val populationPerCounty: Map<Int, Int>
    init {
        require(seats>0 && befolkningPerFylke.isNotEmpty() && befolkningPerFylke.keys == arealPerFylke.keys)
        populationPerCounty=befolkningPerFylke.mapValues { it.value+ arealPerFylke[it.key]!!*1.8 }.mapValues { it.value.toInt() }
    }

    fun results(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>)=seatsFromVotes(seats,
            votesPerParty(votesPerCandidatePerCounty), countySeatsPerParty(votesPerCandidatePerCounty))

    private fun votesPerParty(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>)=votesPerCandidatePerCounty.map { it.value }
            .sumMaps().filter { it.value>=ceil(votesPerCandidatePerCounty.flatMap { it.value.values }.sum()*4.0/100).toInt() }

    private fun countySeatsPerParty(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>)=seatsFromVotes(seats, populationPerCounty)
            .map {seatsFromVotes(it.value-1, votesPerCandidatePerCounty.get(it.key)!!) }.sumMaps()

    private fun <K>seatsFromVotes(seats: Int, votesPerCandidate: Map<K,Int>, initialSeatsAllocated: Map<K,Int> = emptyMap()) = (initialSeatsAllocated.map { it.value }.sum()..seats)
    .map { initialSeatsAllocated }
    .reduce{ acc, _ -> acc+mapOf(votesPerCandidate.mapValues { (it.value / (acc.mapValues { it.value.toDouble()*2+1 }.getOrDefault(it.key, 1.4))).toInt() }.maxBy { it.value }!!.toPair()).mapValues { acc.getOrDefault(it.key, 0) + 1 }}
}

private fun <K> List<Map<K,Int>>.sumMaps()= this.flatMap { it.entries }.groupBy({it.key},{it.value}).mapValues { it.value.sum() }