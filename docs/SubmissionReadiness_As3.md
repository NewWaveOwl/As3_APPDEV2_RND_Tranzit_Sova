# Assignment 3 — Submission readiness review

Student: **Atiom**  
Review date: **5 October 2026**  
Source revision inspected: **4d1b4f5** (“Removed Greetings”)  
Verdict: **Not yet confirmed ready for submission**

Subsequent source update: nickname landing/Profile session and optional review image
links were added after this review. They do not resolve the first-screen input gap.
The source is now modified beyond revision 4d1b4f5; no new build/test results were
produced for these additions. Nickname is memory-only; review URLs use the existing
draft/snapshot restoration and open in the browser from History/details.

This is a read-only source/evidence review against Assignment_3_Handout (3).pptx, slides 1–4. No builds, tests, installations, cleanup, packaging or submission were performed. Only this review document and the AI log were written. A configured target or a source implementation is not proof of a successful run.

## Required items still outstanding

| Priority | Finding | Evidence and required action |
| --- | --- | --- |
| Required functionality | First content screen has no multiple text inputs or image-link input | TransitOpeningScreen generates title “Trip N”, an automatic description and imageUrl = "". README already discloses this gap. The handout slide 1 explicitly requires user-entered multiple text information and an image link on the first screen. Choose a small input dialog/sheet before GO, or obtain an instructor-approved alternative. No form was restored during this review because Atiom explicitly requested its removal. Review comments on a later screen do not satisfy this first-screen requirement. |
| Required execution | Final Android and Desktop/Web operation not established | Existing test XML reports are older than the latest source revisions. Record new build/test results and emulator plus chosen second-platform runs for the actual submitted revision. |
| Required AI summary | Minimum 50% AI contribution has no recorded percentage/basis | AI_Log_As3.md includes meaningful technique entries, ADR decisions, prompts and outputs. Supply an honest contribution estimate and explain what pre-existing code, retained AI output and personal changes it covers. Prompt count or elapsed intervals alone cannot establish the percentage. |
| Required effort record | Final actuals and significant variance explanations remain incomplete | Original estimates are 30 minutes per P0–P11. TimeFlow has partial self-reported sessions and elapsed chat gaps. Allocate recorded active work to WBS tasks, complete missing dates/ranges, and explain significant differences. Do not replace actuals with estimates or assume prompt gaps are active effort. |
| Required machine evidence | Supplied Android Studio images are screen captures | The handout slide 3 requires a photograph showing the main computer, Android Studio, loaded project and Git Branches popup, zoomed out enough to identify the machine; or a school-machine photograph plus explanation. The two supplied screen captures do not establish this requirement. |
| Design evidence | Supplied AI investigation screenshot exists, but lacks the requested single-page summary | The screenshot shows the preliminary prompt and part of the AI response. Capture the preliminary summary from its historical chat output. The current design summary is retrospective and must not be described as an earlier screenshot. |
| Repository completeness | Current branch is one commit ahead of local origin/main; three screenshot files are untracked | Commit the intended evidence/log/report updates and synchronize the repository when ready. Remote refs were inspected locally; no network fetch/push was performed and actual remote synchronization was not verified. |
| Final packaging | Clean ZIP and Lea submission not verified | Preserve reports/evidence first, clean manually, ZIP the complete project and inspect the archive. No ZIP or submission confirmation was found in the reviewed project evidence. |

A formal approval stamp for each ADR is not stated as a handout requirement. At least three actual AI-assisted decisions and an accurate usage summary are required; identify the decisions you made without inventing acceptance dates or rejected work.

## Source and evidence coverage

