# RND Transit — Preliminary Design Summary

**Purpose:** Demonstrate a coherent trip-planning, mock-travel, and completed-History workflow for Assignment 3.

**Record note:** This summary was prepared during Prompt 11 from the design discussion and supplied implementation. It does not claim that an earlier screenshot was captured. Student ADR approvals and runtime verification remain to be recorded.

## User flow

**Planner → Current Trip → Initial Review → History → Details / Edit Review**

The planner collects title, description, HTTPS image URL, planned minutes, transport/routes, and attraction intensity. GO validates and generates one Trip. Current Trip receives that record as a navigation parameter.

Only one unfinished trip exists. Leaving or backgrounding pauses it; Resume continues the same ID and elapsed time. Cancellation creates no completed History entry.

## Architecture and state

TripPoint, Trip, and TripReview are serializable records. A focused generator creates endpoints once. App provides one TripsStore and one Navigator beneath RNDTransitTheme.

MainLayout contains the shared navigation outside destination transitions. Sealed route keys describe destinations. Screens coordinate state; reusable components receive values and callbacks.

Saved state includes active/completed trips, elapsed time, pending review handling, planner inputs, and review drafts. Running jobs and clock marks are excluded.

## Mock journey and feedback

The bundled map shows stored normalized endpoints, a straight line, and an orange person. One progress value drives movement, percentage, track fill, and remaining demo distance over ten active seconds.

Completion records the trip once and opens review. Overall is required; other ratings and comment are optional. Skip preserves an unreviewed trip. Editing updates the same ID; Cancel preserves saved feedback.

History supports filters, expandable reviews, details, editing, and removal. Missing IDs recover safely.

## Day 19 design dimensions

| Dimension | Design intent |
| --- | --- |
| Cohesion | Generator, store, navigation, and visual components have focused responsibilities |
| Coupling | Screens use shared state and explicit callbacks |
| Integrity | Valid records, stable IDs, atomic completion, and guarded updates |
| Evolvability | Separate files and components keep changes manageable |
| Fitness for purpose | A clear transit demo without live-routing infrastructure |

## Assignment fit and limits

Planner, Current Trip, and History provide three related content screens; About supplies information. The design includes multiple inputs, parameter passing, provider access, removal/details, shared layout, and sealed routes.

Configured platforms include Android and Desktop/Web. Existing reports record 43 JVM and 42 Android host tests without failures; device rotation, lifecycle timing, image loading, application launches, and layouts remain unverified.

This is a straight-line mock route with synthetic distance and a separate reference image. Saved-state restoration provides no permanent database, GPS, live map service, or real street guidance.