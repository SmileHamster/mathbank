package com.mathbank.attempt.service;

import com.mathbank.attempt.domain.Student;
import com.mathbank.attempt.dto.StudentFormDto;
import com.mathbank.attempt.mapper.StudentMapper;
import com.mathbank.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;

    public List<Student> getStudentList() {
        return studentMapper.findAll();
    }

    public Student getStudent(Long id) {
        Student student = studentMapper.findById(id);
        if (student == null) {
            throw new ResourceNotFoundException("학생을 찾을 수 없습니다: " + id);
        }
        return student;
    }

    @Transactional
    public Long createStudent(StudentFormDto form) {
        Student student = Student.builder()
                .name(form.getName())
                .gradeTagId(form.getGradeTagId())
                .memo(form.getMemo())
                .build();
        studentMapper.insertStudent(student);
        return student.getId();
    }

    @Transactional
    public void updateStudent(Long id, StudentFormDto form) {
        getStudent(id);
        Student student = Student.builder()
                .id(id)
                .name(form.getName())
                .gradeTagId(form.getGradeTagId())
                .memo(form.getMemo())
                .build();
        studentMapper.updateStudent(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentMapper.deleteById(id);
    }
}
