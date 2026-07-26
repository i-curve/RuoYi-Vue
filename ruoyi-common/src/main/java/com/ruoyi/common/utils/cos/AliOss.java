package com.ruoyi.common.utils.cos;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AliOss {

    private String domain;

    private String t_bucket;

    private OSS ossClient;

    public AliOss(
            String domain,
            String bucket,
            String accessKeyId,
            String accessKeySecret,
            String region) {
        this.t_bucket = bucket;
        this.domain = domain;
        // 创建凭证提供者
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 配置客户端参数
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        // 显式声明使用V4签名算法
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        clientBuilderConfiguration.setSupportCname(true);
        clientBuilderConfiguration.setProtocol(Protocol.HTTPS);

        // 初始化OSS客户端
        ossClient = OSSClientBuilder.create()
                .credentialsProvider(provider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .endpoint(domain)
                .build();
    }

    public OSS getRawClient() {
        return ossClient;
    }

    public String getPreObject(String key, Integer timeout, TimeUnit timeUnit) {
        Date expiration = new Date(new Date().getTime() + timeUnit.toMillis(timeout));
        return ossClient.generatePresignedUrl(t_bucket, key, expiration).toString();
    }

    public String getObject(String key) {
        return domain.startsWith("https://") ? domain + key : "https://" + domain + key;
    }

    /**
     * 上传资源
     *
     * @param key
     * @param stream
     * @return
     */
    public String putObject(String key, InputStream stream) {
        return putObject(t_bucket, key, stream);
    }

    /**
     * 上传资源
     *
     * @param bucket
     * @param key
     * @param stream
     * @return
     */
    public String putObject(String bucket, String key, InputStream stream) {

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream);
            // 创建PutObject请求。
            ossClient.putObject(putObjectRequest);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 删除资源
     *
     * @param key
     */
    public String delObject(String key) {
        return delObject(t_bucket, key);
    }

    /**
     * 删除资源
     *
     * @param bucket 桶名
     * @param key    cos资源
     */
    public String delObject(String bucket, String key) {
        try {
            ossClient.deleteObject(bucket, key);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 复制资源
     *
     * @param key    cos资源
     * @param target 目标资源
     */
    public String copyObject(String key, String target) {
        return copyObject(t_bucket, key, t_bucket, target);
    }

    /**
     * 复制资源
     *
     * @param bucket1 源桶名
     * @param key     源资源
     * @param bucket2 目标桶名
     * @param target  目标资源
     */
    public String copyObject(String bucket1, String key, String bucket2, String target) {
        try {
            ossClient.copyObject(bucket1, key, bucket2, target);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 移动资源
     *
     * @param key    源资源
     * @param target 目标资源
     */
    public String moveObject(String key, String target) {
        return moveObject(t_bucket, key, t_bucket, target);
    }

    /**
     * 移动资源
     *
     * @param bucket1 源桶名
     * @param key     源资源
     * @param bucket2 目标桶名
     * @param target  目标资源
     */
    public String moveObject(String bucket1, String key, String bucket2, String target) {
        try {
            ossClient.copyObject(bucket1, key, bucket2, target);
            ossClient.deleteObject(bucket1, key);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
