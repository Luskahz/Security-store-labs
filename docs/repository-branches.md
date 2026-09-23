# Branches do repositório

Verificado em 2026-09-22:

| Branch remota | Estado | Destino recomendado |
| --- | --- | --- |
| `main` | Linha atual do backend e do IAM; contém a refatoração modular e os commits posteriores. | Definir como branch padrão no GitHub. |
| `master` | Apenas `307464e Initial commit`; não é ancestral de `main`. Ainda é a branch padrão (`HEAD` remoto). | Preservar como histórico antigo, sem mesclar em `main`. |
| `refactor/modular-architecture` | Linha da primeira refatoração, em `d5eb90b`; está atrás de `main`. | Preservar até concluir a revisão do trabalho. |

`main` e `master` possuem históricos independentes. A alteração da branch padrão é uma configuração do repositório no GitHub; não é realizada por `git push`. Se houver acesso administrativo, selecionar **Settings → General → Default branch → main**. Não é necessário mesclar nem excluir `master` para fazer essa troca.
