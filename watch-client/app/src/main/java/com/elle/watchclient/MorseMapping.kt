package com.elle.watchclient

class MorseMapping {
    val morseToAlphabet = mapOf<String, String>(
        ".-" to "A",
        "-..." to "B",
        "-.-." to "C",
        "-.." to "D",
        "." to "E",
        "..-." to "F",
        "--." to "G",
        "...." to "H",
        ".." to "I",
        ".---" to "J",
        "-.-" to "K",
        ".-.." to "L",
        "--" to "M",
        "-." to "N",
        "---" to "O",
        ".--." to "P",
        "--.-" to "Q",
        ".-." to "R",
        "..." to "S",
        "-" to "T",
        "..-" to "U",
        "...-" to "V",
        ".--" to "W",
        "-..-" to "X",
        "-.--" to "Y",
        "--.." to "Z",
        "" to " ",
    )

    var alphabetToMorseMapping =  mutableMapOf("A" to "1")

    init {
        morseToAlphabet.forEach { entry ->
            alphabetToMorseMapping[entry.value] = entry.key
        }
    }

    fun getMorseCode(alphabet:String): String {
        return alphabetToMorseMapping.getOrDefault(alphabet,"")
    }


    fun getVibrationSequence(a:String):LongArray {
        val morseSequence = getMorseCode(a)
        val vibrationSequence = mutableListOf<Long>(300)
        for (i in morseSequence) {
            if (i == '.') {
                vibrationSequence.add(200)
            } else if (i == '-') {
                vibrationSequence.add(1500)
            }
            vibrationSequence.add(200)
        }
        return vibrationSequence.toLongArray()
    }


}