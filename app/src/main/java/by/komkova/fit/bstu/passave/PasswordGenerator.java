package by.komkova.fit.bstu.passave;

import android.util.Range;

import java.util.ArrayList;
import java.util.Random;

public class PasswordGenerator {

    Integer length;
    private Boolean includeUpperCaseLetters, includeLowerCaseLetters, includeSymbols, includeNumbers;

    char[] upperCaseAlphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
    char[] lowerCaseAlphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    PasswordGenerator(int length, boolean includeUpperCaseLetters, boolean includeLowerCaseLetters, boolean includeSymbols, boolean includeNumbers) {
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

//        for(i in 1..length){
//            var choice = list.random()
//            when(choice){
//                0-> password += ('A'..'Z').random().toString()
//                1-> password += ('a'..'z').random().toString()
//                2-> password += ('0'..'9').random().toString()
//                3-> password += listOf('!','@','#','$','%','&','*','+','=','-','~','?','/','_').random().toString()
//            }
//        }

        for (int i = 0; i < length; i++) {
            Random rand = new Random();
            int choice = list.get(rand.nextInt(list.size()));

            switch(choice){
                //case 0: ('A'..'Z').random().toString();
            }
        }

        return password;
    }
}
