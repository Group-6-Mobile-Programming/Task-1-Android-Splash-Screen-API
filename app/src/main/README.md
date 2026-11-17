# UNEB e-Results — Splash `res/` folder

Complete Android resource folder for the UNEB e-Results splash flow, matching all
5 screens of `UNEB e-Results Splash Design.pdf` (system splash → checklist →
time-error card → no-internet card → hand-off to Login/Dashboard).

Drop the `res/` folder into `app/src/main/` (merge with your existing `res/`).

## Contents

```
res/
├── drawable/
│   ├── bg_card.xml                     ← uploaded (error-card background)
│   ├── ic_clock.xml                    ← uploaded (time-error icon)
│   ├── ic_coat_of_arms_placeholder.xml ← uploaded (replace with official asset when permitted)
│   ├── ic_splash_icon.xml              ← uploaded, letter spacing fixed (system splash, screen 1)
│   ├── ic_step_done.xml                ← uploaded (yellow check badge)
│   ├── ic_step_pending.xml             ★ new — grey idle ring
│   ├── ic_uneb_logo.xml                ★ new — main 116dp logo (screens 2–4, shared element)
│   ├── ic_wifi_off.xml                 ★ new — yellow wifi-off icon (screen 4)
│   ├── progress_spinner.xml            ★ new — grey track + spinning yellow arc (active step)
│   ├── spinner_arc.xml                 ★ new — arc used by progress_spinner
│   └── ic_launcher_foreground.xml      ★ new — launcher icon artwork (bonus)
├── layout/
│   ├── activity_splash.xml             ← shell: header, logo, title, statusContainer, footer, flag stripe
│   ├── layout_splash_checklist.xml     ★ new — screen 2 (loading checklist), included
│   ├── layout_splash_time_error.xml    ★ new — screen 3 (date & time error card), included
│   └── layout_splash_no_internet.xml   ★ new — screen 4 (no-internet card), included
├── values/
│   ├── colors.xml                      ← uploaded, unchanged
│   ├── strings.xml                     ← uploaded, unchanged
│   └── themes.xml                      ← uploaded, unchanged
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml                 ★ new — adaptive launcher icon (API 26+)
│   └── ic_launcher_round.xml           ★ new
└── mipmap-{mdpi…xxxhdpi}/
    ├── ic_launcher.png                 ★ new — legacy launcher fallbacks (API < 26)
    └── ic_launcher_round.png           ★ new
```

★ = new files: drawables the uploaded layout/themes referenced but were missing,
the per-page layout split of the splash states, and launcher icons so
`@mipmap/ic_launcher` resolves with UNEB branding (skip the mipmap folders if you
already have launcher icons you want to keep).

### Layout structure (page per page)
Screens 2–4 of the design live in their own layout files, pulled into the splash
shell through `<include>` inside `statusContainer`:

```xml
<FrameLayout android:id="@+id/statusContainer" ...>
    <include layout="@layout/layout_splash_checklist"/>    <!-- screen 2 -->
    <include layout="@layout/layout_splash_time_error"/>   <!-- screen 3 -->
    <include layout="@layout/layout_splash_no_internet"/>  <!-- screen 4 -->
</FrameLayout>
```

- Each included file keeps its original root id (`layoutChecklist`, `cardTimeError`,
  `cardNoInternet`), margins and `visibility`, so **all existing findViewById calls
  and visibility toggles work unchanged**.
- The `<include>` tags deliberately carry no `android:id` / `layout_width` /
  `layout_height` — that way the included root's own parameters apply as-is.
- Verified: shell + includes recompose to exactly the original single-file layout
  (same tags, attributes, children and all 29 ids).
- Screen 1 (system splash) has no layout — it is drawn by the OS from
  `Theme.UNEB.Splash` + `ic_splash_icon.xml`. Screen 5 is your
  `LoginActivity`/`DashboardActivity`, outside this splash package.

### One fix applied to uploaded artwork
In the original `ic_splash_icon.xml` the UNEB letters overlapped slightly
(7-unit stroke on a 5-unit gap) and the “U” sat above the shared baseline.
The lettering was re-spaced to match the design PDF, and the identical fix was
applied to `ic_splash_icon.xml`, `ic_uneb_logo.xml` and `ic_launcher_foreground.xml`
so the system splash, in-app logo and launcher icon stay pixel-consistent.
Everything else in the uploaded files is untouched.

## Required Gradle dependencies

```kotlin
dependencies {
    implementation("com.google.android.material:material:1.12.0")   // Theme.Material3, MaterialButton
    implementation("androidx.core:core-splashscreen:1.0.1")         // Theme.SplashScreen backport
}
```

## Manifest

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:theme="@style/Theme.UNEB" ...>

    <activity
        android:name=".SplashActivity"
        android:exported="true"
        android:theme="@style/Theme.UNEB.Splash">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

## Activity wiring (minimum)

```kotlin
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()   // BEFORE super.onCreate — shows screen 1
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // run checks, then navigate to LoginActivity / DashboardActivity
    }
}
```

Per-step state (each row has an `ImageView` + `ProgressBar` pair):

| State   | ImageView (`ivStep…`)              | ProgressBar (`pbStep…`) | TextView style        |
|---------|------------------------------------|-------------------------|------------------------|
| Pending | `visible`, `src = ic_step_pending` | `gone`                  | `UNEB.Step`            |
| Active  | `gone`                             | `visible` (auto-spins)  | `UNEB.Step.Active`     |
| Done    | `visible`, `src = ic_step_done`    | `gone`                  | `UNEB.Step`            |

Status area — show exactly one of `layoutChecklist`, `cardTimeError`,
`cardNoInternet` (the roots of the three included page layouts) inside
`statusContainer`.

Shared-element hand-off (screen 5): `ivLogo` already carries
`android:transitionName="@string/transition_uneb_logo"` — set the same
transition name on the logo in `LoginActivity`/`DashboardActivity` and launch with
`ActivityOptions.makeSceneTransitionAnimation(...)`.

## Notes

- `progress_spinner.xml` is an `animated-rotate` wrapping a ring + arc; ProgressBar
  starts/stops it automatically when the view becomes visible/gone.
- The coat of arms is a placeholder shield (as in the design); swap in the official
  asset only if you have permission to use it.
- All 20 XML files validated: well-formed, and every `@color/@drawable/@string/@style/@layout`
  reference resolves within this folder (library themes come from the deps above).
