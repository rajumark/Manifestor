# APKLens V2 — AI Product Intelligence Layer
**Goal:** Drop competitor APK → Understand WHAT it does, HOW it does it, WHY it was built this way  
**Audience:** AI coding agent. Assumes V1 is already built. V2 is additive on top.  
**Core philosophy:** Don't show developers raw data. Show product people product answers.

---

## THE CORE INSIGHT (read this first)

V1 = what's inside the APK (facts)  
V2 = what the app IS and what decisions were made (intelligence)

A PM doesn't want to know "38 API endpoints found."  
They want to know: **"This app has a real-time feed, a DM system, a creator monetization layer, and just added a subscription paywall — here's how they built each one."**

V2 takes V1's structured `AnalysisResult` JSON + decompiled source and sends it through Claude API to produce:
1. **Product Story** — what this app does for users, in plain language
2. **Feature Map** — every feature detected, how it works technically
3. **User Flow Reconstruction** — the actual screens and journeys users go through
4. **Tech Decisions Audit** — why they chose each technology
5. **Competitive Intelligence Brief** — actionable PM/founder takeaways

---

## 1. WHO USES V2 (Personas — UX drives from here)

| Persona | Goal | What they need to see |
|---|---|---|
| **Founder** | Understand competitor product depth | Feature map, monetization, growth loops |
| **PM** | Benchmark competitor UX decisions | User flows, onboarding, paywall placement |
| **Investor** | Assess technical maturity of a startup | Stack quality, scalability signals, team size inference |
| **Designer** | Understand competitor's design system | Screen count, navigation patterns, asset quality |
| **Growth Hacker** | Reverse competitor's growth mechanics | Analytics SDKs, A/B test signals, referral patterns |

V2 UI must serve all five. The AI output adapts based on selected persona lens.

---

## 2. V2 SCREENS OVERVIEW

```
Welcome (same as V1)
     │
     ▼
Processing (V1 pipeline runs first)
     │
     ▼
┌─────────────────────────────────────────────────────┐
│  AI ANALYSIS SHELL                                  │
│                                                     │
│  Left Nav          Center                 Right     │
│  ─────────         ──────────────         ──────    │
│  🧠 Story          Main content           AI Chat   │
│  🗺️  Features                             sidebar   │
│  🚶 User Flows                            (always   │
│  💡 Tech Audit                             visible) │
│  📈 Growth                                          │
│  ⚔️  Compete Brief                                  │
│  ─────────                                          │
│  🔬 Raw Data  ← V1 screens live here               │
└─────────────────────────────────────────────────────┘
```

---

## 3. SCREENS — DETAILED

---

### SCREEN A: AI Processing (after V1 pipeline completes)

```
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│   🧠 AI is reading the app...                               │
│                                                              │
│   ✅  Technical extraction complete  (V1 done)              │
│                                                              │
│   Now analyzing with AI:                                     │
│   ⏳  Understanding app purpose and category                │
│   ○   Mapping user-facing features                          │
│   ○   Reconstructing user journeys                          │
│   ○   Auditing technical decisions                          │
│   ○   Building competitive brief                            │
│                                                              │
│   This takes 30–60 seconds                                  │
│                                                              │
│   ┌──────────────────────────────────────────────────────┐  │
│   │  💬 "Found a sophisticated paywall implementation     │  │
│   │      using RevenueCat. Detected 3 subscription        │  │
│   │      tiers from string resources..."                  │  │
│   └──────────────────────────────────────────────────────┘  │
│   Live insight stream ↑ (AI findings appear as discovered)  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

**Key UX detail:** Show a live streaming insight box where findings appear in real-time as Claude processes. Not a spinner — actual content trickling in. This makes the wait feel like value, not delay.

---

### SCREEN B: Product Story (default landing after AI analysis)

**This is the hero screen of V2. The first thing you see.**

```
┌──────────────────────────────────────────────────────────────────────┐
│                                                                      │
│  🧠 Product Story                              [Lens: Founder ▾]     │
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                                                              │   │
│  │  Duolingo is a gamified language learning app targeting      │   │
│  │  casual learners aged 18–35. Its core loop is a daily        │   │
│  │  5-minute lesson → streak reward → social comparison.        │   │
│  │                                                              │   │
│  │  The app monetizes through a freemium model: free users      │   │
│  │  see ads between lessons and have limited "hearts" (lives).  │   │
│  │  Super Duolingo ($6.99/mo via RevenueCat) removes ads,       │   │
│  │  unlocks unlimited hearts and offline mode.                  │   │
│  │                                                              │   │
│  │  Technical maturity is high: 847 classes, modular            │   │
│  │  architecture, heavy A/B testing (Mixpanel + Amplitude +     │   │
│  │  LaunchDarkly flags detected), suggesting a 50+ eng team.   │   │
│  │                                                              │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                      │
│  ── Quick Stats ───────────────────────────────────────────────     │
│  📦 App Category      Language Learning / Education                  │
│  💰 Business Model    Freemium → Subscription                       │
│  🎯 Core Loop         Learn → Streak → Social                       │
│  👥 Team Size Est.    50–100 engineers (complexity signal)          │
│  📱 UX Maturity       High — custom animations, design system       │
│  🧪 A/B Culture       Heavy — 3 analytics SDKs detected            │
│  🌍 Localization      47 languages detected in strings             │
│                                                                      │
│  ── Key Findings ─────────────────────────────────────────────     │
│  🔴 Risk: Highly dependent on Firebase (7 Firebase SDKs)           │
│  🟡 Note: No end-to-end encryption on user data sync               │
│  🟢 Strong: Offline-first architecture detected                     │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

