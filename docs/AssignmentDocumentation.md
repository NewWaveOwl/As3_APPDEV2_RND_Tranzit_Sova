# RND Transit — Assignment 3 Documentation

**Student: Atiom**  
Updated: 5 October 2026  
Final submitted/tested revision: **to record**  
Selected verified second platform: **Desktop or Web — to record**

This document describes the current source implementation and available records. Source presence, historical test reports, manual verification and submission evidence are distinct.

## 1. Current implementation: three main screens

| Main content screen | Implemented interaction | Source responsibility |
| --- | --- | --- |
| Home / GO | Map background; centered minutes/GO; transport/routes; attraction intensity; start/resume | MainScreen, TransitOpeningScreen, TripPlannerContent, GOBox |
| Current Trip | Parameterized Trip; pan/zoom map; directional pointer; synchronized ten-second progress; information/cancel popup; tap 100% for review | CurrentTripScreen, ActiveTripSimulation, CurrentTripContent, MockTripMap |
| History | Completed provider collection; filters; expandable feedback; stable-ID details/edit/removal | HistoryScreen, History content/cards and TripsStore |

Supporting destinations: sequential TripReviewScreen, static TripDetailsScreen, Profile, Settings and About. About remains the information screen and is reached through Profile. Settings is right-aligned in the shared bar.

An introductory landing page now asks for a nonblank nickname before entering the
trip UI. A memory-only process session supplies it to Profile and retains it across
Android activity recreation; no credentials, database or nickname Saver is used.
Fresh process/browser reload starts at the landing page. The three main content
screens remain GO, Current Trip and History.

### Current GO behavior

Planned minutes are **0–360 in five-minute steps**, default 30. Up from 355 reaches 360; further increases stop. Down from 360 reaches 355. The number area fits three digits between dividers and uses text measurement to reduce font size when necessary.

GO validates the existing transport/route selections and snapshots minutes/intensity. It generates one record with an automatic title/description and empty image URL. The generator creates fixed endpoints once, using safe visible-map bounds, minimum separation, bounded attempts and a fallback.

Only one unfinished active trip exists. Resume uses the same ID and elapsed time. Repeated GO taps are guarded. All foreground planner controls slide down together for one second before navigation; the map and shared bar remain in place. Intensity fill is clipped inside the dark oval.

### Current Trip and completed feedback

The bundled PNG uses the same proportional crop as GO. Drag/pinch zoom and zoom/reset controls apply the same transform to the image and overlays. Start is orange; destination is green; the supplied circular pointer points along movement, and travelled route length fills orange.

One authoritative elapsed value produces position, percentage, progress track and remaining demo distance. Duration is 10,000 milliseconds of active time regardless of planned minutes. Leaving/backgrounding pauses; resume retains endpoints/time.

The gradient Current trip title opens stored information and unfinished-trip cancellation. Cancel returns to GO without History. At completion, the store records the trip once and keeps the display at 100%. **The user taps 100% to open review; navigation is not automatic.**

Overall experience is required to save; Quality/Interesting/Fun/comment are optional. Nullable ratings distinguish unrated feedback from valid 1–5 stars. Initial Save/Skip/Close/Back opens History. Editing uses an ID-associated draft; Save replaces feedback and Cancel preserves it. Missing IDs recover without reconstructing deleted trips.

Reviews also accept an optional HTTPS image link. The URL is part of the draft and
saved TripReview, separate from Trip.imageUrl. Blank is valid; invalid URLs prevent
Save without replacing old feedback. History expansion/details render the saved
photo in a rounded frame with loading, failure and retry states, plus a copyable
link and browser-open fallback. Loading does not change saved feedback or trip state.
The nickname plus later review URL still does not satisfy the handout's multiple
text inputs/image link on the first content screen; that gap remains unresolved.

Details displays the original completed map at 100% with no simulation or point generation. Normal GO trips have no reference URL; optional image loading/failure/retry remains available for stored records with a URL and never replaces the mock map.

## 2. Architecture and course mapping