| Handout requirement | Current finding | Status |
| --- | --- | --- |
| Kotlin/Compose Multiplatform, Material 3, Navigation 3 | Shared source and configured dependencies; App owns one saved back stack/Navigator | Present in source; final compilation unverified |
| Android plus Desktop or Web | androidApp, desktopApp and webApp are included in settings | Configured; final emulator and second-platform evidence pending |
| Three related content screens | Home/GO → Current Trip → History, with review/details support | Present in source; end-to-end verification pending |
| Information screen | About contains existing names/photos and is reachable through Profile | Present; visual quality on final targets still to assess |
| Shared layout/navigation | App provides store/navigation and wraps Router in MainLayout | Present in source |
| Sealed-class routes | ScreenKey is a serializable sealed class implementing NavKey | Present in source |
| Concrete route serialization | All eight concrete destinations have registrations | Present in source; serialization/runtime verification pending |
| First-screen multiple text inputs and image link | Removed on explicit request; title/description generated and URL empty | **Unmet** |
| Single entered item passed to screen two | CurrentTripScreenKey carries Trip; Router passes key.trip | Parameter passing present; user-entered text/image part missing |
| Shared interactive collection on screen three | History reads LocalTripsStore.completedTrips; no independent saved collection | Present in source |
| Removal and details | Stable-ID removal, saved-review updates, missing-ID recovery and static details | Present in source; final interaction checks pending |
| Organization and state/component separation | Separate model, store, generator, simulation, screens and UI files | Present in inspected source |
| Rotation robustness | Back-stack restoration, TripsStore snapshot/Saver, saved planner choices/review drafts | Present in source; real Android recreation still unverified |
| Meaningful output and usability | Supplied Android screen capture shows GO UI | Partial visual evidence; does not verify complete flow or latest revision |
| Shared resources | Named generated resource imports checked against bundled filenames | No missing named resource import found by static check; compilation still required |
| Internal documentation and root README | Comments and current README exist | Present |
| WBS, original estimates, dependencies and Gantt | Twelve original 30-minute prompt tasks and sequential chart | Present; actual effort/variance incomplete |
| AI decisions, technique log, prompts/outputs | ADR and Day 17 field entries plus two chat transcripts | Updated; personal contribution/reflection/time fields remain incomplete |
| GitHub repository screenshot | As3_screenshots/assignment 3 project.png shows the Assignment 3 repository | Present; capture does not prove final commit pushed |
| Machine photograph | Existing evidence shows Android Studio/Git Branches on screen | Required photograph still missing |
| Preliminary design screenshot | AI investigation image is present | Partial; single-page preliminary summary screenshot to complete |
| Responsive design bonus | Narrow/wide previews, scrolling, fitted minute control and wide rounded panel | Present in source; device/window checks pending |
| Cleanup, project ZIP and Lea submission | No completed packaging/submission evidence inspected | Pending |

### Trip invariants inspected in source

- Store guards another unfinished trip, rejects missing IDs, replaces observable snapshots, and commits completion with one collection update.
- Completion clears active state, appends one completed record and sets pending review handling coherently.
- Review saves replace the current record by ID; skip/cancel preserve saved feedback; removal clears associated drafts and pending state.
- Simulation uses a monotonic time source, frame scheduling, lifecycle/destination guards and session-guarded final flushing.
- Stored elapsed time drives percentage, map position and remaining demo distance. Completed presentation resolves to exactly 100%.
- Endpoints are generated once and stored; details displays completed data without a simulation effect.
- Mock map drawing uses one displayed image rectangle for background and overlays; pan/zoom uses that same transform.
- Latest requested behavior is **tap 100% to review**, rather than automatic review navigation.
- Planned minutes are **0–360**, separate from the ten-second active-time duration.
- No obsolete TripResults reference or implementation TODO/FIXME was found in the inspected commonMain source search.
- Optional reference-image presentation includes loading, failure and retry. Normal generated GO trips have no URL.

These are code-review findings, not runtime test results.

## Verification evidence and Web launch status

The existing XML reports under shared/build/test-results record:

| Target | Tests | Failures | Errors | Skipped | Timestamp |
| --- | --- | --- | --- | --- | --- |
| JVM | 43 | 0 | 0 | 0 | Approximately 2026-10-05 03:06 UTC |
| Android host | 42 | 0 | 0 | 0 | Approximately 2026-10-05 03:06 UTC |

These reports predate later map/UI/minute changes and revision 4d1b4f5. They cannot certify the final submitted version.

