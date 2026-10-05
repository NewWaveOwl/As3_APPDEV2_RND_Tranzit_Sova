# RND Transit — Assignment 3 Documentation

**Student: Atiom**  
Updated: 5 October 2026  
Documented implementation revision: **9612d72** (before this documentation-only commit)\
Verified platforms: **Android, Desktop and Web — my confirmation on 5 October 2026**

I built RND Transit on my existing Kotlin/Compose Multiplatform project. This record describes the final feature flow, my planning and verification, and the AI assistance I used. I confirmed the final checks as complete; historical reports and screenshot evidence are identified separately.

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
I paste the raw URL, select Overall stars, finish the questions and Save. The photo
appears only when I expand the saved History review or open Details; the draft
form has no live preview. Blank links, Skip and unsaved edits create no new photo.
Editing Cancel preserves the old photo; clearing the link and saving removes it.
The host must return image content. Query URLs need no image filename extension;
Web host/security restrictions can still cause the failure state.

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

## 3. WBS, original estimates and final retrospective effort

I kept the original **30 minutes for each P0–P11 step**. For the final record,
I asked for the gap to the next numbered prompt to estimate effort, rounded to
whole minutes when it is at most 30 minutes. If it is longer than 30 minutes,
I allocated a **45-minute retrospective task estimate**. These are my estimated
actuals, not a stopwatch record or independently verified active-work measurement.

The P6 and P7 raw windows are 34:21 and 33:25, shorter than the allocated 45 minutes.
The 45-minute values are rounded effort blocks I requested; they are not claimed
to be contained entirely inside those windows. Unrelated time is excluded where
identified. A gap above 45 minutes would leave its excess unallocated to the task;
none of the original numbered windows exceeds 45 minutes. No negative break time
is invented. The unrounded gaps remain visible for comparison.

| Step | Deliverable | Dependency | Planned | Raw prompt window | Estimated actual | Variance | Explanation |
| --- | --- | --- | --- | --- | --- | --- | --- |
| P0 | Scope and rules | None | 30 min | 00:00:41 | 1 min | -29 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P1 | Inspection/design | P0 | 30 min | 00:14:48 | 15 min | -15 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P2 | Models/generator | P1 | 30 min | 00:21:35 | 22 min | -8 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P3 | Store/restoration | P2 | 30 min | 00:19:12 | 19 min | -11 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P4 | Map/progress visuals | P3 | 30 min | 00:22:51 | 23 min | -7 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P5 | Active-time simulation | P4 | 30 min | 00:27:31 | 28 min | -2 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P6 | Sequential reviews | P5 | 30 min | 00:34:21 | 45 min | +15 min | Review and History interaction refinements; 45-minute rounded block per my retrospective rule. |
| P7 | History/details | P6 | 30 min | 00:33:25 | 45 min | +15 min | Review and History interaction refinements; 45-minute rounded block per my retrospective rule. |
| P8 | Original planner/images | P7 | 30 min | 00:25:49 | 26 min | -4 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P9 | Sealed navigation | P8 | 30 min | 00:28:57 | 29 min | -1 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P10 | Assembled review/checks | P9 | 30 min | 00:22:55 | 23 min | -7 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| P11 | Assignment documentation | P10 | 30 min | 00:27:54 | 28 min | -2 min | I used the observed prompt window rounded to whole minutes; less than my 30-minute allocation. |
| **Numbered total** | **P0–P11** | | **360 min / 6 h** | | **304 min / 5 h 04 min** | **−56 min** | Retrospective estimates; not elapsed-wall-clock sum. |

### Additional work and totals

| Additional work block | Planned | Reported actual | Variance / basis |
| --- | --- | --- | --- |
| Reading the handout/slides and generating the prompt sequence | 60 min | About 60 min | 0 min; my latest recollection |
| Submission work, debugging, fixes and adding screens | 60 min submission allocation | About 180 min total | +120 min; includes 60 min submission work plus about 120 min debugging/fixes/screens |
| Further docs and AI-assisted implementation after reviewing the assignment with AI | No original allowance | About 120 min additional | Added scope; separate from the preceding 180 min block |

