// Global Variables
let currentUser = null;
let authToken = null;
let stompClient = null;
let currentSessionId = null;
let gameState = {
    board: null,
    score: 0,
    lines: 0,
    level: 1,
    gameOver: false,
    isPaused: false
};

// TODO: Add current piece state management (position, rotation, type)
// TODO: Add next piece preview functionality
// TODO: Add piece spawning and random generation logic
// TODO: Implement collision detection system
// TODO: Add line clearing and board compaction logic
// TODO: Implement score calculation based on lines cleared and level
// TODO: Add game over detection when pieces reach the top

const API_BASE = '/api';
const TETRIS_BLOCKS = [
    [[1, 1, 1, 1]],           // I
    [[1, 1], [1, 1]],         // O
    [[0, 1, 0], [1, 1, 1]],   // T
    [[1, 0, 0], [1, 1, 1]],   // L
    [[0, 0, 1], [1, 1, 1]],   // J
    [[0, 1, 1], [1, 1, 0]],   // S
    [[1, 1, 0], [0, 1, 1]]    // Z
];

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');
    
    if (savedToken && savedUser) {
        authToken = savedToken;
        currentUser = JSON.parse(savedUser);
        showGameSection();
    } else {
        showAuthSection();
    }

    // Setup keyboard controls
    document.addEventListener('keydown', handleKeyPress);
});

// ==================== Authentication ====================

function toggleForms() {
    document.getElementById('login-form').classList.toggle('hidden');
    document.getElementById('register-form').classList.toggle('hidden');
}

async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (data.success) {
            authToken = data.token;
            currentUser = data.user;
            
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showGameSection();
            connectWebSocket();
        } else {
            showError(data.message);
        }
    } catch (error) {
        showError('Login failed: ' + error.message);
    }
}

async function handleRegister(event) {
    event.preventDefault();
    
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, confirmPassword })
        });

        const data = await response.json();

        if (data.success) {
            authToken = data.token;
            currentUser = data.user;
            
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showGameSection();
            connectWebSocket();
        } else {
            showError(data.message);
        }
    } catch (error) {
        showError('Registration failed: ' + error.message);
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    currentUser = null;
    authToken = null;
    currentSessionId = null;
    
    if (stompClient) {
        stompClient.disconnect(() => {});
    }
    
    showAuthSection();
}

// ==================== UI Navigation ====================

function showAuthSection() {
    document.getElementById('auth-section').classList.remove('hidden');
    document.getElementById('game-section').classList.add('hidden');
    document.getElementById('login-form').classList.remove('hidden');
    document.getElementById('register-form').classList.add('hidden');
}

function showGameSection() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('game-section').classList.remove('hidden');
    document.getElementById('current-user').textContent = `Welcome, ${currentUser.username}`;
    updateUserStats();
}

function showMenuSection() {
    hideAllGameSections();
    document.getElementById('menu-section').classList.remove('hidden');
}

function hideAllGameSections() {
    document.getElementById('menu-section').classList.add('hidden');
    document.getElementById('game-board-section').classList.add('hidden');
    document.getElementById('join-game-section').classList.add('hidden');
    document.getElementById('stats-section').classList.add('hidden');
    document.getElementById('history-section').classList.add('hidden');
}

function backToMenu() {
    hideAllGameSections();
    showMenuSection();
}

// ==================== Game Management ====================

async function createGame() {
    try {
        const response = await fetch(`${API_BASE}/game/create-session`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const session = await response.json();
            currentSessionId = session.sessionId;
            initializeGameBoard();
            hideAllGameSections();
            document.getElementById('game-board-section').classList.remove('hidden');
            showSuccess('Game created! Waiting for opponent...');
        } else {
            showError('Failed to create game');
        }
    } catch (error) {
        showError('Error creating game: ' + error.message);
    }
}

function joinGame() {
    hideAllGameSections();
    document.getElementById('join-game-section').classList.remove('hidden');
    loadAvailableGames();
}

