package com.wangyb.learningdemo.authentication.shiro;
import org.apache.shiro.authc.AuthenticationToken;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:17
 * Modified By:
 * Description:
 */
public class JwtToken implements AuthenticationToken {
    private static final long serialVersionUID = 3561624190735137480L;

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
