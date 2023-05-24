package by.komkova.fit.bstu.passave.security.password_helpers;

import by.komkova.fit.bstu.passave.R;

public enum PasswordStrength {

    WEAK(R.string.weak, R.color.weak),
    MEDIUM(R.string.medium, R.color.medium),
    STRONG(R.string.strong, R.color.strong),
    VERY_STRONG(R.string.very_strong, R.color.very_strong);

    public int msg;
    public int color;
    private static int MIN_LENGTH = 10;
    private static int MAX_LENGTH = 14;

    PasswordStrength(int msg, int color) {
        this.msg = msg;
        this.color = color;
    }

    public static PasswordStrength calculate(String password) {
        int score = 0;
        boolean lower = false;
        boolean upper = false;
        boolean digit = false;
        boolean specialChar = false;

        int passwordLength = password.length();

        for(int i = 0; i < password.length(); i++){
            char c = password.charAt(i);

            if(!specialChar && !Character.isLetterOrDigit(c)){
                score++;
                specialChar = true;
            } else {
                if(!digit && Character.isDigit(c)){
                    score++;
                    digit = true;
                } else {
                    if(!upper || !lower){
                        if(Character.isUpperCase(c)){
                            upper = true;
                        } else { lower = true; }

                        if(upper && lower){
                            score++;
                        }
                    }
                }
            }
        }

        if(passwordLength > MAX_LENGTH) { score++; }
        if(passwordLength < MIN_LENGTH) { score = 0 ; }

        switch (score){
            case 0: return WEAK;
            case 1: return MEDIUM;
            case 2: return STRONG;
            case 3: return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }

}
