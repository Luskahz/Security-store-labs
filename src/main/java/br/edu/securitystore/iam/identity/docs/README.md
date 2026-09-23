# Identity

Armazena nome, e-mail único, status e datas. Não guarda senha, sessão ou role. `IdentityService` implementa criação, consulta, edição, habilitação e desabilitação. Usa ports para provisionar credenciais, atribuir a role USER e revogar sessões; a implementação desses ports chama APIs públicas dos demais contextos.
