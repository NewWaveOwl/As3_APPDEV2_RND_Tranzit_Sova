# RND Transit MTL

RND Transit is a Kotlin/Compose Multiplatform transit discovery demo using Material 3, Navigation 3, shared Compose resources, and the RND Transit theme.

The main workflow is:

**Planner → Current Trip → Review → History → Details or review editing**

Home, Profile, Settings, About, and History remain accessible through the shared navigation bar.

## Plan and start a trip

On Home, enter:

- Trip title.
- Trip description.
- A direct HTTPS image URL.
- Planned minutes.
- Transport types and applicable routes.
- Attraction intensity.

GO validates the inputs and selections. A valid action generates one `Trip`, stores its endpoints and planner selections, starts it through the shared store, and passes it to Current Trip as a navigation parameter.

Only one unfinished trip can exist. Repeated GO actions cannot replace it. When an unfinished trip exists, use **Resume trip**.

The selected planner minutes remain part of the trip information. They do not control the demonstration’s duration.

## Current Trip and simulation

Current Trip displays the entered information, planner selections, reference image, bundled mock map, and progress.

The map contains:

- Distinct start and destination markers.
- A straight connecting route line.
- A recognizable orange person moving along that line.

Endpoints are random normalized image positions generated once when starting the trip. Resizing, rotation, resuming, and opening details reuse those stored positions.

The simulation lasts **10,000 milliseconds of active time**. Movement, percentage, progress track, and remaining demo distance use the same progress value.

Leaving Current Trip or backgrounding the application pauses the simulation. Resume continues the same trip ID and retained elapsed time.

**Cancel trip** clears an unfinished trip and returns to the planner. Cancellation does not add a completed History record.

At completion, progress reaches 100%, remaining demo distance becomes zero, and the trip is recorded in History once. The initial review destination replaces Current Trip.

## Reviews

The review uses a sequence of questions with animated transitions:

1. Overall experience and optional written comment.
2. Quality.
3. How interesting the trip was.
4. Fun.

Ratings use 1–5 stars. Overall is required when saving. Optional ratings remain null until selected; skipping never creates a zero-star review.

For the initial review:

- Save updates the completed trip and opens History.
- Skip preserves the completed trip without adding feedback.
- Close and Back behave like Skip.

For review editing:

- The latest saved review loads into a separate draft.
- Save replaces feedback on the same trip ID.
- Cancel, Close, and Back discard unsaved changes.
- The screen returns to its originating History or details view.

Draft ratings, comment, and current question are included in saved state.

## History and details

History reads completed trips from the shared provider. It shows newest completed trips first and supports:

- All, Reviewed, and Not reviewed filters.
- Overall stars and saved review information.
- Expandable review content.
- Details.
- Review or Edit review.
- Removal through the action tray.

Review changes and removal update the same collection immediately.

Details resolves the latest stored trip by ID. Its map shows the original endpoints and a static completed route. Opening details does not generate points or restart the simulation.

Missing or deleted IDs show recovery content. Saving a review cannot recreate a deleted trip.

## Reference images

The entered URL represents a separate trip reference image. It does not replace `map_sample`, the bundled simulation map.

Coil provides loading, failure, and retry presentation. An image may fail because of connectivity, server restrictions, a non-image response, or an unsupported format. The mock route remains available independently.

Use a directly accessible HTTPS bitmap image URL. On Web, the remote server’s browser access policy can also affect loading.

## Shared state and restoration

`App` provides one `TripsStore` alongside the shared Navigator.

The store owns:

- One active trip and accumulated elapsed simulation time.
- The completed `List<Trip>`.
- Pending review-navigation handling.
- Review drafts and saved feedback.

Its serializable snapshot and Saver restore records and elapsed time. Running jobs, callbacks, lifecycle owners, and clock marks are not saved.

Planner fields and selections also use saveable state. Navigation keys are serializable and registered with the back-stack configuration.

Android activity recreation, including rotation, is intended to retain the planner, active trip, elapsed time, completed trips, review drafts, and navigation.

This is **saved-state restoration, not permanent database storage**. A fresh launch without restored instance state starts an empty session. Closing and reopening Desktop or reloading Web does not provide durable trip storage.

## Scope and limitations

- The map is a bundled mock image.
- Routes are straight-line demonstrations.
- Coordinates are normalized image positions, not GPS coordinates.
- Distance is synthetic demo distance, not real navigation distance.
- There is no live routing, street guidance, location tracking, or map service.
- Planned minutes are independent of the ten-second simulation.
- Permanent database storage is outside this implementation.
- Runtime rotation, lifecycle timing, image loading, and visual layouts still require verification.

## Platforms and commands

Android, Desktop JVM, Web JavaScript/Wasm, and iOS targets are configured.

The assignment requires an emulated Android application plus either Desktop or Web. The selected second platform and runtime evidence must be recorded in the assignment documentation.

Run commands from:

```powershell
Set-Location -LiteralPath 'K:\CLASSES 26 FALL\APP_DEV\As3\MainRepo\As3_APPDEV2_RND_Tranzit_Sova'
```

### Android

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
```

Run the `androidApp` configuration in Android Studio on an emulator or connected device.

### Desktop

```powershell
.\gradlew.bat :shared:compileKotlinJvm
.\gradlew.bat :shared:jvmTest
.\gradlew.bat :desktopApp:run
```

### Web: Wasm

```powershell
.\gradlew.bat :shared:wasmJsTest
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

### Web: JavaScript

```powershell
.\gradlew.bat :shared:jsTest
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
```

### iOS

Open `iosApp` in Xcode on macOS. An iOS build or launch is not claimed by the available evidence.

## Verification evidence

These results existed before this documentation was generated. No commands were executed during documentation preparation.

| Check | Available evidence |
| --- | --- |
| JVM tests | Existing XML reports record 43 tests, zero failures, zero errors, and zero skipped |
| Android host tests | Existing XML reports record 42 tests, zero failures, zero errors, and zero skipped |
| Android debug build | Previous README reports successful `:androidApp:assembleDebug`; original build log was not supplied |
| JavaScript compilation | Previous README reports successful `:shared:compileKotlinJs`; original build log was not supplied |
| Wasm compilation | Previous README reports successful `:shared:compileKotlinWasmJs`; original build log was not supplied |
| Application launches | No runtime evidence supplied |
| Browser tests and iOS checks | No passing evidence supplied |
| Device rotation, timing, image loading, and layouts | Manual verification pending |

Existing test-report timestamps are approximately **2026-10-05 03:06 UTC**. The tested commit or revision has not been identified.

Android host tests run on the development machine. They do not establish successful emulator rotation or application lifecycle behavior.

The tests cover state invariants, navigation, serialization, snapshot restoration, review updates, deletion, History presentation, and bounded endpoint generation.

Before submission, identify the tested revision and complete the manual verification checklist in the assignment documentation.

## Assignment documentation

The accompanying assignment record contains:

- WBS, original estimates, dependencies, and Gantt chart.
- Actual-time and variance fields.
- AI-assisted decision records.
- Day 17 AI Technique Log entries.
- Requirement and submission checklists.
- Verification evidence and pending checks.

The preliminary-design summary is supplied separately for a screenshot.

Original proposed effort is **30 minutes per prompt**, as confirmed by the student. Actual effort, ADR approval status, AI contribution percentage, and completed submission evidence must be supplied from the student’s records.