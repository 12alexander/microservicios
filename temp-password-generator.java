import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("admin123 hash: " + encoder.encode("admin123"));
        System.out.println("client123 hash: " + encoder.encode("client123"));
        
        // Verify the hashes work
        String adminHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("admin123 matches: " + encoder.matches("admin123", adminHash));
    }
}