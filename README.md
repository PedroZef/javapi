# API Simples de Tarefas (Simple Tasks API)

Este projeto é uma API RESTful simples para gerenciamento de uma lista de tarefas, construída com Spring Boot.

A API permite listar, criar, atualizar e deletar tarefas. As tarefas são armazenadas em memória de forma segura para concorrência (`thread-safe`), mas serão perdidas quando a aplicação for reiniciada.

## Pré-requisitos

Para executar este projeto, você precisará ter instalado:

-   Java (versão 17 ou superior)
-   Apache Maven

## Como Executar a Aplicação

1.  **Clone o repositório** (ou navegue até o diretório do seu projeto).

2.  **Abra um terminal** na raiz do projeto.

3.  **Compile e execute o projeto** usando o Maven Wrapper. Este comando irá baixar as dependências e iniciar o servidor web embutido (Tomcat) na porta `8080`.

    -   No Windows:
        ```bash
        ./mvnw.cmd spring-boot:run
        ```
    -   No Linux ou macOS:
        ```bash
        ./mvnw spring-boot:run
        ```

4.  A API estará disponível em `http://localhost:8080/tasks`.

---

## Estrutura do Projeto

O projeto segue uma arquitetura padrão de APIs REST com Spring Boot, separando as responsabilidades em diferentes classes.

-   `ApiApplication.java`: Classe principal que inicializa a aplicação Spring Boot. É o ponto de entrada do projeto.

-   `ApiController.java`: O coração da API. Este `RestController` define todos os endpoints (`/tasks`, `/tasks/{id}`, etc.) e lida com as requisições HTTP (GET, POST, PUT, DELETE), orquestrando a lógica de negócio.

-   `Task.java`: Representa o nosso objeto de domínio. É a classe que define a estrutura de uma tarefa, contendo seu `id` e `text`. É o que a API retorna nas respostas.

-   `TaskRequest.java`: Um DTO (Data Transfer Object) usado para modelar os dados que o cliente envia para a API ao criar ou atualizar uma tarefa. Ele contém apenas os campos que o cliente pode modificar (neste caso, `text`) e as regras de validação (`@NotBlank`).

-   `pom.xml`: O arquivo de configuração do Maven. Ele define as informações do projeto, as dependências (como Spring Web, Validation) e como o projeto deve ser construído.

-   `README.md`: Este arquivo. A documentação principal do projeto.

---

## Análise de Padrões e Boas Práticas

A API implementa diversas boas práticas de desenvolvimento de software e de APIs REST, garantindo um código limpo, robusto e semântico.

1.  **Respostas HTTP Semânticas**: A API utiliza os códigos de status HTTP corretos para cada operação, seguindo os padrões REST.
    -   `201 Created` para a criação de um novo recurso, incluindo um cabeçalho `Location` que aponta para a URL do novo item.
    -   `204 No Content` para uma exclusão bem-sucedida, indicando que a operação funcionou, mas não há conteúdo para retornar.
    -   `404 Not Found` quando um recurso específico (como uma tarefa com um ID inexistente) não é encontrado.

2.  **Segurança em Concorrência (`Thread Safety`)**: O armazenamento de tarefas em memória utiliza `Collections.synchronizedList` e um `AtomicLong` para o contador de IDs. Isso garante que a API possa lidar com múltiplas requisições simultâneas de forma segura, evitando condições de corrida e inconsistência de dados.

3.  **Uso de DTO (Data Transfer Object)**: A classe `TaskRequest` atua como um DTO, separando o que o cliente envia (`text`) do modelo de domínio interno (`Task`, que inclui um `id` gerado pelo servidor). Isso torna a API mais segura e flexível a mudanças.

4.  **Validação de Entrada**: O uso da anotação `@Valid` no controller, em conjunto com `@NotBlank` no DTO, garante que dados inválidos (como um texto de tarefa vazio) sejam rejeitados antes mesmo de chegarem à lógica de negócio, retornando um erro `400 Bad Request` claro para o cliente.

