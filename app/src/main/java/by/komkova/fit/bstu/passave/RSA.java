package by.komkova.fit.bstu.passave;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    public PrivateKey privateKey;
    byte[] encryptedBytes, decryptedBytes;
    Cipher cipher, cipher1;
    String encrypted, decrypted;

    private final static String CRYPTO_METHOD = "RSA";
    private final static int CRYPTO_BITS = 2048;

    public RSA()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        generateKeyPair();
    }

    private void generateKeyPair()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD);
        kpg.initialize(CRYPTO_BITS);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();

    }

    public String getPublicKey(String option)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        switch (option) {

            case "pkcs1-pem":
                String pkcs1pem = "-----BEGIN RSA PUBLIC KEY-----\n";
                pkcs1pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs1pem += "-----END RSA PUBLIC KEY-----";

                return pkcs1pem;

            case "pkcs8-pem":
                String pkcs8pem = "-----BEGIN PUBLIC KEY-----\n";
                pkcs8pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs8pem += "-----END PUBLIC KEY-----";

                return pkcs8pem;

            case "base64":
                return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);

            default:
                return null;

        }

    }

    public String encrypt(Object... args)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        String plain = (String) args[0];
        PublicKey rsaPublicKey;

        if (args.length == 1) {
            rsaPublicKey = this.publicKey;
        } else {
            rsaPublicKey = (PublicKey) args[1];
        }

        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String result)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        // cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT));
        decrypted = new String(decryptedBytes);

        return decrypted;
    }

    public static PrivateKey stringToPrivate(String private_key)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        try {
            // Read in the key into a String
            StringBuilder pkcs8Lines = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new StringReader(private_key));
            String line;
            while ((line = rdr.readLine()) != null) {
                pkcs8Lines.append(line);
            }

            // Remove the "BEGIN" and "END" lines, as well as any whitespace

            String pkcs8Pem = pkcs8Lines.toString();
            pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

            // Base64 decode the result

            byte[] pkcs8EncodedBytes = Base64.decode(pkcs8Pem, Base64.DEFAULT);

            // extract the private key

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            return privKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }

    public static PublicKey stringToPublicKey(String publicKeyString)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        try {
            if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
                publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // Log.d("RSA", String.valueOf(spec));
            Log.d("RSA", publicKeyString);


            return keyFactory.generatePublic(spec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        }
    }
}
