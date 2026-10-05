# RND Transit — Current Design Summary

**Student: Atiom · Assignment 3 · Updated 5 October 2026**

This summary reflects the current implementation after the UI revisions. It is not backdated evidence of a preliminary screenshot.

I enter a nickname on the landing screen; Profile shows it for the current
process session. It is demo access, with no password service or permanent account.

## Three main content screens

**Home / GO → Current Trip → History**

Home is a map-first planner with centered GO, transport/routes and attraction intensity. Planned minutes range from 0 to 360 in five-minute steps. Three digits fit between the dividers. GO validates selections and generates one Trip; an unfinished trip offers Resume.

Current Trip receives that Trip as a Navigation 3 parameter and resolves live state by ID. The same cropped PNG remains behind a pan/zoom map. Stored endpoints use an orange start and green destination; the circular pointer faces its movement direction and the travelled line fills orange. A shared progress value drives position, percentage and remaining demo distance over ten active seconds.

History reads the provider's completed List<Trip>, newest first. All/Reviewed/Not reviewed filters, expandable feedback, details, review editing and removal use stable IDs.

## Supporting interaction

GO/transport/intensity controls slide down together for one second before navigation. Shared navigation stays outside destination animations; Settings is right-aligned, and About is reached from Profile.

Tap the gradient Current trip title for information and unfinished-trip cancellation. Leaving/backgrounding pauses; Resume retains ID/endpoints/elapsed time. Cancel adds no History item.

Completion records once and waits at 100%. Tapping 100% opens the review questions. Save or Skip opens History. Overall is required; other ratings/comment are optional. Editing Save updates the same ID; Cancel preserves old feedback. Details shows a static completed map.

I can attach an optional raw HTTPS image link on the Overall question. After Save,
expanding a reviewed History card or opening Details renders the photo. Loading,
failure, Retry and browser fallback are separate from the saved review and map.
Blank or skipped links show no photo. An unsaved draft has no History image.

## Architecture

TripPoint, Trip and TripReview are serializable records. A focused generator creates endpoints once. App supplies one TripsStore and Navigator under RNDTransitTheme, with MainLayout hoisted above Router. ScreenKey is sealed with registered serializers. Coordinators own state/effects; reusable components receive values and callbacks.

Saved state retains trips, elapsed time, pending review handling, planner selections and review drafts. It is not a permanent database.

## Day 19 dimensions

| Dimension | Current design |
| --- | --- |
| Cohesion | Separate generation, state, navigation and presentation responsibilities |
| Coupling | One provider and explicit callback contracts |
| Integrity | Valid minutes/ratings, stable IDs, one active trip and idempotent completion |
| Evolvability | Separate screens and reusable map/review/card components |
| Fitness for purpose | A bounded mock journey without GPS or real routing |

## Coverage and evidence

About remains the information destination. Parameter passing, shared collection, details/removal, shared layout and sealed routes are present in source. The removed first-screen text/image-link form leaves that literal assignment requirement unmet.

I confirmed the Android, Desktop and Web checks as complete on 5 October 2026.
This is my verification statement; no new checks were executed by AI for the
documentation update. Setup/design screenshots are in `As3_screenshots`.
Normal generated trips have no reference URL; review photos are a separate field.
The map/distance are synthetic, with no live navigation. See README, assignment
records and the AI log for evidence and limitations.
