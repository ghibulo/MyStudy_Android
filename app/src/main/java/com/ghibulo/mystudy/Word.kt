package com.ghibulo.mystudy

data class Word (var question: String = "",
                 var answer: String = "",
                 var pronunciation: String = "",
                 var rating: Int = 0,
                 var timeStamp: Long = 0,
                 var futureTimeStamp: Long = 0): Comparable<Word> {

    override fun compareTo(other: Word) = when {
        //rating != other.rating -> rating - other.rating
        futureTimeStamp > other.futureTimeStamp -> 1
        futureTimeStamp < other.futureTimeStamp -> -1
        else -> 0
	}



}
