<!-- design from: "https://hellocode.space/en/double-slider-registration-and-login-page-dengan-html-css-javascript-tutorial/" -->

<template>
  <h1>Maze Bank</h1>
  <div :class="`container ${containerClasses}`" id="container">
    <div class="form-container register-container">
      <div class="form">
        <h1>Registeer</h1>
        <input type="text" placeholder="Voornaam">
        <input type="text" placeholder="Achternaam">
        <input type="text" placeholder="BSN">
        <input type="email" placeholder="Email">
        <input type="tel" placeholder="Telefoonnummer">
        <input type="password" placeholder="Wachtwoord">
        <input type="date" placeholder="Geboortedatum">
        <p id="error">{{ errorMessage }}</p>
        <button @click="handleRegisterClick">Register</button>
      </div>
    </div>

    <div class="form-container login-container">
      <div class="form">
        <h1>Login</h1>
        <input type="email" placeholder="Email">
        <input type="password" placeholder="Password">
        <div class="content">
          <div class="pass-link">
            <a href="#">Wachtwoord vergeten?</a>
          </div>
        </div>
        <button @click="handleLoginClick">Login</button>
      </div>
    </div>

    <div class="overlay-container">
      <div class="overlay">
        <div class="overlay-panel overlay-left">
          <h1 class="title">Log hier in</h1>
          <button class="ghost" id="login" @click="handleOpenLogin">Login
            <i class="lni lni-arrow-left login"></i>
          </button>
        </div>
        <div class="overlay-panel overlay-right">
          <h1 class="title">Nog geen <br> account?</h1>
          <button class="ghost" id="register" @click="handleOpenRegisrer">Register
            <i class="lni lni-arrow-right register"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const containerClasses = ref('')
const errorMessage = ref('')

const handleRegisterClick = () => {
  errorMessage.value = 'Er is iets fout gegaan'
}

const handleLoginClick = () => {
  console.log('login')
}

const handleOpenRegisrer = () => {
  containerClasses.value = 'right-panel-active'
}

const handleOpenLogin = () => {
  containerClasses.value = ''
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Poppins");

* {
  box-sizing: border-box;
}

#error {
  margin-bottom: 0px;
  color: #fd5252;
}


h1 {
  text-align: center;
  margin-top: 2vh;
}

a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
  transition: 0.3s ease-in-out;
}

a:hover {
  color: #4bb6b7;
}

.content {
  display: flex;
  width: 100%;
  height: 50px;
  align-items: center;
  justify-content: space-around;
}

.content .checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
}

.content input {
  accent-color: #333;
  width: 12px;
  height: 12px;
}

.content label {
  font-size: 14px;
  user-select: none;
  padding-left: 5px;
}

button {
  position: relative;
  border-radius: 20px;
  border: 1px solid #4bb6b7;
  background-color: #4bb6b7;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  margin: 10px;
  padding: 12px 80px;
  letter-spacing: 1px;
  text-transform: capitalize;
  transition: 0.3s ease-in-out;
}

button:hover {
  letter-spacing: 3px;
}

button:active {
  transform: scale(0.95);
}

button:focus {
  outline: none;
}

button.ghost {
  background-color: rgba(225, 225, 225, 0.2);
  border: 2px solid #fff;
  color: #fff;
}

button.ghost i {
  position: absolute;
  opacity: 0;
  transition: 0.3s ease-in-out;
}

button.ghost i.register {
  right: 70px;
}

button.ghost i.login {
  left: 70px;
}

button.ghost:hover i.register {
  right: 40px;
  opacity: 1;
}

button.ghost:hover i.login {
  left: 40px;
  opacity: 1;
}

.form {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 50px;
  height: 100%;
  text-align: center;
}

input {
  background-color: #eee;
  border-radius: 10px;
  border: none;
  padding: 12px 15px;
  margin: 8px 0;
  width: 100%;
}

/* input type date */
input[type="date"] {
  background-color: #eee;
  border-radius: 10px;
  border: none;
  padding: 12px 15px;
  margin: 8px 0;
  /* width: 100%;
  height: 50px; */
}

.container {
  margin-top: 2vh;
  background-color: #fff;
  border-radius: 25px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.10), 0 10px 10px rgba(0, 0, 0, 0.10);
  position: relative;
  overflow: hidden;
  width: 768px;
  max-width: 100%;
  min-height: 660px;
}

.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  transition: all 0.6s ease-in-out;
}

.login-container {
  left: 0;
  width: 50%;
  z-index: 2;
}

.container.right-panel-active .login-container {
  transform: translateX(100%);
}

.register-container {
  left: 0;
  width: 50%;
  opacity: 0;
  z-index: 1;
}

.container.right-panel-active .register-container {
  transform: translateX(100%);
  opacity: 1;
  z-index: 5;
  animation: show 0.6s;
}

@keyframes show {

  0%,
  49.99% {
    opacity: 0;
    z-index: 1;
  }

  50%,
  100% {
    opacity: 1;
    z-index: 5;
  }
}

.overlay-container {
  position: absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: transform 0.6s ease-in-out;
  z-index: 100;
}

.container.right-panel-active .overlay-container {
  transform: translate(-100%);
}

.overlay {
  background-image: url('src/assets/login.gif');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 0 0;
  color: #fff;
  position: relative;
  left: -100%;
  height: 100%;
  width: 200%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.overlay::before {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: linear-gradient(to top,
      rgba(46, 94, 109, 0.4) 40%,
      rgba(46, 94, 109, 0));
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

.overlay-panel {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 40px;
  text-align: center;
  top: 0;
  height: 100%;
  width: 50%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.overlay-left {
  transform: translateX(-20%);
}

.container.right-panel-active .overlay-left {
  transform: translateX(0);
}

.overlay-right {
  right: 0;
  transform: translateX(0);
}

.container.right-panel-active .overlay-right {
  transform: translateX(20%);
}
</style>