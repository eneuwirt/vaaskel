# AGENTS.md

## Repository Role

This repository is a reusable technical walking skeleton for Vaadin and Spring Boot applications.

It is not a business application and must not gradually turn into a product-specific codebase.

The purpose of this repository is to provide a stable, production-oriented technical baseline with:

- server-side Vaadin UI
- Spring Boot
- Spring Security
- role-based login and authorization
- JPA/Hibernate
- PostgreSQL
- Flyway migrations
- Docker and Docker Compose
- Maven build
- GitHub Actions / GHCR-ready delivery pipeline

## Current Technical Baseline

The actual repository state is authoritative, especially `pom.xml`, `docker-compose.yml`, `src/main/resources`, and `.github/workflows`.

At the time of this file, the project includes:

- Java 25
- Spring Boot 4.0.2
- Vaadin 25.0.3
- Maven
- PostgreSQL
- Flyway
- Docker Compose

Do not change versions casually. Dependency, framework, Java, Docker image, or CI action upgrades are separate technical tasks and require an explicit request.

## Rules For Codex

Before making changes, classify the task as one of:

- skeleton maintenance
- bug fix
- security fix
- compatibility fix
- build, Docker, or CI fix
- controlled upgrade
- application-specific product development

Application-specific product development does not belong in this repository.

If a request asks for business features, product workflows, new domain models, business tables, or concrete business logic, state that clearly and recommend implementing it in a derived application project instead.

## Allowed Work

Only changes that preserve the technical skeleton character are allowed:

- bug fixes
- security fixes
- build fixes
- Docker and Docker Compose fixes
- CI/CD fixes
- compatibility corrections
- controlled upgrades explicitly requested by the user
- technical maintenance of the existing structure
- documentation and developer guidance improvements that support the skeleton purpose
- tests for existing technical behavior

## Forbidden Work

Do not add or perform:

- business features
- product- or customer-specific views
- new business entities, tables, services, or workflows
- demo business logic
- unnecessary bidirectional JPA relationships
- cosmetic mass refactorings
- architecture replacement without technical necessity
- new frameworks without explicit instruction
- weakened security rules
- debug backdoors, hardcoded logins, or production test shortcuts
- project re-scaffolding

## Architecture Rules

Respect the existing layered structure:

- `ui`: Vaadin views, layouts, and UI components
- `service`: application and technical service logic
- `security`: authentication, authorization, and security configuration
- `domain`: entities and domain objects
- `repository`: Spring Data repositories
- `api`: DTOs and boundary objects
- `resources/db/migration`: Flyway migrations

Rules:

- Keep UI logic in Vaadin components and views.
- Keep business and security-relevant logic in services, not views.
- Prefer transaction boundaries in the service layer.
- Access persistence through repositories and services.
- Keep entities small, explicit, and robust.
- Avoid unnecessary bidirectional relationships.
- Treat fetching deliberately; actively watch for N+1 issues.
- Use cascades only when ownership is technically and semantically clear.
- Use DTOs or projections when leaking entities would be inappropriate.

## Vaadin Rules

- Server-side Vaadin is the default approach.
- Avoid unnecessary client-side custom paths.
- Keep views clear, maintainable, and component-oriented.
- Use Binder, Grid, Dialog, FormLayout, and navigation deliberately and cleanly.
- Do not keep UI state in uncontrolled static fields or global helper constructs.
- Do not hide security and role decisions only in the UI; enforce them server-side.

## Vaadin 25 Development Rules Ported For Codex

These rules adapt the Vaadin Claude plugin skills to Codex. They are not Claude-specific commands. Apply them as repository coding guidance when working on Vaadin UI, forms, navigation, theming, data loading, testing, or security.

### Views And Navigation

- Use explicit `@Route` paths. Do not rely on class-name route derivation for new views.
- Add `@PageTitle` to route targets unless there is a clear reason not to.
- Prefer `RouterLink` for normal navigation because it preserves link semantics and browser behavior.
- Use `UI.getCurrent().navigate(...)` only for event-driven navigation where a link is not appropriate.
- Prefer class-based navigation over hardcoded route strings.
- For reusable navigation targets, centralize navigation in static methods on the target view, for example `showUser(id)` or `createLink(label, id)`.
- Use route parameters for resource identity and query parameters for optional filters, sorting, and pagination.
- Use route templates with `BeforeEnterObserver` for multiple named parameters or constraints.
- Do not store navigation state in static fields.
- Keep login and other standalone views outside the main layout with `autoLayout = false` when appropriate.
- Use `AppLayout` for the application shell instead of rebuilding shell behavior with nested `HorizontalLayout` and `VerticalLayout`.
- Wrap drawer navigation in `Scroller` when menu content can overflow.
- When using `SideNavItem`, set nested matching where nested routes should keep the parent menu item active.

### Layouts

- Use the right layout primitive:
  - `AppLayout` for the application shell.
  - `FormLayout` for forms.
  - CSS Grid or a suitable Vaadin grid-style component for two-dimensional card/widget layouts.
  - `HorizontalLayout` and `VerticalLayout` for one-dimensional component arrangement.
