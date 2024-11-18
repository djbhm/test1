package com.nanak.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: nanak
 * @CreateTime: 2024-11-09
 * @Description:
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
public class Result<T> implements Serializable {
    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        return new Result<>("200", "ok", data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>("500", msg, null);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>("401", msg, null);
    }

    public static <T> Result<T> forbidden(String msg) {
        return new Result<>("403", msg, null);
    }
}
