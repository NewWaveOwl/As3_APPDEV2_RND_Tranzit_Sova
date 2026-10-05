# RND Transit MTL — Assignment 3

**Student: Atiom**  
Kotlin / Compose Multiplatform · Material 3 · Navigation 3

RND Transit is a map-based trip demo. Its three main content screens are **Home / GO**, **Current Trip**, and **History**. A sequential review form and completed-trip details support that flow. **About** remains the information screen, reached from Profile.

## The three main screens

The app first shows a simple nickname landing screen. Enter any nonblank nickname
and press **Continue to GO**; Profile displays it. This is demo access, with no
password, account service, file or database. The nickname stays in process memory
through Android rotation and is forgotten when the process restarts or the browser
reloads. An unfinished landing-field entry may reset on rotation. The landing page
is an introduction; it does not replace the three related trip content screens.

| Screen | What the user does |
| --- | --- |
| Home / GO | Choose planned minutes, transport types/routes and attraction intensity; press GO or resume the existing trip |
| Current Trip | Watch the stored mock route progress, pan/zoom the map, open trip information or cancel, and tap 100% to review after completion |
| History | Filter completed trips, expand reviews, open details, review/edit and remove records |

The shared bar keeps GO/Home, Profile and History on the left and Settings on the right. About is available in Profile. Destinations share one hoisted layout rather than each adding its own navigation bar.

## GO and planned minutes

Home uses the bundled map background with the original horizontal **I have | minutes value | minutes | GO** control, transport panel and attraction panel. The text/title/image-URL form was removed at Atiom's request.

- Default planned time: 30 minutes.
- Allowed choices: **0–360 minutes in five-minute steps**.
- Up from 355 reaches 360; down from 360 returns to 355.
- Arrow taps and vertical dragging cannot exceed 360 or go below 0.
- The up arrow disappears at 360; the down arrow disappears at 0. Empty slots keep the number centred.
- The number area reserves room for three digits and fits the text to its available width.
- Transport and route validation remains; no selected transport produces a clear message.
- Attraction intensity stays within 0–100, with the light-green fill clipped inside its dark oval.
- Transport and attraction panels span the phone content width; teal fills the remaining bottom area.

GO creates one Trip with a generated title such as “Trip 1”, an automatic description, current selections and two fixed random endpoints. Normal GO trips have an empty reference-image URL.

Only one unfinished trip may exist. While it exists, use **Resume trip**; another GO action cannot replace it. Repeated taps are guarded.

When opening a trip, the foreground GO, transport and attraction controls slide down together for one second. Navigation waits for that exit to finish; the map background remains in place.

## Current Trip

The map uses the same proportional crop as GO. It supports drag/pinch zoom and zoom/reset controls. Overlay geometry follows the displayed image transform.

- Orange start marker and green destination marker.
- Supplied circular pointer asset, rotated so its sharp tip faces movement.
- A straight route whose travelled portion fills orange.
- Progress percentage and remaining demo distance from the same elapsed-time value.

The simulation lasts **10,000 milliseconds of active time**, independently of planned minutes—even a 360-minute selection still has a ten-second demo.

Leaving Current Trip or backgrounding pauses the simulation. Returning/resuming retains the same trip ID, endpoints and elapsed time. Android saved-state restoration retains these through rotation.

Tap the gradient **Current trip** title to open the information popup. It contains stored trip information and a prominent **Cancel trip** action while unfinished. Cancellation returns to GO and adds no History record. The visible Current Trip Back control was removed; GO remains in the shared bar.

At completion the trip enters History exactly once, progress remains at **100%**, and remaining demo distance is zero. **Tap the 100% progress bar to open the initial review.** Completion does not automatically open that form.

## Reviews

Questions slide through Overall experience plus an optional comment, Quality, How interesting, and Fun.

Overall is required when saving. Ratings are 1–5; optional categories remain null unless selected. Skip preserves the completed trip without creating a zero-star review.

Initial review Save, Skip, Close and Back lead to History. Later editing loads a separate draft from the latest saved review. Save replaces feedback on the same trip ID; Cancel/Close/Back preserve the old review and return to the originating History/details view.

Draft ratings, comment and question step are included in saved state. Missing/deleted IDs show a recovery action and cannot be recreated by saving.

Overall experience also has an optional **Image link** field. A nonempty link must
use HTTPS with a valid host. Save trims it and attaches it to that review; editing
loads the saved link, Cancel preserves it, and Skip discards the draft link. It is
included in review draft/snapshot restoration. Older reviews without the new field
restore with an empty link. Expand a reviewed History card or open details to see
the photo loaded from the saved URL. A loading indicator appears while fetching;
failure shows an explanation and **Retry image** without affecting the review.
The full photo fits inside a rounded frame. You can also copy the URL or press
**Open review image** to open it in your browser. Blank links show no photo area.
The host must return an image; browser security or host restrictions may prevent
some images loading on Web even when the URL opens in a separate browser tab.

### When will I see my review photo?

1. Finish the trip and tap the **100%** progress bar, or choose **Review / Edit review** for a completed trip.
2. Select at least one **Overall experience** star. Paste a raw HTTPS image URL into **Image link (optional)**; do not paste Markdown brackets or a search-results page.
3. Continue through the optional questions and press **Save review**. Comment and other ratings may stay empty. The link itself is optional.
4. In History, tap the reviewed card to expand it, or choose **Details**. A nonblank saved link loads a photo in the review section.

