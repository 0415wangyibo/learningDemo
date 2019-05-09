package com.wangyb.learningdemo.authentication.utils;

import com.wangyb.learningdemo.authentication.config.Constant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:20
 * Modified By:
 * Description:
 */
public class JwtUtil {
    private static final long EXPIRE_TIME =432000000;


    /**
     * 校验token是否正确
     *
     * @param token
     * @param loginName
     * @param userId
     * @param organizationId
     * @param secret
     * @return
     */
    public static boolean verify(String token, String loginName,Integer userId,Integer organizationId,Integer tokenVersion, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(Constant.LOGIN_NAME, loginName)
                    .withClaim(Constant.USER_ID,userId)
                    .withClaim(Constant.ORGANIZATION_ID,organizationId)
                    .withClaim(Constant.TOKEN_VERSION,tokenVersion)
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
     * @param token
     * @return
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
     * @param token
     * @return
     */
    public static Integer getTokenVersion(String token){
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
     * @param token
     * @return
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
     * 获取token中的用户的organizationId
     *
     * @param token
     * @return
     */
    public static Integer getOrganizationId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.ORGANIZATION_ID).asInt();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 生成签名
     *
     * @param loginName
     * @return
     */
    public static String sign(String loginName,Integer userId,Integer organizationId, Integer tokenVersion,String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withClaim(Constant.LOGIN_NAME, loginName)
                    .withClaim(Constant.USER_ID,userId)
                    .withClaim(Constant.ORGANIZATION_ID,organizationId)
                    .withClaim(Constant.TOKEN_VERSION,tokenVersion)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            return null;
        }
    }
}