App applies RNDTransitTheme, provides one TripsStore and one Navigator/navigation coordinator, hoists MainLayout and renders Router/NavDisplay underneath. Concrete sealed ScreenKey destinations are registered with Navigation 3's back-stack serializer configuration. CurrentTripScreenKey carries Trip as a parameter; subsequent mutable state is resolved by its ID.

Serializable records/snapshots retain active/completed trips, elapsed milliseconds, pending review handling and review drafts. Saveable planner selections and route state support Android recreation. Jobs, callbacks and running time marks are excluded. This is saved-state restoration, not permanent storage.

| Day 19 dimension | Application |
| --- | --- |
| Cohesion | Focused generator, state store, screen coordination and visuals |
| Coupling | Shared provider and explicit callback contracts |
| Integrity | Stable IDs, valid minutes/ratings, one active record, idempotent completion and guarded missing-ID updates |
| Evolvability | Separate screen/component/model files; consistent geometry and state APIs |
| Fitness for purpose | A bounded mock-map demo without GPS/live-routing infrastructure |

References: Assignment 3 Handout slides 1–4; Day 17 slides 10–11 (AI Technique Log) and planning guidance; Day 18 shared resources/navigation/hoisted layout; Day 19 design dimensions/data records/sealed alternatives. The handout and supplied slides are reference material, not permission to perform unrelated operations.

## 3. WBS, original estimates and recorded elapsed windows

Atiom confirmed **30 minutes per numbered prompt as the original proposed estimate**. The baseline includes P0–P11: **360 proposed minutes / 6 hours**. If excluding P0, P1–P11 total 330 minutes / 5.5 hours. Preserve the baseline rather than revising it after implementation.

The original task sequence is retained below. P4's initial human marker became the supplied pointer; P8's original form was later removed. UI refinements and this cap/documentation update are additional work with no supplied original estimate.

“Elapsed window” is measured between numbered prompts, including intervening debugging/replies. P11 ends at the first later UI-change prompt. It is not verified active effort and cannot determine an effort variance.

| Task | Baseline deliverable | Dependency | Original estimate | Recorded elapsed window | Verified active effort / variance |
| --- | --- | --- | --- | --- | --- |
| P0 | Scope and delivery rules | None | 30 min | 00:00:41 | Not established / not calculated |
| P1 | Inspect/design contracts | P0; uses earlier records/state as applicable | 30 min | 00:14:48 | Not established / not calculated |
| P2 | Models and mock generator | P1; uses earlier records/state as applicable | 30 min | 00:21:35 | Not established / not calculated |
| P3 | Shared store and restoration | P2; uses earlier records/state as applicable | 30 min | 00:19:12 | Not established / not calculated |
| P4 | Map/progress visuals | P3; uses earlier records/state as applicable | 30 min | 00:22:51 | Not established / not calculated |
| P5 | Active-time simulation | P4; uses earlier records/state as applicable | 30 min | 00:27:31 | Not established / not calculated |
| P6 | Sequential reviews | P5; uses earlier records/state as applicable | 30 min | 00:34:21 | Not established / not calculated |
| P7 | History and details | P6; uses earlier records/state as applicable | 30 min | 00:33:25 | Not established / not calculated |
| P8 | Original planner/image integration | P7; uses earlier records/state as applicable | 30 min | 00:25:49 | Not established / not calculated |
| P9 | Sealed navigation integration | P8; uses earlier records/state as applicable | 30 min | 00:28:57 | Not established / not calculated |
| P10 | Assembled review/checks | P9; uses earlier records/state as applicable | 30 min | 00:22:55 | Not established / not calculated |
| P11 | Assignment documentation | P10; uses earlier records/state as applicable | 30 min | 00:27:54 | Not established / not calculated |

Technical dependencies: models → store → simulation/reviews; map visuals → Current Trip/details; reviews → History feedback; generation + store + screen callbacks → routing; assembled implementation → verification/documentation. The baseline reflects sequential human review; no parallel work is claimed.

### Relative baseline Gantt

Each slot is 30 proposed minutes, not a date or actual session. An X marks the original allocation.

