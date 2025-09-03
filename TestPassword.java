import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedPassword = "$2a$10$I.nnEuxF31xh1y88dbU32usxmYkvkmSf52X9MtkxfnWvpdkFtB8oq";
        String inputPassword = "123456";
        
        boolean matches = encoder.matches(inputPassword, storedPassword);
        System.out.println("Password matches: " + matches);
    }
}