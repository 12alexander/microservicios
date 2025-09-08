import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate correct hashes
        String admin123Hash = encoder.encode("admin123");
        String client123Hash = encoder.encode("client123");
        String assessor123Hash = encoder.encode("assessor123");
        
        System.out.println("admin123 hash: " + admin123Hash);
        System.out.println("client123 hash: " + client123Hash);
        System.out.println("assessor123 hash: " + assessor123Hash);
        
        // Test the current hash in DB
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("Current hash matches admin123: " + encoder.matches("admin123", currentHash));
        System.out.println("Current hash matches client123: " + encoder.matches("client123", currentHash));
        
        // Try some common passwords
        System.out.println("Current hash matches 'admin': " + encoder.matches("admin", currentHash));
        System.out.println("Current hash matches 'password': " + encoder.matches("password", currentHash));
        System.out.println("Current hash matches '123': " + encoder.matches("123", currentHash));
    }
}