**Lens Selector (top right):**  
Switching lens re-runs the AI summary prompt with a persona-specific instruction.  
Options: `Founder` / `PM` / `Investor` / `Designer` / `Growth`  
Each produces a different Product Story paragraph focused on what that persona cares about.

---

### SCREEN C: Feature Map

**Goal:** Every user-facing feature the app has, how it's built, confidence level.

```
┌──────────────────────────────────────────────────────────────────────┐
│  Feature Map                          [Filter: All ▾]  [Search... 🔍]│
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  23 features detected  |  High confidence: 18  |  Inferred: 5       │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ✅ LESSONS / CORE LEARNING              Confidence: HIGH   │    │
│  │  ─────────────────────────────────────────────────────────  │    │
│  │  What it does: Delivers bite-sized exercises (MCQ, fill-   │    │
│  │  the-blank, speaking, listening) in a lesson wrapper.       │    │
│  │                                                             │    │
│  │  How it's built:                                            │    │
│  │  • LessonActivity.java → 12 exercise type fragments        │    │
│  │  • ExerciseViewModel handles answer validation              │    │
│  │  • Content served from api.duolingo.com/lessons/{id}       │    │
│  │  • Local caching via Room DB (offline support)             │    │
│  │                                                             │    │
│  │  Evidence: 47 classes, 8 API endpoints, Room schema        │    │
│  │  [View Source →]  [View API Endpoints →]                   │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ✅ STREAK SYSTEM                        Confidence: HIGH   │    │
│  │  ─────────────────────────────────────────────────────────  │    │
│  │  What it does: Tracks daily practice, penalizes misses,    │    │
│  │  rewards consistency with badges and XP.                    │    │
│  │                                                             │    │
│  │  How it's built:                                            │    │
│  │  • StreakManager.java — local + server sync                │    │
│  │  • Streak freeze (paid) via StreakFreezeDialog.java        │    │
│  │  • Push notification at 20:00 local time (FCM)            │    │
│  │  [View Source →]                                           │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  🟡 SOCIAL / LEADERBOARDS               Confidence: MED    │    │
│  │  ...                                                        │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  ⚪ AI TUTOR (INFERRED)                  Confidence: LOW   │    │
│  │  Pattern suggests upcoming feature — GPT endpoint           │    │
│  │  detected but not yet exposed in UI strings.                │    │
│  └─────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
```

**Confidence levels:**
- `HIGH` — direct source code evidence + API endpoints found
- `MED` — source found but limited API/UI evidence
- `LOW / INFERRED` — pattern suggests feature exists but not confirmed (upcoming feature signal!)

The "inferred" category is gold for competitive intelligence — you can spot features being built before they ship.

---

### SCREEN D: User Flows

**Goal:** Reconstruct the actual screens and journeys a user goes through.

**Layout:** Visual flow diagram + step list

