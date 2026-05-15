package com.mathbank.problem.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Tag {
    private Long id;
    private String tagType;
    private String tagValue;
    private Integer sortOrder;
}
