package com.anton.book_network.auth;

import com.anton.book_network.email.EmailService;
import com.anton.book_network.email.EmailTemplate;
import com.anton.book_network.repo.RoleRepository;
import com.anton.book_network.repo.TokenRepository;
import com.anton.book_network.repo.UserRepository;
import com.anton.book_network.user.Token;
import com.anton.book_network.user.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final RoleRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${spring.jpa.application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = repository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("role user not found"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sentValidationEmail(user);
    }

    private void sentValidationEmail(User user) throws MessagingException {

        var newToken = generateAndSendActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "account activation"
        );
    }

    private String generateAndSendActivationToken(User user) {
        String generatedToken = generateAndSendActivationCode(6);

        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }


    private String generateAndSendActivationCode(int num) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < num; i++){
            int random = secureRandom.nextInt(chars.length());
            sb.append(chars.charAt(random));
        }
        return sb.toString();
    }
}
