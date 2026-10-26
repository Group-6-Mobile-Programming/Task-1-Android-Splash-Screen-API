# UI Handoff Notes: Splash Screen

XML is in `app/src/main/res/`. `SplashActivity` only calls
`SplashScreen.installSplashScreen(this)` and `setContentView(...)`. All logic is for the logic team.

## Layout: `layout/activity_splash.xml`

The status area (`statusContainer`) shows ONE block at a time. Set the others to `GONE`.

| Block | ID | Notes |
|---|---|---|
| Loading checklist | `layoutChecklist` | Default block (visible) |
| Date/time error | `cardTimeError` | Hidden by default. Button: `btnOpenDateSettings` |
| No internet error | `cardNoInternet` | Hidden by default. Button: `btnTryAgain` |

## Checklist rows

Each of the four steps (`Internet`, `Time`, `Session`, `Announcement`) has:
- `ivStep<Name>`: the status icon. Set `R.drawable.ic_step_done` or `R.drawable.ic_step_pending`.
- `pbStep<Name>`: the spinner. Hidden by default.
- `tvStep<Name>`: the text.

To mark a step:
- **In progress:** hide `ivStep<Name>`, show `pbStep<Name>`, and call `tvStep<Name>.setTextAppearance(...)` or set it white and bold (style `UNEB.Step.Active`).
- **Done:** hide `pbStep<Name>`, show `ivStep<Name>` with `ic_step_done`, and use the normal grey text.
- **Pending:** the default state.

Note: the step strings in `strings.xml` are the "done" wording from the design (for example "Internet connection checked"), and only the session step is worded as in progress. If you want in-progress wording for the others, add new strings.

## Shared-element transition

- The logo is `ivLogo`, with transition name `@string/transition_uneb_logo` (`uneb_logo`).
- The logo view in `LoginActivity` and `DashboardActivity` must use the SAME transition name.
- The activity theme already enables activity transitions (`Theme.UNEB`).
- Use `ActivityOptions.makeSceneTransitionAnimation(this, ivLogo, "uneb_logo")`.

## Other notes

- **Logo and coat of arms are placeholders** (`ic_uneb_logo`, `ic_coat_of_arms_placeholder`). Replace them with the official files when the designer provides them. Keep the same file names and nothing else changes.
- **Permissions:** `INTERNET` and `ACCESS_NETWORK_STATE` are not in the manifest. The logic team must add them.
- **Theme:** the app uses `Theme.UNEB.Splash` (system splash) which switches to `Theme.UNEB` after. Don't change the launcher theme without telling the UI developer.
- If you need a new view, ask the UI developer to add it to the XML instead of editing the layout yourself.
