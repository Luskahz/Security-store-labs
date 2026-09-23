package br.edu.securitystore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.securitystore.iam.authentication.infra.security.jwt.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import br.edu.securitystore.iam.authentication.core.domain.RefreshToken;
import br.edu.securitystore.iam.authentication.core.domain.AuthenticationAccount;
import br.edu.securitystore.iam.authentication.core.repository.AuthenticationRepository;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.springframework.transaction.support.TransactionTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IamFlowTests {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;@Autowired JwtService jwt;
 @Autowired AuthenticationRepository authenticationRepository;@Autowired TransactionTemplate transactions;
 private JsonNode login(String email,String password)throws Exception{
  var response=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
   .content(json.writeValueAsString(java.util.Map.of("email",email,"password",password)))).andReturn().getResponse();
  org.junit.jupiter.api.Assertions.assertEquals(200,response.getStatus(),response.getContentAsString());
  return json.readTree(response.getContentAsString());
 }
 private String bearer(String token){return "Bearer "+token;}
 @Test void loginRefreshRotationAndLogout()throws Exception{
  JsonNode first=login("aluno@lab.local","Aluno123!");
  String access=first.get("accessToken").asText(),refresh=first.get("refreshToken").asText();
  var verified=jwt.verify(access);org.junit.jupiter.api.Assertions.assertNotNull(verified.sessionId());
  mvc.perform(get("/auth/me").header("Authorization",bearer(access))).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("aluno@lab.local"));
  mvc.perform(get("/auth/me/sessions").header("Authorization",bearer(access))).andExpect(status().isOk()).andExpect(jsonPath("$[0].current").value(true));
  var result=mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("refreshToken",refresh)))).andExpect(status().isOk()).andReturn();
  JsonNode second=json.readTree(result.getResponse().getContentAsString());
  mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("refreshToken",refresh)))).andExpect(status().isUnauthorized());
  mvc.perform(post("/auth/logout").header("Authorization",bearer(second.get("accessToken").asText()))).andExpect(status().isOk());
  mvc.perform(get("/auth/me").header("Authorization",bearer(access))).andExpect(status().isUnauthorized());
  mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("refreshToken",second.get("refreshToken").asText())))).andExpect(status().isUnauthorized());
 }
 @Test void invalidCredentialsExpiredJwtAndPermissions()throws Exception{
  mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"aluno@lab.local\",\"password\":\"errada\"}")).andExpect(status().isUnauthorized());
  mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"inexistente@lab.local\",\"password\":\"errada\"}")).andExpect(status().isUnauthorized());
  JsonNode student=login("aluno@lab.local","Aluno123!");
  String access=student.get("accessToken").asText();
  mvc.perform(get("/auth/me").header("Authorization","Bearer garbage")).andExpect(status().isUnauthorized());
  var sid=jwt.verify(access).sessionId();
  String expired=jwt.issue(2L,sid,Instant.now().minusSeconds(3600));
  mvc.perform(get("/auth/me").header("Authorization",bearer(expired))).andExpect(status().isUnauthorized());
  mvc.perform(get("/admin/users").header("Authorization",bearer(access))).andExpect(status().isForbidden());
  JsonNode admin=login("admin@lab.local","Admin123!");
  mvc.perform(get("/admin/users").header("Authorization",bearer(admin.get("accessToken").asText()))).andExpect(status().isOk());
 }
 @Test void disablingIdentityRevokesSession()throws Exception{
  String student=login("aluno@lab.local","Aluno123!").get("accessToken").asText();
  String admin=login("admin@lab.local","Admin123!").get("accessToken").asText();
  mvc.perform(patch("/admin/users/2/disable").header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/auth/me").header("Authorization",bearer(student))).andExpect(status().isUnauthorized());
  mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"aluno@lab.local\",\"password\":\"Aluno123!\"}")).andExpect(status().isUnauthorized());
  mvc.perform(patch("/admin/users/2/enable").header("Authorization",bearer(admin))).andExpect(status().isOk());
  login("aluno@lab.local","Aluno123!");
 }
 @Test void rolePermissionsAreDynamicAndDisabledRoleLosesAccess()throws Exception{
  String student=login("aluno@lab.local","Aluno123!").get("accessToken").asText();
  String admin=login("admin@lab.local","Admin123!").get("accessToken").asText();
  mvc.perform(get("/admin/users").header("Authorization",bearer(student))).andExpect(status().isForbidden());
  JsonNode permissions=json.readTree(mvc.perform(get("/admin/permissions").header("Authorization",bearer(admin))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
  long readPermission=0;for(JsonNode p:permissions)if(p.get("code").asText().equals("IDENTITY_USER_READ"))readPermission=p.get("id").asLong();
  var created=mvc.perform(post("/admin/roles").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
    .content("{\"name\":\"AUDITOR\",\"description\":\"Leitura\"}")).andExpect(status().isCreated()).andReturn();
  long roleId=json.readTree(created.getResponse().getContentAsString()).get("id").asLong();
  mvc.perform(put("/admin/roles/"+roleId+"/permissions").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
    .content("{\"permissionIds\":["+readPermission+"]}")).andExpect(status().isOk());
  mvc.perform(put("/admin/users/2/roles/"+roleId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/admin/users").header("Authorization",bearer(student))).andExpect(status().isOk());
  mvc.perform(patch("/admin/roles/"+roleId+"/disable").header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/admin/users").header("Authorization",bearer(student))).andExpect(status().isForbidden());
 }
 @Test void expiredAndRevokedRefreshTokensAreRejected()throws Exception{
  JsonNode first=login("aluno@lab.local","Aluno123!");String raw=first.get("refreshToken").asText();
  String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
  transactions.executeWithoutResult(s->{RefreshToken old=authenticationRepository.refreshByHashForUpdate(hash).orElseThrow();authenticationRepository.save(new RefreshToken(old.id(),old.sessionId(),old.tokenHash(),old.status(),old.createdAt(),Instant.now().minusSeconds(1),old.usedAt(),old.revokedAt(),old.replacedByTokenId()));});
  mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("refreshToken",raw)))).andExpect(status().isUnauthorized());
  JsonNode second=login("aluno@lab.local","Aluno123!");String raw2=second.get("refreshToken").asText();
  String hash2=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw2.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
  transactions.executeWithoutResult(s->{RefreshToken old=authenticationRepository.refreshByHashForUpdate(hash2).orElseThrow();authenticationRepository.save(old.revoked(Instant.now()));});
  mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(java.util.Map.of("refreshToken",raw2)))).andExpect(status().isUnauthorized());
 }
 @Test void adminRevocationAndOwnershipCheck()throws Exception{
  String student=login("aluno@lab.local","Aluno123!").get("accessToken").asText();
  String admin=login("admin@lab.local","Admin123!").get("accessToken").asText();
  String sessionId=jwt.verify(admin).sessionId();
  mvc.perform(delete("/auth/me/sessions/"+sessionId).header("Authorization",bearer(student))).andExpect(status().isNotFound());
  mvc.perform(delete("/admin/users/2/sessions").header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/auth/me").header("Authorization",bearer(student))).andExpect(status().isUnauthorized());
 }
 @Test void adminCreatesUserAndDuplicateEmailConflicts()throws Exception{
  String admin=login("admin@lab.local","Admin123!").get("accessToken").asText();
  String body="{\"name\":\"Nova pessoa\",\"email\":\"nova@lab.local\",\"password\":\"Senha123!\"}";
  mvc.perform(post("/admin/users").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON).content(body))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ACTIVE")).andExpect(jsonPath("$.roles[0]").value("USER"));
  mvc.perform(post("/admin/users").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
  login("nova@lab.local","Senha123!");
 }
 @Test void roleAndPermissionAssignmentCanBeRemovedWithoutDuplicates()throws Exception{
  String admin=login("admin@lab.local","Admin123!").get("accessToken").asText();
  JsonNode permissions=json.readTree(mvc.perform(get("/admin/permissions").header("Authorization",bearer(admin))).andReturn().getResponse().getContentAsString());
  long permissionId=permissions.get(0).get("id").asLong();
  long roleId=json.readTree(mvc.perform(post("/admin/roles").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
   .content("{\"name\":\"VISITOR\",\"description\":\"Teste\"}")).andReturn().getResponse().getContentAsString()).get("id").asLong();
  mvc.perform(put("/admin/roles/"+roleId+"/permissions/"+permissionId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(put("/admin/roles/"+roleId+"/permissions/"+permissionId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/admin/roles/"+roleId+"/permissions").header("Authorization",bearer(admin))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(put("/admin/users/2/roles/"+roleId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(put("/admin/users/2/roles/"+roleId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/admin/users/2/roles").header("Authorization",bearer(admin))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
  mvc.perform(delete("/admin/users/2/roles/"+roleId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(delete("/admin/roles/"+roleId+"/permissions/"+permissionId).header("Authorization",bearer(admin))).andExpect(status().isOk());
  mvc.perform(get("/admin/roles/"+roleId+"/permissions").header("Authorization",bearer(admin))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
 }
 @Test void lockedAccountLosesAuthentication()throws Exception{
  String token=login("aluno@lab.local","Aluno123!").get("accessToken").asText();
  transactions.executeWithoutResult(s->{AuthenticationAccount old=authenticationRepository.account(2L).orElseThrow();authenticationRepository.save(new AuthenticationAccount(old.identityId(),old.passwordHash(),AuthenticationAccount.Status.LOCKED,old.lastLoginAt(),old.createdAt(),Instant.now()));});
  mvc.perform(get("/auth/me").header("Authorization",bearer(token))).andExpect(status().isUnauthorized());
  mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"aluno@lab.local\",\"password\":\"Aluno123!\"}")).andExpect(status().isUnauthorized());
 }
 @Test void administrativePagesAreServed()throws Exception{
  mvc.perform(get("/login.html")).andExpect(status().isOk());
  mvc.perform(get("/admin/index.html")).andExpect(status().isOk());
  mvc.perform(get("/admin/users.html")).andExpect(status().isOk());
  mvc.perform(get("/admin/roles.html")).andExpect(status().isOk());
 }
}
