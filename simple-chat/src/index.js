import './index.css';

const form = document.querySelector('form');
const input = document.querySelector('.form-input');
const messageDiv = document.querySelector('.messages');
const swapbtn = document.getElementById('change_user');
const avatar = document.querySelector('.ava-img');

let currentUser = 'me'; 
let companion = '';

const currentUrl = window.location.href;
const url = new URL(currentUrl);
const chatId = url.searchParams.get('id'); 
const chats = getMessagesFromLocalStorage(); 

if (chatId && chats[chatId]) {
    companion = chats[chatId].participants.find(p => p !== currentUser) || 'Собеседник';
    document.querySelector('.username').textContent = companion; 
    avatar.src = chats[chatId].image;
    displayMessages(chatId);
} else {
    console.log("chatId не найден");
}

swapbtn.textContent = currentUser;

form.addEventListener('submit', handleSubmit);
swapbtn.addEventListener('click', swapUsers);

function handleSubmit(event) {
    event.preventDefault();
    const messageText = input.value.trim();

    if (messageText) {
        const message = {
            text: messageText,
            sender: currentUser,
            time: new Date().toISOString()
        };

        saveMessagesToLocalStorage(chatId, message);
        input.value = '';
        addNewMesage(message);
        scrollToBottom();
    }
}

function swapUsers() {
    currentUser = currentUser === 'me' ? companion : 'me';
    updateHeader(); 
}

function updateHeader() {
    document.querySelector('.username').textContent = currentUser === 'me' ? companion : currentUser;
    swapbtn.textContent = currentUser;
}

function saveMessagesToLocalStorage(chatId, message) {
    const chats = getMessagesFromLocalStorage();

    if (!chats[chatId]) {
        chats[chatId] = { 
            id: chatId, 
            name: `Чат ${chatId}`, 
            participants: [currentUser, companion], 
            messages: [] 
        };
    }

    chats[chatId].messages.push(message);
    localStorage.setItem('chats', JSON.stringify(chats)); 
}

function getMessagesFromLocalStorage() {
    const storedChats = localStorage.getItem('chats');
    return storedChats ? JSON.parse(storedChats) : {};
}

function displayMessages(chatId) {
    const chats = getMessagesFromLocalStorage();
    const messages = chats[chatId] ? chats[chatId].messages : [];

    messageDiv.innerHTML = '';

    if (messages.length === 0) {
        return; 
    }

    messages.forEach((message) => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message-item', message.sender === 'me' ? 'user2' : 'user');

        messageElement.innerHTML = `
            <strong>${message.sender}</strong> <em class='timestamp'>${new Date(message.time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit'})}</em><br>
            ${message.text}
        `;
        messageDiv.appendChild(messageElement);
    });

    scrollToBottom();
}

function scrollToBottom() {
    messageDiv.scrollTop = messageDiv.scrollHeight;
}

function addNewMesage(message){
    const messageElement = document.createElement('div');
    messageElement.classList.add('message-item', message.sender === 'me' ? 'user2' : 'user');
    messageElement.classList.add('bounceIn');
    messageElement.innerHTML = `
        <strong>${message.sender}</strong> <em class='timestamp'>${new Date(message.time).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit'})}</em><br>
        ${message.text}
    `;
    messageElement.addEventListener('animationend', () => {
        messageElement.classList.remove('bounceIn');
    });

    messageDiv.appendChild(messageElement);
}