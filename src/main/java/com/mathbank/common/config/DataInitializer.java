package com.mathbank.common.config;

import com.mathbank.auth.domain.Member;
import com.mathbank.auth.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(
            PasswordEncoder passwordEncoder,
            MemberMapper memberMapper
    ) {
        return args -> {
            String encoded = passwordEncoder.encode("admin1234");
            System.out.println("=== ENCODED PASSWORD: " + encoded + " ===");

            if (memberMapper.findByUsername("admin") == null) {
                Member member = new Member();
                member.setUsername("admin");
                member.setPassword(encoded);
                member.setRole("ADMIN");
                memberMapper.insert(member);
                System.out.println("=== ADMIN ACCOUNT CREATED ===");
            } else {
                memberMapper.updatePassword("admin", encoded);
                System.out.println("=== ADMIN PASSWORD UPDATED ===");
            }
        };
    }
}
