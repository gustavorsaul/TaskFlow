# TaskFlow – Gerenciador de Tarefas com Assistente de IA

Um sistema para gestão de tarefas, desenvolvido com **Spring Boot** e **JavaScript Puro (Vanilla JS)**. O projeto também oferece integração com inteligência artificial a partir da **API do Gemini (Google AI)**, permitindo que o usuário interaja com uma IA para obter insights, sugestões e apoio contextual baseado em cada tarefa cadastrada.

---

## Principais Funcionalidades

### Gerenciamento de Tarefas:
- Criar, editar e remover tarefas.
- Visualizar detalhes da tarefa selecionada.

### Assistente de IA integrado:
- Enviar prompts para o Gemini com base no contexto da tarefa.

### API REST completa:
- CRUD completo de tarefas.
- Endpoint dedicado à inteligência artificial.

### Persistência de dados:
- Banco PostgreSQL configurado automaticamente via Docker.

---

## Arquitetura e Design

O projeto segue uma arquitetura em camadas limpa, separando responsabilidades:


### Tecnologias Utilizadas

**Back-end:**
* **Java 21**
* **Spring Boot** (Web, Data JPA)
* **PostgreSQL** (Banco de dados)
* **Maven** (Gerenciador de dependências)

**Front-end:**
* **HTML e CSS** 
* **JavaScript** (Lógica de estado, manipulação de DOM e consumo de API via `fetch`)

**Integração com IA:**
* **Gemini API (Google AI)**

**Infraestrutura:**
* **Docker** & **Docker Compose** (Para orquestração de containers do App e do Banco)

---

## Endpoints Principais

### Tarefas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tasks` | Lista todas as tarefas |
| GET | `/api/tasks/{id}` | Busca uma tarefa específica |
| POST | `/api/tasks` | Cria nova tarefa |
| PUT | `/api/tasks/{id}` | Atualiza uma tarefa existente |
| DELETE | `/api/tasks/{id}` | Remove uma tarefa |

### IA (Gemini)

| Método | Endpoint | Corpo esperado |
|--------|----------|----------------|
| POST | `/api/tasks/{id}/ai` | `{ "apiKey": "...", "prompt": "..." }` |

---

## Como Rodar o Projeto

A forma mais simples é utilizando **Docker Compose**, que sobe automaticamente o PostgreSQL e a aplicação.

### Pré-requisitos
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando.

### Passo a Passo

#### 1. Clone o repositório
```bash
git clone https://github.com/gustavorsaul/TaskFlow.git
cd TaskFlow
```

#### 2. Execute o Docker Compose
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute:
```bash
docker-compose up --build
```

#### 3. Acesse a aplicação
Abra seu navegador e vá para:
```
http://localhost:8080
```

### Gerenciamento do Banco de Dados (opcional)
Para visualização do banco de dados, utilize um cliente externo (como DBeaver) com as credenciais:

- **Host**: localhost
- **Porta**: 5432
- **Database**: taskflow
- **User**: postgres
- **Password**: admin

---

## Estrutura do projeto

```
taskflow/
├── src/
│   ├── main/
│   │   ├── java/com/taskflow/
│   │   │   ├── controller/    # Endpoints REST (inclui IA)
│   │   │   ├── service/       # Regras de negócio e integração com Gemini
│   │   │   ├── repository/    # JPA repositories
│   │   │   ├── model/         # Entidade Task
│   │   └── resources/
│   │       └── static/        # Front-end (index.html, style.css, script.js)
├── Dockerfile                 # Build da imagem Java
├── docker-compose.yml         # App + PostgreSQL + Volume
└── pom.xml                    # Dependências Maven
```

---

## Melhorias Futuras

- [ ] Painel de IA com histórico completo do chat
- [ ] Modo escuro
- [ ] Suporte a múltiplos usuários (autenticação com Spring Security / JWT)
- [ ] Organização de tarefas em listas ou categorias
