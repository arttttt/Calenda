# AGENTS.md

> Guidance for AI agents (Claude Code, GitHub Copilot Agents, etc.) working in the **Calenda** repository. This file encodes ground rules, build steps, guardrails, and acceptance checks so agents make safe, high‑quality changes that match the design.

---

## 1) Project at a Glance (repo‑specific)

* **Module layout:** single app module `:app` + local convention plugins under `build-logic/`. (See `settings.gradle.kts`.)
* **Namespace / appId:** `com.arttttt.calenda`.
* **Min/Target/Compile SDK: See `app/build.gradle.kts`.
* **Kotlin / AGP: See `gradle/libs.versions.toml`.
* **UI stack:** Jetpack Compose (Material 3 Expressive), Glance for AppWidget.
* **Architecture:** MVI via **SimpleMVI** (library + ksp codegen), unidirectional data flow, immutable state.
* **Navigation:** AndroidX **Navigation 3** (`androidx.navigation3:*`) plus `io.github.arttttt:nav3router`.
* **Other libs (selected):** DataStore Preferences, kotlinx‑datetime, kotlinx‑serialization.
* **Offline only:** No backend; **read‑only** Calendar Provider access.

**Sources verified:**

* `settings.gradle.kts` — includes `:app`, includes build logic.
* `app/build.gradle.kts` — namespace, SDKs, plugins (`id("app")`, KSP, Metro), deps.
* `gradle/libs.versions.toml` — versions for Kotlin/AGP/Compose/Glance/Nav3/SimpleMVI/etc.

---

## 2) Mandatory Agent Rules

1. **No networking in v1.** Do not add internet permissions, HTTP clients, OAuth, or third‑party calendar SDKs.
2. **Read‑only.** Never create/modify/delete events in the Calendar Provider.
3. **Repositories return `Result<T>`.** Implementations must use `runCatching/mapCatching/recoverCatching`; do not throw across boundaries.
4. **Coroutines:** structured concurrency only; no `GlobalScope`; inject dispatchers.
5. **MVI contracts:** intents → actor → reducer → new state; explicit side‑effects; immutable state.
6. **Security/Privacy:** don’t log PII; redact when needed.
7. **Version discipline:** do **not** bump versions or change plugin IDs (`id("app")`, `id("library")`) without explicit task.

---

## 3) Build & Local Checks (repo‑specific)

> Requires JDK 21 (JBR 21.0.4+), Android SDK 36.

```bash
# Assemble/debug & run KSP (SimpleMVI codegen runs automatically)
./gradlew :app:assembleDebug
```

**Style policy**

* Order: **public → protected → private**; properties above functions.
* Companion objects / nested classes at the top; avoid unnecessary nesting.
* Extension functions go after the last private function.
* Trailing commas enabled; no wildcard imports.

---

## 4) Functional Requirements (agent checklist)

### 4.1 Permissions & Compliance

* Runtime `READ_CALENDAR` in each installed profile; denial → empty states + re‑request CTA.
* **Cross‑profile modes:**

    * **OS calendar sharing** (policy/user‑controlled): work calendars appear read‑only in personal profile.
    * **Same‑app bridge** (AndroidX Connected Apps style): personal = **Aggregator**, work = **Provider**; local IPC, read‑only projection.

### 4.2 Calendar Discovery

* Source: `CalendarContract.Calendars`.
* Fields: id, displayName, accountName/type, color, provider `selected`, profile origin label.
* Present selectable list; persist **own** set of selected calendar IDs (independent from provider’s `selected`).

### 4.3 Agenda Construction

* Source: `CalendarContract.Instances` within `[start, end]`.
* Windows: In‑app **now → +7d**; Widget **Today** + **Tomorrow** (local 00:00–23:59:59).
* Fields per instance: start, end, title, location?, allDay, calendarId, eventId, recurrenceId, availability?, organizer?, selfAttendeeStatus?.
* Sort: start ↑, then duration ↓, then title; group by day; all‑day at top labeled **“All‑day”**.
* Recurrence: rely on provider expansion; exclude canceled.
* TZ/DST: display in local TZ; handle DST correctly.
* Conflicts: do not merge; in app visually overlap; widget shows separate rows.

### 4.4 Refresh Strategy

