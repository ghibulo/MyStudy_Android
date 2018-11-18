package com.ghibulo.mystudy

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TestActivity : AppCompatActivity() {

    var words: WordsCollection? = null
    var testWord: Word? = null
    var stateUncovered: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val message = intent.getStringExtra(EXTRA_MESSAGE)
        if (message == "finito") this.finish()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                10
            )
        } else {

            getQuestion()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            10 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getQuestion()


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun getQuestion() {

        if (words == null) {
            words = WordsCollection.create("data.csv", "import.txt")
        }

        testWord = words?.getWord()


        if (testWord == null) onBackPressed()

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
        val btn1 = findViewById<View>(R.id.button1) as Button
        val btn2 = findViewById<View>(R.id.button2) as Button
        val btn3 = findViewById<View>(R.id.button3) as Button
        val btn4 = findViewById<View>(R.id.button4) as Button
        val btn5 = findViewById<View>(R.id.button5) as Button
        val btnShow = findViewById<View>(R.id.buttonShow) as Button
        val btnRemove = findViewById<View>(R.id.buttonRemove) as Button

        btnRemove.setEnabled(false)

        if (how == StateButtons.BEFORE_UNCOVER) {
            btn1.setEnabled(false)
            btn2.setEnabled(false)
            btn3.setEnabled(false)
            btn4.setEnabled(false)
            btn5.setEnabled(false)
            btnShow.setEnabled(true)
        }

        if (how == StateButtons.AFTER_UNCOVER) {
            btn1.setEnabled(true)
            btn2.setEnabled(true)
            btn3.setEnabled(true)
            btn4.setEnabled(true)
            btn5.setEnabled(true)
            btnShow.setEnabled(false)
        }


    }

    fun onSwapUncover(view: View) {
        if (stateUncovered == 1) {
            val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
                text =  testWord?.pronunciation + "\n---\n" +  testWord?.answer
            }
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

    fun onUncover(view: View) {
        stateUncovered = 1
        val a_textView = findViewById<TextView>(R.id.answerTextView).apply {
            text = testWord?.answer + "\n---\n" + testWord?.pronunciation
        }
        setButtons(StateButtons.AFTER_UNCOVER)
    }

    fun onGrade1(view: View) {
        words?.evaluateWord(testWord!!,  1)
        stateUncovered = 0
        getQuestion()
    }
    fun onGrade2(view: View) {
        words?.evaluateWord(testWord!!,  2)
        stateUncovered = 0
        getQuestion()
    }
    fun onGrade3(view: View) {
        words?.evaluateWord(testWord!!,  3)
        stateUncovered = 0
        getQuestion()
    }
    fun onGrade4(view: View) {
        words?.evaluateWord(testWord!!,  4)
        stateUncovered = 0
        getQuestion()
    }
    fun onGrade5(view: View) {
        words?.evaluateWord(testWord!!,  5)
        stateUncovered = 0
        getQuestion()
    }

    fun onEndButton(view: View) {


            val intent = Intent(this, TrainActivity::class.java).apply {
                //putExtra(EXTRA_MESSAGE, words)
            }
            //clears all previous activity(s) and start new activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
    }

    fun onRemoveButton(view: View) {

    }


    override fun onBackPressed(){

        Toast.makeText(getApplicationContext(), "Saving the data... ", Toast.LENGTH_SHORT).show()
        words?.saveIntoCsv("data.csv")

        this.finish()
    }

}
