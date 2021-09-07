package it.uniupo.progetto

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class PreferenceManager ( )  : AppCompatActivity() {
    private  var pref : SharedPreferences
    private  var editor : SharedPreferences.Editor

    init{
        pref = getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()
    }

}
