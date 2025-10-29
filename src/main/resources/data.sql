-- População inicial do banco FocusFlow
INSERT INTO tasks (title, description, completed)
VALUES
(   
    'Estudar Spring Boot',
    'Ler documentação oficial e entender o ciclo de vida de um projeto REST em Spring Boot. \
    Criar um pequeno CRUD de tarefas e testar endpoints pelo Postman.',
    FALSE
);

INSERT INTO tasks (title, description, completed)
VALUES
(
    'Explorar API Gemini',
    'Fazer integração básica com a API gratuita do Gemini. \
    Testar prompts curtos e observar as respostas do modelo. \
    Depois, pensar em como contextualizar a tarefa atual na requisição.',
    FALSE
);

INSERT INTO tasks (title, description, completed)
VALUES
(
    'Implementar frontend estático',
    'Criar página simples com HTML, CSS e JavaScript para consumir os endpoints REST. \
    Mostrar a lista de tarefas e permitir criar novas sem recarregar a página.',
    FALSE
);

INSERT INTO tasks (title, description, completed)
VALUES
(
    'Ajustar deploy no Heroku',
    'Preparar o projeto para deploy no Heroku ou Render.com. \
    Ajustar o build.gradle/pom.xml e adicionar configurações de ambiente.',
    TRUE
);
