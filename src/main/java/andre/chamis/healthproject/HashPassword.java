package andre.chamis.healthproject;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class HashPassword {
    public static void main(String[] args) {
        System.out.print("Enter your password: ");
        Scanner sc = new Scanner(System.in);
        String password = sc.nextLine();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println("Hashing password...");
        String hashedPassword = bCryptPasswordEncoder.encode(password);

        System.out.println("Hashed password: " + hashedPassword);
    }
}
