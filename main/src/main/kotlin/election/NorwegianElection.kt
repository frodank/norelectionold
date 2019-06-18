package election

import java.lang.Math.ceil

class NorwegianElection(befolkningPerFylke: Map<Int, Int>, arealPerFylke: Map<Int, Int>, private val seats: Int) {
    private val populationPerCounty: Map<Int, Int>
    init {
        require(seats>0 && befolkningPerFylke.isNotEmpty() && befolkningPerFylke.keys == arealPerFylke.keys)
        populationPerCounty=befolkningPerFylke.mapValues { it.value+ arealPerFylke[it.key]!!*1.8 }.mapValues { it.value.toInt() }
    }

    fun results(votesPerCandidatePerCounty: Map<Int, Map<String, Int>>): Map<String, Int> {
        val countySeats = mutableMapOf<Int,Int>()
        for (seatNumber in 0 until seats) {
            var leader = Pair(-1,Int.MIN_VALUE)
            for(county in populationPerCounty) {
                val countySeats = countySeats.getOrDefault(county.key, 0)
                val countyPopulation = county.value/(countySeats*2+1)
                if(countyPopulation>leader.second) {
                    leader= Pair(county.key,countyPopulation)
                }
            }
            countySeats.put(leader.first,countySeats.getOrDefault(leader.first,0)+1)
        }
        val countySeatsPerPartyPerCounty = mutableMapOf<Int, Map<String,Int>>()
        for(countyIndex in 0 until votesPerCandidatePerCounty.size) {
            val countyNr = votesPerCandidatePerCounty.entries.elementAt(countyIndex).key
            val thisCountySeats = (countySeats.get(countyNr)?:0)-1
            val thisCountySeatsPerParty = mutableMapOf<String,Int>()
            for (seatNumber in 0 until thisCountySeats) {
                var leader = Pair("",Int.MIN_VALUE)
                for(party in votesPerCandidatePerCounty.entries.elementAt(countyIndex).value) {
                    val partySeats = thisCountySeatsPerParty.get(party.key)
                    val partyVotes:Int
                    if(partySeats==null){
                        partyVotes = (party.value/1.4).toInt()
                    } else {
                        partyVotes = party.value/(partySeats*2+1)
                    }
                    if(partyVotes>leader.second) {
                        leader= Pair(party.key,partyVotes)
                    }
                }
                thisCountySeatsPerParty.put(leader.first,thisCountySeatsPerParty.getOrDefault(leader.first,0)+1)
            }
            countySeatsPerPartyPerCounty.put(countyNr, thisCountySeatsPerParty)
        }
        val countySeatsPerPartyTemp = mutableMapOf<String,MutableList<Int>>()

        for(countyIndex in 0 until countySeatsPerPartyPerCounty.entries.size) {
            val countySeats = countySeatsPerPartyPerCounty.entries.elementAt(countyIndex).value
            for(candidateIndex in 0 until countySeats.entries.size) {
                val partyEntry = countySeats.entries.elementAt(candidateIndex)
                countySeatsPerPartyTemp.getOrPut(partyEntry.key) { -> mutableListOf()}.add(partyEntry.value)
            }
        }

        val countySeatsPerParty = mutableMapOf<String, Int>()

        for(candidateIndex in 0 until countySeatsPerPartyTemp.entries.size) {
            val partyEntry = countySeatsPerPartyTemp.entries.elementAt(candidateIndex)
            var totalPartyVotes = 0
            for(i in 0 until partyEntry.value.size) {
                totalPartyVotes+=partyEntry.value.get(i)
            }
            countySeatsPerParty.put(partyEntry.key, totalPartyVotes)
        }

        val totalVotesPerPartyTemp = mutableMapOf<String,Int>()
        for(countyIndex in 0 until votesPerCandidatePerCounty.values.size) {
            val votesPerCandidate = votesPerCandidatePerCounty.values.elementAt(countyIndex)
            for(candidateIndex in 0 until votesPerCandidate.entries.size) {
                val candidateName = votesPerCandidate.entries.elementAt(candidateIndex).key
                val candidateVotes = votesPerCandidate.entries.elementAt(candidateIndex).value
                totalVotesPerPartyTemp.put(candidateName, totalVotesPerPartyTemp.getOrDefault(candidateName,0)+candidateVotes)
            }
        }

        val totalVotesPerParty = mutableMapOf<String,Int>()

        for(candidateIndex in 0 until totalVotesPerPartyTemp.values.size) {
            var totalVotes = 0
            for(countyIndex in 0 until votesPerCandidatePerCounty.values.size) {
                val votesPerCandidate = votesPerCandidatePerCounty.values.elementAt(countyIndex)
                for(candidateIndex in 0 until votesPerCandidate.values.size) {
                    totalVotes+= votesPerCandidate.entries.elementAt(candidateIndex).value
                }
            }
            val electionTreshold =  ceil(totalVotes*4.0/100)
            if(totalVotesPerPartyTemp.values.elementAt(candidateIndex)>=electionTreshold) {
                totalVotesPerParty.put(totalVotesPerPartyTemp.keys.elementAt(candidateIndex),totalVotesPerPartyTemp.values.elementAt(candidateIndex))
            }
        }

        var seatsAllocatedToNow=0
        for (i in 0 until countySeatsPerParty.values.size) {
            seatsAllocatedToNow+=countySeatsPerParty.values.elementAtOrNull(i) ?: throw IllegalStateException()
        }

        val totalSeatsPerParty = countySeatsPerParty.toMutableMap()
        for (seatNumber in seatsAllocatedToNow until seats) {
            var leader = Pair("",Int.MIN_VALUE)
            for(party in totalVotesPerParty) {
                val partySeats = totalSeatsPerParty.get(party.key)
                val partyVotes:Int
                if(partySeats==null){
                    partyVotes = (party.value/1.4).toInt()
                } else {
                    partyVotes = party.value/(partySeats*2+1)
                }
                if(partyVotes>leader.second) {
                    leader= Pair(party.key,partyVotes)
                }
            }
            totalSeatsPerParty.put(leader.first,totalSeatsPerParty.getOrDefault(leader.first,0)+1)
        }
        return totalSeatsPerParty
    }
}