```
┌──────────────────────────────────────────────────────────────────────┐
│  User Flows                                                          │
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  [Onboarding] [Core Loop] [Monetization] [Settings] [Re-engage]     │
│  ──────────── (active tab)                                          │
│                                                                      │
│  Onboarding Flow  (9 steps detected)                                │
│                                                                      │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐      │
│  │ Splash   │───▶│ Language │───▶│ Goal     │───▶│ Level    │      │
│  │ Screen   │    │ Select   │    │ Select   │    │ Test     │      │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘      │
│                                                         │            │
│                  ┌──────────┐    ┌──────────┐          │            │
│                  │ Account  │◀───│ First    │◀─────────┘            │
│                  │ Create   │    │ Lesson   │                        │
│                  └──────────┘    └──────────┘                        │
│                       │                                              │
│                  ┌────▼─────┐    ┌──────────┐    ┌──────────┐      │
│                  │ Home     │───▶│ Paywall  │    │ Notif.   │      │
│                  │ Tab      │    │ Upsell   │    │ Prompt   │      │
│                  └──────────┘    └──────────┘    └──────────┘      │
│                                                                      │
│  ── Step Details ─────────────────────────────────────────────     │
│  Step 3: Goal Select                                                │
│  • Activity: OnboardingGoalActivity                                 │
│  • Options: Casual / Regular / Serious / Intense (5/10/15/20 min) │
│  • This data sent to: api.duolingo.com/onboarding/goal            │
│  • Used for: push notification timing personalization             │
│  [View Source]                                                      │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

**How flows are built:**
1. V1 gives us all Activities + their IntentFilters + navigation graph (if Jetpack Nav used)
2. AI reads activity names + decompiled code to infer screen purpose
3. AI reconstructs likely flow order based on `startActivity` calls in source
4. Result: ordered list of screens per journey type

---

### SCREEN E: Tech Decisions Audit

**Goal:** Answer "why did they build it this way?" for every major tech choice.

```
┌──────────────────────────────────────────────────────────────────────┐
│  Tech Decisions Audit                                                │
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  🏗️  ARCHITECTURE                                           │    │
│  │  Pattern: MVVM + Repository + UseCase (Clean Architecture)  │    │
│  │  Evidence: ViewModel/, Repository/, UseCase/ package names  │    │
│  │  Verdict: Mature, scalable. Suggests experienced team.      │    │
│  │  Implication: Hard to move fast, but easy to maintain.      │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  🌐 NETWORKING                                              │    │
│  │  Choice: Retrofit + OkHttp + custom interceptors            │    │
│  │  Evidence: RetrofitClient.java, AuthInterceptor.java        │    │
│  │  Verdict: Standard choice. Auth token refresh intercepted.  │    │
│  │  No GraphQL — pure REST. Suggests older API layer.          │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  💾 DATA / OFFLINE                                          │    │
│  │  Choice: Room DB + custom sync manager                      │    │
│  │  Evidence: *Dao.java files, SyncWorker.java (WorkManager)  │    │
│  │  Verdict: Strong offline support. Lessons cached locally.   │    │
│  │  Risk: Custom sync = complex conflict resolution.           │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  💰 MONETIZATION STACK                                      │    │
│  │  Choice: RevenueCat (not direct Google Play Billing)        │    │
│  │  Evidence: com.revenuecat.purchases package                 │    │
│  │  Verdict: Smart — RevenueCat handles entitlements cross-    │    │
│  │  platform. Implies iOS app shares same subscription logic.  │    │
│  │  Pricing tiers found in strings: $6.99 / $9.99 / annual    │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  🧪 EXPERIMENTATION                                         │    │
│  │  Choice: LaunchDarkly + Amplitude + Mixpanel                │    │
│  │  Evidence: All 3 SDKs present, LDClient initialized early   │    │
│  │  Verdict: Serious A/B culture. Feature flags gate new UX.  │    │
│  │  Implication: What you see in APK ≠ what users see now.   │    │
│  └─────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
```

---

### SCREEN F: Growth Intelligence

**Goal:** Understand how this app acquires, retains, and monetizes users.

```
┌──────────────────────────────────────────────────────────────────────┐
│  Growth Intelligence                                                 │
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  ── Acquisition ───────────────────────────────────────────────     │
│  Referral System        ✅ Detected — InviteManager.java           │
│  Deep Links             ✅ Detected — 14 deep link patterns        │
│  App Indexing           ✅ Detected — content:// URIs exposed      │
│  Attribution SDK        Adjust (com.adjust.sdk)                    │
│                                                                      │
│  ── Retention ─────────────────────────────────────────────────     │
│  Push Notifications     ✅ Firebase FCM — 8 notification types     │
│    Types found:                                                      │
│    • streak_reminder (daily, 20:00 local)                          │
│    • friend_activity (social trigger)                               │
│    • league_ending (urgency — 1hr before reset)                    │
│    • xp_boost_available (engagement hook)                          │
│                                                                      │
│  Streak Mechanics       ✅ Core retention loop                     │
│  Streak Freeze (paid)   ✅ Monetization tied to retention          │
│  Leaderboards           ✅ Weekly reset = re-engagement spike      │
│                                                                      │
│  ── Monetization ──────────────────────────────────────────────     │
│  Model: Freemium → Subscription                                     │
│  Paywall triggers detected:                                         │
│    • After lesson 3 (heart limit hit)                              │
│    • On streak freeze attempt                                       │
│    • On unlimited practice attempt                                 │
│    • After 7-day streak (milestone upsell)                        │
│                                                                      │
│  Pricing strings found:                                             │
│    super_annual_price    = "$47.99/year"                           │
│    super_monthly_price   = "$6.99/month"                           │
│    family_plan_price     = "$9.99/month"                           │
│                                                                      │
│  Ad Network: Google AdMob (detected in non-paid flow)              │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

