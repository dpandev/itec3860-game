# itec3860-game

## Getting Started

1) Install JDK 21 (Temurin recommended). `java -version` should show 21.x
2) Clone the repo: `git clone`
3) Run: `./gradlew --version`  # wrapper downloads Gradle
4) Run: `./gradlew spotlessApply clean check`  # build + tests + style checks
5) IntelliJ: Use the Gradle wrapper and set Gradle JVM = JDK 21.

---

## Build + Format + Test
This project uses Spotless to auto-format your code. Code that does doe follow the formatting specifications cannot be committed to the repo. Refer to the `config/checkstyle` document for more information.

To auto-format your written code + build + test, run the following command in a terminal within the project's root:

`./gradlew spotlessApply clean check`

---

## Daily Workflow
1. Sync and branch from `dev`:
    ```
    git checkout dev
    git pull origin dev
    git checkout -b feature/<short-name>
    ```
2. Code.
3. Before you push:
    ```
    ./gradlew spotlessApply clean check
    ```
4. Open a PR (Pull Request) → `dev`. Fill the PR template. Fix any CI failures.
5. Reviewer approves → squash-merge to `dev`.
> Maintainers will periodically raise a PR from `dev` → `main` for release/tags.
---

## Branch & PR rules

- Branches:
  - `main` = always green, release-ready
  - `dev` = integration branch
  - `feature/<short-name>` from `dev`
- PRs:
    - Target `dev`
    - Must pass CI (`spotlessCheck`, tests, Checkstyle)
    - Must be approved
    - Squash merge only
- Commit messages:
  - Conventional Commits (e.g., `feat(parser): add verb synonyms`)
  - Can also include ref number to specific card on tracking board (Trello or other? → TBD)

---

## Formatting and Style

- Google Java Style is enforced by Spotless (same output on all machines).
  - Auto-format: `./gradlew spotlessApply` (run this before every commit)
  - Check only: `./gradlew spotlessCheck`
- Checkstyle (Google checks) catches naming/Javadoc/import rules that formatters don't.
  - Full pipeline: `./gradlew clean check`
  - Reports (output):
    - Tests → `build/reports/tests/test/index.html`
    - Checkstyle → `build/reports/checkstyle/main.html`
    - Coverage → `build/reports/jacoco/test/html/index.html`

Google Checkstyle Guide:
https://checkstyle.sourceforge.io/styleguides/google-java-style-20250426/javaguide.html

For Javadoc comments in code, refer to [Google Checkstyle Guide: Javadoc](https://checkstyle.sourceforge.io/styleguides/google-java-style-20250426/javaguide.html#s7-javadoc).

---

## Running and Testing
**TODO:** *Testing strategy/implementation TBD, this will be updated.*

- Build + tests + style:
    `./gradlew clean check`
- Tests only:
    `./gradlew test`

---

## Pull Request (PR) Checklist
(what PR reviewers expect)

> Also in tandem with PR template: `.github/workflows/PULL_REQUEST_TEMPLATE.md`
- [ ] Branch from `dev`, PR to `dev`
- [ ] Ran `./gradlew spotlessApply` (auto-formatter)
- [ ] `./gradlew clean check` passes locally
- [ ] Tests added/updated for new behavior
- [ ] Brief Javadoc comments included
- [ ] Docs updated if behavior/commands changed (`/docs` and/or README)
- [ ] No game data in views (MVC separation)

---

## Git commands you'll actually use
```bash
# Start a feature
git checkout dev
git pull origin dev
git checkout -b feature/room-parser

# Regularly sync with dev branch while you work
git fetch orgin
git rebase origin/dev   # or merge

# Push your branch (feature branch)
git push -u origin feature/room-parser
```

---

## Troubleshooting

- **"Gradle 9 / deprecated" messages:** Make sure you run `./gradlew ...` (wrapper), not `gradle ...`
- **Wrong Java version:** Set Project SDK/Gradle JVM to 21 in your IDE
  > `java -version` should show 21.x
- **Formatting fails in CI:** run `./gradlew spotlessApply`, then re-commit.
- **Checkstyle errors:** open `build/report/checkstyle/main.html` to see the exact rule and file/line.
- **Still having issues?** Kill the running daemons and try again:
    ```bash
    ./gradlew --stop
    rm -rf build .gradle
    ./gradlew clean check
    ```
You can always reach out to team members for troubleshooting help and questions via our Discord server (any channel, will update this when appropriate/specific channels are completely set up).

**Any and all questions/requests welcomed**.

---

## What goes where
- **Code**: `src/main/java/...`
- **Tests**: `src/test/java/...`
- **Docs**: `/docs` (gameplay spec, architecture, test plan, release notes)
- **Config**: `.editorconfig`, Spotless/Checkstyle are in `build.gradle` (Google style)

---

## Expectations
- Keep PRs small (easy to review)
- Address PR review comments.
- If you're blocked by something, ask for help early (label PR/Issue `status:blocked`)

---