async function loadAvailableGames() {
    try {
        const response = await fetch(`${API_BASE}/game/waiting-sessions`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        const games = await response.json();
        const gamesList = document.getElementById('available-games-list');
        gamesList.innerHTML = '';

        if (games.length === 0) {
            gamesList.innerHTML = '<p>No available games. <a href="#" onclick="createGame()">Create one</a></p>';
            return;
        }

        games.forEach(game => {
            const gameCard = document.createElement('div');
            gameCard.className = 'game-card';
            gameCard.innerHTML = `
                <span>Session: ${game.sessionId.substring(0, 8)}...</span>
                <button class="btn btn-secondary" onclick="joinSessionGame('${game.sessionId}')">Join</button>
            `;
            gamesList.appendChild(gameCard);
        });
    } catch (error) {
        showError('Error loading games: ' + error.message);
    }
}

async function joinSessionGame(sessionId) {
    try {
        const response = await fetch(`${API_BASE}/game/join-session/${sessionId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const session = await response.json();
            currentSessionId = session.sessionId;
            initializeGameBoard();
            hideAllGameSections();
            document.getElementById('game-board-section').classList.remove('hidden');
            showSuccess('Joined game!');
        } else {
            showError('Failed to join game');
        }
    } catch (error) {
        showError('Error joining game: ' + error.message);
    }
}

function endGame() {
    if (currentSessionId) {
        fetch(`${API_BASE}/game/end-game/${currentSessionId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ winnerId: currentUser.id })
        }).then(() => {
            currentSessionId = null;
            gameState = {
                board: null,
                score: 0,
                lines: 0,
                level: 1,
                gameOver: false,
                isPaused: false
            };
            showMenuSection();
            showSuccess('Game ended');
        });
    }
    
    // TODO: Implement proper game end sequence
    // TODO: Calculate and display final statistics
    // TODO: Show game summary (duration, final score, lines cleared)
    // TODO: Update player statistics on server
    // TODO: Clean up WebSocket subscriptions
    // TODO: Reset all game state variables
    // TODO: Return to main menu with transition animation
}

function pauseGame() {
    gameState.isPaused = !gameState.isPaused;
    const button = event.target;
    button.textContent = gameState.isPaused ? 'Resume' : 'Pause';
    
    // TODO: Broadcast pause/resume state to opponent
    // TODO: Implement synchronized pause (both players must agree)
    // TODO: Add pause timer to prevent abuse
    // TODO: Update game timer to account for paused time
}

// ==================== Game Board ====================

function initializeGameBoard() {
    const boardWidth = 10;
    const boardHeight = 20;
    gameState.board = Array(boardHeight).fill(null).map(() => Array(boardWidth).fill(0));
    gameState.score = 0;
    gameState.lines = 0;
    gameState.level = 1;
    gameState.gameOver = false;
    
    // TODO: Initialize current piece with random Tetromino
    // TODO: Initialize next piece preview
    // TODO: Set up piece spawn position (typically top center)
    
    renderGameBoard();
    startGameLoop();
}

function renderGameBoard() {
    const playerCanvas = document.getElementById('player-canvas');
    const ctx = playerCanvas.getContext('2d');
    
    const blockSize = playerCanvas.width / 10;
    ctx.fillStyle = '#000';
    ctx.fillRect(0, 0, playerCanvas.width, playerCanvas.height);
    
    // Draw grid
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 0.5;
    for (let i = 0; i <= 10; i++) {
        ctx.beginPath();
        ctx.moveTo(i * blockSize, 0);
        ctx.lineTo(i * blockSize, playerCanvas.height);
        ctx.stroke();
    }
    
    for (let i = 0; i <= 20; i++) {
        ctx.beginPath();
        ctx.moveTo(0, i * blockSize);
        ctx.lineTo(playerCanvas.width, i * blockSize);
        ctx.stroke();
    }
    
    // Draw board state
    if (gameState.board) {
        gameState.board.forEach((row, y) => {
            row.forEach((cell, x) => {
                if (cell) {
                    ctx.fillStyle = '#667eea';
                    ctx.fillRect(x * blockSize + 1, y * blockSize + 1, blockSize - 2, blockSize - 2);
                }
            });
        });
    }
    
    // TODO: Render current falling piece on the board
    // TODO: Render ghost piece (preview of where piece will land)
    // TODO: Add piece rotation animation effects
    // TODO: Implement different colors for different Tetromino types
    
    updateScoreDisplay();
}