---

### SCREEN G: Competitive Brief

**The money screen. A ready-to-paste document for founders/PMs.**

```
┌──────────────────────────────────────────────────────────────────────┐
│  Competitive Brief                          [Export PDF] [Copy MD]   │
│  ────────────────────────────────────────────────────────────────    │
│                                                                      │
│  [Select lens to customize brief: Founder ▾]                        │
│                                                                      │
│  ╔══════════════════════════════════════════════════════════════╗   │
│  ║  COMPETITIVE ANALYSIS: DUOLINGO                             ║   │
│  ║  Generated by APKLens V2 · com.duolingo · v5.152.4         ║   │
│  ╠══════════════════════════════════════════════════════════════╣   │
│  ║                                                             ║   │
│  ║  WHAT THEY DO                                              ║   │
│  ║  Gamified language learning. Core value prop: make         ║   │
│  ║  learning a language feel like playing a mobile game.      ║   │
│  ║                                                             ║   │
│  ║  HOW THEY MAKE MONEY                                       ║   │
│  ║  Freemium with aggressive paywall at points of friction    ║   │
│  ║  (heart loss, streak risk). $47.99/yr annual sub.         ║   │
│  ║  Ads shown to free users between lessons.                  ║   │
│  ║                                                             ║   │
│  ║  THEIR MOAT                                               ║   │
│  ║  Streak + social (leaderboards) creates switching cost.    ║   │
│  ║  Not the content — the habit infrastructure.              ║   │
│  ║                                                             ║   │
│  ║  WHAT THEY'RE BUILDING NEXT (inferred)                    ║   │
│  ║  • AI tutor: GPT API endpoint detected, not in prod UI    ║   │
│  ║  • Video lessons: VideoLessonActivity in code, no strings ║   │
│  ║  • Family plan expansion: new tier strings added          ║   │
│  ║                                                             ║   │
│  ║  WEAKNESSES TO EXPLOIT                                     ║   │
│  ║  • No offline AI — lessons need connection                ║   │
│  ║  • Localization gaps in strings (23 lang missing content) ║   │
│  ║  • No teacher/classroom tooling despite edu positioning   ║   │
│  ║                                                             ║   │
│  ║  TECH DEBT SIGNALS                                         ║   │
│  ║  • Legacy Java + Kotlin mixed (migration in progress)     ║   │
│  ║  • 3 different image loading libs (Glide, Coil, Picasso)  ║   │
│  ║  • Custom sync engine = likely reliability issues          ║   │
│  ║                                                             ║   │
│  ╚══════════════════════════════════════════════════════════════╝   │
└──────────────────────────────────────────────────────────────────────┘
```

---

### SCREEN H: AI Chat Sidebar (always visible)

**Right panel, always accessible. Ask anything about the APK.**

