package com.sametakbal.user_management.service.impl;

import com.sametakbal.user_management.common.exceptions.AuthenticateException;
import com.sametakbal.user_management.dto.request.EmailRequest;
import com.sametakbal.user_management.entity.ConfirmationToken;
import com.sametakbal.user_management.entity.Role;
import com.sametakbal.user_management.entity.RoleEntity;
import com.sametakbal.user_management.entity.User;
import com.sametakbal.user_management.repository.ConfirmationTokenRepository;
import com.sametakbal.user_management.repository.RoleRepository;
import com.sametakbal.user_management.repository.UserRepository;
import com.sametakbal.user_management.dto.request.AuthenticationRequest;
import com.sametakbal.user_management.dto.request.RegistrationRequest;
import com.sametakbal.user_management.dto.response.DataResponse;
import com.sametakbal.user_management.security.JwtService;
import com.sametakbal.user_management.service.AuthenticationService;
import com.sametakbal.user_management.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        RoleEntity roleEntity = roleRepository.findByName(Role.USER.getName()).orElseThrow();

        var user = User.builder()
                .name(registrationRequest.getName())
                .surname(registrationRequest.getSurname())
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .enabled(false)
                .roles(Set.of(roleEntity))
                .build();

        userRepository.save(user);
        createConfirmationToken(user);
    }

    @Override
    public DataResponse<String> authenticate(AuthenticationRequest request) {
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmailOrUsername(), request.getPassword()));
            var claims = new HashMap<String, Object>();
            var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
            return new DataResponse<>(jwtToken);
        }catch (Exception e){
            throw new AuthenticateException(e.getMessage());
        }
    }

    @Override
    public void confirmToken(String token) {
        var confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        var user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        confirmationTokenRepository.delete(confirmationToken);
    }


    private void createConfirmationToken(User user){
        String token = generateRandomNumberString();
        var confirmationToken = ConfirmationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15L))
                .build();
        confirmationTokenRepository.save(confirmationToken);
        EmailRequest request = EmailRequest.builder().to(user.getEmail())
                .subject("Hoşgeldiniz!")
                .text(buildEmail(user.getName(),
                        "http://localhost:8080/api/auth/confirm?token=" + token))
                .build();
        emailService.sendEmail(request);
    }


    private String generateRandomNumberString() {
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(random.nextInt(10)); // Generates a random digit (0-9)
        }
        return randomNumber.toString();
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Email doğrulaması</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Merhaba " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Bitig'e hoşgeldin. Lütfen hesabının aktif edilmesi için tıkla: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Aktifleştir</a> </p></blockquote>\n Bu bağlantı 15 dakika geçerlidir. <p>İyi günler</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


}
