package com.ruoyi.common.utils.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.UserSpecifiedEndpointBuilder;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TencentCos {

    private String t_bucket;

    private COSClient cosClient;

    public TencentCos(String domain,
                      String bucket,  // 桶名
                      String secretId,
                      String secretKey,
                      String region) {
        this.t_bucket = bucket;
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(region));

        clientConfig.setHttpProtocol(HttpProtocol.https);
        UserSpecifiedEndpointBuilder endpointBuilder = new UserSpecifiedEndpointBuilder(domain, "service.cos.myqcloud.com");
        clientConfig.setEndpointBuilder(endpointBuilder);

        this.cosClient = new COSClient(cred, clientConfig);
        List<Bucket> buckets = cosClient.listBuckets();
        System.out.println(buckets);
    }

    // 获取原始cos对象
    public COSClient getRawClient() {
        return cosClient;
    }

    /**
     * 获取加密访问url
     *
     * @param key      路径
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @return
     */
    public String getPreObject(String key, Integer timeout, TimeUnit timeUnit) {
        List<Bucket> buckets = cosClient.listBuckets();
        Date expiration = new Date(new Date().getTime() + timeUnit.toMillis(timeout));
        return cosClient.generatePresignedUrl(t_bucket, key, expiration, HttpMethodName.GET).toString();
    }

    /**
     * 直接获取访问连接(公有访问)
     *
     * @param key 路径
     * @return
     */
    public String getObject(String key) {
        return cosClient.getObjectUrl(t_bucket, key).toString();
    }

    /**
     * 上传资源
     *
     * @param key    路径
     * @param stream 文件流
     * @return 错误, 上传成功为null
     */
    public String putObject(String key, InputStream stream) {
        return putObject(t_bucket, key, stream);
    }

    /**
     * 上传资源
     *
     * @param bucket 桶名
     * @param key    路径
     * @param stream 文件流
     * @return 错误, 上传成功为null
     */
    public String putObject(String bucket, String key, InputStream stream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentLength(stream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, objectMetadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
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
            cosClient.deleteObject(bucket, key);
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
            cosClient.copyObject(bucket1, key, bucket2, target);
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
            cosClient.copyObject(bucket1, key, bucket2, target);
            cosClient.deleteObject(bucket1, key);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