I treat the last two hours as additional to the three-hour submission/debugging
block, as stated in my final clarification. These blocks describe later work,
not a second allocation of the original P0–P11 windows. The one-hour submission
allocation is already inside the three hours and is not added again. The older
partial session notes overlap this retrospective account and are not added to it.

**Known planned total: 480 minutes / 8 hours** — 360 for P0–P11, 60 for
reading/prompt preparation, and 60 for submission. The final two-hour revision
block had no original allowance.

**Estimated actual total: 664 minutes / about 11 hours 4 minutes** — 304 for the
numbered sequence, 60 for preparation, 180 for submission/debugging/fixes/screens,
and 120 for the final documentation/AI implementation revisions.

The overall difference is **+184 minutes / about 3 hours 4 minutes** against that
eight-hour plan. This includes added scope, so it is not a like-for-like claim
that every original task took longer. Within the numbered sequence, the estimated
variance is −56 minutes; P6 and P7 each have +15 minutes. Later debugging/redesign
and the unplanned final revision block account for the overall increase.

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
Finish: 360 proposed minutes. Final retrospective actuals are in the WBS above.
~~~

### Final dependency flow and effort allocation

~~~text
Preparation (60 min)
  → P0–P11 in sequence (304 min estimated actual; see per-task table)
  → Submission/debugging/fixes/new screens (180 min combined)
  → Post-review documentation/AI implementation revisions (120 min)
Total: about 664 min / 11 h 04 min.
~~~

This is a relative retrospective allocation, not an invented dated schedule.
The original 30-minute baseline Gantt remains unchanged. Preparation precedes
implementation; models support store/simulation; review state supports History;
assembled routes/screens precede verification, fixes and final documentation.

### My variance explanation

I originally allowed 30 minutes per numbered step. Most observed task windows
were shorter, so I used those rounded windows in the estimated actual column.
The review and History steps needed more interaction work; I recorded 45 minutes
for each using my final rounding rule, giving +15 minutes each against the plan.

Later I changed the GO layout, markers, map gestures, navigation and animation,
then added the nickname screen and saved review photos. Compiler/import fixes,
Web dependency-lock debugging and readability improvements also required follow-ups.
These changes expanded the one-hour submission allocation into a roughly three-hour
combined submission/debugging/fixes block. Reviewing the assignment with AI led
to another two hours of documentation and AI-assisted implementation revisions.
That added scope explains the overall increase without revising my original estimates.

[TimeFlow_As3_Sova.txt](../TimeFlow_As3_Sova.txt) retains the raw prompt timestamps,
older partial session notes and this final calculation. Earlier notes are supporting
history, not extra hours to add to the total.

## 4. AI Decision Log — ADR records

### ADR-001: One shared trip state

**Status:** Implemented and retained. I requested the shared store in Prompt 3.

**Context:** The old planner kept local text summaries while History had unrelated placeholders.

**Alternatives:** Separate screen collections; one provider-backed store.

**Decision:** App provides TripsStore through LocalTripsStore, with active/completed records, elapsed time, pending review state and drafts. Navigation remains outside it.

**Consequences:** Observable, coherent ID-based changes across screens; explicit validation/restoration is required. No permanent database is implied.

**Verification:** I confirmed the final state/restoration checks as complete on 5 October 2026. Existing test code and historical reports remain available; AI did not rerun them in this documentation update.

### ADR-002: Fixed normalized endpoints and a shared map transform

**Status:** Implemented and retained. I requested fixed points and supplied the map/marker refinements.

**Context:** Endpoints must survive composition, rotation, resizing and static details while matching the PNG.

**Alternatives:** Pixel coordinates; repeated point generation; stored normalized coordinates.

**Decision:** Generate/store points once and apply the displayed image's crop/pan/zoom transform to all overlays. Use supplied start/end/pointer artwork; rotate the pointer toward movement.

**Consequences:** Portable fixed records with responsive geometry. Normalized points and demo distance are not geographic data.

**Verification:** I confirmed the final map/gesture checks as complete. The test source and fixed previews document the bounds, interpolation and visual cases.

### ADR-003: Active-time simulation with explicit review action

**Status:** Implemented and retained. I requested active-time movement and later changed review opening to a tap at 100%.

**Context:** A ten-second journey must exclude inactive intervals, complete once and avoid repeatedly reopening review.

