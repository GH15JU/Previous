package calculotprototype.g14.cmpt276.calculot_prototype;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import calculotprototype.g14.cmpt276.calculot_prototype.Classes.CalcQuestion;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Health, XPGained, Level, Completed
        final int[] info = {5,0,1,0};
        int topic = 0;
        final CalcQuestion calc = new CalcQuestion(topic,1);


        // Set Initial TextViews
        final TextView healthfield = (TextView) findViewById(R.id.game_health);
        final TextView xpfield = (TextView) findViewById(R.id.game_xpgained);

        healthfield.setText("Health: " + Integer.toString(info[0]));
        xpfield.setText("XP Gained: " + Integer.toString(info[1]));

        // Wire up all of the TextViews
        final TextView question = (TextView) findViewById(R.id.game_question);
        final TextView answer1 = (TextView) findViewById(R.id.game_answer1);
        final TextView answer2 = (TextView) findViewById(R.id.game_answer2);
        final TextView answer3 = (TextView) findViewById(R.id.game_answer3);
        final TextView answer4 = (TextView) findViewById(R.id.game_answer4);
        final Intent gameOver = new Intent(GameActivity.this, GameOverActivity.class);

        // Set up countdown timer ( 5 seconds currently - 1s = 1000ms)
        final CountDownTimer timer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                info[0]--;
                setQuestion(question, answer1, answer2, answer3, answer4, calc, this);
                healthfield.setText("Health: " + Integer.toString(info[0]));
                if (info[0] == 0) {
                    startActivity(gameOver);
                }
            }
        };

       // Set the first answer field
        setQuestion(question, answer1, answer2, answer3, answer4, calc, timer);

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calc.isCorrect(answer1) == true) {
                    // Do something if correct
                    info[3]++;
                    checkLevel(info);

                    calc.getNewQuestion(0, info[2]);
                    setQuestion(question, answer1, answer2, answer3, answer4, calc,timer);
                } else {
                    // Do something else if incorrect
                    info[0]--;
                }
                xpfield.setText("XP Gained: " + Integer.toString(info[1]));
                healthfield.setText("Health: " + Integer.toString(info[0]));
                if(info[0]<=0){
                    startActivity(gameOver);
                }
            }
        });

        // Set up the second answer field
        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calc.isCorrect(answer2) == true) {
                    // Do something if correct
                    info[3]++;
                    checkLevel(info);

                    calc.getNewQuestion(0, info[2]);
                    setQuestion(question, answer1, answer2, answer3, answer4, calc,timer);
                } else {
                    // Do something else if wrong
                    info[0]--;
                }
                xpfield.setText("XP Gained: " + Integer.toString(info[1]));
                healthfield.setText("Health: " + Integer.toString(info[0]));
                if(info[0]<=0){
                    startActivity(gameOver);
                }
            }
        });

        // Set up the third answer field
        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calc.isCorrect(answer3) == true) {
                    // Do something if correct
                    info[3]++;
                    checkLevel(info);

                    calc.getNewQuestion(0, info[2]);
                    setQuestion(question, answer1, answer2, answer3, answer4, calc,timer);
                } else {
                    // Do something else if wrong
                    info[0]--;
                }
                xpfield.setText("XP Gained: " + Integer.toString(info[1]));
                healthfield.setText("Health: " + Integer.toString(info[0]));
                if(info[0]<=0){
                    startActivity(gameOver);
                }
            }
        });

        // Set up the fourth answer field
        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calc.isCorrect(answer4) == true) {
                    // Do something if correct
                    info[3]++;
                    checkLevel(info);

                    calc.getNewQuestion(0, info[2]);
                    setQuestion(question, answer1, answer2, answer3, answer4, calc,timer);
                } else {
                    // Do something else if wrong
                    info[0]--;
                }
                xpfield.setText("XP Gained: " + Integer.toString(info[1]));
                healthfield.setText("Health: " + Integer.toString(info[0]));
                if(info[0]<=0){
                    startActivity(gameOver);
                }
            }
        });


    }

    // Checks if the user can advance into the next level
    private boolean checkLevel(int[] info) {

        // End the game ( Level 3 and the user has answered 10 questions at level 3)
        if (info[2] == 3 && info[3] % 10 == 0) {
            info[1] += 1000;
            return true;
        }

        // If the user has answered 10 questions at the current level, give the user XP and advance into the next level
        if (info[3] % 10 == 0) {
            info[1]+= 100;
            info[2]++;
            if (info[2] == 2) { info[1] += 50; }
            if (info[2] == 3) { info[1] += 100;}

            return false;
        }

        return false;
    }


    // Sets up the randomly generated question and start the timer up;
    public void setQuestion(TextView question, TextView answer1, TextView answer2,
                            TextView answer3, TextView answer4, CalcQuestion c, CountDownTimer timer) {

        question.setText(c.getQuestion());
        answer1.setText(c.getAnswer_1());
        answer2.setText(c.getAnswer_2());
        answer3.setText(c.getAnswer_3());
        answer4.setText(c.getAnswer_4());

        timer.start();

    }
}