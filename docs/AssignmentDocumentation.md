# RND Transit — Assignment 3 Documentation

## Record status

**Student:** To complete.  
**Student identifier:** To complete.  
**Final submitted commit/revision:** To complete.  
**Selected second platform:** To complete — Desktop or Web.  
**Runtime verification date and device:** To complete.

This document describes the supplied implementation and available evidence. It does not certify that all assignment requirements have been verified.

The student confirmed that **30 minutes per prompt was the original proposed estimate**. Recorded actual times have not been supplied.

Explicit approval of the ADR entries has not been supplied. Their status therefore remains Proposed even where the described approach is present in the source.

## 1. References and assignment scope

The documentation follows:

- Assignment 3 Handout, slides 1–4.
- Day 17: Shared Layout, Gantt Chart, and AI Technique Log, particularly slides 10–12 and the dependency guidance.
- Day 18: shared resources, shared navigation, and hoisted layout.
- Day 19: data records, restricted alternatives, cohesion, coupling, integrity, evolvability, and fitness for purpose.

### Assignment requirements

The handout requires:

- Kotlin/Compose Multiplatform, Material 3, and Navigation 3.
- Emulated Android plus Desktop or Web.
- Three related meaningful content screens and an information screen.
- Multiple text inputs and an image link on the first screen.
- The entered item passed as a parameter to the second screen.
- A provider-backed interactive collection on the third screen.
- Item removal and details.
- Routes defined through a sealed class.
- Shared layout/navigation and rotation robustness.
- Meaningful internal documentation and a root README.
- WBS, original estimates, dependencies, Gantt chart, actual effort, and variance notes.
- Intentional AI use, at least three AI-assisted key decisions, saved prompts/outputs, and an AI usage summary.

Responsive design is a bonus requirement.

### Additional feature requirements supplied by the student

The assignment does not itself prescribe:

- A ten-second active-time trip simulation.
- Random normalized endpoints generated once.
- An orange person marker.
- Pause, resume, cancellation, and exactly-once completion.
- Sequential review questions inspired by supplied screenshots.
- Overall-required and optional-null star ratings.
- Review editing by stable ID.
- A bundled mock map separate from the entered reference image.
- No GPS, real routing, street guidance, or permanent database.

These are the chosen RND Transit feature requirements.

## 2. Work breakdown structure and original estimates

### Estimate source and counting convention

Original proposed estimate: **30 minutes for each prompt**, confirmed by the student during Prompt 11.

The table includes Prompt 0, which established scope and delivery rules, and Prompts 1–11. That is twelve prompt-level tasks:

**12 × 30 minutes = 360 minutes = 6 hours proposed effort.**

If the student's original accounting excluded Prompt 0, record that convention here. Prompts 1–11 alone total 330 minutes, or 5 hours 30 minutes. Do not change individual estimates to match hindsight.

The task labels below organize the prompt sequence for this document. This does not claim that this exact table or chart existed before implementation.

The handout also gives a general six-hour effort guideline. That guideline is not evidence of actual time worked.

