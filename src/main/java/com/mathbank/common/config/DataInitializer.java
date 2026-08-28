package com.mathbank.common.config;

import com.mathbank.auth.domain.Member;
import com.mathbank.auth.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Value("${app.admin.init-password:admin1234}")
    private String initPassword;

    @Bean
    public CommandLineRunner initAdmin(
            PasswordEncoder passwordEncoder,
            MemberMapper memberMapper
    ) {
        return args -> {
            if (memberMapper.findByUsername("admin") == null) {
                Member member = new Member();
                member.setUsername("admin");
                member.setPassword(passwordEncoder.encode(initPassword));
                member.setRole("ADMIN");
                memberMapper.insert(member);
                System.out.println("=== ADMIN ACCOUNT CREATED ===");
            }
        };
    }
}
