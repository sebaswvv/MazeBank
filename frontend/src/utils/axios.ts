import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

// custom instance of axios with a method to update the authorization header
interface CustomAxiosInstance extends AxiosInstance {
  updateAuthorizationHeader(token: string): void;
}

// custom request config with an authorization header
interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  headers: {
    Authorization?: string;
  };
}

const instance: CustomAxiosInstance = axios.create({
  baseURL: `//${location.hostname}:8080/`,
  headers: {
    'Content-Type': 'application/json',
    Authorization: localStorage.getItem('token')
      ? `Bearer ${localStorage.getItem('token')}`
      : '',
  },
}) as CustomAxiosInstance;

// Add a method to update the authorization header
instance.updateAuthorizationHeader = function (token) {
  const config = this.defaults as CustomAxiosRequestConfig;
  config.headers.Authorization = `Bearer ${token}`;
};

export default instance;
