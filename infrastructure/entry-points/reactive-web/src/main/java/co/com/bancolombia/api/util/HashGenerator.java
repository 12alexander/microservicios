package co.com.bancolombia.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashes for both users
        String admin123Hash = encoder.encode("admin123");
        String client123Hash = encoder.encode("client123");
        
        System.out.println("=== NUEVOS HASHES BCRYPT ===");
        System.out.println("admin123 hash: " + admin123Hash);
        System.out.println("client123 hash: " + client123Hash);
        
        System.out.println("\n=== VERIFICACION ===");
        System.out.println("admin123 matches: " + encoder.matches("admin123", admin123Hash));
        System.out.println("client123 matches: " + encoder.matches("client123", client123Hash));
        
        System.out.println("\n=== PARA LIQUIBASE ===");
        System.out.println("Admin password value=\"" + admin123Hash + "\"");
        System.out.println("Client password value=\"" + client123Hash + "\"");
    }
}