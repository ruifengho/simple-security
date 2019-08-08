package com.github.ruifengho.simplesecurity.annotation.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ruifengho.simplesecurity.annotation.Encrypt;
import com.github.ruifengho.simplesecurity.autoconfigure.SimpleSecurityProperties;
import com.github.ruifengho.simplesecurity.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@RestControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {
    private static final Logger logger = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpleSecurityProperties simpleSecurityProperties;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(Encrypt.class) && simpleSecurityProperties.getJwe().isOpen();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String result;
        try {
            String value = objectMapper.writeValueAsString(body);
            logger.info(value);
            logger.info(simpleSecurityProperties.getJwe().getPublicKey());
            result = RSAUtil.encrypt(value, simpleSecurityProperties.getJwe().getPublicKey());
            return result;
        } catch (Exception e) {
            logger.error("Encrypt error", e);
        }


        return body;
    }
}