| ID | Task and deliverable | Prompt | Original estimate | Prerequisite for the sequential workflow | Actual effort | Variance | Explanation |
| --- | --- | --- | --- | --- | --- | --- | --- |
| P0 | Establish project scope, preserved behavior, and delivery rules | 0 | 30 min | None | Not supplied | Not calculated | Student to complete |
| P1 | Inspect project and establish architecture, contracts, and assignment mapping | 1 | 30 min | P0 | Not supplied | Not calculated | Student to complete |
| P2 | Trip records, validation, snapshots, and mock-trip generator | 2 | 30 min | P1 | Not supplied | Not calculated | Student to complete |
| P3 | Shared TripsStore, provider, Saver, snapshot restoration, and App integration | 3 | 30 min | P2 | Not supplied | Not calculated | Student to complete |
| P4 | Mock map, orange person, progress presentation, and static previews | 4 | 30 min | P3; uses P2 records | Not supplied | Not calculated | Student to complete |
| P5 | Current Trip coordination, active-time simulation, pause/resume, and cancellation | 5 | 30 min | P4; uses P3 state | Not supplied | Not calculated | Student to complete |
| P6 | Sequential review form, star controls, review drafts, Save/Skip/edit behavior | 6 | 30 min | P5; uses P3 state | Not supplied | Not calculated | Student to complete |
| P7 | Completed History, filters, cards, removal, and static details | 7 | 30 min | P6; uses P4 visuals | Not supplied | Not calculated | Student to complete |
| P8 | Planner inputs, GO/Resume behavior, image loading, and affected screens | 8 | 30 min | P7; uses P2/P3 | Not supplied | Not calculated | Student to complete |
| P9 | Sealed route hierarchy, registrations, shared navigation, and complete flow | 9 | 30 min | P8; integrates P5–P7 | Not supplied | Not calculated | Student to complete |
| P10 | Assembled-code review, corrections, invariant tests, and verification | 10 | 30 min | P9 | Not supplied | Not calculated | Student to complete |
| P11 | README, planning record, ADRs, technique log, checklists, and design summary | 11 | 30 min | P10; uses available evidence | Not supplied | Not calculated | Student to complete |
| Total | Twelve prompt-level tasks | 0–11 | **360 min / 6 h** | Sequential review gates | **Not supplied** | **Not calculated** | Student to complete |

### Technical dependencies

The prompt sequence was deliberately reviewed one step at a time.

Within that sequence:

- Models and generator establish the records used by the store and screens.
- Store restoration supports simulation and review coordination.
- Map/progress components support Current Trip and completed details.
- Review state/components support saved feedback shown in History.
- Planner generation and callbacks connect to final routing.
- Final navigation integrates all screen contracts and serializers.
- Verification depends on the assembled flow.
- Final documentation depends on the implementation and available evidence.

Some components could technically be developed in parallel. No parallel development or overlapping work is claimed.

## 3. Final Gantt chart and handout milestones

### Relative planned-effort Gantt

Each column represents **30 minutes of planned work**. It does not represent a calendar day or actual work session.

`██` = allocated original estimate.  
`--` = no allocation for that task in that slot.

```text
Planned slot         01 02 03 04 05 06 07 08 09 10 11 12
Elapsed minutes       0 30 60 90 120150180210240270300330
                     -----------------------------------
P0 Scope             ██ -- -- -- -- -- -- -- -- -- -- --
P1 Design            -- ██ -- -- -- -- -- -- -- -- -- --
P2 Models            -- -- ██ -- -- -- -- -- -- -- -- --
P3 Store             -- -- -- ██ -- -- -- -- -- -- -- --
P4 Map visuals       -- -- -- -- ██ -- -- -- -- -- -- --
P5 Simulation        -- -- -- -- -- ██ -- -- -- -- -- --
P6 Reviews           -- -- -- -- -- -- ██ -- -- -- -- --
P7 History/details   -- -- -- -- -- -- -- ██ -- -- -- --
P8 Planner/images    -- -- -- -- -- -- -- -- ██ -- -- --
P9 Navigation        -- -- -- -- -- -- -- -- -- ██ -- --
P10 Verification     -- -- -- -- -- -- -- -- -- -- ██ --
P11 Documentation    -- -- -- -- -- -- -- -- -- -- -- ██
                     -----------------------------------
Planned finish: 360 minutes of work
Actual task dates and bars: not supplied
```

Planned intervals are P0: 0–30, P1: 30–60, P2: 60–90, P3: 90–120, P4: 120–150, P5: 150–180, P6: 180–210, P7: 210–240, P8: 240–270, P9: 270–300, P10: 300–330, and P11: 330–360 minutes.

This chart preserves the supplied estimate and sequential prompt order. Breaks, waiting between prompts, and actual calendar placement are not established.

### Calendar milestones from the handout

| Deadline | Required deliverable | Completion evidence |
| --- | --- | --- |
| October 4, midnight | WBS, original effort estimates, and Gantt chart | Not supplied |
| October 5, midnight | Git/repository evidence, machine setup photograph, preliminary AI design screenshot | Not supplied |
| October 8, midnight | Assignment code, documentation, and actual-effort summary | Not supplied |

