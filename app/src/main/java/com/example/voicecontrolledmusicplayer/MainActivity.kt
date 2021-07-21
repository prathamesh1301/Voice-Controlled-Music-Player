package com.example.voicecontrolledmusicplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var parentRelativeLayout:RelativeLayout?=null
    private var speechRecognizer:SpeechRecognizer?=null
    private var speechRecognizerIntent:Intent?=null
    private var keeper=""
    private var mode="ON"
    public lateinit var mediaPlayer: MediaPlayer
    var songNameTxt=""
    var position=0
    lateinit var allSongs:ArrayList<File>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        voiceEnableBtn.setOnClickListener {
            if(mode.equals("ON")){
                mode="OFF"
                voiceEnableBtn.text="Voice Enabled: OFF"
                lower.visibility=View.VISIBLE
            }else{
                mode="ON"
                voiceEnableBtn.text="Voice Enabled: ON"
                lower.visibility=View.GONE
            }
        }

        pauseBtn.setOnClickListener{
            playPauseFun()
        }

        nextBtn.setOnClickListener {
            if(mediaPlayer.currentPosition>0){
                nextSong()
            }
        }
        previousBtn.setOnClickListener {
            if(mediaPlayer.currentPosition>0){
                previousSong()
            }
        }


        parentRelativeLayout=findViewById(R.id.parentRelativeLayout)
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizerIntent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())

        getValuesFromIntent()


        parentRelativeLayout!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when(event?.action){
                    MotionEvent.ACTION_DOWN->{
                        speechRecognizer?.startListening(speechRecognizerIntent)
                        keeper=""

                    }

                    MotionEvent.ACTION_UP->{
                        speechRecognizer?.stopListening()
                    }
                }

                return false
            }
        })

        speechRecognizer!!.setRecognitionListener(object :RecognitionListener{
            override fun onReadyForSpeech(p0: Bundle?) {
                Log.d("main","main")
                println("hello")
            }

            override fun onBeginningOfSpeech() {

                Log.d("main","main")
                println("hello")
            }

            override fun onRmsChanged(p0: Float) {

                Log.d("main","main")
                println("hello")
            }

            override fun onBufferReceived(p0: ByteArray?) {

                Log.d("main","main")
                println("hello")
            }

            override fun onEndOfSpeech() {

                Log.d("main","main")
            }

            override fun onError(p0: Int) {
                Toast.makeText(applicationContext,"Error $p0",Toast.LENGTH_SHORT).show()
            }

            override fun onResults(p0: Bundle?) {
                val resultList=p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(resultList!=null){
                    if(mode.equals("ON")){
                        keeper= resultList.get(0) as String
                        if(keeper.equals("pause the song")){
                            playPauseFun()
                            Toast.makeText(applicationContext,"Command- $keeper",Toast.LENGTH_SHORT).show()
                        }
                        else if(keeper.equals("play the song")){
                            playPauseFun()
                            Toast.makeText(applicationContext,"Command- $keeper",Toast.LENGTH_SHORT).show()


                        }
                        else if(keeper.equals("next song")){
                            nextSong()
                            Toast.makeText(applicationContext,"Command- $keeper",Toast.LENGTH_SHORT).show()

                        }
                        else if(keeper.equals("previous song")){
                            previousSong()
                            Toast.makeText(applicationContext,"Command- $keeper",Toast.LENGTH_SHORT).show()

                        }
                    }
                }

            }

            override fun onPartialResults(p0: Bundle?) {

                Log.d("main","main")
                println("hello")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {

                Log.d("main","main")
                println("hello")
            }

        })
    }
    private fun checkPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if((ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.RECORD_AUDIO))!=PackageManager.PERMISSION_GRANTED){
                val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+packageName))
                startActivity(intent)
            }
        }
    }

    private fun getValuesFromIntent(){
        songNameTxt= intent.getStringExtra("songName").toString()
        position=intent.getIntExtra("position",0)
        allSongs=(intent.getSerializableExtra("allSongs") as ArrayList<File>)

        songName.text=songNameTxt
        val uri=Uri.parse(allSongs.get(position).toString())
        mediaPlayer=MediaPlayer.create(this@MainActivity,uri)
        mediaPlayer.start()
    }


    private fun playPauseFun(){
        logo.setImageResource(R.drawable.four)
        if(mediaPlayer.isPlaying){
            pauseBtn.setImageResource(R.drawable.play)
            mediaPlayer.pause()
        }
        else{
            pauseBtn.setImageResource(R.drawable.pause)
            mediaPlayer.start()
            logo.setImageResource(R.drawable.five)
        }
    }

    private fun nextSong(){
        mediaPlayer.pause()
        mediaPlayer.stop()
        mediaPlayer.release()

        position=((position+1)%allSongs.size)
        val uri=Uri.parse(allSongs.get(position).toString())
        mediaPlayer=MediaPlayer.create(this@MainActivity,uri)
        songNameTxt=allSongs.get(position).name
        mediaPlayer.start()
        songName.text=songNameTxt

        logo.setImageResource(R.drawable.three)
        if(mediaPlayer.isPlaying){
            pauseBtn.setImageResource(R.drawable.pause)

        }
        else{
            pauseBtn.setImageResource(R.drawable.play)

            logo.setImageResource(R.drawable.five)
        }

    }

    private fun previousSong(){
        mediaPlayer.pause()
        mediaPlayer.stop()
        mediaPlayer.release()

        position=position-1
        if(position<0)
            position=allSongs.size-1
        val uri=Uri.parse(allSongs.get(position).toString())
        mediaPlayer=MediaPlayer.create(this@MainActivity,uri)
        songNameTxt=allSongs.get(position).name
        mediaPlayer.start()
        songName.text=songNameTxt

        logo.setImageResource(R.drawable.two)
        if(mediaPlayer.isPlaying){
            pauseBtn.setImageResource(R.drawable.pause)

        }
        else{
            pauseBtn.setImageResource(R.drawable.play)

            logo.setImageResource(R.drawable.five)
        }
    }
}