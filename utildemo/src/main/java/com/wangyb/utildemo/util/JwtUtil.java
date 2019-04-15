package com.wangyb.utildemo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wangyb.utildemo.config.Constant;

import java.util.Date;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/3 13:58
 * Modified By:
 * 2018-12-14 yangycy修改EXPIRE_TIME 6个月为12小时
 * Description:jwt工具类
 */
public class JwtUtil {
    // 有效期设置成12小时
    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000L;


    /**
     * 校验token是否正确
     *
     * @param token      token
     * @param loginName  登录名
     * @param userId     用户id
     * @param providerId 内容商id
     * @param secret     密码
     * @return token是否正确
     */
    public static boolean verify(String token, String loginName, Integer userId, Integer providerId, Integer tokenVersion, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(Constant.LOGIN_NAME, loginName)
                    .withClaim(Constant.USER_ID, userId)
                    .withClaim(Constant.PROVIDER_ID, providerId)
                    .withClaim(Constant.TOKEN_VERSION, tokenVersion)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取token中的登录名
     *
     * @param token token
     * @return 登录名
     */
    public static String getLoginName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.LOGIN_NAME).asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得token的版本号
     *
     * @param token token
     * @return 版本号
     */
    public static Integer getTokenVersion(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.TOKEN_VERSION).asInt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取token中的用户id
     *
     * @param token token
     * @return 用户id
     */
    public static Integer getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.USER_ID).asInt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取token中的用户的providerId
     *
     * @param token token
     * @return 内容商id
     */
    public static Integer getProviderId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.PROVIDER_ID).asInt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取失效日期
     *
     * @param token token
     * @return token失效日期
     */
    public static Date getExpiresDate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 生成签名
     *
     * @param loginName    登录名
     * @param userId       用户id
     * @param providerId   内容商id
     * @param tokenVersion token版本号
     * @param secret       密码
     * @return 签名
     */
    public static String sign(String loginName, Integer userId, Integer providerId, Integer tokenVersion, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withClaim(Constant.LOGIN_NAME, loginName)
                    .withClaim(Constant.USER_ID, userId)
                    .withClaim(Constant.PROVIDER_ID, providerId)
                    .withClaim(Constant.TOKEN_VERSION, tokenVersion)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            return null;
        }
    }
}