~~~text
Slot                   01 02 03 04 05 06 07 08 09 10 11 12
P0  Scope               X
P1  Design                 X
P2  Models                    X
P3  Store                        X
P4  Map                             X
P5  Simulation                         X
P6  Reviews                               X
P7  History                                  X
P8  Planner                                     X
P9  Navigation                                     X
P10 Verification                                      X
P11 Documentation                                        X
Finish: 360 proposed minutes. Actual task bars are not established.
~~~

### Time records and variance

[TimeFlow_As3_Sova.txt](../TimeFlow_As3_Sova.txt) contains the formatted student session notes and every recorded prompt-to-prompt interval in this chat. Local timestamps use America/Toronto. Complete supplied session ranges total **4 hours 30 minutes**, including ten minutes without an activity label; the incomplete implementation range and eating break are excluded. Dates for those manual notes were not supplied.

The chat's first-to-latest-prompt span is recorded there separately. Do not add it to the session total: the records overlap and chat gaps can include waiting/breaks. Active effort per WBS task and final active-effort total remain unestablished.

Effort variance = verified active effort − original estimate. Do not substitute a prompt gap for verified effort. Record significant variance reasons from actual experience; no reason is invented.

## 4. AI Decision Log — ADR records

### ADR-001: One shared trip state

**Status:** Required in the student's state prompt; separate formal ADR approval/date not recorded.

**Context:** The old planner kept local text summaries while History had unrelated placeholders.

**Alternatives:** Separate screen collections; one provider-backed store.

**Decision:** App provides TripsStore through LocalTripsStore, with active/completed records, elapsed time, pending review state and drafts. Navigation remains outside it.

**Consequences:** Observable, coherent ID-based changes across screens; explicit validation/restoration is required. No permanent database is implied.

**Verification:** Source and supplied focused tests exist. Historical results do not verify this final revision; runtime restoration is pending.

### ADR-002: Fixed normalized endpoints and a shared map transform

**Status:** Required by the model/map prompts and subsequent asset/crop refinements; formal ADR approval/date not recorded.

**Context:** Endpoints must survive composition, rotation, resizing and static details while matching the PNG.

**Alternatives:** Pixel coordinates; repeated point generation; stored normalized coordinates.

**Decision:** Generate/store points once and apply the displayed image's crop/pan/zoom transform to all overlays. Use supplied start/end/pointer artwork; rotate the pointer toward movement.

**Consequences:** Portable fixed records with responsive geometry. Normalized points and demo distance are not geographic data.

**Verification:** Geometry/generator checks and previews exist; final visual alignment/gestures require manual verification.

### ADR-003: Active-time simulation with explicit review action

**Status:** Active-time behavior required originally; tap-100% behavior explicitly requested in the later redesign. Formal ADR approval/date not recorded.

**Context:** A ten-second journey must exclude inactive intervals, complete once and avoid repeatedly reopening review.

**Alternatives:** Count scheduled delays; include background wall-clock time; measure monotonic active intervals and save accumulated elapsed time.

**Decision:** Save elapsed milliseconds and derive all progress from them. Commit completion once; retain pending review handling. Wait for a 100% click, establish review navigation and then acknowledge handling.

**Consequences:** Pause/resume and rotation retain state; lifecycle/session guards need careful testing. Selected 0–360 minutes remain independent of the 10,000-ms demo.

**Verification:** Tests were supplied for elapsed time, restoration and duplicate completion. Final lifecycle/rotation/click behavior remains pending.

### ADR-004: ID-based review editing and missing-record recovery

**Status:** Required in the review/History prompts; formal ADR approval/date not recorded.

**Context:** Sorting/removal and stale routes must not update the wrong item or resurrect deleted records.

**Alternatives:** List-position updates; mutable route snapshots; resolve by stable ID.

**Decision:** Separate unsaved drafts from saved TripReview and update/remove completed records by ID. Cancellation discards edits; missing-ID operations fail clearly.

**Consequences:** Collection size/identity are preserved on review saves; removal clears stale drafts/pending actions.

**Verification:** Existing test code covers these invariants. Final interaction and keyboard checks are pending.

### ADR-005: Complete the planner exit before navigating

**Status:** Correction requested after Atiom reported an instant transition; final runtime acceptance not recorded.

