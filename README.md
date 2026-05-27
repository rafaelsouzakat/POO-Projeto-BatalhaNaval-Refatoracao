# POO-Projeto-BatalhaNaval-Refatoracao

Projeto da disciplina de Programação Orientada a Objetos: jogo Batalha Naval em Java com persistência SQLite.

## Requisitos

- Java 17
- Maven
- Conexão de internet apenas para baixar dependências na primeira execução

## Como compilar

No diretório do projeto, execute:

```bash
mvn clean package
```

## Como executar

Existem duas formas principais:

1. Usando Maven:

```bash
mvn spring-boot:run -Dspring-boot.run.main-class=batalhanaval.Main
```

2. Usando o JAR gerado (após `mvn clean package`):

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

> Importante: execute o jogo a partir da raiz do projeto, pois o banco SQLite usa o caminho relativo `data/batalha_naval.db` definido em `src/main/resources/game.properties`.

## Como testar

Execute os testes do projeto com:

```bash
mvn test
```

## Dependências

O projeto usa estas dependências principais:

- `org.springframework.boot:spring-boot-starter`
- `org.xerial:sqlite-jdbc` (driver SQLite)
- `org.springframework.boot:spring-boot-starter-test` para testes

## Como funciona a persistência

O arquivo de configurações `src/main/resources/game.properties` define:

- `db.enabled=true`
- `db.type=sqlite`
- `db.sqlite.file=data/batalha_naval.db`

Ao iniciar o jogo, o sistema cria as tabelas SQLite automaticamente e salva a partida somente quando o jogo termina com um vencedor.

## Possível causa do problema de salvamento no banco

Se o jogo estiver funcionando para você, mas não para os colegas, as prováveis causas são:

- o projeto não está sendo executado com o diretório de trabalho correto;
- o arquivo de banco usado é relativo a `data/batalha_naval.db`, então cada pessoa pode estar criando o arquivo em um local diferente;
- o diretório `data/` não existe no local em que o jogo está sendo executado;
- a partida não terminou completamente; o código só salva o jogo quando uma partida chega ao fim e o vencedor é definido.

### O que verificar

- executar o comando a partir da raiz do projeto;
- confirmar que `data/batalha_naval.db` foi criado na raiz do projeto;
- confirmar que `db.enabled=true` está ativo no `game.properties` em uso;
- verificar se o jogo chegou ao fim (após o fim a mensagem de sucesso aparece).

## Observação

Se quiser garantir um caminho fixo, você pode alterar `db.sqlite.file` para um caminho absoluto no `game.properties`, por exemplo:

```properties
db.sqlite.file=/caminho/completo/para/data/batalha_naval.db
```

Isso evita diferenças entre máquinas e diretórios de trabalho.
