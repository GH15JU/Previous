package calculotprototype.g14.cmpt276.calculot_prototype.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import calculotprototype.g14.cmpt276.calculot_prototype.Classes.User;

/**
 * Created by ephronax on 3/7/2017.
 */

public class UserDatabaseHelper extends SQLiteOpenHelper {

    // DATABASE META INFORMATION
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 2;

    // TABLE META INFORMATION
    private static final String TABLE_NAME = "User_Information";
    private static final String TABLE_ID = "_id";
    private static final String TABLE_USERNAME = "Username";
    private static final String TABLE_FIRSTNAME = "First_Name";
    private static final String TABLE_PASSWORD = "Password";
    private static final String TABLE_TOTALXP = "Total_XP";
    private static final String TABLE_LEARNINGXP = "Learning_XP";
    private static final String TABLE_PRACTICEXP = "Practice_XP";
    private static final String TABLE_VECTORLVL = "Vector_LVL";

    // onCreate statement
    private static final String STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TABLE_USERNAME + " TEXT NOT NULL," +
            TABLE_FIRSTNAME + " TEXT NOT NULL," +
            TABLE_PASSWORD + " TEXT NOT NULL," +
            TABLE_TOTALXP + " INTEGER NOT NULL," +
            TABLE_LEARNINGXP + " INTEGER NOT NULL," +
            TABLE_PRACTICEXP + " INTEGER NOT NULL" +
            TABLE_VECTORLVL + " INTEGER NOT NULL" +
            ");";


    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STATEMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    // Inserts a user into the database
    public long insertUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TABLE_USERNAME, user.getUsername());
        cv.put(TABLE_FIRSTNAME, user.getFirstname());
        cv.put(TABLE_PASSWORD, user.getPassword());
        cv.put(TABLE_TOTALXP, 0);
        cv.put(TABLE_LEARNINGXP, 0);
        cv.put(TABLE_PRACTICEXP, 0);
        cv.put(TABLE_VECTORLVL, user.getVectorLvl());

        return db.insert(TABLE_NAME, null, cv);

    }

    // Looks for the user in the database. If found and username & password are equivalent, return true. Else, return false
    public boolean isValidUser(String _username, String _password) {

        // Initialize Database and Cursor
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_USERNAME + ", " + TABLE_PASSWORD + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String password = cursor.getString(1);

                if ( username.equals(_username) && password.equals(_password)) {
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        else{
            cursor.close();
            return false;
        }
        return false;

    }

    // Stores user information into the parameter 'user' -- TO TEST
    public User getUser(String _username) {
        // Initialize Database and Cursor
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_USERNAME + ", " + TABLE_FIRSTNAME + ", " + TABLE_PASSWORD + ", " +
                TABLE_TOTALXP + ", " + TABLE_LEARNINGXP + ", " + TABLE_PRACTICEXP + ", " + TABLE_VECTORLVL + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                // Check if this user is the correct user we are trying to access
                if (username.equals(_username)) {
                    // Query all of the values from the table to the user parameter.
                    User user = new User(cursor.getString(0), //username
                            cursor.getString(1), //first_name
                            cursor.getString(2), //password
                            cursor.getInt(3), //totalXP
                            cursor.getInt(4), //learningXP
                            cursor.getInt(5), //practiceXP
                            cursor.getInt(6)  //vectorlvl
                    );
                    cursor.close();
                    return user;
                }
            } while (cursor.moveToNext());
        }
        // Destroy cursor
        cursor.close();
        return null;

    }

    // Returns false if username is already taken, true if still available
    public boolean userNotTaken(String _username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_USERNAME + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                if (username.equalsIgnoreCase(_username)){
                    return false;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return true;
    }

    // EDITS XP VALUES TO THE PARAMETERS NEEDED -- TO TEST
    public void editXP(int _TotalXP, int _PracticeXP, int _LearningXP, int _VectorLvl, String _username) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Query statement: UPDATE User-Information SET Total XP = _TotalXP , Learning XP = _LearningXP, Practice XP = _PracticeXP WHERE Username ='_username'
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + TABLE_TOTALXP + " = " +
                                    _TotalXP + ", " + TABLE_LEARNINGXP + " = " + _LearningXP + ", " +
                                    TABLE_PRACTICEXP + " = " + _PracticeXP + ", " + TABLE_VECTORLVL + " = " + _VectorLvl + " WHERE " + TABLE_USERNAME + " ='" +
                                    _username + "'");

    }

    // EDITS THE PASSWORD OF THE USER IF THE OLD PASSWORD MATCHES THE ONE IN THE DATABASE, FROM THE USERNAME.
    public void editPassword(String newPassword, String oldPassword, String _username){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + TABLE_PASSWORD + " = " + newPassword +
                                    " WHERE " + TABLE_USERNAME + " = " + "'" + _username + "'" + " AND " + TABLE_PASSWORD + " = " + "'" + oldPassword + "'" ,null);


    }

    public void addPracticeXP(String _username,int xpgained) {
        // Query DB for _username.
        // Write DB with username to add xpgained to practice XP and total XP - DONE;

        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "UPDATE " + TABLE_NAME + " SET " + TABLE_TOTALXP + " = " + TABLE_TOTALXP + " + " + Integer.toString(xpgained) +
                ", " + TABLE_PRACTICEXP + " = " + TABLE_PRACTICEXP + " + " + Integer.toString(xpgained) + " WHERE " + TABLE_USERNAME + " = "
                + "'" +  _username + "';";

        db.execSQL(QUERY);

        db.close();

    }

    public void editVectorLvl(String _username,int _vectorlvl) {
        // Query DB for _username.
        // Write DB with username to add xpgained to practice XP and total XP - DONE;

        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "UPDATE " + TABLE_NAME + " SET " + TABLE_VECTORLVL + " = " + Integer.toString(_vectorlvl) + " WHERE " + TABLE_USERNAME + " = "
                + "'" +  _username + "';";

        db.execSQL(QUERY);

        db.close();

    }


    // Returns number of users in database (for use in login)
    public int numUsers(){
        int userCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_USERNAME + ", " + TABLE_PASSWORD + " FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()){
            do{
                userCount++;
            } while(cursor.moveToNext());
        }
        cursor.close();
        return userCount;
    }

    // Gives a cursor for acces from Java code to run through database for information
    public Cursor getAnyUserInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TABLE_USERNAME + ", " + TABLE_FIRSTNAME + " FROM " + TABLE_NAME, null);
        return cursor;
    }
}