**Alternatives:** Count scheduled delays; include background wall-clock time; measure monotonic active intervals and save accumulated elapsed time.

**Decision:** Save elapsed milliseconds and derive all progress from them. Commit completion once; retain pending review handling. Wait for a 100% click, establish review navigation and then acknowledge handling.

**Consequences:** Pause/resume and rotation retain state; lifecycle/session guards need careful testing. Selected 0–360 minutes remain independent of the 10,000-ms demo.

**Verification:** I confirmed the final timing, pause/resume, rotation, completion and review checks as complete. Automated test fixtures cover the state invariants.

### ADR-004: ID-based review editing and missing-record recovery

**Status:** Implemented and retained. I requested editing and removal by trip ID.

**Context:** Sorting/removal and stale routes must not update the wrong item or resurrect deleted records.

**Alternatives:** List-position updates; mutable route snapshots; resolve by stable ID.

**Decision:** Separate unsaved drafts from saved TripReview and update/remove completed records by ID. Cancellation discards edits; missing-ID operations fail clearly.

**Consequences:** Collection size/identity are preserved on review saves; removal clears stale drafts/pending actions.

**Verification:** I confirmed the final review/edit/removal and layout checks as complete. The store test fixtures cover ID and collection-size preservation.

### ADR-005: Complete the planner exit before navigating

**Status:** Implemented and retained. I reported the instant transition and requested a visible downward exit.

**Context:** Extending animation duration alone did not help when GO changed destinations immediately.

**Alternatives:** Immediate navigation with outgoing animation; wait for actual planner exit completion.

**Decision:** GO/transport/intensity slide down as one foreground group, then the completion callback requests Current Trip. Shared bar/map remain stationary.

**Consequences:** A deliberate handoff with repeat-tap/restoration guards. Timer begins only on the active destination.

**Verification:** I confirmed the final platform checks as complete after the transition correction.

## 5. AI Technique Log and usage summary

[AI_Log_As3.md](../AI_Log_As3.md) is the canonical Day 17 technique log and verbatim textual conversation archive. It includes this update request and Atiom's later correction to a 360-minute maximum, original proposed estimates, timestamp-based intervals, ADR references and both shared links:

