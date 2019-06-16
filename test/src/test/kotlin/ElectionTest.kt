import election.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.lang.IllegalArgumentException

internal class ElectionTest {
    companion object {
        val befolkningPerFylke2004 = mapOf(Pair(1,256668),Pair(2,488618), Pair(3,521886), Pair(4,188326),Pair(5,183690),
                Pair(6,242331),Pair(7,219480),Pair(8,166124),Pair(9,103374),Pair(10,160127),Pair(11,388848),
                Pair(12,445059),Pair(14,107222),Pair(15,244570),Pair(16,270266),Pair(17,127973),Pair(18,237057),
                Pair(19,152628),Pair(20,73210))
        val befolkningPerFylke2012 = mapOf(Pair(1,278352),Pair(2,556254), Pair(3,613285), Pair(4,192791),Pair(5,187147),
                Pair(6,265164),Pair(7,236424),Pair(8,170023),Pair(9,111495),Pair(10,174324),Pair(11,443115),
                Pair(12,490570),Pair(14,108201),Pair(15,256628),Pair(16,297950),Pair(17,133390),Pair(18,238320),
                Pair(19,158650),Pair(20,73787))
        val arealPerFylke2004 = mapOf(Pair(1,4182),Pair(2,4918), Pair(3,454),Pair(4,27397),Pair(5,25192),Pair(6,14910),
                Pair(7,2224),Pair(8,15299),Pair(9,9157),Pair(10,7276),Pair(11,9378),Pair(12,15460),Pair(14,18623),
                Pair(15,15121),Pair(16,18848),Pair(17,22412),Pair(18,38456),Pair(19,25877),Pair(20,48618))
        val arealPerFylke2012 = mapOf(Pair(1,4182),Pair(2,4918), Pair(3,454),Pair(4,27398),Pair(5,25192),Pair(6,14911),
                Pair(7,2224),Pair(8,15298),Pair(9,9157),Pair(10,7277),Pair(11,9376),Pair(12,15440),Pair(14,18623),
                Pair(15,15115),Pair(16,18856),Pair(17,22415),Pair(18,38462),Pair(19,25870),Pair(20,48617))
    }

    @Test internal fun `test simple election`(){
        val result = NorwegianElection(mapOf(Pair(1,1000000)),289).results(mapOf(Pair(1, mapOf(Pair("NA", 2000000)))))
        assertEquals(1, result.size)
        assertEquals(289, result["NA"])
    }

    @Test internal fun `illegal instantiations of NorwegianElection`() {
        assertThrows<IllegalArgumentException> { NorwegianElection(emptyMap(),0) }
        assertThrows<IllegalArgumentException> { NorwegianElection(emptyMap(),-3) }
        assertThrows<IllegalArgumentException> { NorwegianElection(mapOf(Pair(1,1)),0) }
        assertThrows<IllegalArgumentException> { NorwegianElection(mapOf(Pair(1,1)),-1) }
        assertThrows<IllegalArgumentException> { NorwegianElection(emptyMap(),1) }
    }
    @Test internal fun `election 2017`(){
        val result = NorwegianElection(mandatPoengPerFylke(befolkningPerFylke2012,arealPerFylke2012),169).results(getVotesPerPartyPerCounty(File(ClassLoader.getSystemResource("partifordeling_1_st_2017.csv").file)))
        assertEquals(9, result.size)
        assertEquals(49, result["A"])
        assertEquals(45, result["H"])
        assertEquals(27, result["FRP"])
        assertEquals(19, result["SP"])
        assertEquals(11, result["SV"])
        assertEquals(8, result["V"])
        assertEquals(8, result["KRF"])
        assertEquals(1, result["MDG"])
        assertEquals(1, result["RØDT"])
    }
    @Test internal fun `election 2013`(){
        val result = NorwegianElection(mandatPoengPerFylke(befolkningPerFylke2012, arealPerFylke2012),169).results(getVotesPerPartyPerCounty(File(ClassLoader.getSystemResource("partifordeling_1_st_2013.csv").file)))
        assertEquals(8, result.size)
        assertEquals(55, result["A"])
        assertEquals(48, result["H"])
        assertEquals(29, result["FRP"])
        assertEquals(10, result["SP"])
        assertEquals(10, result["KRF"])
        assertEquals(9, result["V"])
        assertEquals(7, result["SV"])
        assertEquals(1, result["MDG"])
    }
    @Test internal fun `election 2009`(){
        val result = NorwegianElection(mandatPoengPerFylke(befolkningPerFylke2004, arealPerFylke2004),169).results(getVotesPerPartyPerCounty(File(ClassLoader.getSystemResource("partifordeling_1_st_2009.csv").file)))
        assertEquals(7, result.size)
        assertEquals(64, result["A"])
        assertEquals(41, result["FRP"])
        assertEquals(30, result["H"])
        assertEquals(11, result["SP"])
        assertEquals(11, result["SV"])
        assertEquals(10, result["KRF"])
        assertEquals(2, result["V"])
    }
    private fun mandatPoengPerFylke(befolkningPerFylke: Map<Int, Int>, arealPerFylke: Map<Int, Int>) = befolkningPerFylke
            .mapValues { it.value+ arealPerFylke[it.key]!!*1.8 }.mapValues { it.value.toInt() }
}