No revised deadline or completed submission is claimed.

## 4. Actual effort and variance record

For each WBS task, copy the student's recorded actual time into the table.

Calculate:

**Variance in minutes = actual effort − original estimate.**

A positive value means more time than estimated. A negative value means less time. Zero means the recorded effort matches the estimate.

For a significant difference, explain the observed cause in one or two sentences. No cause has been inferred here.

Record these totals after entering actuals:

- Actual total: Not supplied.
- Original estimated total: 360 minutes under the P0–P11 convention.
- Total variance: Not calculated.
- Most significant variance and explanation: Student to complete.
- Counting convention, including whether P0 was tracked: Student to confirm.

### Optional session record

| Date | WBS task | Active work start/end or duration | Work performed | Evidence/reference |
| --- | --- | --- | --- | --- |
| Not supplied | Student to complete | Student to complete | Student to complete | Student to complete |

Include relevant debugging and AI interaction time. Exclude unrelated breaks.

AI Technique Log time can overlap WBS time. Do not add it again as separate effort unless the original accounting explicitly treated it separately.

Test execution duration in a report is not the student's total work time.

## 5. AI Decision Log — ADR records

### ADR-001: Shared trip state

**Status:** Proposed — explicit student ADR approval not supplied.  
**Approval/date:** Student to complete.

**Context:**  
The described starting planner used local saved text summaries while History displayed placeholders. The new workflow needs active-trip coordination and a shared completed collection.

**Alternatives:**

1. Maintain separate trip collections in planner, History, and review screens.
2. Provide one shared TripsStore from App.

These are architectural alternatives, not a claim that the student implemented or rejected each one.

**Decision:**  
The supplied implementation uses one TripsStore through LocalTripsStore. It owns active state, elapsed time, completed trips, pending review handling, and review drafts. Screens derive their displays from that store.

**Consequences:**  
Review updates and removal affect the same records shown throughout the app. Atomic snapshot replacement keeps updates observable. The cost is explicit store operations and restoration validation. Navigation remains outside the store.

**Verification:**  
Existing test reports record successful collection, review-update, deletion, and snapshot tests. Device UI updates and the submitted revision still require confirmation.

### ADR-002: Stored normalized endpoints generated once

**Status:** Proposed — explicit student ADR approval not supplied.  
**Approval/date:** Student to complete.

**Context:**  
A mock route must survive resizing, rotation, resuming, and details navigation without changing endpoints. Pixel positions depend on display size.

**Alternatives:**

1. Store display-specific pixel coordinates.
2. Regenerate endpoints when displaying the map.
3. Store normalized endpoints generated once per trip.

No rejected implementation history is claimed.

**Decision:**  
The supplied generator creates and stores normalized start and destination points once. It uses safe margins, minimum separation, bounded attempts, and a fallback. MockTripMap converts those points through the actual displayed image rectangle.

**Consequences:**  
The route can retain its geometry at different sizes. The image and overlays must use the same transform. Normalized coordinates and synthetic distance must be explained clearly because they are not geographic data.

**Verification:**  
Existing reports include seeded-generation, endpoint-margin, separation, fallback, and retained-endpoint tests. Visual alignment and midpoint position still require runtime or preview inspection.

### ADR-003: Restorable active time and idempotent completion

**Status:** Proposed — explicit student ADR approval not supplied.  
**Approval/date:** Student to complete.

**Context:**  
The simulation must last ten seconds of active time, pause outside Current Trip or while backgrounded, resume after recreation, and enter History once.

**Alternatives:**

1. Count scheduled delay iterations.
2. Use wall-clock time that includes paused intervals.
3. Measure active intervals monotonically and save accumulated elapsed time.

No rejected implementation history is claimed.

**Decision:**  
The supplied implementation uses transient monotonic time marks and frame scheduling. TripsStore saves accumulated elapsed milliseconds. Progress drives all visuals. Reaching the duration commits completion in one observable state update, clears active state, and creates pending review handling.

Review navigation is acknowledged after the destination is established.

