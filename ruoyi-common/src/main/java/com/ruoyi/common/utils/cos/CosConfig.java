package com.ruoyi.common.utils.cos;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {
    // 腾讯云cos
    @Value("${cos.domain:null}")
    private String t_Domain;
    @Value("${cos.bucket:null}")
    private String t_Bucket;
    @Value("${cos.secretId:null}")
    private String t_SecretId;
    @Value("${cos.secretKey:null}")
    private String t_SecretKey;
    @Value("${cos.region:null}")
    private String t_Region;
    // 阿里云oss
    @Value("${oss.domain:null}")
    private String a_Domain;
    @Value("${oss.bucket:null}")
    private String a_Bucket;
    @Value("${oss.accessId:null}")
    private String a_AccessId;
    @Value("${oss.accessSecret:null}")
    private String a_AccessSecret;
    @Value("${oss.region:null}")
    private String a_Region;

    @Bean
    public TencentCos getTencentCos() {
        if (!checkCondition(t_Bucket, t_Domain, t_SecretKey, t_SecretKey, t_Region))
            return null;
        return new TencentCos(t_Domain, t_Bucket, t_SecretId, t_SecretKey, t_Region);
    }

    @Bean
    public AliOss getAliOss() {
        if (!checkCondition(a_Domain, a_Bucket, a_AccessId, a_AccessSecret, a_Region))
            return null;
        return new AliOss(a_Domain, a_Bucket, a_AccessId, a_AccessSecret, a_Region);
    }

    private boolean checkCondition(String... args) {
        for (String arg : args) {
            if (StringUtils.isEmpty(arg)) return false;
        }
        return true;
    }
}
