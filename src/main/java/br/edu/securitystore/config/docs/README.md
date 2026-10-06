# Config

Documenta as configurações gerais de inicialização da aplicação. As configurações de banco, JPA, e-mail, bootstrap de administrador e recuperação de senha ficam em `src/main/resources/application.properties`; o perfil `mysql` mantém opções específicas em `application-mysql.properties`.

As propriedades aceitam variáveis de ambiente, documentadas no [README principal](../../../../../../../../README.md). O perfil `local` só é aplicado quando ativado explicitamente; seu arquivo não é versionado.