**Consequences:**  
Paused intervals can be excluded and restored trips retain elapsed time. Session guards prevent stale cancellation from stopping a newer session. Lifecycle and destination ownership require careful coordination. Jobs and time marks are not persisted.

**Verification:**  
Existing reports include irregular-frame timing, excluded paused time, restored elapsed time, stale-session cancellation, duplicate completion, and pending-navigation tests. Actual device lifecycle and rotation timing remain pending.

### ADR-004: Review editing and removal by stable ID

**Status:** Proposed — explicit student ADR approval not supplied.  
**Approval/date:** Student to complete.

**Context:**  
A review can be created immediately after completion or edited later from History/details. A referenced trip may be removed.

**Alternatives:**

1. Update records by their current list position.
2. Save feedback into a route's retained Trip snapshot.
3. Resolve and update the current stored record by stable ID.

No rejected implementation history is claimed.

**Decision:**  
The supplied implementation resolves completed trips by ID. Unsaved feedback stays in an ID-associated draft. Save replaces the existing review without appending a trip. Cancel discards the draft. Missing-ID saves return an unsuccessful result and cannot recreate records.

**Consequences:**  
Sorting and filtering do not change record identity. Cancel preserves saved feedback. Removal must clear matching drafts and pending actions, and missing destinations need recovery content.

**Verification:**  
Existing reports cover review replacement, preserved IDs and collection size, deleted-ID protection, draft restoration, and editing return destinations. Keyboard, interaction, and visual checks remain pending.

## 6. AI Technique Log — Day 17 fields

These entries summarize meaningful challenges. They do not replace saved prompts and outputs.

### Technique entry 1: Architecture, records, and shared state

**Challenge/context:**  
Replace disconnected summaries/placeholders with structured trips and one provider-backed collection. Related prompts: 0–3.

**AI tool:**  
Codex in this conversation. Exact model/version was not recorded in the supplied evidence.

**Prompting approaches:**  
Structured prompting, decomposition, multi-step prompting, and prompt chaining.

**Prompt elements:**  
Goal, existing-project context, assignment requirements, constraints, Kotlin examples, and a required delivery format.

**Iteration/adaptation:**  
The requests established separate records, generator, store, provider, and Saver responsibilities. Follow-up questions identified rating-property syntax and navigation import problems.

**Verification:**  
Read-only source review and existing model/state/serialization reports. Student runtime checks are not recorded.

**How the output was used:**  
Corresponding implementation files are present. Student to specify whether output was used essentially as generated, modified, used as reference, or rejected.

**Approximate code impact:**  
Models, generator, state, and App integration. Percentage not supplied.

**Time spent:**  
Not supplied. Record total active interaction/debugging minutes.

**Result/reflection:**  
The supplied code contains structured records and a coherent shared state layer. Student to record what they understood, changed, and found effective.

### Technique entry 2: Mock map and active-time movement

**Challenge/context:**  
Adapt the supplied map mockup while keeping fixed endpoints, consistent scaling, synchronized progress, and pause/resume. Related prompts: 4–5.

**AI tool:**  
Codex in this conversation.

**Prompting approaches:**  
Structured prompting, decomposition, prompt chaining, and visual-reference guidance.

**Prompt elements:**  
Map mockup, fixed-point interpolation example, resource rules, ten-second timing requirements, lifecycle constraints, previews, and expected checks.

**Iteration/adaptation:**  
The conversation included a Dp-to-pixel error report and unresolved test imports. The supplied files use explicit density conversion and commonTest test placement.

**Verification:**  
Existing simulation reports cover actual elapsed intervals, pausing, restoration, and stale sessions. Map alignment, recognizability, and observed timing remain manual checks.

**How the output was used:**  
Map, marker, progress, and simulation files are present. Student to record retained output and manual modifications.

**Approximate code impact:**  
Mock map, marker, progress presentation, Current Trip coordination, and timing tests. Percentage not supplied.

**Time spent:**  
Not supplied. Include debugging time associated with this challenge.

**Result/reflection:**  
The implementation separates the stored route from its rendering and timing coordination. Student reflection and observed runtime outcome remain to complete.

