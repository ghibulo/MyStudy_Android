package com.ghibulo.mystudy

import android.os.Environment
import android.util.Log
import java.io.File
import java.time.Instant


class WordsCollection(val csvFile: String, val importFile: String? = null) : TrainWordsCollection() {

    val maxNewRepeatedWords = 10
    var newRepeatedWords = 0
    var trainWords : TrainWordsCollection = TrainWordsCollection()

    init {
        loadFromCsv(csvFile)
        if (importFile != null) {
            importFileListWords(importFile)
        }
    }

    companion object {
        private lateinit var instance: WordsCollection
        fun create(csvFile: String, importFile: String? = null): WordsCollection {
                instance = WordsCollection( csvFile,  importFile)
                return instance
        }
        fun getInstance(): WordsCollection {
            return instance
        }
    }


    class BufferItems {
        private var items: Word = Word()
        private var ind:Int = 0

        fun saveItem(item: String): Word? {
            //if (Global.DEBUG) {
                println ("called saveItem... items = ${items}")
            //}
            if (item.substring(0..2) == "---") {
                val result: Word? = items
                if (ind>0) {
                    ind = 0
                    items = Word()
                }
                return result
            } else {
                when (ind++) {
                    0 -> items.question = item
                    1 -> items.answer = item
                    2 -> items.pronunciation = item
                    else -> print("Error in the data file!")
                }
            }
            return null
        }//fun
    }




    fun importFileListWords(fileName: String)  {
        val outDir = getPublicAlbumStorageDir("MyFacts")
        val file = File(outDir, fileName)
        if (file.exists()) {
            var buffer: BufferItems = BufferItems()
            File(fileName).useLines { lines ->
                lines.forEach { line ->
                    val tempLine: Word? = buffer.saveItem(line)
                    if (tempLine != null) data.add(tempLine)
                }
            }
            data = data.sorted().toMutableList()
        }
    }

    override fun getWord(): Word? {
        data = data.sorted().toMutableList()
        val nowStamp: Long = Instant.now().getEpochSecond()
        var i:Int = 0
        while (i < data.size) {
            if ( (newRepeatedWords > maxNewRepeatedWords) && (data[i].timeStamp.equals(0L)) ) {i++;continue}
            if ((nowStamp - data[i].timeStamp) > minDuration) break
            i++
        }
        println ("We test the ${i}.word from the end")
        if (data.size <= i) i = data.size-1
        if (data[i].timeStamp.equals(0L)) newRepeatedWords++
        return data[i]
    }


    override fun evaluateWord(which: Word, nextRating: Int) {
        super.evaluateWord(which, nextRating)
        if (nextRating > 3) {
            trainWords.data.add(which.copy())
        }
    }

    fun getPublicAlbumStorageDir(albumName: String): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), albumName)
        if (!file?.mkdirs()) {
            Log.e("WordsCollection.getPublicAlbumStorageDir", "Directory not created")
        }
        return file
    }


    fun loadFromCsv(fileName: String ) {

        data = mutableListOf<Word>()
        val outDir = getPublicAlbumStorageDir("MyFacts")
        Log.e("WordsCollection.loadFromCsv", "after getPublicAlbum...")
        val file = File(outDir, fileName)
        Log.e("WordsCollection.loadFromCsv", "after get file")
        if (file.exists()) {
            file.useLines { lines ->
                lines.forEach { line ->
                    Log.e("WordsCollection.load...line:", "line")
                    val item: List<String> = line.split("\",\"")
                    //println (item)
                    if (item.size != 6) {
                        println("error during read csv - not 5 items!")
                    }
                    data.add(
                        Word(
                            item[0].drop(1), item[1], item[2],
                            item[3].toInt(), item[4].toLong(), item[5].dropLast(1).toLong()
                        )
                    )
                }
            }
        } else {
            data.add(
                Word( "stůl", "table", "tejbl", 0, 0, 0)
            )
            data.add(
                Word( "ahoj", "hello", "helou", 0, 0, 0)
            )
        }
        data = data.sorted().toMutableList()
    } //loadFromCsv(fileName: String )


    fun saveIntoCsv(fileName: String) {
        val outDir = getPublicAlbumStorageDir("MyFacts")
        val file = File(outDir, fileName)
        file.bufferedWriter().use { out ->
            for (word in data) {
                out.write("\"" + word.question + "\"," +
                          "\"" + word.answer + "\"," +
                          "\"" + word.pronunciation + "\"," +
                          "\"" + word.rating + "\"," +
                          "\"" + word.timeStamp + "\"," +
                          "\"" + word.futureTimeStamp + "\"\n"

                         )
            }
        }
    }



}