- [Extend RND Transit app](https://chatgpt.com/s/cx_6ac332b2d3888191bd989e18aa194765)
- [Write prompts for trip features](https://chatgpt.com/s/cx_6ac332cd06d48191a686a0a3777468a1)
- [Open the implementation chat in Codex](codex://threads/01a10922-a539-7d00-a97a-4809cce23449)

I used the slide 11 fields: challenge/context, AI tool, prompting approaches/elements, iteration, verification, use of output, code impact, time and reflection. Its full transcripts supplement the concise challenge entries.

Recorded approaches include staged structured prompts, prompt chaining, supplied examples, screenshot-based refinements and debugging feedback. Early output was delivered in chat; later source edits were explicitly authorized. Do not describe the whole record as manual paste only.

### My AI contribution estimate and basis

I used AI heavily for the models/generator, store/restoration, simulation, map,
review/History/details, navigation integration, landing page, image rendering,
debugging and documentation. My approximate code contribution estimate is **69% of
retained shared Kotlin source**, using a deliberately limited and reproducible basis.

At implementation revision `9612d72`, `shared/src/commonMain/kotlin` contains **9,452
nonblank Kotlin lines**. **6,509 lines across 52 files** are in files first added
following the imported-app baseline `2c8bef7`; these files were developed through
this chat's AI-assisted feature steps. 6,509 / 9,452 × 100 = **68.9%**, rounded to 69%.
The count includes comments and previews. It excludes generated code, resources,
build files, platform entry points, documentation and tests outside commonMain.
It also excludes AI rewrites of pre-existing files, so it does not claim that all
AI assistance is included. Personal refinements can remain within those files;
this is an approximate contribution/provenance measure, not a token-level audit
or a measured percentage for every byte in the repository.

This scoped estimate supports the handout's minimum 50% AI-use summary for the
shared application implementation. I supplied the requirements, mockups and icon
assets, reviewed/adapted the output, reported errors and confirmed the final checks.
At least three key AI-assisted decisions are recorded in the ADRs above.


I used staged prompts, supplied my mockups and icons, reviewed the generated files, reported compiler errors, and requested interface corrections. My decisions changed the final flow: a map-first GO screen, a circular directional pointer, explicit tap-100% review, and rendered review photos. I remain responsible for understanding and explaining the submitted implementation.

## 6. Verification record

**Complete — my confirmation on 5 October 2026.** I reported that the final
Android and Desktop/Web build-and-run checks are done and the app works on all
these systems, and asked for the test checklist to be marked complete. The source
revision documented here is `9612d72`; this follow-up changes documentation only.
AI did not execute builds or tests during this update.

Retained XML reports record 43 JVM and 42 Android host tests, zero failures, errors
or skipped tests, around 2026-10-05 03:06 UTC. Those counts are historical and
predate the photo changes. Current checklist completion is based on my later
confirmation; no new counts, precise test-device identifiers or timings are invented.
The confirmation is preserved in [the AI log](../AI_Log_As3.md#final-student-confirmation).

| Check | Expected result | Final status |
| --- | --- | --- |
| Platform builds and runs | Android, Desktop and Web work; current completion is my report, not a new AI-run build | Complete — my confirmation |
| Automated/state test checklist | Marked complete on my confirmation; no new numerical results are supplied | Complete — my confirmation |
| Nickname | Nonblank nickname enters GO and is shown in Profile; no permanent account | Complete — my confirmation |
| Minutes | 0–360; boundary arrows disappear; 355 → 360 → 355; three digits fit | Complete — my confirmation |
| Selection validation | Invalid transport/routes show a message and do not create a trip | Complete — my confirmation |
| Rapid GO and transition | One active ID; planner foreground exits down for one second before handoff | Complete — my confirmation |
| Map and gestures | Distinct original points, directional circle, orange travelled route; pan/zoom/reset stay aligned | Complete — my confirmation |
| Timing and progress | About 50% after five active seconds; all visuals share elapsed time | Complete — my confirmation |
| Rotation and inactive time | Same ID/endpoints/elapsed; leaving and backgrounding pause; drafts/selections restore | Complete — my confirmation |
| Popup/cancel | Current trip title opens information; cancel returns GO without History | Complete — my confirmation |
| Completion/review opening | Exactly 100%, zero remaining distance, one History item; tap opens review once | Complete — my confirmation |
| Initial review and Skip | Save/Skip/Close/Back opens History; Skip retains a not-reviewed trip | Complete — my confirmation |
| Edit/save/cancel | Save updates the same ID; cancel/back preserves old feedback and image | Complete — my confirmation |
| Review validation | Overall required; optional unrated categories null; image URL optional and HTTPS | Complete — my confirmation |
| Review photos | Save link, expand History/open Details; image or loading/failure/retry/browser fallback | Complete — my confirmation |
| History and details | Filters/expansion update immediately; details is static and keeps original points | Complete — my confirmation |
| Remove/stale IDs | Only selected trip removed; stale routes recover without recreating it | Complete — my confirmation |
| Layouts/navigation | Phone, rotation, keyboard and Desktop/Web usable; shared destinations/root protection retained | Complete — my confirmation |

### Screenshot evidence I supplied

The four files are in [As3_screenshots](../As3_screenshots/). They were inspected
for this documentation update and remain the original captures.

| Evidence | File | What it shows |
| --- | --- | --- |
| Repository | [Assignment 3 project](<../As3_screenshots/assignment 3 project.png>) | Assignment 3 GitHub repository and project folders |
| Machine setup capture | [Android Studio and Git Branches](<../As3_screenshots/Android studio with your project loaded and the Git Branches pop-up.png>) | Loaded project, branch popup and Android emulator |
| Larger emulator capture | [Larger phone view](<../As3_screenshots/Android studio with your project loaded and the Git Branches pop-up_BIIGERPHONE.png>) | GO layout running in the Android emulator |
| Preliminary AI investigation | [Design investigation](<../As3_screenshots/investigation into the app design using AI.png>) | My initial feature prompt and part of the AI response |

The handout specifically asks for a physical-machine photograph, while the supplied
files are screen captures. The design capture shows the investigation rather than
a complete one-page summary. These distinctions remain disclosed; successful app
checks do not change the contents of those evidence files.

## 7. Assignment coverage

| Requirement | Current implementation | Verification / gap |
| --- | --- | --- |
| Kotlin/Compose Multiplatform, Material 3, Navigation 3 | Shared code/theme, one stack/Navigator | Complete — my platform confirmation |
| Three related meaningful content screens | Home/GO, Current Trip, History | Complete — my flow confirmation |
| Information screen | About through Profile | About retained; I confirmed app operation |
| Multiple text inputs and image link on first screen | Removed from GO at Atiom's request | **Currently unmet** |
| Single entered item passed to second screen | Generated Trip passed in CurrentTripScreenKey | Parameter passing present; record is generated from selections rather than the removed text form |
| Provider-backed interactive collection | LocalTripsStore / completed List<Trip> | Complete — my checklist confirmation |
| Removal/details | ID-based removal and TripDetailsScreen | Complete — my checklist confirmation |
| Sealed-class routes | Sealed ScreenKey and concrete serializers | Complete — my navigation confirmation |
| Shared layout/navigation | App → providers → MainLayout → Router | Source present |
| State/stateless separation | Coordinators and reusable components | Source present |
| Rotation robustness | Explicit snapshots/Savers/saveable route and draft state | Complete — my checklist confirmation |
| Android plus Desktop or Web | Configured targets | Complete — Android + Desktop, with Web also confirmed |
| Responsive design bonus | Width constraints, scrolling, static previews and fitted minutes | Complete — my layout confirmation |
| Internal documentation/root README | Comments and current README | Updated to match the current feature flow |
| WBS ≥5 tasks, estimates, dependencies/Gantt | Twelve baseline tasks, 30-minute estimates and chart | Recorded; no invented original calendar chart |
| Actuals and variance | Student-reported extra blocks and prompt-window estimates | About 11 h 04 min; +3 h 04 min against documented 8 h plan; retrospective basis disclosed |
| ≥3 AI-assisted decisions and technique log | ADRs and AI_Log_As3.md | Implemented decisions and scoped 69% code estimate |
| Save prompts and outputs | Both textual chat archives, attachments and deep links | Present; binary screenshot references stay in shared chats |
| Setup/design/submission evidence | Supplied screenshot files/checklist below | Evidence identified separately from runtime results |

## 8. Submission checklist

### Handout dates

- **October 4, midnight:** WBS, original estimates and Gantt.
- **October 5, midnight:** Git/repository screenshot, main-machine photograph with Android Studio/Git Branches, preliminary AI design screenshot.
- **October 8, midnight:** Code, documentation and actual-effort summary.

No submission or extension is claimed.

### Before submission

- [x] Identify the documented implementation: `9612d72`, followed by this documentation commit.
- [x] Explicitly disclose the first-screen input gap; no instructor approval is claimed.
- [x] Confirm final Android, Desktop and Web operation.
- [x] Confirm the final test checklist; retain historical XML reports and supplied screenshots.
- [x] Include the GitHub and Android Studio/Git Branches screen captures in `As3_screenshots`.
- [ ] Supply a physical-machine photograph if required by the instructor; current captures are screenshots.
- [x] Include the original AI investigation screenshot.
- [ ] Check the separate one-page preliminary-summary screenshot requirement; current design summary is retrospective.
- [x] Record final retrospective effort, per-step variance, overlap exclusions and my rework reflection.
- [x] Record at least three implemented AI-assisted decisions and my role in the iterations.
- [x] Record an approximate 69% shared-Kotlin contribution with the count, scope and limitations.
- [x] Include README, assignment record, TimeFlow and AI prompt/output evidence.
- [ ] Preserve verification evidence, manually clean the project, inspect the complete ZIP and submit on Lea.
- [ ] Retain the actual submission confirmation.

Manual cleanup command, only after preserving evidence: run **.\gradlew.bat clean** from the project root. No cleanup/packaging/submission was performed here.

The handout specifies 10% per day late penalty up to three days, with no acceptance beyond that without prior arrangement. No extension is inferred.

## 9. Evidence fields still to complete

Final retrospective effort and the scoped AI contribution estimate are recorded above. Physical-machine/one-page-summary evidence and ZIP/Lea submission confirmation remain separate from my completed application checks. My reported submission effort does not itself establish a Lea submission receipt; no deadline extension is invented.
