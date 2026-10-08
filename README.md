# ☕ Brewkery

A coffee and bakery ordering app built with **Kotlin** and **Jetpack Compose** as part of the Clickretina Android Developer take-home assignment.

The whole menu, store settings and per-item customization options are loaded from a remote JSON API, so nothing about the menu is hardcoded in the app.

---

## Screenshots

| Splash | Home | Item Customizer |
|:---:|:---:|:---:|
| _add screenshot_ | _add screenshot_ | _add screenshot_ |

| Cart | Order Placed | Home (active order) |
|:---:|:---:|:---:|
| _add screenshot_ | _add screenshot_ | _add screenshot_ |

> Replace the placeholders with images in a `/screenshots` folder, for example `![Home](screenshots/home.png)`.

---

## Features

- **Splash screen** that leaves the back stack once finished, so back from Home exits the app.
- **Home**
  - Top bar with brand and a cart icon with a live item-count badge.
  - Store info card (delivery time and flat fee from the API).
  - Live search over item name and tagline.
  - Category chips built from the API, with an "All Items" option.
  - Menu cards with image, badge, rating, review count and price.
  - Floating cart bar showing item count and subtotal.
  - After an order is placed, the store info card is replaced by an **Active Order** card with a Track button.
- **Item Customizer (details)**
  - Loads one item from `api/items/{id}.json`.
  - Size, milk/spread and sugar/serving selectors that come from the API.
  - Price updates live as options change.
  - Quantity stepper and **Add to Cart • total** button.
  - Badge tag on the image and a favorite toggle in the top bar.
- **Cart**
  - Quantity steppers (reducing to 0 removes the line) and Clear Cart.
  - Identical items with identical options are merged into one line.
  - Subtotal, delivery fee, estimated tax (rate from the API) and total payable.
  - Empty state, and Place Order is disabled while the cart is empty.
- **Order Placed**
  - Generated ticket number, estimated wait from the API and item count.
  - Back to Menu returns to Home.
- Loading, error and retry states for every network call.

---

## Tech Stack

| Area | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose (sealed `Screen` routes) |
| State | `ViewModel` with Compose `mutableStateOf` |
| Networking | Retrofit 2 |
| JSON | kotlinx.serialization |
| Images | Coil |
| Cart | In-memory singleton (`CartRepository`) |

No local database is used because this is a demo app. The cart and the active order reset when the app process is closed.

---

## API

