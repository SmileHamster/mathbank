package com.mathbank.problem.service;

import com.mathbank.problem.domain.Tag;
import com.mathbank.problem.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    private static final String[] TAG_TYPE_ORDER =
            {"GRADE", "SEMESTER", "UNIT", "SUB_UNIT", "TYPE", "DIFFICULTY"};

    public Map<String, List<Tag>> getTagsGroupedByType() {
        Map<String, List<Tag>> result = new LinkedHashMap<>();
        for (String type : TAG_TYPE_ORDER) {
            result.put(type, tagMapper.findByType(type));
        }
        return result;
    }
}
