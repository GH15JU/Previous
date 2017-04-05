package calculotprototype.g14.cmpt276.calculot_prototype.Classes;

import android.text.TextUtils;

import calculotprototype.g14.cmpt276.calculot_prototype.VectorGame.VectorGameActivity;

/**
 * Created by ephronax on 3/7/2017.
 */

public class User {

    // Fields for user class
    private String username;
    private String firstname;
    private String password;
    private int TotalXP;
    private int LearningXP;
    private int PracticeXP;
    private int VectorLvl = 1;

    final int avatarLevel1 = 0; //1st avatar at level 0
    final int avatarLevel2 = 5; //2nd avatar at level 5
    final int avatarLevel3 = 10; //3rd avatar at level 10


    // Add int array for achievements? or topics completed?

    // CONSTRUCTOR METHOD
    public User(String _username, String _firstname, String _password) {
        username = _username;
        firstname= _firstname;
        password = _password;
        TotalXP = 0;
        LearningXP = 0;
        PracticeXP = 0;
        //VectorLvl = 1;
    }

    public User(String _username, String _firstname, String _password, int _totalXP, int _learningXP, int _practiceXP, int _vectorLevel) {
        username = _username;
        firstname= _firstname;
        password = _password;
        TotalXP = _totalXP;
        LearningXP = _learningXP;
        PracticeXP = _practiceXP;
        VectorLvl = _vectorLevel;
    }
    // GET METHODS;

    // Levels go in increments of 500 XP.
    public int getlevel() {
        return TotalXP/500;
    }

    // GET REMAINDER XP
    public int getRemainderXP() {
        return TotalXP%500;
    }

    // GET USERNAME
    public String getUsername() {
        return username;
    }

    // GET FIRST NAME
    public String getFirstname() {
        return firstname;
    }

    // Private String for security.
    public String getPassword() {
        return password;
    }

    // GET TOTAL XP
    public int getTotalXP() {
        return TotalXP;
    }

    // GET LEARNING XP
    public int getLearningXP() {
        return LearningXP;
    }

    // GET PRACTICE XP
    public int getPracticeXP() {
        return PracticeXP;
    }

    public int getAvatar() {
        int level = getlevel();
        if (level >= avatarLevel3) return 3;
        if (level >= avatarLevel2) return 2;
        return 1;
    }

    public int getVectorLvl() {
        return VectorLvl;
    }

    // SET METHODS:

    // SET USERNAME
    public void setUsername(String _username) {
        username = _username;
    }

    // SET FIRST NAME
    public void setFirstname(String _firstname) {
        firstname = _firstname;
    }

    // SET PASSWORD (ONLY IF VALID)
    public boolean setPassword(String _password) {
        boolean isValid = checkPassword(_password);
        if (isValid) {
            password = _password;
            return true;
        }
        else {
            return false;
        }

    }

    // SET TOTAL XP
    public void setTotalXP(int _TotalXP) {
        TotalXP = _TotalXP;
    }

    // SET LEARNING XP
    public void setLearningXP(int _LearningXP){
        LearningXP = _LearningXP;
    }

    // SET PRACTICE XP
    public void setPracticeXP(int _PracticeXP){
        PracticeXP = _PracticeXP;
    }

    public void setVectorLvl(int _VectorLvl) {
        VectorLvl += _VectorLvl;
    }


    // OTHER FUNCTIONS;

    // Check the password if it contains at least 1 uppercase and 1 number
    private boolean checkPassword(String password) {
        // TO IMPLEMENT
        if (    TextUtils.isEmpty(password)
            ||  !(password.matches(".*[0-9].*"))
            || !(password.matches(".*[A-Z].*")))return false;
        return true;
    }
}