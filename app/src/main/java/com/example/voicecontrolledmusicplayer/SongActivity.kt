package com.example.voicecontrolledmusicplayer

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class SongActivity : AppCompatActivity(), PermissionListener {

    private lateinit var listView:ListView
    private var mode:String="ON"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        listView=findViewById(R.id.listView)
        getPermissionForStorage()


    }

    private fun getPermissionForStorage(){
        Dexter.withActivity(this@SongActivity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(this@SongActivity)
            .check()
    }

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        displaySongs()
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()
    }

    fun fetchSongs(file: File): ArrayList<File>? {
        val arrayList: ArrayList<File> = ArrayList()
        val songs = file.listFiles()
        if (songs != null) {
            for (myFiles in songs) {
                if (!myFiles.isHidden && myFiles.isDirectory) {
                    arrayList.addAll(fetchSongs(myFiles)!!)
                } else {
                    if (myFiles.name.endsWith(".mp3") && !myFiles.name.startsWith(".")) {
                        arrayList.add(myFiles)
                    }
                }
            }
        }
        return arrayList
    }

    private fun displaySongs(){

        val songs=fetchSongs(Environment.getStorageDirectory())
        val songNames:ArrayList<String> =  ArrayList()
        val len= songs!!.size-1
        for(i in 0..len){
            if (songs != null) {
                songNames.add(songs.get(i).name)
            }

        }
        val arrayAdapter=ArrayAdapter<String>(this@SongActivity,android.R.layout.simple_list_item_1,songNames)
        listView.adapter=arrayAdapter
        listView.setOnItemClickListener { adapterView, view, i, l ->
            val mainActivity=MainActivity()
            mainActivity.mediaPlayer= MediaPlayer()
            if(mainActivity.mediaPlayer.isPlaying){
                mainActivity.mediaPlayer.pause()
                mainActivity.mediaPlayer.stop()
                mainActivity.mediaPlayer.release()
            }

            val intent= Intent(this@SongActivity,MainActivity::class.java)
            intent.putExtra("songName",songNames[i])
            intent.putExtra("allSongs",songs)
            intent.putExtra("position",i)
            startActivity(intent)
        }
    }



}