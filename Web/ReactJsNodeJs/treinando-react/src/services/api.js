import axios from "axios";

// Configuração da URL base para a API do TMDb
const api = axios.create({
    baseURL: 'https://api.themoviedb.org/3/', // URL base
});

// Exporte o objeto api para usar em outras partes do seu código
export default api;
