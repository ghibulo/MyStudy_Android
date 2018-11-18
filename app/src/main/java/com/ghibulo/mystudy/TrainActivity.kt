package com.ghibulo.mystudy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TrainActivity : AppCompatActivity() {


    var words: WordsCollection? = null
    var stateUncovered: Int = 0
    var testWord: Word? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)
        words = WordsCollection.getInstance()

        val btnShow = findViewById<View>(R.id.buttonShow) as Button
        val btnEnd = findViewById<View>(R.id.buttonEnd) as Button
        btnShow.setEnabled(true)
        btnEnd.setEnabled(true)

        getQuestion()

        // Get the Intent that started this activity and extract the string
        //val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        /*
        val textView = findViewById<TextView>(R.id.textView).apply {
            text = message
        }
        */
    }

    fun onUncover(view: View) {
        if (stateUncovered == 0) {
            val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
                text = testWord?.answer + "\n---\n" + testWord?.pronunciation
            }
            setButtons(StateButtons.AFTER_UNCOVER)
            stateUncovered = 1
        } else {

            getQuestion()
        }

    }


    fun onRemoveButton(view: View) {

        words?.trainWords?.deleteCurrentWord()
        getQuestion()
    }


    fun getQuestion() {

        if (words == null) {
            Log.d("Get question from TrainActivity:", "words == null!")
        }

        testWord = words?.trainWords?.getWord()

        if (testWord == null) onBackPressed()

        stateUncovered = 0
        setButtons(StateButtons.BEFORE_UNCOVER)

        // Capture the layout's TextView and set the string as its text
        val q_textView = findViewById<TextView>(R.id.questionTextView).apply {
            text = testWord?.question

        }
        val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
            text = ""
        }
    }




    fun setButtons(how: StateButtons) {
        //val btnShow = findViewById<View>(R.id.buttonShow) as Button
        val btnRemove = findViewById<View>(R.id.buttonRemove) as Button
        //val btnEnd = findViewById<View>(R.id.buttonEnd) as Button


        if (how == StateButtons.BEFORE_UNCOVER) {
            //btnShow.setEnabled(true)
            btnRemove.setEnabled(false)
            //btnEnd.setEnabled(true)
        }

        if (how == StateButtons.AFTER_UNCOVER) {
            //btnShow.setEnabled(true)
            btnRemove.setEnabled(true)
            //btnEnd.setEnabled(true)
        }



    }

    fun onSwapUncover(view: View) {
        if (stateUncovered == 1) {
            val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
                text =  testWord?.pronunciation + "\n---\n" +  testWord?.answer
            stateUncovered}
        }

        if (stateUncovered == 2) {
            val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
                text = testWord?.answer + "\n---\n" + testWord?.pronunciation
            }
        }
        if ((stateUncovered == 1)||(stateUncovered ==2)) {
            stateUncovered = if (stateUncovered == 1) { 2 } else {1}
        }

    }

    override fun onBackPressed() {


        Toast.makeText(getApplicationContext(), "Saving the data... ", Toast.LENGTH_LONG).show()
        words?.saveIntoCsv("data.csv")

        /*
        val intent = Intent(this, TestActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "finito")
        }
        startActivity(intent)
        */

        this.finish()
    }

    fun onEndButton(view: View){
        onBackPressed()
    }


}
