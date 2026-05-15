package com.mathbank.auth.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Member {
    private Long id;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt;
}
