<!-- design from: "https://hellocode.space/en/double-slider-registration-and-login-page-dengan-html-css-javascript-tutorial/" -->

<template>
  <h1>Maze Bank</h1>
  <div :class="`container ${containerClasses}`" id="container">
    <div class="form-container register-container">
      <div class="form">
        <h1>Registreer</h1>
        <input type="text" placeholder="Voornaam" v-model="firstName">
        <input type="text" placeholder="Achternaam" v-model="lastName">
        <input type="text" placeholder="BSN" v-model="bsn">
        <input type="email" placeholder="Email" v-model="emailRegister">
        <input type="tel" placeholder="Telefoonnummer" v-model="phoneNumber">
        <input type="password" placeholder="Wachtwoord" v-model="passwordRegister">
        <input type="date" placeholder="Geboortedatum" v-model="dateOfBirth">
        <p id="error">{{ errorMessage }}</p>
        <button class="btn-primary" @click="handleRegisterClick">Registreer</button>
      </div>
    </div>

    <div class="form-container login-container">
      <div class="form">
        <h1>Login</h1>
        <input @keydown.enter="handleLoginClick" type="email" placeholder="Email" v-model="email">
        <input @keydown.enter="handleLoginClick" type="password" placeholder="Password" v-model="password">
        <p id="error">{{ errorMessage }}</p>
        <button class="btn-primary" @click="handleLoginClick">Login</button>
      </div>
    </div>

    <div class="overlay-container">
      <div class="overlay">
        <div class="overlay-panel overlay-left">
          <h1 class="title">Log hier in</h1>
          <button class="btn-secondary" id="login" @click="handleOpenLogin">Login
            <i class="lni lni-arrow-left login"></i>
          </button>
        </div>
        <div class="overlay-panel overlay-right">
          <h1 class="title">Nog geen <br> account?</h1>
          <button class="btn-secondary" id="register" @click="handleOpenRegisrer">Registreer
            <i class="lni lni-arrow-right register"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import { useRouter } from 'vue-router';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import Login from "../interfaces/requests/Login.ts";
import Register from "../interfaces/requests/Register.ts";

const authenticationStore = useAuthenticationStore();
const router = useRouter();

const containerClasses = ref('');
const errorMessage: any = ref('');
const firstName = ref('');
const lastName = ref('');
const bsn: any = ref('');
const emailRegister = ref('');
const phoneNumber = ref('');
const passwordRegister = ref('');
const dateOfBirth = ref('');
const password = ref('');
const email = ref('');

const handleRegisterClick = async () => {
  if (!checkRegitserFields()) return;

  // register the user
  const registerRequest: Register = {
    firstName: firstName.value,
    lastName: lastName.value,
    bsn: bsn.value,
    email: emailRegister.value,
    phoneNumber: phoneNumber.value,
    password: passwordRegister.value,
    dateOfBirth: dateOfBirth.value
  }

  await authenticationStore.register(registerRequest);

  // check if the user is logged in
  if (authenticationStore.isLoggedIn) {
    router.push('/dashboard');
    errorMessage.value = '';
  } else {
    showErrorMessage('Er is iets fout gegaan bij het registreren, neem contact op met de bank');
  }
}

const handleLoginClick = async () => {
  // check if all fields are filled in
  if (email.value === '' || password.value === '') {
    showErrorMessage('Vul alle velden in');
    return;
  }

  const loginRequest: Login = {
    email: email.value,
    password: password.value
  }

  await authenticationStore.login(loginRequest);

  // check if the user is logged in
  if (authenticationStore.isLoggedIn) {
    router.push('/dashboard');
    errorMessage.value = '';
  } else {
    showErrorMessage('Email of wachtwoord is onjuist');
  }
}

const handleOpenRegisrer = () => {
  containerClasses.value = 'right-panel-active';
}

const handleOpenLogin = () => {
  containerClasses.value = '';
}


const checkRegitserFields = () => {
  // check if all fields are filled in
  if (firstName.value === '' || lastName.value === '' || bsn.value === '' || emailRegister.value === '' || phoneNumber.value === '' || passwordRegister.value === '' || dateOfBirth.value === '') {
    showErrorMessage('Vul alle velden in');
    return false;
  }

  // check bsn
  if (bsn.value.length !== 9) {
    showErrorMessage('BSN moet 9 cijfers bevatten');
    return false;
  }

  // check email
  const emailRegex = new RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');
  if (!emailRegex.test(emailRegister.value)) {
    showErrorMessage('Email is niet geldig');
    return false;
  }

  // check phoneNumber
  if (phoneNumber.value.length !== 10) {
    showErrorMessage('Telefoonnummer moet 10 cijfers bevatten');
    return false;
  }

  // check password
  const passwordRegex = new RegExp('^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};\':\"\\\\|,.<>\\/?]).{8,}$');
  if (!passwordRegex.test(passwordRegister.value)) {
    showErrorMessage('Wachtwoord moet minimaal 8 karakters bevatten, 1 hoofdletter, 1 kleine letter, 1 cijfer en 1 speciaal karakter');
    return false;
  }

  // check if the date of birth makes the user 18 years or older
  // check if date of birth is filled in
  const dateOfBirthDate = new Date(dateOfBirth.value);
  const today = new Date();
  let age = today.getFullYear() - dateOfBirthDate.getFullYear();
  const month = today.getMonth() - dateOfBirthDate.getMonth();
  if (month < 0 || (month === 0 && today.getDate() < dateOfBirthDate.getDate())) {
    age--;
  }
  if (age < 18) {
    showErrorMessage('Je moet 18 jaar of ouder zijn om een account te maken');
    return false;
  }
  return true;
}

const showErrorMessage = (message: String) => {
  errorMessage.value = message
}
</script>

<style scoped>
/* deze css exporteren naar main */
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

.btn-secondary {
  background: #fff;
  border: none;
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