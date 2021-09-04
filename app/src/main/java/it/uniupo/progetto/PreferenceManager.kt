package it.uniupo.progetto

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class PreferenceManager ( )  : AppCompatActivity() {
    private var  IS_FIRST_TIME_LAUNCH :String = "IsFirstTimeLaunch"
    private  var pref : SharedPreferences
    private  var editor : SharedPreferences.Editor

    init{
        pref = getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    fun setFirstTimeLaunch(isFirstTime : Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    fun  isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