function updateScoreDisplay() {
    document.getElementById('player-score').textContent = gameState.score;
    document.getElementById('player-lines').textContent = gameState.lines;
    document.getElementById('player-level').textContent = gameState.level;
}

function startGameLoop() {
    const gameSpeed = 500 - (gameState.level * 30);
    setInterval(() => {
        if (!gameState.isPaused && !gameState.gameOver && currentSessionId) {
            // TODO: Implement core game loop logic
            // TODO: Apply gravity to current piece
            // TODO: Detect completed lines and clear them
            // TODO: Update score and level progression
            // TODO: Update game speed based on level
            // TODO: Generate next piece preview
            renderGameBoard();
            broadcastGameState();
        }
    }, gameSpeed);
}

// ==================== WebSocket ====================

function connectWebSocket() {
    const socket = new SockJS('/api/ws/tetris');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');
        
        // Subscribe to game updates
        if (currentSessionId) {
            stompClient.subscribe(`/topic/game/${currentSessionId}`, (message) => {
                const gameMessage = JSON.parse(message.body);
                handleGameMessage(gameMessage);
            });
        }
    }, (error) => {
        console.error('WebSocket connection error:', error);
    });
}

function broadcastGameState() {
    if (!stompClient || !currentSessionId || gameState.isPaused) return;
    
    const message = {
        messageType: 'GAME_STATE_UPDATE',
        sessionId: currentSessionId,
        playerId: currentUser.id,
        boardState: gameState.board,
        score: gameState.score,
        lines: gameState.lines,
        level: gameState.level,
        gameOver: gameState.gameOver,
        timestamp: Date.now()
    };
    
    stompClient.send(`/app/game/${currentSessionId}/update`, {}, JSON.stringify(message));
}

function handleGameMessage(message) {
    console.log('Received game message:', message);
    
    switch (message.messageType) {
        case 'OPPONENT_MOVE':
            // TODO: Process opponent's move and update their board state
            // TODO: Animate opponent's piece movement for visual feedback
            updateOpponentBoard(message);
            break;
        case 'GAME_STATE_UPDATE':
            updateOpponentStats(message);
            break;
        case 'PLAYER_JOINED':
            showSuccess('Opponent joined!');
            // TODO: Initialize opponent's game state
            // TODO: Start synchronized game timer
            break;
        case 'GAME_ENDED':
            handleGameEnd(message);
            break;
        case 'PLAYER_LEFT':
            showError('Opponent disconnected');
            endGame();
            break;
        case 'PIECE_LOCKED':
            // TODO: Handle when opponent locks a piece (line clear potential)
            // TODO: Update opponent's board with new locked piece
            break;
        case 'LINES_CLEARED':
            // TODO: Handle opponent's line clears (may send garbage lines)
            // TODO: Add garbage lines to player's board from bottom
            break;
    }
}

function updateOpponentBoard(message) {
    const opponentCanvas = document.getElementById('opponent-canvas');
    const ctx = opponentCanvas.getContext('2d');
    
    // TODO: Implement opponent board rendering with piece preview
    // TODO: Display opponent's active piece and next piece
    // TODO: Animate opponent's piece movements
    const blockSize = opponentCanvas.width / 10;
    ctx.fillStyle = '#000';
    ctx.fillRect(0, 0, opponentCanvas.width, opponentCanvas.height);
    
    if (message.boardState) {
        message.boardState.forEach((row, y) => {
            row.forEach((cell, x) => {
                if (cell) {
                    ctx.fillStyle = '#ff6b6b';
                    ctx.fillRect(x * blockSize + 1, y * blockSize + 1, blockSize - 2, blockSize - 2);
                }
            });
        });
    }
}

function updateOpponentStats(message) {
    document.getElementById('opponent-score').textContent = message.score || 0;
    document.getElementById('opponent-lines').textContent = message.lines || 0;
    document.getElementById('opponent-level').textContent = message.level || 1;
}

