package calculotprototype.g14.cmpt276.calculot_prototype.calcGame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import calculotprototype.g14.cmpt276.calculot_prototype.Classes.User;
import calculotprototype.g14.cmpt276.calculot_prototype.Databases.UserDatabaseHelper;
import calculotprototype.g14.cmpt276.calculot_prototype.R;
import calculotprototype.g14.cmpt276.calculot_prototype.VectorGame.VectorGameActivity;

public class GameMenu extends AppCompatActivity {
    int VectorGameLvl = 1;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        TextView goToCalcGame = (TextView)findViewById(R.id.gameMenu_Defense);
        TextView goToTrigGame = (TextView)findViewById(R.id.gameMenu_CrystalBall);

        goToCalcGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent initCalcGame = new Intent(GameMenu.this,GameDiff.class);
                startActivity(initCalcGame);
            }
        });


        //getting vectorgamelvl
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
        String username = pref.getString("username",null); //Gets current logged in username from SharedPreferences

        UserDatabaseHelper DB = new UserDatabaseHelper(this);
        user = DB.getUser(username); //Gets User object from database (includes all user info)
        VectorGameLvl = user.getVectorLvl();
        // Do the same for goToTrigGame
        goToTrigGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent initVectorGame = new Intent(GameMenu.this,VectorGameActivity.class);
                initVectorGame.putExtra("Difficulty",1);    //Medium
                initVectorGame.putExtra("EasyLevel",1);
                initVectorGame.putExtra("MediumLevel",VectorGameLvl);
                initVectorGame.putExtra("HardLevel",1);
                startActivity(initVectorGame);
            }
        });
    }
}
