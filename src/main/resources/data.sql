-- População inicial do banco FocusFlow
INSERT INTO tasks (title, description, time_estimated, completed, created_at)
VALUES
(   
    'Estudar Spring Boot',
    'Ler documentação oficial e entender o ciclo de vida de um projeto REST em Spring Boot. \
    Criar um pequeno CRUD de tarefas e testar endpoints pelo Postman.',
    120,
    FALSE,
    CURRENT_TIMESTAMP
);

INSERT INTO tasks (title, description, time_estimated, completed, created_at)
VALUES
(
    'Explorar API Gemini',
    'Fazer integração básica com a API gratuita do Gemini. \
    Testar prompts curtos e observar as respostas do modelo. \
    Depois, pensar em como contextualizar a tarefa atual na requisição.',
    90,
    FALSE,
    CURRENT_TIMESTAMP
);

INSERT INTO tasks (title, description, time_estimated, completed, created_at)
VALUES
(
    'Implementar frontend estático',
    'Criar página simples com HTML, CSS e JavaScript para consumir os endpoints REST. \
    Mostrar a lista de tarefas e permitir criar novas sem recarregar a página.',
    180,
    FALSE,
    CURRENT_TIMESTAMP
);

INSERT INTO tasks (title, description, time_estimated, completed, created_at)
VALUES
(
    'Ajustar deploy no Heroku',
    'Preparar o projeto para deploy no Heroku ou Render.com. \
    Ajustar o build.gradle/pom.xml e adicionar configurações de ambiente.',
    60,
    TRUE,
    CURRENT_TIMESTAMP
);
