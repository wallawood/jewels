# Jewels

A Gemini capsule where users share shower thoughts. Accounts are tied to TLS client certificates — no passwords, no email.

## Features

- Post, edit, and delete short entries ("jewels")
- Certificate-based authentication via TOFU
- Moderator role with elevated permissions
- Full-text search
- SQLite persistence

## Requirements

- Java 21+
- A Gemini client that supports client certificates (e.g. [Lagrange](https://gmi.skyjake.fi/lagrange/))

## Getting started

**1. Clone and run**

```bash
git clone https://github.com/wallawood/jewels.git
cd jewels
./gradlew run
```

The capsule starts on `gemini://localhost:1965`. A self-signed TLS certificate is generated automatically on first run.

**2. Create an account**

Navigate to `gemini://localhost:1965/signup` in your Gemini client. You will need a client certificate — most Gemini clients can generate one for you. Sign up as a user or moderator.

**3. Browse jewels**

Once signed up, visit `gemini://localhost:1965/jewels` to read, post, and search entries.

## Configuration

Create an `application.properties` file in the working directory to override defaults:

```properties
wallawood.hostname=example.com
wallawood.port=1965
wallawood.cert.path=cert.pem
wallawood.key.path=key.pem
```

## Roles

| Role | Level | Permissions |
|---|---|---|
| User | 1 | Post, edit and delete own jewels |
| Moderator | 3 | All user permissions + delete any jewel, access mod guide |

## Built with

- [Wallawood](https://github.com/wallawood/wallawood) — Gemini server framework
- [SQLite](https://www.sqlite.org) via [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)
- [Handlebars](https://github.com/jknack/handlebars.java) — Gemtext templating
