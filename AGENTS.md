# AGENTS.md

Guidance for AI coding agents working on this repository. For human contribution
rules (CLA, PR process, AI-assisted contribution policy), see [CONTRIBUTING.md](CONTRIBUTING.md).

## Project overview

Sample code for the Maps SDK for Android. This is a multi-app repository:

| Area | Purpose |
| --- | --- |
| `ApiDemos` | Feature demos in parallel `java-app` and `kotlin-app` variants plus `common-ui` |
| `snippets` | Code excerpts published into the official documentation (`app`, `app-ktx`, `app-utils`, `app-utils-ktx`, `app-compose`, `app-places-ktx`) |
| `tutorials` | Standalone mini-projects backing written tutorials, in `java/` and `kotlin/` |
| `FireMarkers` | Firebase + Maps sample app |
| `WearOS` | Wear OS sample app |

## Region tags feed official documentation

Code excerpts in `snippets/`, as well as key sample activities in `ApiDemos` and
root build files, are extracted into developers.google.com pages via
region tag comment markers (paired START and END lines wrapping each excerpt).

- Never rename, remove, or reorder region tags, and keep every START/END pair
  balanced.
- Do not reformat or "clean up" code inside a tagged region unless the change
  is the point of the PR; the published docs mirror it verbatim.
- Java and Kotlin snippet modules (`app` vs `app-ktx`) document the same
  features; a change to one usually needs the equivalent change in the other.

The same parity rule applies to `ApiDemos`: `java-app` and `kotlin-app`
demonstrate the same features and must stay in sync.

## Code style and hygiene

- Adhere to formatting rules defined in `.editorconfig`.
- Do not use wildcard imports (`import foo.*`); use explicit imports.
- Avoid fully qualified class names in source code; declare explicit imports at the file level instead (except to resolve naming collisions or in XML layouts).

## Building and testing

- **Full project verification:**
  ```bash
  ./scripts/verify_all.sh                 # run assemble, unit tests, lint, and doc version checks across all root modules
  ```

- **Targeted module builds:**
  ```bash
  ./gradlew :ApiDemos:kotlin-app:assembleDebug   # build a specific demo app
  ./gradlew :snippets:app-ktx:assembleDebug      # compile a specific snippet module
  ```

- **Documentation version check:**
  ```bash
  python3 scripts/update_docs_versions.py --check # verify doc snippet versions match version catalog
  ```

Snippet modules do not contain unit tests; they are verified through successful compilation (`assembleDebug`), Android lint (`lintDebug`), and doc version synchronization.

Running the apps requires a Maps API key: copy the keys named in
`local.defaults.properties` (e.g. `MAPS_API_KEY`) into `local.properties`.
The secrets-gradle-plugin injects them at build time. Never hardcode or
commit API keys.

Note that most `tutorials/` projects are standalone Gradle builds not wired
into the root `settings.gradle.kts`; build them from their own directory.

## Pull requests

- Use Conventional Commit messages (`feat:`, `fix:`, `docs:`, ...).
  release-please parses them to generate versions and CHANGELOG.md; a wrong
  prefix causes a wrong release bump. Never edit CHANGELOG.md by hand.
- Keep changes scoped to one sample or one feature across its language
  variants; do not mix unrelated samples in one PR.
- Build and test the affected modules before declaring work done, and report actual
  results.
- AI tools must not be listed as authors or co-authors on commits or PRs, and
  unsolicited bot-generated PRs are prohibited (see CONTRIBUTING.md).
