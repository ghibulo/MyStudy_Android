package com.ghibulo.mystudy

import java.io.File
import java.time.Instant


open class TrainWordsCollection {

    var data: MutableList<Word> = mutableListOf<Word>()

    val ratingTimeExtension: DoubleArray = doubleArrayOf(1.4, 1.2, 1.0, 0.8, 0.6)
    val ratingStartTime: LongArray = longArrayOf(5*24*3600, 3*24*3600, 24*3600, 12*3600, 4*3600)
    //number of seconds after which the same word can be used
    val minDuration: Int = 2*60
    var index : Int = -1


    fun add(word: Word) {
        data.add(word)
    }

    fun showAll() {
        data = data.sorted().toMutableList()
        data.forEachIndexed { i, line -> println("${i}: " + line) }
    }

    fun getAtIndex(i: Int): Word? {
        return data.getOrNull(i)
    }


    open fun evaluateWord(which: Word, nextRating: Int) {

        val nowStamp: Long = Instant.now().getEpochSecond()
        //println ("nowStamp = ${nowStamp}")
        //println ("which... ${which}")
        //println ("which.timeStamp... ${which.timeStamp}")
        val duration: Long = if (nowStamp>which.futureTimeStamp)  (nowStamp - which.timeStamp) else (which.futureTimeStamp - which.timeStamp)

        //println ("duration = ${duration}")
        which.rating = nextRating
        //println ("which.rating = ${which.rating}")

        if (which.timeStamp.equals(0L)) {

            //println ("which.timeStamp equals 0, so it's set to ${nowStamp}")
            which.timeStamp =  nowStamp
            which.futureTimeStamp = which.timeStamp + ratingStartTime[nextRating-1]

            //println ("which.futureTimeStamp set to ${which.futureTimeStamp}")
        } else {

            //println ("which.timeStamp is not 0, so it's set to ${nowStamp}")
            which.timeStamp = nowStamp

            //println ("duration is multiply by coef ${ratingTimeExtension[nextRating-1]}")
            which.futureTimeStamp = (nowStamp + duration*ratingTimeExtension[nextRating-1]).toLong()
        }
    }

    //simple go through all the items
    open fun getWord(): Word? {
        if (data.lastIndex == -1) return null
        //to prevent asking the same question after shuffle
        var lastQuestion: String? = null
        if (++index > data.lastIndex) {
            index = 0
            lastQuestion = data.get(data.lastIndex).question
        }
        if (index == 0) {
            data.shuffle()
            if ((data.lastIndex > 0) and (data.get(0).question == lastQuestion)) index=1
        }
        return data.getOrNull(index)
    }

    fun deleteCurrentWord() {
        if ((data.lastIndex >= index) && (index >=0)) {
            data.removeAt(index)
        }
    }

}
