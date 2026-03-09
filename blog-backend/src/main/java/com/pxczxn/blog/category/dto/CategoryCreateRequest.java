package com.pxczxn.blog.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotBlank(message = "name must not be blank")
    @Size(max = 100, message = "name length must be <= 100")
    private String name;
}