* Single app‑wide `ContentObserver` (Events/Instances) + **2–5 s** debounce.
* WorkManager periodic fallback (≈ **6 h**).
* Midnight rollover rebuilds; widget also rolls Tomorrow→Today at 00:00.
* Manual refresh via pull‑to‑refresh / settings.

### 4.5 App Widget (Glance)

* Sections: **Today** and **Tomorrow**; row shows time or “All‑day”, title, color dot, small **Work** badge.
* Taps: row → open app on date; header/empty → open app / calendar picker.
* Resizable: adapt visible items; overflow “+X more”.
* Update triggers: selection/permission changes, observer events, midnight, manual; bridge signals debounced.
* Perf target: restrict provider queries to two days; cache last result; **< 300 ms** update on mid‑range devices.

### 4.6 In‑App UI (Core Screens)

* **Onboarding:** permission + quick calendar selection; if work profile detected, surface OS‑sharing vs bridge.
* **Agenda:** grouped by day; filters: all‑day toggle, show/hide declined; date picker for start; **Work** badge.
* **Calendar Picker:** color chips with toggles, **Select All / Clear All**; segmented Personal / Work.
* **Settings:** theme (system/light/dark), widget density, 24‑h clock (default to system), first day of week (default to locale), cross‑profile status + **Recheck**.

### 4.7 Failure Handling

* OS sharing off → suggest bridge.
* Bridge unavailable (not installed / policy) → show guidance; continue Personal‑only.
* Work profile locked → non‑blocking banner; exclude Work data until unlocked.
* Provider failures → log; show stale data timestamp + Retry.

---

## 5) Quality, Tests, and Previews

* Tests are not needed at the moment
* Compose previews for agenda rows & empty states.

---

## 6) Task Recipes for Agents

1. **Add “Show declined” filter** — use `selfAttendeeStatus` when available; expose state/intent; filter in repository; tests for on/off.
2. **Optimize widget updates** — debounce ContentObserver + bridge; cache Today/Tomorrow; invalidate on boundary/permission/selection.
3. **Enable cross‑profile bridge (phase 2)** — detect second Calenda, handshake (Connected Apps), read‑only projection (pagination/deltas), explicit consent, UI badges.
4. **Selection migration** — keep own ID set; resync on `Calendars` changes; soft‑unsubscribe removed calendars and notify user.

---

## 7) Boundaries (Do/Don’t)

* **Do not** add network code/permissions.
* **Do not** write to Calendar Provider.
* **Do not** change sorting/grouping without tests + UX confirmation.
* **Do not** remove debounces/midnight updates.
* **Do** keep plugin application via convention plugin `id("app")`/`id("library")` intact.

---

## 8) PR Communication Style

* PR body: `## Why`, `## How`, `## Tests`, `## Risks & Mitigations`.
* List touched modules/packages; include local command checklist (build/test/lint).

---

## 9) Diagnostics & Logging

* Local debug logs only: provider query durations, agenda build durations, widget update durations.
* Never log personal/sensitive data; redact as needed.

---

## 10) Compatibility & System Events

* Listen for TZ/locale changes; rebuild agenda & refresh widget.
* DST: rely on Instances; all‑day without time; multi‑day all‑day appears on each expanded day.

---

## 11) Agent Acceptance Checklist

* [ ] No network/SDK added.
* [ ] Permissions requested correctly; denial handled with UI states.
* [ ] Instances windows are minimal.
* [ ] Debounces, midnight rollover, WorkManager fallback configured.
* [ ] Profile labels (e.g., **Work**) present in UI.
* [ ] Tests & previews added/updated.
* [ ] Widget update time ≤ **300 ms** on mid‑range devices.

---

## 12) Prompt Starters for Agents

* “Analyze `CalendarContract.Instances` and add an all‑day filter with a DST edge‑case test.”
* “Optimize widget updates: coalesce ContentObserver + midnight triggers; add a 2‑day cache.”
* “Implement a read‑only cross‑profile bridge (Connected Apps) with explicit user consent and source badges.”

---

### Appendix — Verified Repository Paths

* `settings.gradle.kts` (module includes, build logic). citeturn6view0
* `app/build.gradle.kts` (namespace, SDKs, plugins, dependencies). citeturn6view1
* `gradle/libs.versions.toml` (versions for Kotlin/AGP/Compose/Glance/Nav3/SimpleMVI). citeturn10view0

**End of file.**
