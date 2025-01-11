import { useEffect, useState } from "react";
import api from '../../services/api';

function Home() {
    const [filmes, setFilmes] = useState([]); // Definindo estado para armazenar os filmes
    const [error, setError] = useState(null); // Estado para armazenar possíveis erros

    useEffect(() => {
        async function loadFilmes() {
            try {
                const response = await api.get("search/movie", {
                    params: {
                        api_key: "9e26bd4c9992519583035f3f69ef485f", // Sua chave de API
                        query: "batman", // Você pode colocar um termo de pesquisa aqui
                    }
                });

                // Atualizando o estado com os filmes retornados
                setFilmes(response.data.results);
            } catch (err) {
                setError('Erro ao carregar os filmes');
                console.error(err);
            }
        }

        loadFilmes();
    }, []);

    return (
        <div>
            <h1>Lista de Filmes</h1>
            {error && <p>{error}</p>} {/* Exibindo erro, se houver */}
            <div>
                {filmes.length > 0 ? (
                    filmes.map(filme => (
                        <div key={filme.id}>
                            <h3>{filme.title}</h3>
                            <p>{filme.overview}</p>
                            <img 
                                src={`https://image.tmdb.org/t/p/w500${filme.poster_path}`} 
                                alt={filme.title} 
                            />
                        </div>
                    ))
                ) : (
                    <p>Carregando filmes...</p>
                )}
            </div>
        </div>
    );
}

export default Home;