```
┌───────────────────────────────┐
│  Ask about this app   [×]     │
│  ─────────────────────────── │
│                               │
│  ┌───────────────────────┐   │
│  │ 👤                    │   │
│  │ How does their        │   │
│  │ paywall work?         │   │
│  └───────────────────────┘   │
│                               │
│  ┌───────────────────────┐   │
│  │ 🧠                    │   │
│  │ They use a "hearts"   │   │
│  │ system — free users   │   │
│  │ get 5 hearts. Wrong   │   │
│  │ answers cost 1 heart. │   │
│  │ Paywall appears when  │   │
│  │ hearts hit 0, or when │   │
│  │ streak is at risk...  │   │
│  └───────────────────────┘   │
│                               │
│  ┌───────────────────────┐   │
│  │ 👤                    │   │
│  │ What SDKs handle      │   │
│  │ their payments?       │   │
│  └───────────────────────┘   │
│                               │
│  ┌───────────────────────┐   │
│  │ 🧠 RevenueCat         │   │
│  │ (com.revenuecat) —    │   │
│  │ not direct Play       │   │
│  │ Billing. Found in     │   │
│  │ PurchaseManager.java  │   │
│  │ [View Source →]       │   │
│  └───────────────────────┘   │
│                               │
│  ─────────────────────────── │
│  [Ask anything...        ▶]  │
└───────────────────────────────┘
```

**Key UX behavior:**
- AI chat has full `AnalysisResult` JSON in its context window
- Also has access to decompiled source files (chunked if large)
- Answers cite source files with clickable `[View Source →]` that opens Screen 4 (Source Code) at that file
- Chat history persists per APK session in DB

---

## 4. AI PIPELINE ARCHITECTURE

### Step 1: Context Assembly (before any Claude API call)

```kotlin
data class AiContext(
    val appInfo: String,           // AppInfo as structured text
    val manifestSummary: String,   // key manifest components
    val classSummary: String,      // package tree + class count by type
    val sdkList: String,           // all detected SDKs + categories
    val networkEndpoints: String,  // all API endpoints
    val permissions: String,       // all permissions with levels
    val keyStrings: String,        // notable strings (pricing, feature flags, URLs)
    val sourceSnippets: String,    // top 20 most interesting class decompilations
)
```

Build `AiContext` from `AnalysisResult`. This is the shared context all AI prompts receive.

### Step 2: Parallel AI Analysis Jobs

Run these as parallel coroutines (all call Claude API simultaneously):

```kotlin
coroutineScope {
    val storyDeferred    = async { generateProductStory(ctx, persona) }
    val featuresDeferred = async { generateFeatureMap(ctx) }
    val flowsDeferred    = async { generateUserFlows(ctx) }
    val techDeferred     = async { generateTechAudit(ctx) }
    val growthDeferred   = async { generateGrowthAnalysis(ctx) }
    val briefDeferred    = async { generateCompetitiveBrief(ctx, persona) }

    AiAnalysisResult(
        story     = storyDeferred.await(),
        features  = featuresDeferred.await(),
        flows     = flowsDeferred.await(),
        techAudit = techDeferred.await(),
        growth    = growthDeferred.await(),
        brief     = briefDeferred.await(),
    )
}
```

Each job returns structured JSON (Claude instructed to return JSON only).

### Step 3: Claude API Call Pattern

```kotlin
suspend fun callClaude(systemPrompt: String, userPrompt: String): String {
    val response = httpClient.post("https://api.anthropic.com/v1/messages") {
        header("x-api-key", apiKey)
        header("anthropic-version", "2023-06-01")
        contentType(ContentType.Application.Json)
        setBody(ClaudeRequest(
            model = "claude-sonnet-4-6",
            max_tokens = 4096,
            system = systemPrompt,
            messages = listOf(Message(role = "user", content = userPrompt))
        ))
    }
    return response.body<ClaudeResponse>().content[0].text
}
```

---

## 5. PROMPT DESIGNS

### Prompt 1: Product Story

```
SYSTEM:
You are a senior product analyst. You will receive structured data extracted from 
an Android APK by a reverse engineering tool. Your job is to produce a clear, 
accurate product analysis for a {persona} audience.

Return ONLY a JSON object with this shape:
{
  "summary": "2-3 paragraph product narrative",
  "category": "app category",
  "businessModel": "one line",
  "coreLoop": "one line", 
  "teamSizeEstimate": "range based on codebase complexity",
  "uxMaturity": "Low|Medium|High",
  "abCulture": "Low|Medium|Heavy",
  "localization": "count of detected languages",
  "keyFindings": [{"type": "risk|note|strength", "text": "..."}]
}

USER:
Here is the APK analysis data:
{aiContext}

Persona focus: {persona}
```

