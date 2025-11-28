package com.booklending.util;

import lombok.Data;

@Data
public class Response<T> {
    private int code; // 状态码：200成功，其他失败
    private String message; // 响应消息
    private T data; // 响应数据
    
    // 成功响应
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    // 失败响应
    public static <T> Response<T> error(int code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    // 失败响应（默认错误码）
    public static <T> Response<T> error(String message) {
        return error(500, message);
    }
}