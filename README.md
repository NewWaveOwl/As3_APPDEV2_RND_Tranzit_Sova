# RND Transit MTL

RND Transit is a Kotlin/Compose Multiplatform transit discovery demo.
It uses Material 3, Navigation 3, a shared layout, shared resources,
and the project's LINE Seed JP typography.

The project includes Android, Desktop, Web, and iOS targets.
The assignment requires Android plus either Desktop or Web.

## Using the app

1. Open Home and enter a trip title, description, and HTTPS image URL.
2. Choose planned minutes, transport types, routes, and attraction intensity.
3. Press GO to start one mock trip.
4. Watch the orange person travel between the stored start and destination.
5. After completion, save a review or skip it.
6. Open History to filter completed trips, view details, review or edit
   feedback, and remove records.

Only one unfinished trip can exist. Home and the shared header offer
Resume trip when an unfinished trip is available.

Leaving Current Trip or backgrounding the application pauses the demo.
Returning to that trip resumes its stored elapsed time.

## Mock route and reference image

The map is a bundled image. Endpoints are generated once per trip and
stored as normalized image positions.

The route is a straight-line visual demonstration. Its distance is
synthetic; it is not a geographic measurement or street-routing result.

The simulation uses 10,000 milliseconds of active time. Planned minutes
describe the user's preference and do not change the simulation duration.

The entered image URL is a separate trip reference image. Loading or
decoding failure shows an unavailable state and retry action. Image
failure does not replace the bundled map or stop the simulation.

Use a directly accessible HTTPS bitmap image URL. A URL may have valid
syntax and still fail because the server is unavailable, access is
restricted, the response is not an image, or the format is unsupported.

## Reviews and History

Overall experience is required when saving a review.
Quality, Interesting, Fun, and the written comment are optional.

Unselected optional ratings remain null. Skipping does not create a
zero-star review.

Initial review Save, Skip, Close, and Back lead to History.
Editing Save returns to the originating History or details view.
Editing Cancel, Close, and Back discard unsaved changes.

History contains completed trips only. Cancelling an unfinished trip
does not create a History entry.

## Saved state and limitations

TripsStore is the shared source of active and completed trip records,
elapsed time, pending review handling, and review drafts.

Android saved-instance-state restoration is intended to preserve trips,
elapsed time, review drafts, planner inputs, and navigation during
activity recreation such as rotation.

Permanent database storage is outside this demo's scope. A fresh launch
without restored instance state starts an empty session. Closing and
relaunching Desktop or reloading Web is not permanent storage.

There is no GPS, live map service, real routing, or street guidance.

Automated build and test results for this revision are recorded below.
Runtime timing, rotation, image loading, and layouts still require
verification on the final assembled application.

## Project structure

- `shared/src/commonMain/kotlin`: shared application, screens, models,
  state, simulation coordination, and reusable UI.
- `shared/src/commonMain/composeResources`: shared images, vectors,
  fonts, and transport data.
- `shared/src/commonTest/kotlin`: shared state, navigation, simulation,
  and presentation tests.
- `androidApp`: Android application entry point.
- `desktopApp`: Desktop window entry point.
- `webApp`: browser entry point.
- `iosApp`: iOS application entry point.
- `docs/MainDesignDoc.md`: broader product design reference.

## Commands on Windows

Run these from the project root.

### Android

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
```

Run `androidApp` from Android Studio on an emulator or connected device
for lifecycle, rotation, keyboard, and visual checks.

### Desktop

```powershell
.\gradlew.bat :shared:compileKotlinJvm
.\gradlew.bat :shared:jvmTest
.\gradlew.bat :desktopApp:run
```

### Web

Wasm:

```powershell
.\gradlew.bat :shared:wasmJsTest
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

JavaScript:

```powershell
.\gradlew.bat :shared:jsTest
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
```

Browser image requests remain subject to the remote server's browser
access policy.

### iOS

Open `iosApp` in Xcode on macOS and run its application configuration.

## Verification

The following automated checks passed for these corrections:

| Check | Result |
| --- | --- |
| `:shared:jvmTest` | 43 tests passed; none failed or skipped |
| `:shared:testAndroidHostTest` | 42 tests passed; none failed or skipped |
| `:androidApp:assembleDebug` | Android debug APK built successfully |
| `:shared:compileKotlinJs` | JavaScript compilation passed |
| `:shared:compileKotlinWasmJs` | Wasm compilation passed |

All three `TripNavigationRegressionTest` cases failed against the previous
navigation code and passed after applying the fixes. Android host tests
run on the development machine; they do not verify device rotation.
Browser tests, iOS checks, and application launches were not run for this
revision.

The common tests cover navigation, serialization, restoration, review
updates, deletion, collection integrity, and bounded endpoint generation.
`TripNavigationRegressionTest` checks that completion-boundary Back
preserves the preceding page and that section shortcuts close reviews.

Verify these behaviors on the required platforms:

- Invalid text, image URLs, and transport selections show validation and
  do not start a trip.
- Repeated GO taps start one trip; Resume keeps its ID, endpoints, and
  elapsed progress.
- Leaving Current Trip or backgrounding excludes paused time. Ten active
  seconds completes one History record at 100% and zero remaining distance.
- Rotation preserves planner inputs, trip progress, review ratings,
  comment, current question, and review return destination.
- Initial Save/Skip/Back reaches History. Editing Save/Cancel/Back returns
  to History or Details; cancelling keeps previously saved feedback.
- Leaving initial Review through Profile and pressing Back reaches History.
  Leaving a Details edit through Settings and pressing Back returns to
  Details with the previous saved review. The closed review cannot reopen.
- History filters update after saving, editing, or removing a trip. Cards
  expand reviews and expose edit/remove actions by swipe or Actions.
- Completed Details uses the original endpoints and a static route. A
  removed record shows recovery content and cannot be recreated by saving.
- Image loading, failure, and retry work without changing the mock route.
- Narrow phones, landscape, an open keyboard, and Desktop keep controls
  reachable. Cancelling predictive Back keeps the review draft.
- About shows the existing photographs and the names Caio, Artiom, and
  Jimmy. Home remains protected from Back.

Actual device rotation, lifecycle timing, predictive Back, image loading,
application launch, and visual layout require runtime verification.
iOS builds and tests require macOS.

## Assignment documentation

The submission also requires:

- A WBS with at least five tasks.
- Original effort estimates and dependencies.
- A Gantt chart.
- Actual time tracking and explanations of significant differences.
- An AI Technique Log with at least three decisions in ADR format.
- Saved prompts and AI responses.
- An evidence-based AI contribution summary.
- Git, machine setup, and preliminary design evidence.

These records must reflect the work actually performed. Estimates,
accepted decisions, actual time, contribution percentages, and successful
verification must not be inferred from the presence of generated code.
