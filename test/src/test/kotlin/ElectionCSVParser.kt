import java.io.File

fun getVotesPerPartyPerCounty(file: File) = file.useLines { it.drop(1).map { it.split(";") }
        .map { Pair(it[0].toInt(),Pair(it[6],it[12].toInt())) }
        .groupBy ({ it.first }, {it.second})
        .mapValues { it.value.toMap() }}