- Be explicit with layout sizing, padding, spacing, and margin when composing nested layouts.
- Remember the important defaults:
  - `VerticalLayout`: width 100%, height undefined, spacing on, padding on.
  - `HorizontalLayout`: width undefined, height undefined, spacing on, padding off.
- Avoid unnecessary layout nesting. Do not add one-child layouts just for alignment if parent alignment is enough.
- Use `setFlexGrow(...)`, `setFlexShrink(...)`, `setMinWidth("0")`, and `setMinHeight("0")` deliberately when solving flex sizing or overflow issues.
- In Vaadin 25, do not apply outdated Vaadin 24 advice that treats `setWidthFull()` as always causing sibling-shrinking problems.

### Responsive Layouts

- Prefer CSS media queries, CSS container queries, and built-in responsive Vaadin components over server-side viewport detection.
- Do not use `Page.retrieveExtendedClientDetails()` or resize listeners as the default way to choose layout structure.
- Design mobile-first: define the small-screen layout first, then add wider-screen enhancements.
- Prefer built-in responsive behavior from `AppLayout`, `FormLayout`, `MenuBar`, `Tabs`, `Dialog`, `ConfirmDialog`, and similar components before custom code.
- Use container queries for reusable components that must adapt to their own container width.
- Test wrapping and intermediate widths, not only one mobile and one desktop size.

### Theming And Frontend Design

- Pick one Vaadin theme approach for the application and keep it consistent.
- Do not mix Aura tokens with Lumo tokens.
- Do not use `LumoUtility` classes unless the Lumo theme and utility stylesheet are actually configured.
- Prefer theme properties and component variants before custom CSS.
- Prefer `@StyleSheet` over `@CssImport` for Vaadin 25 styling.
- Load theme stylesheets before application stylesheets.
- Use active theme tokens for colors, spacing, font sizes, radius, and shadows instead of hardcoded values.
- Use `::part()` selectors when styling Vaadin component internals.
- If changing colors, verify both light and dark mode behavior.
- Keep visual hierarchy deliberate: limited text styles, consistent density, consistent elevation, and restrained motion.
- CSS animations should be subtle, CSS-driven, and generally below 400ms.

### Forms And Validation

- Use `Binder` or `BeanValidationBinder` for non-trivial forms.
- Prefer explicit `binder.forField(...).bind(...)` chains for forms with validation or conversion.
- Use buffered Binder mode for forms with Save/Cancel behavior.
- Use write-through mode only for simple settings or filters where immediate updates are intended.
- Keep validation at the right layer:
  - format, required, range: field/binding validators
  - cross-field consistency: Binder-level validators
  - business invariants: service layer
- Use converters when UI field types and model types differ.
- Display Binder-level errors in a clear status area.
- Use `FormLayout` for form field layout and responsive columns.

### Data Providers, Grid, And Lazy Loading

- Use in-memory `setItems(...)` only for small datasets that comfortably fit in server memory.
- Use lazy loading for large datasets, especially Grid and ComboBox data.
- Prefer Spring integration with `setItemsPageable(...)` where available.
- Prefer `Slice`-style repository access for Grid page loading when total count is not needed; avoid unnecessary `Page` count queries for visible-row fetching.
- Provide a separate count callback when the UI needs a proper scrollbar or known total size.
- Use `ValueChangeMode.LAZY` for filter fields that trigger backend queries.
- Call `refreshAll()` when filter criteria change.
- Declare sortable column keys deliberately and keep property names refactor-safe where practical.
- Do not call lazy data APIs in a way that loads the entire dataset into memory.
- Do not make `Grid` or UI-bound `DataProvider` instances singleton Spring beans; they hold UI/session state.
- Ensure item identity used by Grid is stable and not based on mutable fields.

### Reusable Components

- Start with a clear view implementation and extract components when there is real pressure: repeated UI patterns, cohesive sections, isolated state, or views growing beyond roughly 200 lines.
- Prefer `Composite<T>` for extracted compound components because it hides internals and exposes a deliberate API.
- Extend an existing Vaadin component only when the full parent API should intentionally remain public.
- Expose intent-based methods rather than internal components.
- Use constructor parameters for required state so invalid component states are harder to create.
- Use setters for parent-to-child updates.
- Use typed `ComponentEvent` plus `addXxxListener()` returning `Registration` for reusable child-to-parent communication.
- Use simple callbacks only for one-off local components.
- Clean up subscriptions, listeners, and external resources in `onDetach()` when created in `onAttach()`.

### Browserless UI Tests

- Prefer Vaadin browserless tests for most server-side Flow view behavior.
- Use browserless tests for view logic, navigation, Binder validation, component state, and simple interaction behavior.
- Use `test(component)` interaction helpers rather than direct component method calls when simulating users.
- Use component queries deliberately and avoid brittle positional lookups where stable IDs or direct package-private fields are clearer.
- Keep view fields package-private when tests in the same package need direct access.
- Use `@ViewPackages` or equivalent package restriction to keep test startup fast in larger applications.
- Keep each test focused on one user-visible behavior.

