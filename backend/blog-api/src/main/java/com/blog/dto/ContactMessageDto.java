package com.blog.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 联系消息DTO
 */
@Data
public class ContactMessageDto {
    
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度必须在2-50个字符之间")
    private String name;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "主题不能为空")
    @Size(min = 2, max = 200, message = "主题长度必须在2-200个字符之间")
    private String subject;
    
    @NotBlank(message = "内容不能为空")
    @Size(min = 10, max = 2000, message = "内容长度必须在10-2000个字符之间")
    private String content;
}