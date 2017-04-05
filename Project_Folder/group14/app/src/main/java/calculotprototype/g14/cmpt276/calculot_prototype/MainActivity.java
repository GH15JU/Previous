package calculotprototype.g14.cmpt276.calculot_prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import calculotprototype.g14.cmpt276.calculot_prototype.Databases.UserDatabaseHelper;


public class MainActivity extends AppCompatActivity {

    //MainActivity will act as the splashscreen, as well as to load anything you need to do before main menu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        splashy();
        //Code here will run during splashscreen

}

    public void splashy(){
        Log.i("splashy","Splashy started");
        TextView title = (TextView) findViewById(R.id.splashscreen_title); //Get reference to Splashscreen Title
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in); //Get reference to fade_in animation
        title.startAnimation(fadein); //Fade title in

        fadein.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {} //Don't delete, auto-generated
            public void onAnimationEnd(Animation animation) {
                //Check if user is already logged in
                //SharedPreferences preferences = getSharedPreferences("MyPref", 0);
                //SharedPreferences.Editor editor = preferences.edit();
                //editor.putString("username",null);
                //editor.commit();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                String username = pref.getString("username",null); //Gets current logged in username from SharedPreferences

                Intent afterSplashscreen;
                if (username != null){ //If user logged in, bypass main menu
                    afterSplashscreen = new Intent(MainActivity.this, WhatToDo.class);
                }
                else afterSplashscreen = new Intent(MainActivity.this, MainMenu.class);

                startActivity(afterSplashscreen);
                finish(); //Ends splash screen, so you can't go back to it
                Log.i("splashy","Fade in finished");
            }
            public void onAnimationRepeat(Animation animation) {} //Don't delete, auto-generated
        });
    }
}