### Prompt 2: Feature Map

```
SYSTEM:
You are a reverse engineering expert. Given APK analysis data, identify every 
user-facing feature in this app.

For each feature, determine:
- What it does (user perspective, 1-2 sentences)
- How it's built (technical implementation, citing specific classes/APIs found)
- Confidence: HIGH (direct evidence), MED (partial evidence), LOW (inferred)

Return ONLY JSON:
{
  "features": [
    {
      "name": "Feature name",
      "description": "What it does",
      "implementation": ["bullet 1", "bullet 2"],
      "evidence": ["ClassName.java", "api endpoint"],
      "confidence": "HIGH|MED|LOW",
      "isInferred": false
    }
  ]
}

USER:
{aiContext}
```

### Prompt 3: User Flows

```
SYSTEM:
You are a UX researcher analyzing an app from its decompiled code.
Reconstruct the main user journeys based on Activity names, navigation patterns,
and string resources found.

Return ONLY JSON:
{
  "flows": [
    {
      "name": "Onboarding",
      "steps": [
        {
          "screenName": "SplashActivity",
          "inferredPurpose": "App loading + auth check",
          "technicalNote": "Checks SharedPreferences for auth token",
          "nextScreens": ["LoginActivity", "HomeActivity"]
        }
      ]
    }
  ]
}

USER:
{aiContext}
```

### Prompt 4: Tech Audit

```
SYSTEM:
You are a senior Android engineer doing a technical audit of a competitor's app.
For each major technical decision detected, explain:
- What they chose
- Why they likely chose it
- What it signals about the team/company
- Any risks or weaknesses it reveals

Categories to cover: Architecture, Networking, Data/Storage, 
Monetization Stack, Analytics/Experimentation, Security.

Return ONLY JSON:
{
  "decisions": [
    {
      "category": "Architecture",
      "choice": "MVVM + Clean Architecture",
      "evidence": ["class names", "patterns"],
      "verdict": "explanation",
      "implication": "what this means competitively",
      "risk": "optional weakness"
    }
  ]
}

USER:
{aiContext}
```

### Prompt 5: Growth Analysis

```
SYSTEM:
You are a growth analyst reverse engineering a competitor's growth mechanics.
Identify their acquisition, retention, and monetization systems from the APK.

Pay special attention to:
- Attribution SDKs (how they track installs)
- Push notification types and triggers
- Referral/invite mechanics
- Paywall trigger points
- Pricing found in strings
- Re-engagement mechanics

Return ONLY JSON:
{
  "acquisition": { ... },
  "retention": {
    "pushNotifications": [{"type": "...", "trigger": "..."}],
    "retentionMechanics": [...]
  },
  "monetization": {
    "model": "...",
    "paywallTriggers": [...],
    "pricingFound": [...],
    "adNetworks": [...]
  }
}

USER:
{aiContext}
```

### Prompt 6: Competitive Brief (persona-aware)

```
SYSTEM:
You are a competitive intelligence analyst writing a brief for a {persona}.
Using all available APK analysis data, write an actionable competitive brief.

For a FOUNDER: focus on moat, weaknesses to exploit, what they're building next
For a PM: focus on UX decisions, feature gaps, onboarding patterns
For an INVESTOR: focus on technical maturity, team size signals, scalability
For a DESIGNER: focus on design system evidence, screen count, navigation patterns
For a GROWTH: focus on acquisition, retention loops, monetization mechanics

Return ONLY JSON:
{
  "whatTheyDo": "...",
  "howTheyMakeMoney": "...",
  "theirMoat": "...",
  "buildingNext": ["inferred upcoming features"],
  "weaknessesToExploit": ["..."],
  "techDebtSignals": ["..."],
  "keyTakeaway": "single most important insight for this persona"
}

USER:
{aiContext}
Persona: {persona}
```

---

## 6. DATA MODELS (V2 additions)