The supplied Web launch log completed shared/Web Kotlin JS compilation but failed at :kotlinStoreYarnLock because the dependency lock changed. The later commit 7b19f1b includes a Wasm lock-file update and a message claiming a Web build fix; no successful follow-up launch log was supplied. A commit message and changed lock file are not execution evidence.

## Manual verification and commands

Run from the project root. Record the tested commit, date, platform/device and results. Commands below are instructions for Atiom; they were not executed for this review.

```powershell
Set-Location -LiteralPath 'K:\CLASSES 26 FALL\APP_DEV\As3\MainRepo\As3_APPDEV2_RND_Tranzit_Sova'
```

Android build and host state tests:

```powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
```

Launch androidApp on an emulator from Android Studio. Host tests do not replace emulator/rotation/lifecycle checks.

Desktop, if selected as the required second platform:

```powershell
.\gradlew.bat :shared:jvmTest
.\gradlew.bat :desktopApp:run
```

Web, if selected instead (choose JS or Wasm):

```powershell
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

If JS reports the same lock mismatch, follow the task named in that error:

```powershell
.\gradlew.bat kotlinUpgradeYarnLock
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
```

For a Wasm lock mismatch, use the Wasm task named in its error (kotlinWasmUpgradeYarnLock). Do not treat the earlier JS error as proof that the Wasm target has the same current failure.

### Expected runtime results

- [ ] No selected transport: clear validation; no new trip.
- [ ] Minutes stop at 0/360; corresponding arrow disappears; three digits fit.
- [ ] Intensity stays within 0–100 and the fill stays inside the oval.
- [ ] Valid GO/repeated taps: one fixed trip and one active destination; foreground controls finish the one-second downward exit before handoff.
- [ ] Start/destination are distinct and orange/green; circular pointer points along motion; travelled line fills orange.
- [ ] About five active seconds: about 50%, matching position and remaining distance.
- [ ] Rotate midway: same ID, endpoints and accumulated time.
- [ ] Leave and Resume: same trip, with paused interval excluded.
- [ ] Background and return: background interval excluded.
- [ ] Ten active seconds: 100%, zero remaining distance, exactly one History entry.
- [ ] Tap 100%: initial review opens once. Rotation must not append a trip or repeat navigation.
- [ ] Skip: entry retained as “Not reviewed”.
- [ ] Save/later review: same trip ID and collection size; optional unrated categories remain null.
- [ ] Edit/save replaces review; edit/cancel and Back preserve the old review.
- [ ] Review draft/comment/question step survive rotation.
- [ ] All/Reviewed/Not reviewed filters update immediately.
- [ ] Details uses original static endpoints and does not restart simulation.
- [ ] Remove deletes only the selected trip; stale details/review IDs recover without recreating it.
- [ ] Popup Cancel clears an unfinished trip without adding History.
- [ ] Phone, landscape, keyboard-open review and chosen Desktop/Web window remain usable.
- [ ] Profile → About, History, Settings and GO remain reachable; root Back is protected.
- [ ] Once the first-screen input gap is addressed, entered text/image link appear on the parameterized trip presentation; loading/failure do not break the bundled map.

## Finish before submission

1. Resolve the first-screen input requirement or record the instructor's approved alternative.
2. Record final Android and Desktop/Web build/run results and manual checks.
3. Complete actual-effort/variance, AI contribution basis and personal reflection fields.
4. Add the required machine photograph and preliminary-summary screenshot.
5. Commit intended source/docs/evidence and synchronize the repository; record the final revision.
6. Preserve verification evidence, run clean manually, ZIP the project, inspect it and submit through Lea.

Handout dates: October 4 midnight planning; October 5 midnight setup/design evidence; October 8 midnight code/documentation/actuals. No deadline extension, successful submission or on-time delivery is assumed.

The user-edited documentation snapshot supplied in this chat remains historical user-authored material. It was not overwritten; its earlier input/automatic-review statements should not be used as proof of the current implementation. The AI log preserves that supplied snapshot separately from the ordinary prompt index.