function handleGameEnd(message) {
    gameState.gameOver = true;
    if (message.winnerId === currentUser.id) {
        showSuccess('You won!');
    } else {
        showSuccess('Game ended');
    }
    setTimeout(() => {
        endGame();
    }, 3000);
}

// ==================== Statistics ====================

function viewStats() {
    hideAllGameSections();
    document.getElementById('stats-section').classList.remove('hidden');
    updateUserStats();
}

function updateUserStats() {
    if (currentUser) {
        document.getElementById('stat-high-score').textContent = currentUser.highScore;
        document.getElementById('stat-total-games').textContent = currentUser.totalGames;
        document.getElementById('stat-total-wins').textContent = currentUser.wins;
        
        const winRate = currentUser.totalGames > 0 
            ? Math.round((currentUser.wins / currentUser.totalGames) * 100)
            : 0;
        document.getElementById('stat-win-rate').textContent = winRate + '%';
    }
}

// ==================== Game History ====================

async function viewHistory() {
    hideAllGameSections();
    document.getElementById('history-section').classList.remove('hidden');
    
    try {
        const response = await fetch(`${API_BASE}/game/history`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        const games = await response.json();
        const historyList = document.getElementById('history-list');
        historyList.innerHTML = '';

        if (games.length === 0) {
            historyList.innerHTML = '<p>No games played yet.</p>';
            return;
        }

        games.forEach(game => {
            const historyItem = document.createElement('div');
            historyItem.className = 'history-item';
            
            const date = new Date(game.startedAt).toLocaleString();
            const result = game.winnerId === currentUser.id ? 'Won' : game.status;
            
            historyItem.innerHTML = `
                <div class="date">${date}</div>
                <div class="result">${result} - Session: ${game.sessionId.substring(0, 8)}...</div>
            `;
            
            historyList.appendChild(historyItem);
        });
    } catch (error) {
        showError('Error loading history: ' + error.message);
    }
}

// ==================== Input Handling ====================

function handleKeyPress(event) {
    if (!currentSessionId || gameState.gameOver || gameState.isPaused) return;

    const moveMessage = {
        userId: currentUser.id,
        sessionId: currentSessionId,
        messageType: 'MOVE',
        timestamp: Date.now()
    };

    switch (event.key) {
        case 'ArrowLeft':
            moveMessage.action = 'LEFT';
            // TODO: Implement left movement logic - check collision before moving
            event.preventDefault();
            break;
        case 'ArrowRight':
            moveMessage.action = 'RIGHT';
            // TODO: Implement right movement logic - check collision before moving
            event.preventDefault();
            break;
        case 'ArrowDown':
            moveMessage.action = 'DOWN';
            // TODO: Implement soft drop logic - move piece down faster
            event.preventDefault();
            break;
        case ' ':
            moveMessage.action = 'ROTATE_CW';
            // TODO: Implement clockwise rotation logic - check collision after rotation
            event.preventDefault();
            break;
        case 'p':
        case 'P':
            pauseGame();
            event.preventDefault();
            return;
        default:
            return;
    }

    if (stompClient && moveMessage.action) {
        stompClient.send(
            `/app/game/${currentSessionId}/move`,
            {},
            JSON.stringify(moveMessage)
        );
    }
}

// ==================== Utility Functions ====================

function showSuccess(message) {
    console.log('Success:', message);
    // TODO: Implement proper toast notification system
    // TODO: Add success message styling and auto-dismiss
}

function showError(message) {
    console.error('Error:', message);
    alert(message); // TODO: Replace with proper toast notification system
    // TODO: Add error message styling and user-friendly display
    // TODO: Implement error logging and reporting
}

// TODO: Add utility functions for:
// TODO: - Tetromino shape definitions and rotation matrices
// TODO: - Collision detection algorithms
// TODO: - Line clearing and board compaction
// TODO: - Score calculation formulas
// TODO: - Game speed and level progression curves
// TODO: - Piece spawning and bag randomization (7-bag system)
// TODO: - Sound effect management
// TODO: - Animation and particle effects