### Technique entry 3: Sequential reviews and interactive History

**Challenge/context:**  
Adapt the supplied review screenshots to transit feedback, optional ratings, editing, filters, expandable cards, and deletion. Related prompts: 6–7.

**AI tool:**  
Codex in this conversation.

**Prompting approaches:**  
Structured prompting, decomposition, prompt chaining, and visual-reference guidance.

**Prompt elements:**  
Screenshots, category names, star-row example, required/optional rules, draft restoration, navigation behavior, and complete-file requirements.

**Iteration/adaptation:**  
The request specified a sequence of prompted questions with rolling transitions. A follow-up distinguished saved TripReview data from TripReviewMode navigation behavior. History interaction requirements added expansion and edit/remove actions.

**Verification:**  
Existing review and History reports cover required overall feedback, optional null values, saved-review preservation, draft restoration, filtering, and removal. Interaction, keyboard, and layout checks are pending.

**How the output was used:**  
Review and History implementation files are present. Student to record whether they used or modified the generated presentation.

**Approximate code impact:**  
Review screen/components/state, History cards/content, details, and associated tests. Percentage not supplied.

**Time spent:**  
Not supplied.

**Result/reflection:**  
The implementation separates unsaved drafts from saved feedback and supports later editing. Student to record usability observations and any changes they made.

### Technique entry 4: Planner, images, navigation, and verification

**Challenge/context:**  
Replace obsolete GO/results behavior and integrate the complete Navigation 3 flow without replaying completed trips. Related prompts: 8–10.

**AI tool:**  
Codex in this conversation.

**Prompting approaches:**  
Structured prompting, decomposition, prompt chaining, and review-driven refinement.

**Prompt elements:**  
Existing contracts, complete route flow, serialization examples, preservation requirements, official image-loading documentation, and explicit verification scenarios.

**Iteration/adaptation:**  
The student explicitly requested Current Trip routing during the planner step. The review later identified completion-boundary Back and retained review destinations underneath header shortcuts. Corrected navigation and focused regression tests are present.

**Verification:**  
Existing XML reports record 43 JVM and 42 Android host tests with no failures. Build/compilation successes are recorded in the previous README. Final-revision confirmation and runtime checks remain pending.

**How the output was used:**  
The corrected navigation and regression files are present. Student to record who applied changes, any modifications, and their own verification.

**Approximate code impact:**  
Planner, reference-image presentation, routing/controller integration, shared header, previews, and regression tests. Percentage not supplied.

**Time spent:**  
Not supplied. Build-report execution seconds are not a substitute for interaction/debugging time.

**Result/reflection:**  
The review exposed two navigation edge cases and supplied testable corrections. Student to explain what they learned and whether the approach reduced integration risk.

### Technique entry 5: Evidence-based assignment documentation

**Challenge/context:**  
Document the final implementation without inventing effort, accepted decisions, verification, or submission evidence. Related prompt: 11.

**AI tool:**  
Codex in this conversation.

**Prompting approaches:**  
Structured prompting, decomposition, and clarification of ambiguous information.

**Prompt elements:**  
Required documents, ADR structure, Day 17 fields, preservation of original estimates, handout deadlines, and chat-only delivery.

**Iteration/adaptation:**  
The student supplied 30 minutes per prompt and clarified that this was the original proposed estimate. Actual effort was left separate.

**Verification:**  
Course references, source/configuration, existing README, and existing XML reports were read without saving files or running checks.

**How the output was used:**  
Documentation supplied in chat for student review and manual saving. Final use is not yet confirmed.

**Approximate code impact:**  
Documentation only; no production-code changes. Assignment-document contribution percentage not supplied.

**Time spent:**  
Not supplied. The original 30-minute estimate is not an actual-time measurement.

**Result/reflection:**  
Known evidence and unknown fields are separated. Student to complete actuals, approvals, contribution summary, and submission evidence.

## 7. AI usage summary

The handout requires at least 50% AI usage and at least three AI-assisted key decisions.

The conversation documents AI assistance with architecture, code generation, UI adaptation, debugging, navigation review, testing suggestions, and documentation.

