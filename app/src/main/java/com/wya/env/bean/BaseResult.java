package com.wya.env.bean;

/**
 * @date: 2018/7/3 13:50
 * @author: Chunjiang Mao
 * @classname: BaseResult
 * @describe:
 */

public class BaseResult<T> {
    public String code;
    public String msg;
    public boolean success;
    public T data;
}
