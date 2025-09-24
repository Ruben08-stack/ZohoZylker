// Define variables for game state
let secretNumber;
let attempts;
const maxAttempts = 10;

// Function to initialize the game
function initializeGame() {
    // Generate a random number as the secret number
    secretNumber = Math.floor(Math.random() * 100) + 1;
    attempts = 0;
    
    // Display initial message or game instructions
    displayMessage("Welcome to Guess the Number!");
    displayMessage("Try to guess the number between 1 and 100.");
    displayMessage("You have " + maxAttempts + " attempts.");
}

// Function to handle user's guess
function handleGuess() {
    const userGuess = parseInt(document.getElementById("guessInput").value);
    attempts++;
    
    if (userGuess === secretNumber) {
        displayMessage("Congratulations! You guessed the number in " + attempts + " attempts.");
        initializeGame(); // Restart the game
    } else if (attempts >= maxAttempts) {
        displayMessage("Sorry, you've run out of attempts. The correct number was " + secretNumber + ".");
        initializeGame(); // Restart the game
    } else if (userGuess < secretNumber) {
        displayMessage("Try a higher number.");
    } else {
        displayMessage("Try a lower number.");
    }
    
    // Clear the input field
    document.getElementById("guessInput").value = "";
}

// Function to display messages to the user
function displayMessage(message) {
    const messageContainer = document.getElementById("messageContainer");
    const messageElement = document.createElement("p");
    messageElement.textContent = message;
    messageContainer.appendChild(messageElement);
}

// Initialize the game when the page loads
window.onload = initializeGame;
