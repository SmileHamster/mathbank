package com.mathbank.auth.mapper;

import com.mathbank.auth.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    Member findByUsername(String username);
    void insert(Member member);
    void updatePassword(@org.apache.ibatis.annotations.Param("username") String username,
                        @org.apache.ibatis.annotations.Param("password") String password);
}
