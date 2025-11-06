const API_BASE = "http://localhost:8080/api/tasks";
let selectedTask = null;
let isEditing = false;

const taskListEl = document.getElementById("taskList");
const titleEl = document.getElementById("taskTitle");
const descEl = document.getElementById("taskDescription");
const taskButtonsEl = document.getElementById("taskButtons");
const aiPanelEl = document.getElementById("aiPanel");
const responseEl = document.getElementById("aiResponse");

// === Carrega tarefas ao iniciar ===
async function loadTasks() {
  const res = await fetch(API_BASE);
  const tasks = await res.json();
  renderTasks(tasks);
}

function renderTasks(tasks) {
  taskListEl.innerHTML = "";
  tasks.forEach(task => {
    const div = document.createElement("div");
    div.className = "task-item" + (selectedTask?.id === task.id ? " active" : "");
    div.innerText = task.title;
    div.onclick = () => selectTask(task.id);
    taskListEl.appendChild(div);
  });
}

async function selectTask(id) {
  const res = await fetch(`${API_BASE}/${id}`);
  if (!res.ok) {
    alert("Erro ao carregar tarefa");
    return;
  }
  const task = await res.json();
  selectedTask = task;
  // Resetar modo edição se estiver ativo
  if (isEditing) {
    cancelEdit();
  }
  titleEl.innerText = task.title;
  descEl.innerText = task.description;
  taskButtonsEl.classList.remove("hidden");
  loadTasks(); // atualiza o destaque
}

function startInlineEdit() {
  if (!selectedTask || isEditing) return;
  isEditing = true;

  // Transformar título em input
  const titleInput = document.createElement('input');
  titleInput.id = 'editTitleInput';
  titleInput.type = 'text';
  titleInput.className = 'inline-input';
  titleInput.value = selectedTask.title || '';
  titleInput.placeholder = 'Digite o novo título...';
  titleEl.innerHTML = '';
  titleEl.appendChild(titleInput);

  // Transformar descrição em textarea
  const descInput = document.createElement('textarea');
  descInput.id = 'editDescInput';
  descInput.className = 'inline-textarea';
  descInput.value = selectedTask.description || '';
  descInput.placeholder = 'Digite a nova descrição (opcional)...';
  descEl.innerHTML = '';
  descEl.appendChild(descInput);

  // Ajustar botões para salvar/cancelar
  taskButtonsEl.innerHTML = `
    <button class="btn-submit" onclick="saveEdit()">Salvar</button>
    <button class="btn-cancel" onclick="cancelEdit()">Cancelar</button>
    <button onclick="deleteTask()" style="background-color:#fea116">Excluir</button>
    <button onclick="toggleAI()">Abrir Chat IA</button>
  `;

  // Foco no título
  titleInput.focus();
}

