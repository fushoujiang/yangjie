package com.example.yangjie.id.idworker;

/**
 * 生成ID异常类
 * @author wenqi.wu
 */
public class IdGenerateException extends RuntimeException {

    private String source;

    public IdGenerateException(String message){
        super(message);
    }

    public IdGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        if(source != null && !"".equals(source.trim())){
            return "[" + source + "]," + super.getMessage();
        }
        return super.getMessage();
    }

    public void setSource(String source) {
        this.source = source;
    }
}
