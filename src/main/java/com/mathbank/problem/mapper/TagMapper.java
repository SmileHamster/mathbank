package com.mathbank.problem.mapper;

import com.mathbank.problem.domain.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> findAll();
    List<Tag> findByType(String tagType);
}