async function saveEdit() {
  if (!isEditing || !selectedTask) return;
  const newTitle = document.getElementById('editTitleInput')?.value.trim();
  const newDescription = document.getElementById('editDescInput')?.value.trim();
  if (!newTitle) {
    alert('Por favor, insira um título.');
    return;
  }

  const res = await fetch(`${API_BASE}/${selectedTask.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ title: newTitle, description: newDescription || '' })
  });

  if (res.ok) {
    // Atualizar UI
    selectedTask.title = newTitle;
    selectedTask.description = newDescription || '';
    titleEl.innerText = selectedTask.title;
    descEl.innerText = selectedTask.description;
    isEditing = false;
    setDefaultTaskButtons();
    loadTasks();
  } else {
    alert('Erro ao atualizar tarefa');
  }
}

function cancelEdit() {
  if (!selectedTask) return;
  titleEl.innerText = selectedTask.title || 'Sem título';
  descEl.innerText = selectedTask.description || '';
  isEditing = false;
  setDefaultTaskButtons();
}

function setDefaultTaskButtons() {
  taskButtonsEl.innerHTML = `
    <button onclick="startInlineEdit()">Editar</button>
    <button onclick="deleteTask()" style="background-color:#fea116">Excluir</button>
    <button onclick="toggleAI()">Abrir Chat IA</button>
  `;
}

async function deleteTask() {
  if (!selectedTask) return;
  if (!confirm("Deseja realmente excluir esta tarefa?")) return;

  const res = await fetch(`${API_BASE}/${selectedTask.id}`, { method: "DELETE" });

  if (res.ok) {
    selectedTask = null;
    titleEl.innerText = "Selecione uma tarefa";
    descEl.innerText = "Aqui aparecerá a descrição da tarefa.";
    taskButtonsEl.classList.add("hidden");
    loadTasks();
  } else {
    alert("Erro ao excluir tarefa");
  }
}

function toggleAI() {
  aiPanelEl.classList.toggle("hidden");
}

async function sendPrompt() {
  if (!selectedTask) {
    alert("Selecione uma tarefa antes de enviar o prompt.");
    return;
  }

  const apiKey = document.getElementById("apiKey").value.trim();
  const prompt = document.getElementById("prompt").value.trim();
  const sendBtn = document.getElementById("sendPromptBtn");

  if (!apiKey || !prompt) {
    alert("Informe a API key e o prompt.");
    return;
  }

  // Bloqueia o botão para evitar múltiplas chamadas simultâneas
  if (sendBtn) {
    sendBtn.disabled = true;
    sendBtn.textContent = "Enviando...";
  }

  responseEl.innerText = "Enviando para o Gemini...";

  // Função utilitária para aguardar
  const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

  // Até 3 tentativas em caso de sobrecarga (503)
  let lastError = null;
  for (let attempt = 1; attempt <= 3; attempt++) {
    try {
      const res = await fetch(`${API_BASE}/${selectedTask.id}/ai`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ apiKey, prompt })
      });

      const text = await res.text();
      let data = null;
      try { data = JSON.parse(text); } catch {}

      // Se o backend responder 503 diretamente
      if (!res.ok) {
        if (res.status === 503 && attempt < 3) {
          responseEl.innerText = `Modelo sobrecarregado (503). Tentando novamente... [tentativa ${attempt+1}/3]`;
          await delay(500 * attempt); // backoff incremental
          continue;
        }
        throw new Error(text || `Erro HTTP ${res.status}`);
      }

      // Backend responde 200 sempre; verificar texto de erro encapsulado
      const responseText = data?.response ?? text;
      if (typeof responseText === 'string' && responseText.startsWith("Erro ao gerar a resposta: 503")) {
        if (attempt < 3) {
          responseEl.innerText = `Modelo sobrecarregado (503). Tentando novamente... [tentativa ${attempt+1}/3]`;
          await delay(500 * attempt);
          continue;
        }
        throw new Error("Modelo sobrecarregado (503). Tente novamente mais tarde.");
      }

      responseEl.innerText = responseText || "(Resposta vazia)";
      lastError = null;
      break;
    } catch (err) {
      lastError = err;
      if (attempt < 3) {
        await delay(500 * attempt);
        continue;
      }
    }
  }

  if (lastError) {
    responseEl.innerText = "Erro: " + lastError.message;
  }

  // Reabilita o botão
  if (sendBtn) {
    sendBtn.disabled = false;
    sendBtn.textContent = "Enviar";
  }
}

// Funções do Modal
function openModal() {
  document.getElementById('taskModal').style.display = 'block';
  document.getElementById('newTaskTitle').focus();
}

function closeModal() {
  document.getElementById('taskModal').style.display = 'none';
  document.getElementById('taskForm').reset();
}

// Fechar modal ao clicar fora
window.onclick = function(event) {
  const modal = document.getElementById('taskModal');
  if (event.target === modal) {
    closeModal();
  }
}

// Função para criar tarefa via modal
async function handleCreateTask(event) {
  event.preventDefault();
  
  const title = document.getElementById('newTaskTitle').value.trim();
  const description = document.getElementById('newTaskDescription').value.trim();

  if (!title) {
    alert('Por favor, insira um título para a tarefa.');
    return;
  }

  const res = await fetch(API_BASE, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ title, description })
  });

  if (res.ok) {
    closeModal();
    loadTasks();
  } else {
    alert("Erro ao criar tarefa");
  }
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  loadTasks();
});