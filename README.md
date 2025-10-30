# itec3860-game

## Getting Started


### Mac OS / Linux
1) Install JDK 21 (Temurin recommended). `java -version` should show 21.x
2) Clone the repo: `git clone ...`
3) Run: `./gradlew --version`  # wrapper downloads Gradle
4) Run: `./gradlew spotlessApply clean check`  # build + tests + style checks
5) IntelliJ: Use the Gradle wrapper and set Gradle JVM = JDK 21.

### Windows OS
1) Install Java 21 (Temurin): `winget install EclipseAdoptium.Temurin.21.JDK`. `java -version` should show 21.x
2) Clone the repo: `git clone ...`
3) Run: `.\gradlew.bat --version`
4) Run: `.\gradlew.bat spotlessApply clean check`
> Notice that for Windows, the Gradle command will differ slightly. The rest of this document uses the command for Mac/Linux.
> If using Windows, just replace the `./gradlew` with `.\gradlew.bat` unless if using **Git Bash** (recommended) or WSL2.

If using IntelliJ on Windows...
- File → Settings → Build Tools → Gradle → Gradle JVM → set to `JDK 21`
- EditorConfig support on (on by default)

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
2. Code + commit (follow commit/branch conventions detailed [further below](#commits-branches-conventions)).
3. Before you push:
    ```
    ./gradlew spotlessApply clean check
    ```
4. Open a PR (Pull Request) → `dev`. Fill the PR template. Fix any CI failures.
   - `./gradlew spotlessApply`
   - `./gradlew clean check`
5. Reviewer approves → merges to `dev`.
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
  - Full pipeline: `./gradlew spotlessApply clean check`
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
> To mark an item as completed/checked when filling out the PR, replace space between brackets with "x" `[x]`

---

## Git workflow

- Branches
    - `main` – release-only, protected.
    - `dev` – integration branch. All feature/fix PRs target `dev`.

- Merge policy
    - Use **Merge** commits. No rebase. No squash on protected branches.
    - Linear history is **not** required.

- Daily flow
    1. Sync `dev`
       ```bash
       git switch dev
       git pull origin dev
       ```
    2. Start work
       ```bash
       git switch -c feat/<topic>   # or fix/<topic>
       ```
    3. Commit small changes
       ```bash
       git add -A
       git commit -m "feat: <what> <why>"
       ```
    4. Push and open PR → base=`dev`
       ```bash
       git push -u origin HEAD
       ```
    5. Keep your branch up to date using **merge**
       ```bash
       git fetch origin
       git merge origin/dev   # resolve conflicts, commit, push
       ```
    6. After approval and green CI, click **Merge** in GitHub. Delete the branch.

- Release flow
    1. Open PR `dev` → `main` when stable.
    2. Ensure CHANGELOG or Release Notes are updated.
    3. Click **Merge**. CI must pass.

- Commit and PR rules
    - Link issues: “Fixes #123”.
    - Keep PRs focused. Prefer < ~500 lines changed.
    - Run CI locally before pushing:
      ```bash
      ./gradlew spotlessApply clean check
      ```

## Conflict resolution (quick guide)

```bash
git merge origin/dev
# edit files to resolve conflicts
git add <resolved files>
git commit # completes the merge
git push
```

---

#### Never open PRs from `main` → `dev`.
- Edit PR → change base to `dev`

<span id="commits-branches-conventions"></span>
> **Conventional branch names** for non-feature changes → use: `chore`/`docs`/`fix`/`hot-fix` / `test` / `refactor` prefixes.

- **Features**: `feature/<short-slug>`
  - *Example*: `feature/combat-parser`
- **Bug fixes** (normal): `fix/<tracker-id>-<short-slug>`
  - *Examples*: `fix/trello-42-null-save`, `fix/13-map-content-loader`
- **Hotfixes** (urgent bugs in main): `hotfix/<version>-<short-slug>`
  - *Example*: `hotfix/1.0.1-npe-on-start`
- **Chores / Docs / Refactors / Tests**:
  - `chore/<slug>`, `docs/<slug>`, `refactor/<slug>`, `test/<slug>`

> Use **conventional commit** messages: `fix: ...`, `feat: ...`, `chore: ...`, `docs: ...`, etc.

---

## Quick workflows

#### Fix a bug on `dev` (example):
```bash
# get the latest code from the dev branch
git checkout dev && git pull
# create a new branch for the issue/bug/fix
git checkout -b fix/trello-42-null-save
# code + tests + commit your work
# "Trello-42" would be the id of the appropriate Trello card
git add -A && git commit -m "fix(save): handle null save slots (Trello-42)"
git push -u origin fix/trello-42-null-save
# then open a PR to dev
```

#### Hotfix on `main`, then back into `dev` (example):
> In most cases, bugs will be fixed on the dev branch using the previous workflow above.
> The workflow below is for extreme cases only and will be coordinated by maintainers.
```bash
git checkout main && git pull
git checkout -b hotfix/1.0.1-npe-on-start
# code + tests + commit work
git commit -m "fix(startup): prevent NPE on empty args"
git push -u origin hotfix/1.0.1-npe-on-start
# PR to main, merge, tag v1.0.1, then:
git checkout dev && git pull
git merge origin/main
git push
```

---

## Maintainers

#### Releasing (tags trigger the release workflow)
```bash
# Open a PR from dev -> main in GitHub, get approvals, merge (squash)
# After merging dev -> main
git checkout main
git pull

# Choose next version (tag a release). Examples:
# v0.2.0 = playable prototype, v1.0.0 = final
git tag -a v0.2.0 -m "Playable prototype"
git push origin v0.2.0
```
CI will build and attach the JAR to the tag's release.
At the time of writing this, a baseline/skeleton "Scaffold only" exists (Pre-release, v0.1.0).

#### Versioning and tags

- **Patch**: `vX.Y.Z` for small fixes
- **Minor**: bump (increment) Y for meaningful features/milestones (prototypes → feature complete).
- **Major**: `v1.0.0` for final submission.

---

## Troubleshooting

- **"Gradle 9 / deprecated" messages:** Make sure you run `./gradlew ...` (wrapper), not `gradle ...`
- **Wrong Java version:** Set Project SDK/Gradle JVM to 21 in your IDE
  > `java -version` should show 21.x
- **Formatting fails in CI:** run `./gradlew spotlessApply`, then `./gradlew clean check`, then re-commit.
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
