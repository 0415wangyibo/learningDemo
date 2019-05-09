package com.wangyb.learningdemo.authentication.service;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.StringUtils;
import org.springframework.stereotype.Service;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:30
 * Modified By:
 * Description:
 */
@Service
public class MyPasswordService extends DefaultPasswordService {

    public static final String MY_HASH_ALGORITHM = "SHA-512";
    public static final int MY_HASH_ITERATIONS = 1;
    public static final String MY_PRIVATE_SALT = "wangyb123";

    private HashService hashService;

    public MyPasswordService() {
        DefaultHashService hashService = new DefaultHashService();
        hashService.setHashAlgorithmName(MY_HASH_ALGORITHM);
        hashService.setHashIterations(MY_HASH_ITERATIONS);
        hashService.setGeneratePublicSalt(true);
        hashService.setPrivateSalt(createByteSource(MY_PRIVATE_SALT));
        this.hashService = hashService;
    }

    @Override
    public String encryptPassword(Object plaintext) {
        Hash hash = hashPassword(plaintext);
        StringBuilder sb = new StringBuilder().append(hash.toBase64()).append("$");
        ByteSource salt = hash.getSalt();
        if (salt != null) {
            sb.append(salt.toBase64());
        }
        return sb.toString();
    }

    @Override
    public Hash hashPassword(Object plaintext) {
        ByteSource plaintextBytes = createByteSource(plaintext);
        if (plaintextBytes == null || plaintextBytes.isEmpty()) {
            return null;
        }
        HashRequest request = createHashRequest(plaintextBytes);
        return hashService.computeHash(request);
    }

    @Override
    public boolean passwordsMatch(Object plaintext, Hash saved) {
        ByteSource plaintextBytes = createByteSource(plaintext);

        if (saved == null || saved.isEmpty()) {
            return plaintextBytes == null || plaintextBytes.isEmpty();
        } else {
            if (plaintextBytes == null || plaintextBytes.isEmpty()) {
                return false;
            }
        }

        HashRequest request = buildHashRequest(plaintextBytes, saved);

        Hash computed = this.hashService.computeHash(request);

        return saved.equals(computed);
    }

    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String saved) {
        ByteSource plaintextBytes = createByteSource(submittedPlaintext);

        if (saved == null || saved.length() == 0) {
            return plaintextBytes == null || plaintextBytes.isEmpty();
        } else {
            if (plaintextBytes == null || plaintextBytes.isEmpty()) {
                return false;
            }
        }

        String[] parts = saved.split("\\$");
        String saltBase64 = parts[1];

        ByteSource salt = null;

        if (StringUtils.hasLength(saltBase64)) {
            byte[] saltBytes = Base64.decode(saltBase64);
            salt = ByteSource.Util.bytes(saltBytes);
        }

        byte[] digest = Base64.decode(parts[0]);

        SimpleHash hash = new SimpleHash(MY_HASH_ALGORITHM);
        hash.setBytes(digest);
        hash.setSalt(salt);
        hash.setIterations(MY_HASH_ITERATIONS);

        return passwordsMatch(submittedPlaintext, hash);
    }
}
