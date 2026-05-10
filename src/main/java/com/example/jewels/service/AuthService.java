package com.example.jewels.service;

import com.example.jewels.repository.UserRepository;
import com.example.jewels.repository.dto.User;
import io.github.wallawood.CertUtil;
import io.github.wallawood.annotations.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HexFormat;

/**
 * Handles certificate-based authentication for the Jewels demo. Hashes the
 * client certificate's CN and fingerprint before storing.
 *
 * <p><b>This is a toy demo.</b> Library users are responsible for their own
 * application's security. Do not use this as a reference implementation for
 * production authentication.
 */
@Component
public class AuthService {

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public User authenticate(X509Certificate cert) {
        return users.findByCertHash(identityHash(cert));
    }

    public User register(X509Certificate cert, String displayName, int level) {
        return users.create(identityHash(cert), displayName, level);
    }

    public boolean isRegistered(X509Certificate cert) {
        return users.findByCertHash(identityHash(cert)) != null;
    }

    static String identityHash(X509Certificate cert) {
        return sha256(CertUtil.cn(cert) + ":" + CertUtil.fingerprint(cert));
    }

    private static String sha256(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(input.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
