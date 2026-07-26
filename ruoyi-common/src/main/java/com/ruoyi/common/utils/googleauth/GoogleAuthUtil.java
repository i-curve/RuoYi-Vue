package com.ruoyi.common.utils.googleauth;

import com.ruoyi.common.utils.qr.QrUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthUtil {
    private String appName;
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    private final String KEY_FORMAT = "otpauth://totp/%s:%s?secret=%s";

    public GoogleAuthUtil(@Value("${ruoyi.name}") String appName) {
        this.appName = appName;
    }

    // 生成秘钥
    public String getSecret() {
        return googleAuthenticator.createCredentials().getKey();
    }

    // 生成base64图片
    public String getBase64Img(String secret, String username) {
        try {
            String content = String.format(KEY_FORMAT, appName, username, secret, appName);
            return QrUtil.generateBase64QRCode(content, 300, 300);
        } catch (Exception e) {
            return null;
        }
    }

    // 验证code
    public boolean validCode(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }
}
