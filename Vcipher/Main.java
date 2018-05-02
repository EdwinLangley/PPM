package Vcipher;

public class Main {
    public static void main(String[] args) {

        /* the key, used to encrypt and decrypt the password */
        String key = "MAVISKNOWSTHINGS";

        /*this will be set but the users choice so needs changing*/
        String usersPassword = "haPPPPPy4521dsahdbad";

        /*testing*/
        String encrypted = encrypt(usersPassword, key);
        String decrypted = decrypt(encrypted,key);
        System.out.println("encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    static String encrypt(String usersPassword, final String key){
        String encrypted = "";
        usersPassword = usersPassword.toUpperCase();

        for (int i = 0, j = 0; i< key.length(); i++){

            char letter = usersPassword.charAt(i);
            if(letter < 'A' || letter > 'Z'){
                continue; /*removes characters that aren't upper case letters*/
            }
            encrypted += (char)((letter + key.charAt(j) - 130) % 26 + 65);

            /*ensures the key repeats fully*/
            j = (j + 1) % key.length();
        }
        return encrypted;
    }

    static String decrypt(String encryptedPass, final String key){
        String decrypted = "";
        encryptedPass = encryptedPass.toUpperCase();
        for (int i = 0, j = 0; i < encryptedPass.length(); i++) {
            char letter = encryptedPass.charAt(i);
            if(letter < 'A' || letter > 'Z'){
                continue; /*removes characters that aren't upper case letters*/
            }
            decrypted += (char)((letter - key.charAt(j) + 26)  %26 + 65);

            /*ensures the key repeats fully*/
            j = (j + 1) % key.length();
        }
        return decrypted;
    }
}
