package com.blog.utils;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result {
    
    private Integer code;
    private String message;
    private Object data;
    
    public Result() {}
    
    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功返回结果
     */
    public static Result success() {
        return new Result(200, "操作成功", null);
    }
    
    /**
     * 成功返回结果
     */
    public static Result success(String message) {
        return new Result(200, message, null);
    }
    
    /**
     * 成功返回结果
     */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }
    
    /**
     * 成功返回结果
     */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }
    
    /**
     * 失败返回结果
     */
    public static Result error() {
        return new Result(500, "操作失败", null);
    }
    
    /**
     * 失败返回结果
     */
    public static Result error(String message) {
        return new Result(500, message, null);
    }
    
    /**
     * 失败返回结果
     */
    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }
    
    /**
     * 失败返回结果
     */
    public static Result error(Integer code, String message, Object data) {
        return new Result(code, message, data);
    }
}