5.  **Controle Fino com `ResponseEntity`**: A API utiliza `ResponseEntity` em todos os seus endpoints. Essa é uma prática recomendada no Spring, pois dá controle total sobre a resposta HTTP, permitindo customizar o código de status, os cabeçalhos e o corpo da resposta de forma explícita e clara.

---

## Guia da API

A seguir estão os detalhes de cada endpoint disponível.

### 1. Listar todas as tarefas

-   **Método:** `GET`
-   **Endpoint:** `/tasks`
-   **Descrição:** Retorna uma lista com todas as tarefas atualmente armazenadas.
-   **Exemplo de uso (curl):**
    ```bash
    curl -X GET http://localhost:8080/tasks
    ```
-   **Resposta de Sucesso (200 OK):**
    ```json
    [
        {
            "id": 1,
            "text": "Comprar pão"
        },
        {
            "id": 2,
            "text": "Estudar Spring Boot"
        }
    ]
    ```

### 2. Criar uma nova tarefa

-   **Método:** `POST`
-   **Endpoint:** `/tasks`
-   **Descrição:** Adiciona uma nova tarefa à lista.
-   **Corpo da Requisição (JSON):**
    ```json
    {
        "text": "Descrição da nova tarefa"
    }
    ```
-   **Exemplo de uso (curl):**
    ```bash
    curl -i -X POST -H "Content-Type: application/json" -d "{\"text\": \"Fazer exercícios\"}" http://localhost:8080/tasks
    ```
-   **Resposta de Sucesso (201 Created):** Retorna a tarefa recém-criada e o cabeçalho `Location` com o link para o novo recurso.
    ```http
    HTTP/1.1 201 Created
    Location: http://localhost:8080/tasks/3
    Content-Type: application/json

    {
        "id": 3,
        "text": "Fazer exercícios"
    }
    ```

### 3. Obter uma tarefa por ID

-   **Método:** `GET`
-   **Endpoint:** `/tasks/{id}`
-   **Descrição:** Retorna uma tarefa específica com base em seu ID.
-   **Exemplo de uso (curl):**
    ```bash
    curl -X GET http://localhost:8080/tasks/1
    ```
-   **Resposta de Sucesso (200 OK):**
    ```json
    {
        "id": 1,
        "text": "Comprar pão"
    }
    ```
-   **Resposta de Erro (404 Not Found):** Se o ID fornecido não existir.

### 4. Atualizar uma tarefa existente

-   **Método:** `PUT`
-   **Endpoint:** `/tasks/{id}`
-   **Descrição:** Atualiza o texto de uma tarefa com base em seu ID.
-   **Corpo da Requisição (JSON):**
    ```json
    {
        "text": "Novo texto da tarefa"
    }
    ```
-   **Exemplo de uso (curl para atualizar o item com ID 1):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -d "{\"text\": \"Comprar pão integral\"}" http://localhost:8080/tasks/1
    ```
-   **Resposta de Sucesso (200 OK):** Retorna o objeto da tarefa com os dados atualizados.
    ```json
    {
        "id": 1,
        "text": "Comprar pão integral"
    }
    ```
-   **Resposta de Erro (404 Not Found):** Se o ID fornecido não existir.

### 5. Deletar uma tarefa

-   **Método:** `DELETE`
-   **Endpoint:** `/tasks/{id}`
-   **Descrição:** Remove uma tarefa da lista com base em seu ID.
-   **Exemplo de uso (curl para deletar o item com ID 1):**
    ```bash
    curl -i -X DELETE http://localhost:8080/tasks/1
    ```
-   **Resposta de Sucesso (204 No Content):** A resposta não terá corpo, indicando que a remoção foi bem-sucedida.
    ```http
    HTTP/1.1 204 No Content
    ```
-   **Resposta de Erro (404 Not Found):** Se o ID fornecido não existir.