```kotlin
data class AiAnalysisResult(
    val id: String,                           // same as AnalysisResult.id
    val persona: Persona,
    val generatedAt: Long,
    val story: ProductStory,
    val features: List<Feature>,
    val flows: List<UserFlow>,
    val techAudit: List<TechDecision>,
    val growth: GrowthAnalysis,
    val brief: CompetitiveBrief,
    val chatHistory: List<ChatMessage>,
)

enum class Persona { FOUNDER, PM, INVESTOR, DESIGNER, GROWTH }

data class ProductStory(
    val summary: String,
    val category: String,
    val businessModel: String,
    val coreLoop: String,
    val teamSizeEstimate: String,
    val uxMaturity: MaturityLevel,
    val abCulture: AbCultureLevel,
    val localizationCount: Int,
    val keyFindings: List<Finding>,
)

data class Finding(
    val type: FindingType,   // enum: RISK, NOTE, STRENGTH
    val text: String,
)

data class Feature(
    val name: String,
    val description: String,
    val implementation: List<String>,
    val evidence: List<String>,
    val confidence: ConfidenceLevel,  // HIGH, MED, LOW
    val isInferred: Boolean,          // true = not in current UI, likely upcoming
)

data class UserFlow(
    val name: String,                 // "Onboarding", "Core Loop", etc.
    val steps: List<FlowStep>,
)

data class FlowStep(
    val screenName: String,
    val inferredPurpose: String,
    val technicalNote: String,
    val nextScreens: List<String>,
    val sourceFile: String?,          // link back to V1 source viewer
)

data class TechDecision(
    val category: String,
    val choice: String,
    val evidence: List<String>,
    val verdict: String,
    val implication: String,
    val risk: String?,
)

data class GrowthAnalysis(
    val acquisition: AcquisitionData,
    val retention: RetentionData,
    val monetization: MonetizationData,
)

data class CompetitiveBrief(
    val whatTheyDo: String,
    val howTheyMakeMoney: String,
    val theirMoat: String,
    val buildingNext: List<String>,
    val weaknessesToExploit: List<String>,
    val techDebtSignals: List<String>,
    val keyTakeaway: String,
    val persona: Persona,
)

data class ChatMessage(
    val role: ChatRole,   // USER, ASSISTANT
    val content: String,
    val sourceReferences: List<SourceRef>,  // clickable links to V1 source viewer
    val timestamp: Long,
)

data class SourceRef(
    val filePath: String,
    val lineNumber: Int?,
    val displayLabel: String,
)
```

---

## 7. NAVIGATION UPDATES (V2 additions to RootComponent)

```kotlin
// AnalysisComponent now has two tab groups
class AnalysisComponent {
    sealed class Tab {
        // V2 AI tabs
        sealed class AiTab : Tab() {
            object Story : AiTab()
            object Features : AiTab()
            object UserFlows : AiTab()
            object TechAudit : AiTab()
            object Growth : AiTab()
            object Brief : AiTab()
        }
        // V1 Raw tabs (same as before, now nested under "Raw Data")
        sealed class RawTab : Tab() {
            object Overview : RawTab()
            object Manifest : RawTab()
            object Structure : RawTab()
            object SourceCode : RawTab()
            object Assets : RawTab()
            object Network : RawTab()
            object Permissions : RawTab()
            object Dependencies : RawTab()
            object Report : RawTab()
        }
    }
}
```

Left nav structure:
```
AI INSIGHTS          ← section header
  🧠 Product Story   ← default
  🗺️  Features
  🚶 User Flows
  💡 Tech Audit
  📈 Growth
  ⚔️  Brief

RAW DATA             ← collapsed by default
  📋 Overview
  📄 Manifest
  (... all V1 screens)
```

---

## 8. KEY UX DECISIONS (for AI agent to implement correctly)

### 8.1 Streaming Insight Feed (Processing Screen)
As each parallel Claude job completes, stream findings to the processing screen live. Don't wait for all 6 to finish. First finding appears ~5 seconds in.

### 8.2 Source Links Throughout
Every AI claim that references code must have a `[View Source →]` button that:
1. Switches to left nav "Raw Data > Source Code"
2. Opens the referenced file
3. Scrolls to the referenced line

This is the killer feature that separates APKLens from a generic AI summary tool.

### 8.3 Confidence Indicators
Always show confidence level on AI-generated content. Never present AI inference as fact without labeling it.

### 8.4 Regenerate with Persona
Any screen can be re-generated for a different persona without re-running the full pipeline. Only re-calls the Claude API for that screen with the new persona.

### 8.5 Inferred / Upcoming Features Callout
"INFERRED" features (things detected in code but not yet in UI) get a distinct visual treatment — dashed border, 👀 icon, "Not yet live" label. This is high-value competitive signal.

