# ☕ Brewkery

A coffee and bakery ordering app built with **Kotlin** and **Jetpack Compose** for the Clickretina Android Developer take-home assignment.

I took a set of design mockups and a remote JSON API and turned them into a working multi-screen app: splash, menu, item customizer, cart, order confirmation and a live "active order" state on the home screen. Nothing about the menu is hardcoded. Items, categories, prices, delivery fee, tax rate and customization options all come from the API.

---

## Screenshots

| Splash | Home | Item Customizer |
|:---:|:---:|:---:|
|![ splash ](https://github.com/ArghaGhosh1/BrewKery/blob/ae59c1c2c9d9e8722d6e9d9dbdd097e93570da88/app/src/main/res/drawable/splashscreen.jpeg) | _add screenshot_ | _add screenshot_ |

| Cart | Order Placed | Home (active order) |
|:---:|:---:|:---:|
| _add screenshot_ | _add screenshot_ | _add screenshot_ |

> Put the images in a `/screenshots` folder and link them, for example `![Home](screenshots/home.png)`.

---

## What I Built

### 1. Splash
- Branded splash that moves to Home after a short delay.
- Removes itself from the back stack, so pressing back on Home exits the app instead of returning to the splash.

### 2. Home
- Fixed top bar with the brand and a cart icon with a live item-count badge.
- Store info card with delivery time and flat fee, read from the API.
- **Active Order card**: once an order is placed, the store info card is replaced by "Active Order #BK-xxxxx" with an ETA and a Track button.
- Live search over item name and tagline.
- Category chips generated from the API, plus "All Items".
- Menu cards with image, badge, rating, review count and price.
- Floating cart bar with item count and subtotal.

### 3. Item Customizer
- Separate top bar (back, title, favorite toggle) that matches the reference design.
- Loads a single item from `api/items/{id}.json`.
- Badge tag on the hero image.
- Size, milk/spread and sugar/serving selectors driven by the item's own options.
- Price updates live as selections change, and the button shows `unit price × quantity`.
- Quantity stepper and **Add to Cart**, which jumps straight to the cart.

### 4. Cart
- Quantity steppers (going to 0 removes the line) and Clear Cart.
- Identical items with identical options merge into a single line.
- Subtotal, delivery fee, estimated tax (rate from the API) and total payable.
- Empty state, and Place Order is disabled while the cart is empty.

### 5. Order Placed
- Generated ticket number, estimated wait from the API and item count.
- Back to Menu returns to Home, where the Active Order card now shows.

Every network call has loading, error and retry states.

---

## Tech Stack

| Area | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose with sealed-class routes |
| State | `ViewModel` and Compose `mutableStateOf` |
| Networking | Retrofit 2 |
| JSON | kotlinx.serialization |
| Images | Coil |
| Cart | In-memory singleton (`CartRepository`) |

No local database is used, since this is a demo app. The cart and the active order reset when the app process is closed.

---

## API

Base URL: `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/`

| Endpoint | Purpose |
|---|---|
| `data.json` | Store config (`meta`), categories and the full menu |
| `api/items/{id}.json` | Full details and customization options for one item |

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
4. To build a debug APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. The file is at `app/build/outputs/apk/debug/app-debug.apk`.

---

## Challenges I Worked Through

- **Serialization failing at runtime.** Fixed a missing Gradle plugin (details in the bug section below).
- **Emulator networking.** The app reported `Unable to resolve host 'raw.githubusercontent.com'`. I traced it to the emulator's DNS rather than my code and sorted it out on the emulator side.
- **Navigation back stack.** Corrected the flow so the splash and the detail screen don't reappear when going back, and so Place Order lands on a confirmation screen instead of an empty cart.
- **Package structure.** Lined up Kotlin package directives and imports with my folder layout (`data`, `Presentation`, `viewModel`, `navigation`).
- **Matching the reference design.** Compared my build against the mockups screen by screen and caught what was missing: the home top bar, the Active Order card, the separate customizer top bar and the item badge tags.

---

## 🤖 AI Usage

### How I used it
I used **Claude** (Anthropic) as a pair-programming assistant. I decided the screen order, the navigation approach and the project structure, and I supplied the mockups and the API responses. Claude produced first drafts of the Compose code. I then ran each draft on the emulator, compared it with the design, and sent back what was wrong or missing until it matched.

### What I did myself
- Set up the project, folder structure and Gradle dependencies.
- Wrote my own navigation layer (the `Screen` sealed class and `NavHost`) and the splash screen, and had the AI adapt the other screens to my style.
- Integrated every generated file into my packages and fixed the imports.
- Tested each screen on the emulator and debugged build errors, runtime errors and network issues.
- Reviewed the UI against the mockups and decided what to change.
- Built the debug APK and wrote this README.

### Prompts used

| # | Prompt (summarized) | Result |
|---|---|---|
| 1 | "Design this in Kotlin Jetpack Compose" (home screen mockup) | First static version of the home screen. |
| 2 | "Make the UI accordingly because the full menu and store configuration is given" (API link and JSON) | Models, Retrofit repository, ViewModel and a UI driven by the JSON. |
| 3 | "First just code the home screen for now, don't go beyond" | Scoped the work to the home screen only. |
| 4 | "Make a screen where tapping an item shows its details" (mockup and `api/items/{id}.json`) | Detail screen, `DetailViewModel` and live price logic. |
| 5 | "I want this type of navigation" (my own `Screen` and `NavHost` code) | Navigation rewritten in my sealed-class style with an `id` argument. |
| 6 | "Now code this order cart and connect everything" (two cart mockups) | Cart screen, shared `CartRepository` and cart bar wiring. |
| 7 | "After clicking Add to Cart it should go to the cart section" | Navigation to the cart with the detail screen removed from the back stack. |
| 8 | "Make this order placed screen and do all the connections" | Order Placed screen and the `placeOrder()` flow. |
| 9 | "You did not do the top bar section; if an order is placed the store info card changes to the active order card" | Home top bar and the Active Order card. |
| 10 | "The demo has a separate top bar on the details screen and the tags are not visible" | Separate Item Customizer top bar and the badge tag. |
| 11 | "When returning from the home screen it is showing the splash screen" (my splash code) | Splash removed from the back stack. |

I also pasted Android Studio errors and emulator screenshots back to the AI to debug them.

### How I checked the AI's output
- Ran every screen on the emulator and compared it with the reference mockups.
- Checked the models against the real fields in `data.json`.
- Tested the full flow end to end: add to cart, change quantities, place an order and return Home.
- Sent every mismatch or error back and confirmed the fix actually worked before moving on.

### 🐞 Bug caught and fixed

**Symptom:** The home screen showed its error state with this message:

```
Unable to create converter for class com.example.brewkery.data.BrewkeryResponse
for method BrewkeryApi.getMenu
```

**Cause:** My data classes were annotated with `@Serializable`, but the **Kotlin serialization Gradle plugin** was not applied. The annotation is only a marker, and the plugin is what generates the serializer at compile time. Without it, Retrofit's kotlinx converter could not find a serializer for `BrewkeryResponse`. The project compiled without complaint, so the problem only appeared at runtime.

**Fix:**
1. Added the plugin to the version catalog (`libs.versions.toml`):
   ```toml
   kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
   ```
2. Added `alias(libs.plugins.kotlin.serialization) apply false` to the project-level `build.gradle.kts`.
3. Added `alias(libs.plugins.kotlin.serialization)` to the app-level `build.gradle.kts`.
4. Synced and ran **Clean Project → Rebuild Project**.

**Result:** The menu parsed and rendered. The next error, `Unable to resolve host`, was an emulator networking problem and not a code problem.

### Other issues I caught
- **Splash left in the back stack:** back from Home showed the splash again. Fixed with `popUpTo(splash) { inclusive = true }`.
- **Stale `DetailScreen` signature:** after moving to `navController`, the old `onBack` parameter was still required. I removed it and derived `onBack` from the controller.
- **Package vs folder mismatch:** package directives did not match my folders. I fixed the package lines and imports.

---

## Known Limitations (demo scope)

- Cart and active order are held in memory only.
- Placing an order does not call a backend or take payment.
- The favorite heart on the customizer is a local toggle and is not saved.
- The item endpoint has no currency field, so the customizer uses `$`.
