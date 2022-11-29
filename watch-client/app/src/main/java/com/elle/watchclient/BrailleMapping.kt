package com.elle.watchclient


class BrailleMapping {

    private var alphabetMapping: Map<String, String> = mapOf(
        "A" to "1",
        "B" to "12",
        "C" to "14",
        "D" to "145",
        "E" to "15",
        "F" to "124",
        "G" to "1245",
        "H" to "125",
        "I" to "24",
        "J" to "245",
        "K" to "13",
        "L" to "123",
        "M" to "134",
        "N" to "1345",
        "O" to "135",
        "P" to "1234",
        "Q" to "12345",
        "R" to "1235",
        "S" to "234",
        "T" to "2345",
        "U" to "136",
        "V" to "1236",
        "W" to "2456",
        "X" to "1346",
        "Y" to "13456",
        "Z" to "1356",
    )

    private var numberMapping  = mutableMapOf("1" to "A")

    // We currently send back _ when characters cannot be found
    var KEY_NOT_FOUND = "_"

    init {
        alphabetMapping.forEach { entry ->
            numberMapping[entry.value] = entry.key
        }
    }

    fun getAlphabetToNumberMapping(): Map<String, String> {
        return alphabetMapping;
    }

    fun getNumberToAlphabetMapping(): Map<String, String> {
        return numberMapping;
    }

    fun getNumberStringFromAlphabet(a:String):String {
        return alphabetMapping.getOrDefault(a, KEY_NOT_FOUND)
    }

    fun getAlphabetFromNumberString(a:String):String {
        return numberMapping.getOrDefault(a, KEY_NOT_FOUND)
    }
}