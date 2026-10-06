# Platform

Reúne infraestrutura transversal. `security` configura as regras HTTP e valida o Bearer JWT com os contratos públicos de IAM. `privacy` cifra dados pessoais com AES-GCM e exige `PII_ENCRYPTION_KEY` Base64 de 32 bytes. `exception` padroniza respostas de erro.

`config` habilita execução assíncrona para o envio de e-mail de recuperação. `trafficlab` habilita o scheduler de ações sintéticas do laboratório. Essas ações usam serviços internos e são registradas no console; não representam requisições HTTP externas.

Veja a [instalação](../../../../../../../../README.md) para configurar as chaves e o banco.
