package br.edu.securitystore.iam.authentication.core.port;
public interface PasswordVerifier{boolean matches(String raw,String hash);String hash(String raw);}