That scope does not establish a contribution percentage.

Complete before submission:

- Estimated AI contribution to assignment code: Not supplied.
- Basis for that estimate: Student to complete.
- Distinction between pre-existing code, retained AI output, and student changes: Student to complete.
- At least three confirmed AI-assisted decisions: Student to select and confirm ADR status.
- Student review/adaptation of generated code: Student to complete.
- Ability to explain the final code: Student to confirm through preparation.
- Saved prompt/output archive location: Not supplied.

Do not calculate code contribution from the number of prompts or from time estimates. Explain the basis used.

## 8. Verification record

### Available evidence

Existing XML test reports record:

| Target | Tests | Failures | Errors | Skipped |
| --- | --- | --- | --- | --- |
| JVM | 43 | 0 | 0 | 0 |
| Android host | 42 | 0 | 0 | 0 |

Report timestamps are approximately 2026-10-05 03:06 UTC. A tested commit/revision was not supplied.

The previous README also records successful Android debug assembly, JavaScript compilation, and Wasm compilation. Original build logs were not supplied, so these remain reported results.

No commands were executed while preparing this documentation.

### Runtime checks to record

| Check | Expected result | Actual result/evidence |
| --- | --- | --- |
| Planner validation | Invalid inputs do not start a trip | Pending |
| Repeated GO | One active trip and destination | Pending |
| Map | Distinct endpoints, connecting line, orange person | Pending |
| Five active seconds | Approximately 50%, with matching movement and remaining distance | Pending |
| Rotation | Same ID, endpoints, elapsed time, inputs, and drafts | Pending |
| Leave and Resume | Paused time excluded; same trip continues | Pending |
| Background/return | No extra active time counted | Pending |
| Completion | 100%, zero remaining distance, one History entry | Pending |
| Initial review | Automatic navigation once; Save/Skip/Back opens History | Pending |
| Later review/edit | Same ID and collection size; Cancel preserves saved feedback | Pending |
| Details | Static original route; no simulation restart | Pending |
| Removal/stale IDs | Only selected record removed; recovery without recreation | Pending |
| Filters/cards | Immediate updates; expansion and edit/remove actions work | Pending |
| Reference image | Loading, failure, and retry; map unaffected | Pending |
| Layouts | Phone, landscape, open keyboard, and selected second platform usable | Pending |
| Shared navigation | Existing sections reachable; Home root protected | Pending |
| About | Existing photographs and visible names; acceptable presentation | Pending |

Record device/platform, application revision, date, result, and evidence for each completed check.

## 9. Assignment requirement checklist

| Requirement | Implementation/document evidence | Verification or completion status |
| --- | --- | --- |
| Kotlin/Compose Multiplatform | Shared commonMain code and configured targets | Source present |
| Material 3 | Shared Material 3 UI and theme | Source present; visual check pending |
| Navigation 3 | One saved back stack, Navigator, and Router | Existing navigation/serialization reports; runtime pending |
| Android plus Desktop or Web | Android, JVM, JS, and Wasm configured | Android build reported; required launches pending |
| Three related content screens | Planner, Current Trip, History | Source present; end-to-end check pending |
| Information screen | Existing About, supplied photographs and names | Source present; visual quality pending |
| Multiple text inputs/image link | Title, description, HTTPS image URL | Source present; device validation pending |
| Single entered item passed as parameter | CurrentTripScreenKey carries Trip | Existing parameter serialization tests; runtime pending |
| Provider-backed interactive list | LocalTripsStore and completed List<Trip> | Existing state/History reports; UI pending |
| Removal and details | ID-based removal and TripDetailsScreen | Existing deletion tests; UI pending |
| Sealed-class routes | Sealed ScreenKey and registrations | Source present; existing serialization reports |
| Shared layout/navigation | App provides state/navigation and hoists MainLayout | Source present; runtime pending |
| Organized components/state separation | Separate models, state, screens, and reusable UI | Source inspected |
| Rotation robustness | Savers, serialized snapshots, saveable inputs/drafts/routes | Snapshot tests reported; device rotation pending |
| Compilation and meaningful runtime output | Build results recorded; test reports present | Runtime output evidence pending |
| Internal documentation/root README | Comments present; README supplied | Student saving/final review pending |
| WBS with at least five tasks | Twelve prompt-level tasks in this document | Supplied; baseline inclusion convention to confirm |
| Original effort estimates | Student-confirmed 30 minutes per prompt | Recorded |
| Dependencies/Gantt | Sequential dependencies and relative effort chart | Supplied; actual calendar bars not supplied |
| Actual effort/variance explanations | Fields supplied | Student completion required |
| At least three AI-assisted key decisions | Four ADR entries supplied | Explicit approvals/decision confirmation required |
| Day 17 AI Technique Log | Five meaningful challenge entries | Student usage, impact, time, and reflection fields required |
| At least 50% AI contribution summary | Summary fields supplied | Percentage and basis not supplied |
| Saved prompts and outputs | Conversation is the primary record | Saved archive not supplied |
| Git/repository and machine evidence | Local Git working tree confirmed | Required screenshot/photograph not supplied |
| Preliminary design screenshot | Separate one-page summary supplied | Screenshot not supplied |
| Responsive design bonus | Width limits, scrolling, and previews present | Phone/desktop or web verification pending |