### End-To-End Browser Tests

- Use TestBench or other real-browser tests only for critical user journeys, client-side behavior, visual regression, cross-browser behavior, or external authentication flows.
- Keep end-to-end tests few, focused, and independent.
- Prefer page objects for non-trivial end-to-end tests.
- Use stable component IDs for key elements.
- Use explicit waits such as `waitForFirst()` or equivalent; do not use fixed sleeps.
- Remember that Vaadin TestBench requires a commercial Vaadin subscription.

### Third-Party Components

- Prefer standard Vaadin components before adding third-party UI dependencies.
- If an npm package provides a Web Component, prefer the Web Component integration path over a React adapter.
- Pin exact npm versions in `@NpmPackage`.
- Ensure `@Tag` exactly matches the browser-side custom element name.
- Pair npm package imports with the required `@NpmPackage` and `@JsModule`.
- Use `PropertyDescriptor` for repeated element property access.
- Use `@DomEvent` and `@EventData` for typed DOM event integration.
- Use `AbstractSinglePropertyField` when the wrapped component is a single-value form field that should work with Binder.
- Keep React adapter `.tsx` files thin. They should bridge state/events, not contain business logic or data fetching.

### Signals And Reactive State

- Do not introduce Vaadin Signals casually. Use ordinary fields, Binder, component state, or services unless reactive state solves a real coordination problem.
- Use local signals for single-UI state such as panel visibility, local filters, or per-view reactive UI state.
- Use shared signals only for multi-user/shared state and enable push when live propagation is required.
- Prefer immutable signal values such as records, strings, and primitive wrappers.
- Use `peek()` outside reactive contexts and `get()` only inside effects, computed signals, or reactive bindings.
- Prefer direct bindings such as `bindText`, `bindVisible`, `bindEnabled`, and `bindValue` over custom effects for simple UI updates.
- Use transactions only with shared signals and only when atomic multi-signal updates are required.

### Client-Side Views / Hilla

- This repository defaults to server-side Vaadin Flow. Do not add React/Hilla client-side views unless explicitly requested.
- If client-side views are explicitly requested, keep `@BrowserCallable` endpoints stateless and secured.
- Add explicit access annotations to every exposed endpoint or method.
- Use `ViewConfig` for client-side route metadata and access control, but rely on server-side annotations for enforcement.
- Handle endpoint validation, endpoint errors, and network failures explicitly.
- Keep generated frontend files untouched.

## Spring Boot And Security Rules

- Spring Security remains the security baseline.
- Do not weaken login, role model, authorization, or password handling.
- Call out every security-relevant change explicitly.
- Do not introduce production default credentials.
- Do not add hidden bypasses for local development.
- Keep configuration profiles clear; do not mix dev, int, prod, and test concerns.

## JPA, Hibernate, And Database Rules

- PostgreSQL is the target database.
- H2 may only be used for tests when the existing setup provides for it.
- Flyway remains the migration baseline.
- Do not add business tables or columns without explicit instruction.
- Do not create destructive migrations without clear justification and explicit approval.
- Preserve existing user, role, and settings persistence.
- Always check schema, migration, tests, and fetching impact when changing entities.

## Docker And CI/CD Rules

- Keep Docker and Compose setup simple, reproducible, and locally understandable.
- Keep app, database, persistence, and network concerns separated.
- Keep ports, volumes, and environment variables explicit.
- Do not rewrite GHCR or GitHub Actions logic based on personal preference.
- Change container and pipeline logic only for technical need.

## Build And Verification

Run checks appropriate to the change.

Standard checks:

```bash
mvn -DskipTests compile
mvn test
```

Production build:

```bash
mvn -DskipTests -Pproduction package
```

Vaadin frontend checks for build or dependency issues:

```bash
mvn vaadin:clean-frontend
mvn -DskipTests vaadin:prepare-frontend
```

Local startup:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Docker stack:

```bash
docker compose pull
docker compose up -d
docker compose ps
```

Codex should run only the checks that are relevant to the change. If checks cannot be run, state the reason.

## Codex Working Style

- Read the existing structure before changing code.
- Prefer small, targeted changes.
- Do not revert user changes or unrelated worktree changes.
- Do not perform mass formatting without technical reason.
- Do not upgrade dependencies incidentally.
- Do not add abstractions without real value.
- Add or adjust tests proportionally to risk.
- For review tasks, lead with concrete findings.
- For debugging, separate cause, possible secondary causes, verification, and fix.

## Response Format For Repository Work

Default responses for technical work in this repository should briefly cover:

1. Technical classification of the task
2. Relevant assumptions
3. Affected files or layers
4. Concrete change made
5. Impact on build, security, database, Docker, and CI/CD
6. Risks, migration notes, or follow-up work

For small changes, a compact summary with verification is enough.

## Priorities When Tradeoffs Conflict

1. Preserve buildability
2. Preserve local startup behavior
3. Preserve the security baseline
4. Preserve the skeleton character
5. Preserve Docker and CI/CD operability
6. Preserve upgradeability
7. Minimize the change set
