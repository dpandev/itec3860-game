# Contributing

Thanks for helping! Please read this before opening Issues or PRs.

## Branching & PRs
- Branch from `main`: `feature/<short-name>` or `fix/<short-name>`.
- Keep PRs small and focused; squash merge.
- Link the related Issue and Milestone.

## Code style & quality
- Java 21, Gradle wrapper checked in.
- Run locally before pushing: (`./gradlew clean check`)

This runs compile, tests, Checkstyle, PMD, and JaCoCo.

- Style: see `.editorconfig` and `config/checkstyle/checkstyle.xml`.
- Public classes and methods should have brief Javadoc.

## Branching model  
- Create branches from `dev`: `feature/<short-name>` / `fix/<short-name>`.
- Open PRs into `dev`. CI (./gradlew clean check) must pass.
- Maintainers periodically PR `dev` -> `main` for releases.

## Build & Test  
- Use the Gradle wrapper with JDK 21:
  `./gradlew clean` check


## Tests
- Use JUnit 5. Add/extend tests for new behavior.
- For bugs, add a failing test first when practical.

## Docs
- Update `/docs` and `README.md` when behavior or commands change.
- Keep the RTM (`/docs/rtm.md`) in sync with new/changed requirements.

## Commit messages
- Use present tense (“Add X”, not “Added X”).
- Reference Issues: `Fixes #123` or `Refs #123`.

## Code ownership
- See `.github/CODEOWNERS`. PRs will request appropriate reviewers.

## Communication
- Be respectful and follow the Code of Conduct.
