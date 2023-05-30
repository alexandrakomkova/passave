package by.komkova.fit.bstu.passave.security.security_algorithms;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 {
//    public static String md5Custom(String st) {
//        if (st.isEmpty()){
//            return "";
//        }
//
//        MessageDigest messageDigest = null;
//        byte[] digest = new byte[0];
//
//        try {
//            messageDigest = MessageDigest.getInstance("MD5");
//            messageDigest.reset();
//            messageDigest.update(st.getBytes());
//            digest = messageDigest.digest();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        BigInteger bigInt = new BigInteger(1, digest);
//        String md5Hex = bigInt.toString(16);
//
//        while( md5Hex.length() < 32 ){
//            md5Hex = "0" + md5Hex;
//        }
//
//        return md5Hex;
//    }

    public static String sha512Custom(String str) {
        if (str.isEmpty()){
            return "";
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }

        return String.valueOf(sb);
    }
}
