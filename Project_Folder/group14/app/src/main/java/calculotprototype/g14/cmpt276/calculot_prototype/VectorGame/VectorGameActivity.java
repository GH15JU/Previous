package calculotprototype.g14.cmpt276.calculot_prototype.VectorGame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import calculotprototype.g14.cmpt276.calculot_prototype.Classes.ClockVector;
import calculotprototype.g14.cmpt276.calculot_prototype.Classes.CrystalBall;
import calculotprototype.g14.cmpt276.calculot_prototype.Classes.VectorQuestionGenerator;
import calculotprototype.g14.cmpt276.calculot_prototype.R;
import calculotprototype.g14.cmpt276.calculot_prototype.calcGame.GameOverActivity;

import static android.graphics.Paint.Style.FILL;

//kza21
public class VectorGameActivity extends AppCompatActivity {

    Intent gameOver;

    //Game Activity Data: Difficulty, Level, Shells, Points in Shell, Potential gain/loss, change in XP, timer
    int Difficulty; //Choose Easy, Medium, or Hard
    int EasyLevel;
    int MediumLevel;
    int HardLevel;

    VectorQuestionGenerator TheGenerator;
    CrystalBall TheCrystal;

    //Game View
    RelativeLayout GameView;
    int GameHeight;
    int GameWidth;
    int GameXOrigin;
    int GameYOrigin;
    Bitmap BMGrid;
    Bitmap BMQuestionVector;
    Bitmap BMClockVector; //includes potential gain
    Bitmap BMCrystalBall;
    Bitmap BMShellOutline;
    Paint BlackPaint;
    Paint ShellPaint;
    Paint PotentialGainPaint;
    Paint PotentialLossPaint;
    Paint EraserPaint;
    Canvas GridCanvas;
    Canvas ShellCanvas;
    Canvas QuestionVectorCanvas;
    Canvas ClockVectorCanvas;
    Canvas ShellOutline;
    ImageView GridImage;
    ImageView ShellImage;
    ImageView QuestionVectorImage;
    ImageView ClockVectorImage;
    ImageView ShellOutlineImage;

    //Clock Vector
    ClockVector clockVector;

    //Multiple Choice: Question, QuestionInfo, (2-5) Answer choices
    LinearLayout MultipleChoice;
    String[] AnswerArray;
    TextView[] ChoiceArray; //Index: 0 - Question, 1 - QuestionInfo, 2 and beyond correspond to the AnswerArray index i+2; unneeded?
    int AnswerArraySize;
    int AnswerArrayIndex;
    int AnswerChoices;      //number of answer choices left -> decrement by 1 if incorrect answer chosen -> if 1 then fail current question
    String Question;
    String QuestionInfo;

    TextView addQuestion;
    TextView addQuestionInfo;

    //Game Info: Timer, Level, Shell Level, etc.
    LinearLayout GameInfoTop;
    LinearLayout GameInfoBot;
    CountDownTimer Timer;
    TextView TextTimer;
    float TextTime = 0;       //the time left for the current question vector
    int QuestionTime;       //the time for each question at that particular level
    TextView TextLevel;     //the level textview
    int Level;
    int DecreaseAmount;  //+180 when a user picks the wrong option -> if >0, the user input is frozen and points/time decrease rapidly
    TextView ShellLevel;
    TextView PotentialText;
    int TimeFlash = 5;

    // set toast for right/wrong answer
    Toast wrongAnswer;
    Toast rightAnswer;