**Context:** Extending animation duration alone did not help when GO changed destinations immediately.

**Alternatives:** Immediate navigation with outgoing animation; wait for actual planner exit completion.

**Decision:** GO/transport/intensity slide down as one foreground group, then the completion callback requests Current Trip. Shared bar/map remain stationary.

**Consequences:** A deliberate handoff with repeat-tap/restoration guards. Timer begins only on the active destination.

**Verification:** Source correction is present; rebuilt-device confirmation is pending.

## 5. AI Technique Log and usage summary

[AI_Log_As3.md](../AI_Log_As3.md) is the canonical Day 17 technique log and verbatim textual conversation archive. It includes this update request and Atiom's later correction to a 360-minute maximum, original proposed estimates, timestamp-based intervals, ADR references and both shared links:

- [Extend RND Transit app](https://chatgpt.com/s/cx_6ac332b2d3888191bd989e18aa194765)
- [Write prompts for trip features](https://chatgpt.com/s/cx_6ac332cd06d48191a686a0a3777468a1)
- [Open the implementation chat in Codex](codex://threads/01a10922-a539-7d00-a97a-4809cce23449)

The log follows the slide 11 fields: challenge/context, AI tool, prompting approaches/elements, iteration, verification, use of output, code impact, time and reflection. Its full transcripts supplement the concise challenge entries.

Recorded approaches include staged structured prompts, prompt chaining, supplied examples, screenshot-based refinements and debugging feedback. Early output was delivered in chat; later source edits were explicitly authorized. Do not describe the whole record as manual paste only.

The handout requires at least 50% AI generation and at least three AI-assisted key decisions. Assistance is documented across design, models/state, visuals, simulation, reviews/History, navigation, debugging and documentation. **A defensible code-contribution percentage and its basis remain for Atiom to supply.** Prompt counts, code length and clock intervals do not establish a contribution percentage or active effort.

Atiom should record personal reflections, retained AI output versus personal changes, formal decision approvals where applicable, and ability to explain the submitted code.

## 6. Verification record

Earlier documentation recorded 43 JVM and 42 Android host tests without failures around 2026-10-05 03:06 UTC, and reported Android/JS/Wasm compilation successes. The tested revision was not identified. These historical claims do not establish verification of subsequent map/layout/navigation revisions or the current minute cap.

No builds/tests were executed for this update. Focused minute-boundary/restoration tests and 360-minute narrow-phone previews were added, but remain unexecuted. Run the Android/Desktop commands in README and record revision/date/device/results.

| Manual check | Expected result | Final evidence |
| --- | --- | --- |
| Minutes | 0 minimum, 360 maximum; boundary arrows disappear; 355 → 360 → 355; three digits fit on 320-dp phone and Resume layout | Pending |
| Selection validation | No valid transport selection produces a clear message and no new trip | Pending |
| Rapid GO | One active ID; one foreground exit; one destination | Pending |
| Transition | All planner controls slide down for one second before Current Trip; stationary map/shared bar | Pending |
| Map | Original fixed endpoints; orange start/green end; directional circular pointer; orange travelled line | Pending |
| Gestures | Pan/pinch/zoom/reset retain aligned map/markers/route | Pending |
| Five active seconds | Approximately 50%, matching pointer/percentage/remaining distance | Pending |
| Rotation | Same ID/endpoints/time and saveable selections/drafts | Pending |
| Leave/resume/background | Inactive intervals excluded; same stored trip resumes | Pending |
| Popup/cancel | Gradient title opens information; unfinished cancel returns GO with no History entry | Pending |
| Completion | Exactly 100%, zero remaining distance, one History item; waits for click | Pending |
| Initial review | Tap 100% opens once; Save/Skip/Close/Back opens History | Pending |
| Edit/cancel | Same ID and collection size; Cancel preserves saved feedback | Pending |
| History | Filters, expansion, details/edit/remove and immediate observable updates | Pending |
| Details/stale IDs | Static original route; no restart; deleted IDs show recovery without recreation | Pending |
| Layouts | Phone, rotated, keyboard-open review and selected Desktop/Web platform usable | Pending |
| Navigation | GO/Profile/History/Settings reachable; About from Profile; Home root protected | Pending |
| Optional reference image | For a stored URL, failure/retry is isolated from the map; normal GO uses no URL | Pending |

## 7. Assignment coverage

| Requirement | Current implementation | Verification / gap |
| --- | --- | --- |
| Kotlin/Compose Multiplatform, Material 3, Navigation 3 | Shared code/theme, one stack/Navigator | Final compile/run pending |
| Three related meaningful content screens | Home/GO, Current Trip, History | Source present; runtime flow pending |
| Information screen | About through Profile | Visual quality pending |
| Multiple text inputs and image link on first screen | Removed from GO at Atiom's request | **Currently unmet** |
| Single entered item passed to second screen | Generated Trip passed in CurrentTripScreenKey | Parameter passing present; record is generated from selections rather than the removed text form |
| Provider-backed interactive collection | LocalTripsStore / completed List<Trip> | Source present; final UI check pending |
| Removal/details | ID-based removal and TripDetailsScreen | Source present; final check pending |
| Sealed-class routes | Sealed ScreenKey and concrete serializers | Source present; final serialization check pending |
| Shared layout/navigation | App → providers → MainLayout → Router | Source present |
| State/stateless separation | Coordinators and reusable components | Source present |
| Rotation robustness | Explicit snapshots/Savers/saveable route and draft state | Final Android recreation check pending |
| Android plus Desktop or Web | Configured targets | Final launches/second-platform selection pending |
| Responsive design bonus | Width constraints, scrolling, static previews and fitted minutes | Final phone/desktop check pending |
| Internal documentation/root README | Comments and current README | Updated; Atiom review pending |
| WBS ≥5 tasks, estimates, dependencies/Gantt | Twelve baseline tasks, 30-minute estimates and chart | Recorded; no invented original calendar chart |
| Actuals and variance | Supplied session notes plus labelled prompt gaps | Verified effort allocation/variance incomplete |
| ≥3 AI-assisted decisions and technique log | ADRs and AI_Log_As3.md | Formal approvals/reflections/contribution basis pending |
| Save prompts and outputs | Both textual chat archives, attachments and deep links | Present; binary screenshot references stay in shared chats |
| Setup/design/submission evidence | Fields/checklists below | Not established by source presence |

## 8. Submission checklist

### Handout dates

- **October 4, midnight:** WBS, original estimates and Gantt.
- **October 5, midnight:** Git/repository screenshot, main-machine photograph with Android Studio/Git Branches, preliminary AI design screenshot.
- **October 8, midnight:** Code, documentation and actual-effort summary.

No submission or extension is claimed.

### Before submission

- [ ] Identify the final submitted/tested commit.
- [ ] Resolve or explicitly disclose the missing first-screen text/image-link requirement.
- [ ] Demonstrate meaningful output on emulated Android and the chosen Desktop/Web target.
- [ ] Complete the current manual checks and save logs/screenshots with revision/date/device.
- [ ] Supply the GitHub Assignment 3 repository screenshot and required machine photograph.
- [ ] Supply the preliminary AI design screenshot; the updated design summary is not proof of an earlier capture.
- [ ] Complete effort allocation/variance and personal reflections from actual records.
- [ ] Confirm at least three AI-assisted decisions and explain the code.
- [ ] Provide the AI contribution percentage/basis required by the handout.
- [ ] Include README, assignment record, TimeFlow and AI prompt/output evidence.
- [ ] Preserve verification evidence, manually clean the project, inspect the complete ZIP and submit on Lea.
- [ ] Retain the actual submission confirmation.

Manual cleanup command, only after preserving evidence: run **.\gradlew.bat clean** from the project root. No cleanup/packaging/submission was performed here.

The handout specifies 10% per day late penalty up to three days, with no acceptance beyond that without prior arrangement. No extension is inferred.

## 9. Evidence fields still to complete

Final tested/submitted revision; Android and second-platform runtime evidence; GitHub screenshot; main-machine photograph; preliminary-design screenshot; active effort per task and variance; missing manual-note dates/range/activity; AI contribution percentage/basis; formal ADR approvals if applicable; submission confirmation.