Base URL: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/`

| Endpoint | Purpose |
|---|---|
| `data.json` | Store config (`meta`), categories and the full menu |
| `api/items/{id}.json` | Full details and customization options for one item |

The `meta` block drives the delivery fee, tax rate, currency symbol and estimated delivery time.

---

## Project Structure

```
com.example.brewkery
├── data
│   ├── Models.kt              // Serializable models for the API
│   ├── BrewkeryRepository.kt  // Retrofit service and loader
│   ├── CartLine.kt
│   ├── CartRepository.kt      // In-memory cart and order logic
│   └── Order.kt
├── navigation
│   ├── screen.kt              // Sealed class of routes
│   └── navHost.kt             // NavHost setup
├── Presentation
│   ├── splashScreen
│   ├── homeScreen.kt
│   ├── detailScreen.kt
│   ├── CartScreen.kt
│   └── OrderPlacedScreen.kt
├── viewModel
│   ├── HomeViewModel.kt
│   └── DetailViewModel.kt
└── MainActivity.kt
```

**Flow:** Splash → Home → Item Customizer → Cart → Order Placed → Home

---

## Getting Started

1. Clone the repository.
   ```bash
   git clone https://github.com/<your-username>/Brewkery.git
   ```
2. Open it in **Android Studio** and let Gradle sync.
3. Run on an emulator or device. **An internet connection is required**, because the menu is fetched from GitHub.
4. To build a debug APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. The file is created at `app/build/outputs/apk/debug/app-debug.apk`.

---

## Known Limitations (demo scope)

- Cart and active order are held in memory only.
- Placing an order does not call a backend or take payment.
- The favorite heart on the details screen is a local toggle and is not saved.
- The item endpoint has no currency field, so the details screen uses `$`.

---

## 🤖 AI Usage

### Tools used
I used **Claude** (Anthropic) as a pair-programming assistant. I gave it screenshots of the target design and the API responses, built the app screen by screen, and ran everything on an Android emulator.

### Workflow
1. Share a screenshot or a spec for one screen.
2. Review the generated Compose code and paste it into my own package structure.
3. Run it on the emulator, then feed back errors and visual differences from the reference design.

I wrote the navigation setup (`Screen` sealed class and `NavHost`) and the splash screen myself in my own style, then had the AI adapt the rest of the code to it.

### Prompts used

| # | Prompt (summarized) | Result |
|---|---|---|
| 1 | "Design this in Kotlin Jetpack Compose" (home screen screenshot) | First static version of the home screen. |
| 2 | "Make the UI accordingly because the full menu and store configuration is given" (API link and JSON) | Models, Retrofit repository, ViewModel and a UI driven by the JSON. |
| 3 | "First just code the home screen for now, don't go beyond" | Trimmed the work down to the home screen only. |
| 4 | "Make a screen where tapping an item shows its details" (design and `api/items/{id}.json`) | Detail screen, `DetailViewModel` and live price logic. |
| 5 | "I want this type of navigation" (my `Screen` and `NavHost` code) | Navigation rewritten to my sealed-class style with an `id` argument. |
| 6 | "Now code this order cart and connect everything" (two cart screenshots) | Cart screen, shared `CartRepository` and home cart bar wiring. |
| 7 | "After clicking Add to Cart it should go to the cart section" | Navigation to the cart with the detail screen removed from the back stack. |
| 8 | "Make this order placed screen and do all the connections" | Order Placed screen and the `placeOrder()` flow. |
| 9 | "You did not do the top bar section; if an order is placed the store info card changes to the active order card" | Home top bar and the Active Order card. |
| 10 | "In the demo the details screen has a separate top bar and the tags are not visible" | Separate Item Customizer top bar and the badge tag. |
| 11 | "When returning from the home screen it is showing the splash screen" (my splash code) | Splash removed from the back stack. |

I also pasted Android Studio errors back to the AI for debugging (see below).

### How I verified the AI's output
- Ran every screen on the emulator and compared it with the reference designs.
- Fixed package names and imports to match my folder structure.
- Cross-checked the JSON models against the real `data.json` fields.
- Fed runtime errors and screenshots back and confirmed each fix actually worked.

### 🐞 Bug caught and fixed

**Symptom:** The home screen showed an error state with this message:

```
Unable to create converter for class com.example.brewkery.data.BrewkeryResponse
for method BrewkeryApi.getMenu
```

**Cause:** The data classes were annotated with `@Serializable`, but the **Kotlin serialization Gradle plugin** had not been applied. The annotation is only a marker, and the plugin is what generates the serializer at compile time. Without it, Retrofit's kotlinx converter could not find a serializer for `BrewkeryResponse`. The Gradle setup the AI gave me did not list the plugin clearly, and the compiler did not complain, so the problem only appeared at runtime.

**Fix:**
1. Added the plugin to the version catalog (`libs.versions.toml`):
   ```toml
   kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
   ```
2. Added `alias(libs.plugins.kotlin.serialization) apply false` to the project-level `build.gradle.kts`.
3. Added `alias(libs.plugins.kotlin.serialization)` to the app-level `build.gradle.kts`.
4. Synced and ran **Clean Project → Rebuild Project**.

**Result:** The menu parsed correctly. The next error, `Unable to resolve host 'raw.githubusercontent.com'`, was a DNS problem in the emulator rather than in the code, and a cold boot of the emulator fixed it.

### Other issues found during development
- **Splash in the back stack:** pressing back on Home brought the splash back. Fixed with `popUpTo(splash) { inclusive = true }`.
- **Mismatched `DetailScreen` signature:** after switching to `navController`, the old `onBack` parameter was still required. Removed it and derived `onBack` from the controller.
- **Package vs folder mismatch:** Kotlin package directives did not match my folders. Fixed the package lines and imports.
