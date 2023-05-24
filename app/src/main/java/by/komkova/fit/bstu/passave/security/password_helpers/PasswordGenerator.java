package by.komkova.fit.bstu.passave.security.password_helpers;

import android.util.Log;
import android.util.Range;

import java.util.ArrayList;
import java.util.Random;

public class PasswordGenerator {
    // https://github.com/abhinav0612/PasswordGeneratorLibrary/blob/master/passwordgenerator/src/main/java/com/abhinav/passwordgenerator/PasswordGenerator.kt

    Integer length;
    private Boolean includeUpperCaseLetters, includeLowerCaseLetters, includeSymbols, includeNumbers;

    char[] upperCaseAlphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
    char[] lowerCaseAlphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    int[] numbers = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
    char[] symbols = new char[] { '!','@','#','$','%','&','*','+','=','-','~','?','/','_' };

    public PasswordGenerator(int length, boolean includeUpperCaseLetters, boolean includeLowerCaseLetters, boolean includeSymbols, boolean includeNumbers) {
        this.length = length;
        this.includeUpperCaseLetters = includeUpperCaseLetters;
        this.includeLowerCaseLetters = includeLowerCaseLetters;
        this.includeSymbols = includeSymbols;
        this.includeNumbers = includeNumbers;
    }

    public String generatePassword() {
        String password = "";
        ArrayList<Integer> list  = new ArrayList<Integer>();

        if(includeUpperCaseLetters) { list.add(0); }
        if(includeLowerCaseLetters) { list.add(1); }
        if(includeNumbers) { list.add(2); }
        if(includeSymbols) { list.add(3); }


        for (int i = 0; i < length; i++) {
            Random rand = new Random();
            int choice = list.get(rand.nextInt(list.size()));

            switch(choice){
                case 0:
                    password += upperCaseAlphabet[rand.nextInt(upperCaseAlphabet.length)]; break;
                case 1:
                    password += lowerCaseAlphabet[rand.nextInt(upperCaseAlphabet.length)]; break;
                case 2:
                    password += String.valueOf(numbers[rand.nextInt(numbers.length)]); break;
                case 3:
                    password += symbols[rand.nextInt(symbols.length)]; break;
            }
        }

        return password;
    }
}
