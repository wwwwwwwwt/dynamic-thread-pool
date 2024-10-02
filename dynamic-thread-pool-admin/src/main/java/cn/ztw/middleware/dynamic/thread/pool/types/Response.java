package cn.ztw.middleware.dynamic.thread.pool.types;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> implements Serializable {
    private static final long serialVersionUID = -2474596551402989285L;
    private String code;
    private String info;
    private T data;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Code{
        SUCCESS("01", "调用成功"),
        UN_ERROR("02", "调用失败"),

        ILLEGAL_PARAMETER("03", "非法参数")
        ;
        private String code;
        private String info;
    }
}