The review form keeps a draft; it does not show an image preview. An unsaved link
is not displayed in History. Skip discards a new draft; Cancel while editing keeps
the previously saved review and image. Clearing the link and saving removes the
photo from that review. A valid HTTPS format does not guarantee that the host will
return a supported image. Links with query parameters work without a `.jpg` or
`.png` suffix when the server returns an image. Retry or use the browser fallback
if an image is unavailable. The bundled trip map is always separate from the photo.

## History and details

History reads the shared completed List<Trip>, newest completion first, using stable IDs.

- All, Reviewed and Not reviewed filters.
- Overall stars and expandable saved feedback.
- “Not reviewed” when no feedback was saved.
- Review/Edit review, Details and Remove actions; the card action tray exposes edit/remove.
- Immediate observable collection updates after saving or removal.

Details resolves the current stored trip by ID and displays its original endpoints, static completed route, selections and saved ratings/comment. It does not generate points or run the simulation.

Profile displays the session nickname and opens About. Its Email/Change password
labels are placeholders, not account features. Settings keeps the Trip Settings
shortcut to GO; Theme and Language cards remain placeholders.

## Images and resources

map_sample is a bundled PNG, independent of network access. Start, destination and pointer assets are shared XML resources converted from the supplied artwork.

The existing optional reference-image component supports loading, failure and retry for stored trips that contain a URL. The current GO screen does not ask for one, and generated trips use an empty URL. A failed optional image cannot break the map simulation. No remote-image loading is needed for the normal GO flow.

## Architecture and saved state

App applies RNDTransitTheme, provides one TripsStore and Navigator/navigation coordinator, and hoists MainLayout above Router/NavDisplay. The sealed ScreenKey hierarchy has explicit concrete NavKey serializer registrations. CurrentTripScreenKey carries the generated Trip; screens resolve mutable state through its ID.

The serializable store snapshot/Saver includes active/completed records, elapsed milliseconds, pending review handling and review drafts. Planner selections and route state use saved state as well. Coroutine jobs, callbacks and running clock marks are excluded.

This is **saved-state restoration, not permanent database storage**. A fresh launch without restored state begins an empty session; Desktop restart/browser reload are not durable History storage.

## Limits and assignment coverage

The map is a mock image, the route is straight, coordinates are normalized image positions, and distance is synthetic demo distance. There is no GPS, real street routing, turn guidance or live map service.

The three main screens, provider collection, parameter passing, details/removal, shared layout and sealed routes remain implemented in source. **The assignment's first-screen multiple-text-input and image-link requirements are currently unmet after removal of that form.** Reviews provide text input elsewhere, but that does not satisfy the literal first-screen requirement.

The new nickname introduction and later review-image input do **not** close this
gap: information must be entered together on the first content screen, passed to
the second, and represented in the third screen's provider-backed collection.

I confirmed on 5 October 2026 that the final checks are complete and the app works
on Android, Desktop and Web. Verification is recorded as my confirmation; the AI
did not execute builds or tests for this documentation update.

## Platforms and manual commands

I tested Android, Desktop JVM and Web. Desktop is the required second platform;
Web is additional coverage. Both JavaScript and Wasm Web targets are configured,
but my confirmation does not identify which Web target I ran. iOS is configured
and requires macOS/Xcode; no specific iOS run is recorded.

Run from the project root:

~~~powershell
Set-Location -LiteralPath 'K:\CLASSES 26 FALL\APP_DEV\As3\MainRepo\As3_APPDEV2_RND_Tranzit_Sova'
~~~

Android:

~~~powershell
.\gradlew.bat :androidApp:assembleDebug
.\gradlew.bat :shared:testAndroidHostTest
~~~

Launch androidApp in Android Studio for emulator/device checks.

Desktop:

~~~powershell
.\gradlew.bat :shared:compileKotlinJvm
.\gradlew.bat :shared:jvmTest
.\gradlew.bat :desktopApp:run
~~~

Optional Web, choosing one configured target:

~~~powershell
.\gradlew.bat :shared:wasmJsTest
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
~~~

~~~powershell
.\gradlew.bat :shared:jsTest
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
~~~

For iOS, open iosApp with Xcode on macOS. No iOS run is claimed.

## Verification status

**Complete — student confirmed, 5 October 2026.** I confirmed that the final
Android/Desktop/Web build-and-run checks and test checklist are done and working.
The documented implementation is revision `9612d72` (image preview and its Coil
import fix); the documentation commit following it changes no production code.

The retained XML reports separately record **43 JVM tests and 42 Android host tests,
zero failures/errors/skips**, around 2026-10-05 03:06 UTC. Those reports are older
than the image changes. They are preserved as historical counts, not presented as
a newly generated final report. Current completion is based on my later confirmation
in the [AI log](AI_Log_As3.md#final-student-confirmation). No fresh test counts,
device names or build timings are invented.

## Assignment records

- [Assignment documentation](docs/AssignmentDocumentation.md): WBS, original estimates, dependencies/Gantt, ADRs, coverage and submission checks.
- [Current design summary](docs/PreliminaryDesignSummary.md): current three-screen architecture; no earlier screenshot is implied.
- [AI technique log and complete chat evidence](AI_Log_As3.md): both conversations, prompts/outputs and deep links.
- [Time flow](TimeFlow_As3_Sova.txt): supplied work notes plus measured intervals between chat prompts.
- [Assignment screenshots](As3_screenshots/): repository, Android Studio/Git Branches, emulator and preliminary AI investigation captures.

Original estimate: 30 minutes per numbered prompt. Chat intervals include response/waiting time and possible breaks; they are recorded as elapsed intervals, not automatically treated as active work.
