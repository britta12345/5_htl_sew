<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="login" class="login-form">
      <div class="input-group">
        <label for="username">Benutzername:</label>
        <input type="text" v-model="username" id="username" required />
      </div>
      <div class="input-group">
        <label for="password">Passwort:</label>
        <input type="password" v-model="password" id="password" required />
      </div>
      <button type="submit">Einloggen</button>
    </form>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "",
      password: "",
      errorMessage: ""
    };
  },
  methods: {
    async login() {
      const credentials = {
        username: this.username,
        password: this.password
      };

      console.log('Sende Login-Daten:', credentials); // Debugging

      try {
        const response = await fetch('http://localhost:8082/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(credentials)
        });

        console.log('Antwort erhalten:', response); // Debugging

        if (response.ok) {
          const data = await response.json();
          console.log('Token erhalten:', data.token); // Debugging
          localStorage.setItem('authToken', data.token);
          this.$router.push('/songs');
        } else {
          const errorText = await response.text();
          console.error('Fehlerhafte Antwort:', response.status, errorText);
          this.errorMessage = "Falscher Benutzername oder Passwort!";
        }
      } catch (error) {
        console.error('Login fehlgeschlagen:', error);
        this.errorMessage = "Ein Fehler ist aufgetreten. Versuchen Sie es später noch einmal.";
      }
    }
  }
};
</script>

<style scoped>
/* Grundlegende Stile für die Login-Seite */
.login-container {
  font-family: 'Arial', sans-serif;
  background-color: #f1e3dd; /* Cremefarben als Hintergrund */
  padding: 50px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  flex-direction: column;
}

h2 {
  color: #667292; /* Graublauer Farbton für den Titel */
  font-size: 36px;
  text-align: center;
  margin-top: 30px;
}

/* Formular-Stile */
.login-form {
  background-color: #bccad6; /* Heller Blauton für den Formular-Hintergrund */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 400px;
}

.input-group {
  margin-bottom: 20px;
}

label {
  font-size: 14px;
  color: #667292; /* Graublauer Farbton für die Beschriftung */
  margin-bottom: 5px;
}

input[type="text"],
input[type="password"] {
  width: 90%;
  padding: 10px;
  border: 1px solid #bccad6; /* Heller Blauton für den Rand */
  border-radius: 4px;
  font-size: 16px;
  background-color: #f1e3dd; /* Heller Hintergrund passend zur Seite */
  margin-bottom: 10px;
}

/* Button */
button {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  background-color: #8d9db6; /* Dunklerer Blauton für den Button-Hintergrund */
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
}

button:hover {
  background-color: #667292; /* Graublauer Farbton beim Hover-Effekt */
}

/* Fehlernachricht */
.error-message {
  color: #e74c3c; /* Rote Farbe für Fehler */
  font-size: 14px;
  margin-top: 20px;
  text-align: center;
}

/* Optional: Eingabefeld bei Fokus */
input:focus {
  outline: none;
  border-color: #8d9db6;
  box-shadow: 0 0 5px rgba(141, 157, 182, 0.5);
}
</style>
