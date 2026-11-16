# Kithlore

Monorepo for the Kithlore web application.

## Local environment

Requirements: Docker Engine with the Compose plugin. From the repository root:

```sh
docker compose up -d --build
```

The stack starts PostgreSQL, the Spring Boot API, and the Angular site served by Nginx. PostgreSQL starts first; API and web health checks gate their dependants. Open the web app at <http://localhost:4200> and the API at <http://localhost:8081>.

Copy `.env.example` to `.env` to customize ports, database credentials, LLM providers and keys, the JWT secret, and token ceilings. Keep `.env` private; it is ignored by Git. The default values in Compose are for local development only.

### ZimaBoard resource simulation

```sh
docker compose --profile zimaboard-sim up -d --build
docker compose ps
docker stats
```

Compose applies limits to every stack in both modes: PostgreSQL 1.5 CPU / 2 GiB, API 1.99 CPU / 2 GiB, and web 0.5 CPU / 512 MiB. The `zimaboard-sim` profile adds a small healthy marker container (0.01 CPU / 16 MiB); the aggregate cap is 4 CPU / 4.5 GiB, below the 6 GiB target. Inspect configured limits with `docker inspect` because `docker stats` reports usage and memory ceilings, not CPU quotas.

Stop the stack with `docker compose down`; add `-v` only when you also intend to delete the local PostgreSQL data volume.
