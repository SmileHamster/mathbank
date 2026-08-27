package com.mathbank.attempt.mapper;

import com.mathbank.attempt.domain.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
    void insertStudent(Student student);
    List<Student> findAll();
    Student findById(Long id);
    void updateStudent(Student student);
    void deleteById(Long id);
}
