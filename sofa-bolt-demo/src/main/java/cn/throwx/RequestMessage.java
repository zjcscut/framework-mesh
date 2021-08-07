package cn.throwx;

import lombok.Data;

import java.io.Serializable;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/1 22:27
 */
@Data
public class RequestMessage implements Serializable {

    private Long id;

    private String content;
}