### 8.6 Export Everything
Every V2 screen exports to:
- PDF (full formatted report)
- Markdown (for Notion/Obsidian/GitHub)
- JSON (for feeding into other tools / other AI systems)

---

## 9. SETTINGS (V2 additions)

```
Settings
  ── AI Configuration ──
  Anthropic API Key    [••••••••••••••]  [Test]
  Model                claude-sonnet-4-6 (recommended) / claude-opus-4-6
  Default Persona      [Founder ▾]
  
  ── Analysis Options ──
  Auto-run AI after V1  [Toggle ON]
  Stream live insights  [Toggle ON]
  Include source snippets in AI context [Toggle ON — uses more tokens]
  
  ── Token Usage ──
  This session:  ~42,000 tokens (~$0.05)
  Total used:    ~840,000 tokens (~$1.00)
```

Show token cost estimates. Founders care about this. Transparency builds trust.

---

## 10. V2 DB SCHEMA (additions to SQLDelight)

```sql
-- Store AI results separately from V1 (can regenerate AI without re-parsing)
CREATE TABLE AiAnalysisSession (
    id TEXT PRIMARY KEY,
    analysis_result_id TEXT NOT NULL REFERENCES AnalysisSession(id),
    persona TEXT NOT NULL,
    generated_at INTEGER NOT NULL,
    story_json TEXT NOT NULL,
    features_json TEXT NOT NULL,
    flows_json TEXT NOT NULL,
    tech_audit_json TEXT NOT NULL,
    growth_json TEXT NOT NULL,
    brief_json TEXT NOT NULL,
);

CREATE TABLE ChatMessage (
    id TEXT PRIMARY KEY,
    ai_session_id TEXT NOT NULL REFERENCES AiAnalysisSession(id),
    role TEXT NOT NULL,
    content TEXT NOT NULL,
    source_refs_json TEXT,
    timestamp INTEGER NOT NULL
);
```

---

## 11. FILE STRUCTURE (V2 additions only)

```
apklens/
└── composeApp/src/desktopMain/kotlin/
    ├── ui/
    │   └── screens/
    │       ├── ai/
    │       │   ├── AiProcessingScreen.kt
    │       │   ├── ProductStoryScreen.kt
    │       │   ├── FeatureMapScreen.kt
    │       │   ├── UserFlowsScreen.kt
    │       │   ├── TechAuditScreen.kt
    │       │   ├── GrowthScreen.kt
    │       │   └── CompetitiveBriefScreen.kt
    │       └── components/
    │           ├── AiChatSidebar.kt
    │           ├── ConfidenceBadge.kt
    │           ├── SourceRefLink.kt
    │           ├── FlowDiagram.kt          ← custom Compose canvas flow chart
    │           ├── PersonaSelector.kt
    │           └── InsightCard.kt
    └── domain/
        ├── ai/
        │   ├── AiContextBuilder.kt         ← builds AiContext from AnalysisResult
        │   ├── ClaudeClient.kt             ← API wrapper
        │   ├── AiAnalysisPipeline.kt       ← orchestrates parallel jobs
        │   ├── prompts/
        │   │   ├── ProductStoryPrompt.kt
        │   │   ├── FeatureMapPrompt.kt
        │   │   ├── UserFlowsPrompt.kt
        │   │   ├── TechAuditPrompt.kt
        │   │   ├── GrowthPrompt.kt
        │   │   └── CompetitiveBriefPrompt.kt
        │   └── parsers/
        │       ├── FeatureParser.kt        ← JSON → Feature list
        │       ├── FlowParser.kt
        │       └── ...
        └── db/
            └── AiHistoryRepository.kt
```

---

## 12. BUILD ADDITIONS (build.gradle.kts)

```kotlin
desktopMain.dependencies {
    // existing V1 deps +
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
}
```

---

## 13. V3 SEEDS (don't build now, design for)

- **Compare Mode:** Analyze 2 APKs side-by-side, AI diffs them
- **Track Over Time:** Re-analyze same app after each major update, AI shows what changed
- **Market Map:** Analyze 10 apps in same category, AI produces market landscape
- **Team Intelligence:** Infer engineering team size, hiring patterns from commit meta + complexity
- **Export to Notion/Linear:** Push competitive brief directly to PM tools

---

*End of V2 Technical Plan*  
*V1 Plan: APKLens_V1_Technical_Plan.md*
