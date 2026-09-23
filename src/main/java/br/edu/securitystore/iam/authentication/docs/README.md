# Authentication

Armazena AuthenticationAccount por identityId, sessões independentes por login e refresh tokens com hash. Login exige Identity ACTIVE e conta ENABLED. Refresh rotaciona o token de uso único. Logout e revogação administrativa encerram sessões e tokens ativos. O filtro de JWT fica em `platform.security`, usando apenas contratos públicos de IAM.
