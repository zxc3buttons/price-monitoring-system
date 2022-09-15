package ru.tokarev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorDto {

    private Integer status;

    private String message;

    private List<String> errors;

    private String path;

    private String timestamp;
}
