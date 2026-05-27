# POO-Projeto-BatalhaNaval-Refatoracao

Projeto da disciplina de Programação Orientada a Objetos: jogo Batalha Naval em Java com persistência SQLite.

## Objetivo deste README

Este arquivo explica como preparar, compilar, executar e testar o projeto de forma reproduzível, para entrega à disciplina.

## Requisitos

- Java 17 (JDK)
- Maven 3.6+ (ou compatível)
- Espaço em disco para criar o arquivo SQLite (diretório `data/`)

O projeto não depende de serviços externos; a dependência do SQLite é fornecida pelo driver JDBC (`org.xerial:sqlite-jdbc`).

## Preparação (reproduzibilidade)

1. Abra um terminal e posicione-se na raiz do repositório.
2. Garanta que o diretório `data/` exista (o projeto usa `data/batalha_naval.db` por padrão):

```bash
mkdir -p data
```

3. Confirme a versão do Java:

```bash
java -version
```

Use JDK 17 para executar e testar o projeto.

## Compilar

No diretório do projeto, execute:

```bash
mvn clean package
```

Isso irá baixar dependências e gerar o JAR em `target/`.

## Executar

Após `mvn clean package`, execute o JAR gerado:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Execute os comandos a partir da raiz do projeto para garantir que o caminho relativo para o banco (`data/batalha_naval.db`) seja resolvido corretamente.

## Testes

Execute a suíte de testes com:

```bash
mvn test
```

Para execução reprodutível em integração contínua, use exatamente as mesmas versões do JDK e do Maven informadas acima.

## Dependências principais

- `org.springframework.boot:spring-boot-starter`
- `org.xerial:sqlite-jdbc`
- `org.springframework.boot:spring-boot-starter-test` (testes)

As dependências completas estão definidas no `pom.xml`.

## Configuração do banco (opcional)

O arquivo `src/main/resources/game.properties` define o arquivo SQLite usado pelo projeto:

- `db.enabled=true`
- `db.type=sqlite`
- `db.sqlite.file=data/batalha_naval.db`

Se preferir um local fixo para o banco, altere `db.sqlite.file` para um caminho absoluto nesse arquivo.

## Execução em CI (exemplo)

Comandos mínimos para integrar em um pipeline (por exemplo, GitHub Actions):

```bash
mkdir -p data
mvn --batch-mode -DskipTests=false clean package
mvn test
```

## Observações finais

Execute sempre a partir da raiz do projeto e use JDK 17 para garantir resultados reproduzíveis ao compilar e testar.