    //Question points
    int Shell;              //base shell level 1
    int MaxShell;           //the shell level required to progress to the next level
    int ShellPoints;        //Points in total
    int TotalGain = 0;      //XP to be gained
    TextView TextTotalGain; //display total XP to be gained
    double PotentialGain = 0;      //current question potential change in points: -360 to 360 -> upon reaching less than -360, fail current question
    final int BaseGainAmount = 100; //Theoretical maximum points per question is 100 * multiplier
    double ScoreMultiplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vectorgame);

        gameOver = new Intent(VectorGameActivity.this, GameOverActivity.class);

        //Retrieve Difficulty and Respective Levels
        Difficulty = getIntent().getIntExtra("Difficulty", 1);
        EasyLevel = getIntent().getIntExtra("EasyLevel", 1);
        MediumLevel = getIntent().getIntExtra("MediumLevel", 1);
        HardLevel = getIntent().getIntExtra("HardLevel", 1);

        //Setup the Question Generator
        TheGenerator = new VectorQuestionGenerator(Difficulty, EasyLevel, MediumLevel, HardLevel);
        TheCrystal = TheGenerator.getCrystalBall();

        ShellPoints = (int) TheCrystal.getMass();
        MaxShell = TheCrystal.getShellLevelMax();

        //Layouts
        MultipleChoice = (LinearLayout) findViewById(R.id.vectorMultipleChoiceLayout);
        GameInfoTop = (LinearLayout) findViewById(R.id.vectorGameTopInfoLayout);
        GameInfoBot = (LinearLayout) findViewById(R.id.vectorGameBottomInfoLayout);
        GameView = (RelativeLayout) findViewById(R.id.crystalBallLayout);

        //GameView Dimensions
        GameWidth = TheGenerator.getHalfWidth() * 2;
        GameHeight = TheGenerator.getHalfHeight() * 2;
        GameXOrigin = TheGenerator.getHalfWidth();
        GameYOrigin = TheGenerator.getHalfHeight();

        //Toasts -> add potential gain?
        wrongAnswer = Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT);
        rightAnswer = Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);


        //Setup GameInfoTop Textviews
        setBlackPaint(false);
        drawGameInfo();

        //Initialize Grid View
        setupDrawGrid();

        //Shells
        setupDrawShells();
        drawShells();
        //------

        setupDrawQuestionVector();
        setupEraserPaint();
        setupDrawClockVector();
        startQuestion();
    }

    //Level Methods
    private int getLevel() {
        if (Difficulty == 0)
            return EasyLevel;
        else if (Difficulty == 1)
            return MediumLevel;
        else return HardLevel;
    }

    private void changeLevel(int _changelevel) {
        if (Difficulty == 0) {
            EasyLevel = _changelevel;
            TheGenerator.setEasyLevel(EasyLevel);
        }
        else if (Difficulty == 1) {
            MediumLevel = _changelevel;
            TheGenerator.setMediumLevel(MediumLevel);
        }
        else {
            HardLevel = _changelevel;
            TheGenerator.setHardLevel(HardLevel);
        }

        Level = getLevel();
        TextLevel.setText("Level: "+String.valueOf(Level));
    }

    private void goToGameOver() {
        gameOver.putExtra("xp", TotalGain*2);   //since gameoveractivity divides XP by 2
        gameOver.putExtra("game", 1);
        gameOver.putExtra("vectorlvl", MediumLevel);
        Timer.cancel();
        finish();   //pop this activity off the stack
        startActivity(gameOver);
    }

    //Question Methods
    private void startTimer() {
        //stop timer if the potential gain results in a shell mass <= 0
        // Set up countdown timer depending on difficulty
        TextTime = QuestionTime;
        updateDrawTimer();

        //temporary
        PotentialGain = (double) Math.round(TextTime / QuestionTime * BaseGainAmount * ScoreMultiplier); //do calculations when drawing instead?
        final double PotentialGainDecrement = -( 0.05 * BaseGainAmount * ScoreMultiplier / QuestionTime);
        updateDrawPotential();
        Timer = new CountDownTimer(QuestionTime * 2 * 1000, 50) {   //every 20th of a second
            @Override
            public void onTick(long millisUntilFinished) {
                TextTime -= 0.05;
                updateDrawTimer();

                PotentialGain += PotentialGainDecrement;

                updateDrawPotential();
                clockVector.incrementAngle();
                drawClockVector();

                /*Early termination
                if (ShellPoints+(int)PotentialGain <= 0) {
                    //TextTime = -QuestionTime;
                    Timer.cancel();
                    TheCrystal.changeMass(- ((int) TheCrystal.getMass()) );
                    ShellPoints = 0;

                    testShellPoints();
                }*/
            }

            @Override
            public void onFinish() {
                //game over scenario
                //TextTime = -QuestionTime;
                if((MultipleChoice).getChildCount() > 0)
                    (MultipleChoice).removeAllViews();
                MultipleChoice.invalidate();

                Timer.cancel();
                TheCrystal.changeMass(-360);
                ShellPoints = (int) TheCrystal.getMass();

                drawShells();
                testShellPoints();
            }
        };
        Timer.start();
    }

    private void startQuestion() {
        TheGenerator.generateQuestion();
        clockVector = TheGenerator.getClockVector();
        ScoreMultiplier = TheGenerator.getScoreMultiplier();
        drawQuestionVector();
        drawClockVector();
        drawGridLabels();

        //Multiple Choice: Question, QuestionInfo, (2-7) Answer choices depending on difficulty
        AnswerArray = TheGenerator.getAnswerArray();
        AnswerArraySize = TheGenerator.getAnswerArraySize();
        AnswerArrayIndex = TheGenerator.getAnswerArrayIndex();
        Question = TheGenerator.getQuestion();
        QuestionInfo = TheGenerator.getQuestionInfo();

        QuestionTime = TheGenerator.getQuestionTime();

        TextView addQuestion = new TextView(this);
        TextView addQuestionInfo = new TextView(this);

        addQuestion.setText(Question);
        addQuestionInfo.setText(QuestionInfo);

        addQuestion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        addQuestionInfo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addQuestion.setTextColor(getResources().getColor(R.color.colorGreen));
        addQuestionInfo.setTextColor(getResources().getColor(R.color.colorGreen));

        MultipleChoice.addView(addQuestion);
        MultipleChoice.addView(addQuestionInfo);

        for (int i = 0; i < AnswerArraySize; i++) {
            //need to set onclick listeners for correct/incorrect answers
            TextView addText = new TextView(this);  //scope of addText is within this for loop
            addText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addText.setTextSize(17);
            addText.setText(AnswerArray[i]);

            if (i == AnswerArrayIndex) {
                //right answer
                addText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //implement
                        rightAnswer.show();

                        Timer.cancel();
                        if((MultipleChoice).getChildCount() > 0)
                            (MultipleChoice).removeAllViews();
                        MultipleChoice.invalidate();

                        TheCrystal.changeMass(Math.round(TextTime/QuestionTime*360));
                        ShellPoints = (int) TheCrystal.getMass();
                        drawShells();

                        TotalGain += (int) PotentialGain;

                        updateDrawTotalGain();
                        testShellPoints();
                    }
                });
            }
            else {
                //wrong answer
                addText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //implement
                        wrongAnswer.show();

                        Timer.cancel();
                        if((MultipleChoice).getChildCount() > 0)
                            (MultipleChoice).removeAllViews();
                        MultipleChoice.invalidate();

                        //temporary -> instant fail
                        TheCrystal.changeMass(-360);
                        ShellPoints = (int) TheCrystal.getMass();
                        drawShells();

                        //totalgain?
                        //Timer.onFinish();
                        //Timer.cancel();
                        testShellPoints();
                    }
                });
            }

            MultipleChoice.addView(addText);
        }

        startTimer();
    }

    public void testShellPoints() { //next level or fail level
        if (ShellPoints <= 0)
            goToGameOver();
        else if ( Math.floor(ShellPoints / 360) >= MaxShell) {
            changeLevel( getLevel() + 1 );  //increment level by 1
            TheCrystal = TheGenerator.getCrystalBall();

            ShellPoints = (int) TheCrystal.getMass();
            MaxShell = TheCrystal.getShellLevelMax();

            updateDrawShellLevel();
            drawShells();

            startQuestion();
        }
        else {
            updateDrawShellLevel();
            startQuestion();
        }
    }

    //Draw Methods
    private void updateDrawTimer() {
        if (TextTime>=0) {
            TextTimer.setTextColor(getResources().getColor(R.color.colorAccent));
            TextTimer.setText("Time: "+String.format("%.1f",TextTime)+"/"+String.valueOf(QuestionTime));
        }
        else {
            if (TextTime >= 0) {
                TextTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
                TextTimer.setText("\t\tPot'l XP: "+String.format("%.0f", PotentialGain)+" (+"+String.format("%.0f", Math.floor( TextTime * 100 / QuestionTime ))+"%)" );
            }
            else {
                if (TimeFlash>-5)
                    TimeFlash--;
                else TimeFlash = 5;

                if (TimeFlash >= 0)   //0 to 4
                    TextTimer.setTextColor(getResources().getColor(R.color.colorRed));
                else TextTimer.setTextColor(getResources().getColor(R.color.colorAccent)); //-1 to -5
                TextTimer.setText("Time: " + String.format("%.1f", TextTime) + "/" + String.valueOf(QuestionTime));
            }
        }
    }

    private void updateDrawShellLevel() {
        Shell = TheCrystal.getShellLevel();
        ShellLevel.setText("\t\tShell: "+String.valueOf(Shell)+"/"+String.valueOf(MaxShell)+" (+"+String.format("%.0f", Math.floor( (float)(ShellPoints-360*Shell)/3.6))+"%)" );
    }

    private void updateDrawTotalGain() {
        TextTotalGain.setText("\t\tTotal XP+: "+String.valueOf(TotalGain));
    }

    private void updateDrawPotential() {
        if (TextTime >= 0) {
            PotentialText.setTextColor(getResources().getColor(R.color.colorPrimary));
            PotentialText.setText("\t\tPot'l XP: "+String.format("%.0f", PotentialGain)+" (+"+String.format("%.0f", Math.floor( TextTime * 100 / QuestionTime ))+"%)" );
        }
        else {
            if (TimeFlash>=0)   //0 to 4
                PotentialText.setTextColor(getResources().getColor(R.color.colorAccent));
            else PotentialText.setTextColor(getResources().getColor(R.color.colorRed)); //-1 to -5
            PotentialText.setText("\t\tPot'l XP: "+String.format("%.0f", PotentialGain)+" ("+String.format("%.0f", Math.floor( TextTime * 100 / QuestionTime ))+"%)" );
        }
    }

    private void setBlackPaint(boolean _declared) {
        if (!_declared) {
            BlackPaint = new Paint();
            BlackPaint.setColor(Color.BLACK);
        }
        BlackPaint.setTextSize(20);
        BlackPaint.setTextAlign(Paint.Align.LEFT);
        BlackPaint.setStrokeWidth(3);
    }

    private void setupEraserPaint() {
        EraserPaint = new Paint();
        EraserPaint.setColor(Color.BLUE);
    }

    private void setupDrawGrid() {
        BMGrid = Bitmap.createBitmap(GameWidth, GameHeight, Bitmap.Config.ARGB_8888);
        GridCanvas = new Canvas(BMGrid);

        GridImage = new ImageView(this);
        GridImage.setImageBitmap(BMGrid);
        GameView.addView(GridImage);

        drawGridLabels();
    }

    private void drawGridLabels() {
        BMGrid.eraseColor(Color.TRANSPARENT);
        GridCanvas.drawLine(0, GameYOrigin, GameWidth, GameYOrigin, BlackPaint);  //x axis
        GridCanvas.drawLine(GameXOrigin, 0, GameXOrigin, GameHeight, BlackPaint);  //y axis

        int XCoordinate = TheGenerator.getX();
        int YCoordinate = TheGenerator.getY();
        //labels
        BlackPaint.setTextSize(18);
        BlackPaint.setStrokeWidth(1);
        BlackPaint.setTextAlign(Paint.Align.RIGHT);
        GridCanvas.drawText("Img./Y-axis", GameXOrigin - 2, 20, BlackPaint);

        //GridCanvas.drawLine(XCoordinate, GameYOrigin-5, XCoordinate, GameYOrigin+5, BlackPaint);    //x mark on x axis
        //GridCanvas.drawLine(GameXOrigin-5, YCoordinate, GameXOrigin+5, YCoordinate, BlackPaint);    // y mark on y axis

        if (XCoordinate<0) {
            GridCanvas.drawText("Real/X-axis", GameWidth, GameYOrigin + 20, BlackPaint);    //String.valueOf(XCoordinate)
        }
        else {
            BlackPaint.setTextAlign(Paint.Align.LEFT);
            GridCanvas.drawText("Real/X-axis", 2, GameYOrigin + 20, BlackPaint);
        }
        setBlackPaint(true);
    }

    private void drawGameInfo() {
        //Top Info

        //TextView for Level
        TextLevel = new TextView(this);
        TextLevel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Level = getLevel();
        TextLevel.setText("Level "+String.valueOf(Level));
        TextLevel.setTextColor(getResources().getColor(R.color.colorPrimary));

        GameInfoTop.addView(TextLevel);

        //TextView for Shell Level
        ShellLevel = new TextView(this);
        ShellLevel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ShellLevel.setTextColor(getResources().getColor(R.color.colorPrimary));
        updateDrawShellLevel();

        GameInfoTop.addView(ShellLevel);

        //TextView for total XP gain
        TextTotalGain = new TextView(this);
        TextTotalGain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        updateDrawTotalGain();
        TextTotalGain.setTextColor(getResources().getColor(R.color.colorPrimary));

        GameInfoTop.addView(TextTotalGain);

        //Bottom View

        //TextView for Timer
        TextTimer = new TextView(this);
        TextTimer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //TextTimer.setText("Time: "+String.format("%.1f",String.valueOf(TextTime))+"/"+String.valueOf(QuestionTime));
        TextTimer.setTextColor(getResources().getColor(R.color.colorAccent));

        GameInfoBot.addView(TextTimer);

        //TextView for Potential -> % of shell or XP
        PotentialText = new TextView(this);
        PotentialText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        updateDrawPotential();
        PotentialText.setTextColor(getResources().getColor(R.color.colorPrimary));

        GameInfoBot.addView(PotentialText);
    }

    private void setupDrawShells() {
        ShellPaint = new Paint();
        ShellPaint.setColor(Color.BLUE);
        //ShellPaint.setAlpha(160);
        ShellPaint.setStrokeWidth(1);
        ShellPaint.setStyle(FILL);

        BMCrystalBall = Bitmap.createBitmap(GameWidth, GameHeight, Bitmap.Config.ARGB_8888);
        ShellCanvas = new Canvas(BMCrystalBall);

        ShellImage = new ImageView(this);
        ShellImage.setImageBitmap(BMCrystalBall);
        GameView.addView(ShellImage);

        setupDrawShellOutline();
    }

    private void setupDrawShellOutline() {
        BMShellOutline = Bitmap.createBitmap(GameWidth, GameHeight, Bitmap.Config.ARGB_8888);
        ShellOutline = new Canvas(BMShellOutline);

        ShellOutlineImage = new ImageView(this);
        ShellOutlineImage.setImageBitmap(BMShellOutline);
        GameView.addView(ShellOutlineImage);
    }

    private void setupDrawPotentialShell() {
        PotentialGainPaint = new Paint();
        PotentialGainPaint.setColor(Color.BLUE);
        PotentialGainPaint.setAlpha(128);
        PotentialGainPaint.setStrokeWidth(1);
        PotentialGainPaint.setStyle(FILL);

        PotentialLossPaint = new Paint();
        PotentialLossPaint.setColor(Color.RED);
        PotentialLossPaint.setAlpha(160);
        PotentialLossPaint.setStrokeWidth(1);
        PotentialLossPaint.setStyle(FILL);
    }

    private void setupDrawClockVector() {
        setupDrawPotentialShell();
        BMClockVector = Bitmap.createBitmap(GameWidth, GameHeight, Bitmap.Config.ARGB_8888);
        ClockVectorCanvas = new Canvas(BMClockVector);

        ClockVectorImage = new ImageView(this);
        ClockVectorImage.setImageBitmap(BMClockVector);
        GameView.addView(ClockVectorImage);
    }

    private void drawShells() {
        BMCrystalBall.eraseColor(Color.TRANSPARENT);
        ShellCanvas.drawCircle(GameXOrigin, GameYOrigin, (TheCrystal.getMass()/360) * TheCrystal.getShellWidth(), ShellPaint);

        drawShellOutline();
        refreshGameView();
    }

    private void drawShellOutline() {
        BMShellOutline.eraseColor(Color.TRANSPARENT);

        BlackPaint.setStyle(Paint.Style.STROKE);
        BlackPaint.setStrokeWidth(1);   //temporary use of thinner black paint
        for (int i=1; i<=TheCrystal.getShellLevel(); i++) {
            ShellOutline.drawCircle(GameXOrigin, GameYOrigin, i * TheCrystal.getShellWidth(), BlackPaint);
        }
        setBlackPaint(true);    //reset black paint settings
    }

    private void setupDrawQuestionVector() {
        BMQuestionVector = Bitmap.createBitmap(GameWidth, GameHeight, Bitmap.Config.ARGB_8888);
        QuestionVectorCanvas = new Canvas(BMQuestionVector);

        QuestionVectorImage = new ImageView(this);
        QuestionVectorImage.setImageBitmap(BMQuestionVector);
        GameView.addView(QuestionVectorImage);
    }

    private void drawQuestionVector() {
        //draw
        BMQuestionVector.eraseColor(Color.TRANSPARENT);
        QuestionVectorCanvas.drawLine(GameXOrigin, GameYOrigin, GameXOrigin + TheGenerator.getX(), GameYOrigin - TheGenerator.getY(), BlackPaint);

        BlackPaint.setTextSize(20);
        BlackPaint.setStrokeWidth(2);
        BlackPaint.setTextAlign(Paint.Align.CENTER);
        double Angle = Math.atan2(TheGenerator.getY(), TheGenerator.getX());
        int XCoord = TheGenerator.getX() + (int) Math.round(Math.cos(Angle) * 15);
        int YCoord = TheGenerator.getY() + (int) Math.round(Math.sin(Angle) * 15);
        QuestionVectorCanvas.drawText("V", GameXOrigin + XCoord, GameYOrigin - YCoord, BlackPaint);
        setBlackPaint(true);

        GameView.invalidate();
    }

    private void drawClockVector() {
        //erase
        BMClockVector.eraseColor(Color.TRANSPARENT);

        //draw shells
        RectF ClockRect = new RectF(GameXOrigin - clockVector.getNorm(),
                GameYOrigin - clockVector.getNorm(),
                GameXOrigin + clockVector.getNorm(),
                GameYOrigin + clockVector.getNorm());

        if (PotentialGain>=0) {
            ClockVectorCanvas.drawArc(ClockRect,
                    clockVector.getCurrentTheta(),
                    ((360-clockVector.getPotentialGainAngle()) % 360),
                    true,
                    PotentialGainPaint
                    );
        }
        else {
            ClockVectorCanvas.drawArc(ClockRect,
                    clockVector.getStartTheta(),
                    (clockVector.getPotentialGainAngle() % 360),
                    true,
                    PotentialLossPaint
            );

            //erase middle of potential loss arc to make potential loss only one shell layer thick
            RectF ClockInnerRect = new RectF(GameXOrigin - clockVector.getInnerRadius(),
                    GameYOrigin - clockVector.getInnerRadius(),
                    GameXOrigin + clockVector.getInnerRadius(),
                    GameYOrigin + clockVector.getInnerRadius());

            ClockVectorCanvas.drawArc(ClockInnerRect,
                    clockVector.getStartTheta(),
                    (clockVector.getPotentialGainAngle() % 360),
                    true,
                    EraserPaint
            );
        }

        //draw clock vector
        ClockVectorCanvas.drawLine(GameXOrigin, GameYOrigin, GameXOrigin + clockVector.getX(), GameYOrigin + clockVector.getY(), BlackPaint);
        //draw text label
        BlackPaint.setTextSize(20);
        BlackPaint.setStrokeWidth(1);
        BlackPaint.setTextAlign(Paint.Align.CENTER);
        double Angle = Math.atan2(clockVector.getY(), clockVector.getX());
        int XCoord = Math.round(clockVector.getX()) + (int) Math.round(Math.cos(Angle) * 15);
        int YCoord = Math.round(clockVector.getY()) + (int) Math.round(Math.sin(Angle) * 15);
        ClockVectorCanvas.drawText("CV", GameXOrigin + XCoord, GameYOrigin + YCoord, BlackPaint);
        setBlackPaint(true);

        refreshGameView();
    }

    private void refreshGameView() {
        GameView.bringChildToFront(ClockVectorImage);
        GameView.bringChildToFront(ShellOutlineImage);
        GameView.bringChildToFront(QuestionVectorImage);
        GameView.bringChildToFront(GridImage);
        GameView.invalidate();
    }

    @Override
    public void onBackPressed() {
        //finish activities to avoid large stack of activities?
        goToGameOver(); //call goToGameOver() which cancels the timer
    }
}