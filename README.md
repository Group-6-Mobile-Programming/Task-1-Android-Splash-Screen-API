# UNEB e-Results — Splash UI package (XML)

**Spec:** `UNEB e-Results Splash Design.pdf` (5 screens). This package contains the
UI resources for that spec only — no extra screens, no app logic.

## Team split

| Role | Owns |
|---|---|
| **XML / UI** (this package) | Layouts, drawables, colors, strings, themes — matching the design |
| **Logic** (teammate) | `SplashActivity` behavior: running the checks, switching states, navigation — per the contract below |

## Design screen → file map

| # | Design screen | Delivered by |
|---|---|---|
| 1 | System splash (UNEB roundel on dark) | `themes.xml` → `Theme.UNEB.Splash` + `drawable/ic_splash_icon.xml`. No layout file — Android draws it before the activity starts. |
| 2 | Loading checklist | `layout/activity_splash.xml` (shell) + `layout/layout_splash_checklist.xml` |
| 3 | Date & time error card | shell + `layout/layout_splash_time_error.xml` |
| 4 | No internet card | shell + `layout/layout_splash_no_internet.xml` |
| 5 | Hand-off to Login / Dashboard | Outside this package (teammate's activities). The logo `ivLogo` already carries `android:transitionName="@string/transition_uneb_logo"` for the shared-element move. |

Screens 2–4 are one activity with three states: the shell's `statusContainer`
(FrameLayout) includes the three page layouts; exactly one is visible at a time.

## Files (17 XML)

```
res/
├── drawable/
│   ├── bg_card.xml                     error-card background (surface, stroke, 20dp corners)
│   ├── ic_clock.xml                    time-error icon (screen 3)
│   ├── ic_coat_of_arms_placeholder.xml header shield — placeholder, as in the design
│   ├── ic_splash_icon.xml              system-splash roundel, padded for the circular crop (screen 1)
│   ├── ic_step_done.xml                yellow check badge
│   ├── ic_step_pending.xml             grey idle ring
│   ├── ic_uneb_logo.xml                main logo, 116dp (screens 2–4)
│   ├── ic_wifi_off.xml                 no-internet icon (screen 4)
│   ├── progress_spinner.xml            active-step spinner (grey track + yellow arc)
│   └── spinner_arc.xml                 arc used by progress_spinner
├── layout/
│   ├── activity_splash.xml             shell: header, logo, title, tagline, statusContainer, footer, flag stripe
│   ├── layout_splash_checklist.xml     screen 2 page
│   ├── layout_splash_time_error.xml    screen 3 page
│   └── layout_splash_no_internet.xml   screen 4 page
└── values/
    ├── colors.xml                      design palette
    ├── strings.xml                     all design copy
    └── themes.xml                      Theme.UNEB, Theme.UNEB.Splash, UNEB.Step styles
```

## Handoff contract for the logic developer

**Setup (one-time):**
```kotlin
// build.gradle
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.core:core-splashscreen:1.0.1")
```
```xml
<!-- manifest: application theme @style/Theme.UNEB; splash activity: -->
<activity android:name=".SplashActivity" android:exported="true"
          android:theme="@style/Theme.UNEB.Splash"> … LAUNCHER intent-filter … </activity>
```
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()               // before super.onCreate — renders design screen 1
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
}
```

**State switching** — show exactly one root inside `statusContainer`:
- `R.id.layoutChecklist` — while checks run (screen 2)
- `R.id.cardTimeError` — when official-time verification fails (screen 3)
- `R.id.cardNoInternet` — when there is no connectivity (screen 4)

**Checklist rows** (screen 2) — each step has three views, `…Internet`, `…Time`,
`…Session`, `…Announcement`:

| Step state | `ivStep…` | `pbStep…` | `tvStep…` style |
|---|---|---|---|
| pending | visible, `ic_step_pending` | gone | `UNEB.Step` |
| active | gone | visible (spins automatically) | `UNEB.Step.Active` |
| done | visible, `ic_step_done` | gone | `UNEB.Step` |

**Buttons (as labelled in the design):**
- `R.id.btnOpenDateSettings` → open the system date & time settings
- `R.id.btnTryAgain` → re-run the checks

**Exit (screen 5):** when all steps pass → `LoginActivity`, or `DashboardActivity`
if already logged in. Use a scene transition on `ivLogo`
(`@string/transition_uneb_logo` — same name on the destination logo).

How the checks themselves are performed (time source, connectivity, session,
announcement) is entirely the logic side's decision — the UI only renders states.

## UI notes

- `tools:` attributes in the layouts are **preview-only** (stripped at build): the
  error pages preview visible standalone but start `gone` at runtime; the shell's
  includes keep them hidden in the composed preview. Runtime behavior is unchanged.
- The UNEB lettering was re-spaced to match the design PDF (letters separated,
  common baseline); the identical artwork is used in `ic_splash_icon.xml` and
  `ic_uneb_logo.xml` so screens 1 and 2–4 stay consistent.
- Coat of arms is the design's placeholder shield — replace only with an approved
  official asset.
- Validated: all 17 XML well-formed; every `@color/@drawable/@string/@style/@layout`
  reference resolves; shell + pages recompose to the approved single-file layout
  (all 29 view IDs preserved).
