package com.github.shangtanlin.result;


import lombok.Data;


//用于返回请求结果
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //静态方法--成功
    public static <T> Result<T> ok(T data) {
        return new Result<>(200,"success",data);
    }

    public static <T> Result<Void> ok() {
        return new Result<>(200,"success",null);
    }

    //静态方法--失败
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500,msg,null);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code,msg,null);
    }
}
