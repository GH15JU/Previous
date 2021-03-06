package calculotprototype.g14.cmpt276.calculot_prototype.Classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import calculotprototype.g14.cmpt276.calculot_prototype.R;
import calculotprototype.g14.cmpt276.calculot_prototype.calcGame.GameOverActivity;


/**
 * Created by Jean Pierre on 2017-03-25.
 */

public class calcGameGraphics extends View {

    // ENVIROMENT MEMBERS
    Bitmap background;
    Bitmap castle_img;
    Paint paintFloor = new Paint();
    Paint paintSky = new Paint();
    int skyblue = getContext().getResources().getColor(R.color.skyblue);
    int grassgreen = getContext().getResources().getColor(R.color.grassgreen);
    int floor_right;
    int floor_bottom;
    int floor_top;
    int castle_top;
    int castle_width;
    int castle_height;

    // SCREEN MEMBERS
    int width;
    int height;

    // WIZARD MEMBERS
    int char_left;
    int char_top;
    int char_width;
    int char_height;
    Bitmap character_img;

    // MONSTER MEMBERS
    Monster monster;
    int monster_left;
    int monster_top;
    int spawn_left;
    int monster_width;
    int monster_height;
    Bitmap monster_img;

    // ARROW MEMBERS
    Bitmap arrow_img;
    int arrow_width;
    int arrow_height;

    // META MEMBERS
    Context c;
    int[] info;
    TextView hpfield;
    int userLevel;

    // Initialization
    public calcGameGraphics(Context context, Monster _monster, int[] _info,TextView _hpfield, int _Level) {
        super(context);
        c = context;
        monster = _monster;
        setAssets();
        spawn_left = width;
        monster.spawnMonster(spawn_left);
        info = _info;
        hpfield = _hpfield;
        userLevel = _Level;
    }

    // Drawing the game
    @Override
    public void onDraw(Canvas canvas) {
        drawStatics(canvas);
        drawMonster(canvas);
        invalidate();
    }

    // This method encapsulates all of the static graphics in the calcGame
    private void drawStatics(Canvas canvas){
        // Position all of the items ( floor, castle, etc. )
        floor_right = canvas.getWidth();
        floor_bottom = canvas.getHeight();
        floor_top = (int) Math.round(0.9*floor_bottom);
        castle_top = (int) Math.round( floor_bottom - castle_img.getHeight()-(0.5*(floor_bottom - floor_top)));
        char_left = (int)Math.round(castle_width / 2);
        char_top = castle_top - character_img.getHeight();
        monster_top = (int) ((floor_bottom - floor_top) * 0.75 + floor_top - monster_height);

        paintSky.setColor(skyblue);
        canvas.drawRect(0,0,floor_right,floor_bottom,paintSky);

        paintFloor.setColor(grassgreen);
        canvas.drawRect(0,floor_top,floor_right,floor_bottom,paintFloor);

        canvas.drawBitmap(castle_img,0,castle_top,null);
        canvas.drawBitmap(character_img,char_left,char_top,null);
    }

    private void drawMonster(Canvas canvas){
        if (monster.getXCoord() <= (castle_width)) {
            info[0]--;
            checkGameOver();
            hpfield.setText("Health: " + Integer.toString(info[0]));
            monster.respawnMonster(info[2]);
            monster.deselectMonster();


        }else {
            sendMonsterTop();
            if (monster.getMonster_selected() == true) {
                canvas.drawBitmap(arrow_img,monster.getXCoord(),monster_top - monster_height,null);
            }
            canvas.drawBitmap(monster_img,monster.getXCoord(),monster_top,null);
            monster.moveMonster(width);
        }

    }

    private void checkGameOver() {
        if (info[0] <= 0) {
            Intent goToGameOver = new Intent(c, GameOverActivity.class);
            goToGameOver.putExtra("xp",info[1]);
            c.startActivity(goToGameOver);
        }
    }

    // This method converts all jpeg and png images into bitmap objects, and prepares them for use. (BROKEN BACKGROUND)
    private void setAssets() {

        // get phone size
        DisplayMetrics phoneSize = getContext().getResources().getDisplayMetrics();
        width =  phoneSize.widthPixels;
        height= phoneSize.heightPixels;

        // GENERATE BACKGROUND
        //background =  BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg);
        //background = Bitmap.createScaledBitmap(background,1000,1000,true);

        // GENERATE CASTLE
        castle_img = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.castle);
        castle_width = (int)Math.round(0.25*width);
        int castle_height = (int)Math.round(0.30*height);
        castle_img = Bitmap.createScaledBitmap(castle_img,castle_width,castle_height,true);
        //makeTransparent(castle_img);

        // GENERATE CHARACTER
        character_img = getCharacter(userLevel);
        char_width = (int)Math.round(0.075*width);
        char_height= (int)Math.round(0.075*height);
        character_img = Bitmap.createScaledBitmap(character_img,char_width,char_height,true);

        // GENERATE MONSTER
        monster_img = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.demon);
        monster_width = (int)Math.round(0.175*width);
        monster_height = (int)Math.round(0.125*height);
        monster_img = Bitmap.createScaledBitmap(monster_img,monster_width,monster_height,true);
        sendMonsterDimension();

        // GENERATE ARROW
        arrow_height = (int)Math.round(0.5*monster_height);
        arrow_width = monster_width;
        arrow_img = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.arrow);
        arrow_img = Bitmap.createScaledBitmap(arrow_img,arrow_width,arrow_height,true);
    }

    private Bitmap getCharacter(int userLevel) {
        if (userLevel >= 25) {
            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.malewizard3);
            return bmp;
        } else if (userLevel >= 10){
            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.malewizard2);
            return bmp;
        } else {
            Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.malewizard1);
            return bmp;
        }

    }

    // This method turns all black pixels (0xffffff) into TRANSPARENT
    private void makeTransparent(Bitmap bmp) {

        int height = bmp.getHeight();
        int width = bmp.getWidth();

        for (int y = 0; y < height;y++) {
            for (int x = 0; x < width; x++) {
                if (bmp.getPixel(x,y) == Color.BLACK ) {
                    bmp.setPixel(x,y,Color.TRANSPARENT);
                } else {
                    bmp.setPixel(x,y,bmp.getPixel(x,y));
                }
            }
        }
        bmp.setHasAlpha(true);
    }

    private void sendMonsterDimension(){
        monster.setMonster_width(monster_width);
        monster.setMonster_height(monster_height);
    }
    private void sendMonsterTop() {
        monster.setMonster_Y(monster_top);
    }

}
