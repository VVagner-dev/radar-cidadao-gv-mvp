# Radar Cidadão GV

Plataforma cidadã para registrar problemas urbanos na Grande Vitória. O fluxo público prioriza uma denúncia simples por endereço ou pino no mapa, com anexos de fotos. Um painel separado exibe dados agregados e ocorrências sem expor anexos.

## Funcionalidades do MVP

- Denúncia anônima com categoria, descrição, endereço/pino e até três fotos.
- Geocodificação de endereço via Nominatim/OpenStreetMap e ajuste manual no mapa Leaflet.
- Classificação de categoria e prioridade pelo Groq, com contingência local se a API não estiver disponível.
- Protocolo de confirmação, persistência H2 e arquivos locais ignorados pelo Git.
- Painel público com mapa, métricas, listagem e exportação CSV. Fotos não aparecem no painel.

## Estrutura

- `src/`: API Spring Boot 4 com Java 17, H2 e armazenamento local de anexos.
- `frontend/`: aplicação Angular 21 com as rotas `/` e `/painel`.
- `docs/`: relatório da primeira entrega.

## Como executar

1. Inicie a API na raiz do repositório:

```powershell
.\mvnw.cmd spring-boot:run
```

2. Em outro terminal, inicie o Angular:

```powershell
cd frontend
pnpm install
pnpm start
```

3. Abra `http://localhost:4200`.

O backend estará em `http://localhost:8080`. Para usar a classificação real, defina a chave antes de iniciar o backend:

```powershell
$env:GROQ_API_KEY = "sua_chave"
```

Sem chave, a API usa regras locais e identifica a resposta como `FALLBACK_LOCAL`.

Para as sugestões de endereço, configure também `LOCATIONIQ_TOKEN` no terminal que inicia o backend. Esse token fica apenas no ambiente local e não deve ser versionado.

## API

| Método | Rota | Uso |
| --- | --- | --- |
| `POST` | `/api/denuncias` | Cria denúncia multipart com a parte JSON `denuncia` e fotos opcionais. |
| `GET` | `/api/denuncias` | Lista denúncias para o painel. |
| `GET` | `/api/denuncias/resumo` | Retorna indicadores. |
| `GET` | `/api/denuncias/exportar.csv` | Exporta dados públicos. |
| `PATCH` | `/api/denuncias/{id}/situacao` | Prepara atualização administrativa futura. |

## Limites e privacidade

O MVP aceita apenas JPG, PNG e WebP, com até 5 MB por imagem e máximo de três anexos. Arquivos vão para `uploads/` e não são versionados. O painel não retorna anexos nem dados pessoais.

## Roteiro de vídeo

1. Abrir a página de denúncia e escolher uma categoria.
2. Buscar um endereço ou colocar o pino no mapa.
3. Anexar uma foto e enviar a denúncia.
4. Mostrar protocolo e classificação.
5. Abrir `/painel`, apresentar métricas, mapa e exportação CSV.

## Equipe e responsabilidades

- **Wagner dos Santos Cristo:** backend de denúncias, anexos e integração do MVP.
- **Hevellyn Monteiro Medeiros:** classificação por IA, fallback e exportação de dados.
- **Felipe Rodrigues Barzilai:** massa de dados fictícia para demonstração.
- **[Gabriel Rodrigo Lapa Rocha](https://github.com/Gabriel-RodrigoL):** frontend cidadão, mapa e painel público.
- **Ricardo da Silva Júnior:** documentação técnica, roteiro e apresentação em vídeo do MVP.

## Trabalho acadêmico

Projeto Integrador IV — Aplicações de Inteligência Artificial, FAESA 2026. Equipe: Felipe Rodrigues Barzilai, Gabriel Rodrigo Lapa Rocha, Ricardo da Silva Júnior, Hevellyn Monteiro Medeiros e Wagner dos Santos Cristo.
