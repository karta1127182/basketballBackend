# Basketball Backend API

## Start PostgreSQL

```powershell
docker compose up -d
```

Database settings:

| Setting | Value |
| --- | --- |
| Host | `localhost` |
| Port | `5433` |
| Database | `basketball` |
| Username | `postgres` |
| Password | `123456` |

## Start API

```powershell
mvn spring-boot:run
```

## Endpoints

| Method | Path |
| --- | --- |
| `GET` | `/api/players` |
| `GET` | `/api/players/{id}` |
| `POST` | `/api/players` |
| `PUT` | `/api/players/{id}` |
| `DELETE` | `/api/players/{id}` |

Example:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/api/players `
  -ContentType application/json `
  -Body '{"name":"Stephen Curry","team":"Warriors","position":"PG","jerseyNumber":30}'
```