## 10. Submission checklist

### October 4, midnight — planning deliverables

- [ ] Confirm the original WBS accounting convention, including Prompt 0.
- [ ] Submit WBS tasks with original estimates.
- [ ] Include dependencies and Gantt chart.
- [ ] Preserve the initial estimates for later comparison.

### October 5, midnight — setup and design evidence

- [ ] Capture the GitHub repository page showing the Assignment 3 project.
- [ ] Ensure the project name does not identify it as Assignment 2.
- [ ] Take a photograph of the main computer with Android Studio, the project, and Git Branches visible.
- [ ] If using only a school computer, provide its photograph and the explanatory note required by the handout.
- [ ] Capture the preliminary AI design investigation/summary.
- [ ] Record the actual evidence locations; do not mark them complete without creating them.

### October 8, midnight — code, documentation, and actuals

- [ ] Identify the submitted revision.
- [ ] Demonstrate meaningful output on emulated Android and the selected Desktop/Web platform.
- [ ] Complete rotation, lifecycle, input, image, review, History, and layout checks.
- [ ] Save verification logs and required evidence before cleaning generated output.
- [ ] Complete the README and known-limitations section.
- [ ] Fill actual effort and variance notes from recorded work.
- [ ] Confirm at least three AI-assisted key decisions and their ADR status.
- [ ] Complete the separate Day 17 Technique Log fields.
- [ ] Save prompts and AI outputs; the technique log does not replace them.
- [ ] Supply the AI usage summary with the contribution estimate and its basis.
- [ ] Ensure internal documentation is meaningful.
- [ ] Be prepared to explain the implementation, decisions, and personal contributions.
- [ ] Clean the project before preparing the ZIP, as required by the handout.
- [ ] ZIP the complete project and required documentation/evidence.
- [ ] Inspect the ZIP contents.
- [ ] Submit through Lea and retain the actual submission confirmation.

Manual cleanup command, to run from the project root after preserving evidence:

```powershell
.\gradlew.bat clean
```

No cleanup or packaging was performed during documentation generation.

The handout states a late penalty of 10% per day for up to three days, with nothing accepted after three days without prior arrangement. It recommends an explained incomplete submission on time when necessary, followed by the completed version. No deadline extension or submission is assumed.

## 11. Evidence fields to complete

- GitHub repository screenshot: Not supplied.
- Main-machine photograph with Git Branches: Not supplied.
- Preliminary-design screenshot: Not supplied.
- Prompt/output archive: Not supplied.
- Final Android runtime evidence: Not supplied.
- Selected second-platform runtime evidence: Not supplied.
- Final tested/submitted revision: Not supplied.
- Build logs confirming reported compilation results: Not supplied.
- Recorded actual-time source: Not supplied.
- ADR acceptance confirmations: Not supplied.
- AI contribution basis and percentage: Not supplied.
- Lea submission confirmation: Not supplied.