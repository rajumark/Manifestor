# APKLens V1 — Full Technical Plan
**Target:** Kotlin Multiplatform Desktop App (macOS + Windows)  
**Goal:** Upload APK → Full reverse-engineered breakdown, presented clearly  
**Audience:** This document is written for an AI coding agent to implement  

---

## 1. TECH STACK

| Layer | Choice | Reason |
|---|---|---|
| UI Framework | Compose Multiplatform (Desktop) | KMP-native, declarative, rich layout control |
| APK Parsing | `apktool` (CLI via Process) + `dex2jar` + `jadx-lib` | Industry standard; wrap as subprocess calls |
| Manifest Parsing | `XmlPullParser` on decoded AndroidManifest.xml | Built-in Kotlin XML parsing |
| DEX Analysis | `jadx-core` (JVM library, call directly) | Decompile DEX → Java/Kotlin source tree |
| String/Resource Extraction | Custom parser on `res/` folder post-apktool decode | Read values/strings.xml etc. |
| Dependency Detection | Scan class package names + known SDK fingerprints | Rule-based matching against known SDK list |
| File I/O | `kotlinx-io` + Java NIO | Cross-platform file handling |
| State Management | Decompose (Arkadiy Ivanov) or simple ViewModel pattern | Navigation + lifecycle |
| Navigation | Decompose library | Multi-pane desktop navigation |
| Persistence | SQLDelight (local DB for analysis history) | Store past APK analysis sessions |
| Coroutines | `kotlinx.coroutines` | Async APK processing pipeline |

---

## 2. APP LAYOUT — OVERALL SHELL

```
┌─────────────────────────────────────────────────────────────────────┐
│  TITLE BAR: "APKLens"                          [minimize][max][close]│
├──────────────┬──────────────────────────────────────────────────────┤
│              │                                                        │
│  LEFT NAV    │   MAIN CONTENT PANEL                                  │
│  (240px)     │   (flex fill)                                         │
│              │                                                        │
│  [sidebar]   │   [dynamic content based on nav selection]            │
│              │                                                        │
└──────────────┴──────────────────────────────────────────────────────┘
```

### Left Nav — Sections (only visible after APK is loaded)

```
┌─────────────────────┐
│  APKLens            │
│  ─────────────────  │
│  [+ Open APK]       │
│                     │
│  ▾ Current APK      │
│    com.example.app  │
│    v3.2.1           │
│  ─────────────────  │
│  📋 Overview        │  ← default selected
│  📄 Manifest        │
│  🏗️  Structure      │
│  💻 Source Code     │
│  🎨 Assets          │
│  🔗 Network         │
│  📦 Dependencies    │
│  🔑 Permissions     │
│  📊 Report          │
│  ─────────────────  │
│  🕘 History         │
│  ⚙️  Settings       │
└─────────────────────┘
```

---

## 3. SCREENS — DETAILED LAYOUT & CONTENT

---

### SCREEN 0: Welcome / Drop Zone (no APK loaded)

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│                    APKLens                               │
│           Reverse Engineer Any Android App               │
│                                                          │
│        ┌──────────────────────────────────┐              │
│        │                                  │              │
│        │     🗂️  Drop APK file here       │              │
│        │                                  │              │
│        │     or  [Browse File]            │              │
│        │                                  │              │
│        └──────────────────────────────────┘              │
│                                                          │
│        Supported: .apk files up to 500MB                 │
│                                                          │
│        ─────────── Recent ───────────                    │
│        com.instagram.android    2 days ago   [Open]      │
│        com.spotify.music        5 days ago   [Open]      │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

**Behavior:**
- Drag-and-drop APK onto window triggers file ingestion pipeline
- Browse opens native OS file picker filtered to `.apk`
- On file selected → transition to Processing screen

---

### SCREEN 0b: Processing / Analysis Screen

```
┌──────────────────────────────────────────────────────────┐
│                                                          │
│   Analyzing  com.example.app.apk                        │
│                                                          │
│   ████████████████░░░░░░░░░░   64%                      │
│                                                          │
│   ✅  Unpacking APK                                      │
│   ✅  Parsing AndroidManifest.xml                        │
│   ✅  Extracting resources                               │
│   ⏳  Decompiling DEX → Source (this may take a while)  │
│   ○   Scanning dependencies                              │
│   ○   Extracting network endpoints                       │
│   ○   Building overview report                           │
│                                                          │
│                                       [Cancel]           │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

**Behavior:**
- Each step is a suspend function in a coroutine pipeline
- Steps run sequentially; progress updates via `StateFlow`
- On complete → auto-navigate to Overview screen

---

### SCREEN 1: Overview

**Layout:** Card grid (2-col on wide, 1-col on narrow)

```
┌─────────────────────────────────────────────────────────────┐
│  Overview — com.instagram.android                           │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  ┌─────────────────────┐  ┌─────────────────────┐          │
│  │ 📱 App Info         │  │ 🔑 Permissions       │          │
│  │ Name: Instagram     │  │ 42 permissions       │          │
│  │ Package: com.ins... │  │ 12 dangerous         │          │
│  │ Version: 312.0.0    │  │ [View All →]         │          │
│  │ Min SDK: 26 (8.0)   │  └─────────────────────┘          │
│  │ Target SDK: 34      │                                    │
│  │ Size: 87.4 MB       │  ┌─────────────────────┐          │
│  └─────────────────────┘  │ 📦 Dependencies      │          │
│                           │ 134 libraries found  │          │
│  ┌─────────────────────┐  │ Firebase, Retrofit,  │          │
│  │ 🏗️  Structure        │  │ OkHttp, Glide...    │          │
│  │ 847 classes         │  │ [View All →]         │          │
│  │ 23 Activities       │  └─────────────────────┘          │
│  │ 8 Services          │                                    │
│  │ 12 Receivers        │  ┌─────────────────────┐          │
│  │ 4 Providers         │  │ 🔗 Network           │          │
│  │ [View All →]        │  │ 38 endpoints found   │          │
│  └─────────────────────┘  │ api.instagram.com    │          │
│                           │ graph.facebook.com   │          │
│  ┌─────────────────────┐  │ [View All →]         │          │
│  │ 🎨 Assets           │  └─────────────────────┘          │
│  │ 1,204 files         │                                    │
│  │ 342 images          │                                    │
│  │ 18 fonts            │                                    │
│  │ [View All →]        │                                    │
│  └─────────────────────┘                                    │
│                                                             │
│  [Export Full Report PDF]                    [Copy Summary] │
└─────────────────────────────────────────────────────────────┘
```

---

### SCREEN 2: Manifest

**Layout:** Split — tree on left, detail panel on right

```
┌────────────────────────────────────────────────────────────────┐
│  Manifest                                      [Raw XML] [Tree] │
│  ──────────────────────────────────────────────────────────── │
│  ┌──────────────────┐  ┌─────────────────────────────────────┐ │
│  │ ▾ manifest       │  │  <activity>                         │ │
│  │   ▾ application  │  │                                     │ │
│  │     ▾ activities │  │  Name:                              │ │
│  │       MainActivity│  │  com.instagram.MainActivity        │ │
│  │       LoginAct..  │  │                                     │ │
│  │       SplashAct.. │  │  Exported: true                    │ │
│  │     ▾ services   │  │  LaunchMode: singleTask             │ │
│  │       Push...     │  │  IntentFilters:                    │ │
│  │     ▾ receivers  │  │    ACTION_MAIN                      │ │
│  │       Boot...     │  │    CATEGORY_LAUNCHER               │ │
│  │     ▾ providers  │  │                                     │ │
│  │       FileProvider│  │  [View Raw XML]                    │ │
│  └──────────────────┘  └─────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- Tree built from parsed `AndroidManifest.xml` nodes
- Clicking a node populates right panel with all attributes
- Toggle between Tree view and raw XML syntax highlighted view
- Search bar filters tree nodes

---

### SCREEN 3: Structure

**Layout:** File explorer tree + class viewer

```
┌────────────────────────────────────────────────────────────────┐
│  Structure                     [Search classes...        🔍]   │
│  ────────────────────────────────────────────────────────────  │
│  ┌──────────────────────┐  ┌───────────────────────────────┐   │
│  │ ▾ com                │  │  Package: com.instagram.ui    │   │
│  │   ▾ instagram        │  │  ───────────────────────────  │   │
│  │     ▾ ui             │  │  Classes (47)                 │   │
│  │       HomeFragment   │  │                               │   │
│  │       ReelsFragment  │  │  HomeFragment        Activity │   │
│  │       StoryViewer    │  │  ReelsFragment       Fragment │   │
│  │     ▾ data           │  │  StoryViewerActivity Activity │   │
│  │       ▾ network      │  │  FeedAdapter         Adapter  │   │
│  │         ApiService   │  │  ...                          │   │
│  │         RetrofitClient│  │                              │   │
│  │     ▾ domain         │  │  [Open in Source →]           │   │
│  └──────────────────────┘  └───────────────────────────────┘   │
│                                                                 │
│  Total: 847 classes across 34 packages                         │
└────────────────────────────────────────────────────────────────┘
```

---

### SCREEN 4: Source Code

**Layout:** File tree left, code viewer right (syntax highlighted)

```
┌────────────────────────────────────────────────────────────────┐
│  Source Code                    [Search in files...      🔍]   │
│  ────────────────────────────────────────────────────────────  │
│  ┌──────────────────────┐  ┌───────────────────────────────┐   │
│  │ ▾ sources            │  │  HomeFragment.java         [x]│   │
│  │   ▾ com.instagram    │  │  ─────────────────────────────│   │
│  │     ▾ ui             │  │  1  package com.instagram.ui; │   │
│  │       HomeFragment   │  │  2                            │   │
│  │       ReelsFragment  │  │  3  import androidx...        │   │
│  │     ▾ data           │  │  4                            │   │
│  │       ApiService     │  │  5  public class HomeFragment │   │
│  │                      │  │  6    extends Fragment {      │   │
│  │                      │  │  7                            │   │
│  │                      │  │  8    @Override               │   │
│  │                      │  │  9    public View onCreateV.. │   │
│  │                      │  │  ...                          │   │
│  │                      │  │                               │   │
│  │                      │  │  [Copy] [Search References]   │   │
│  └──────────────────────┘  └───────────────────────────────┘   │
│  ⚠️  Decompiled code may not be fully accurate                  │
└────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- Code viewer: syntax highlighting via Compose custom `AnnotatedString` renderer
- Tabs for multiple open files
- Ctrl+F search within current file
- Global search across all decompiled files (grep-style via coroutine + file scan)

---

### SCREEN 5: Assets

**Layout:** Filter bar + grid/list toggle

```
┌────────────────────────────────────────────────────────────────┐
│  Assets                                                         │
│  [All ▾] [Images] [Fonts] [Layouts] [Raw] [Values]   🔍       │
│  ──────────────────────────────────────────────────────────── │
│  Images (342)                              [Grid] [List]       │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐               │
│  │ img  │ │ img  │ │ img  │ │ img  │ │ img  │               │
│  │      │ │      │ │      │ │      │ │      │               │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘               │
│  ic_home  ic_reel  ic_plus  ic_msg   logo_main               │
│                                                               │
│  Strings (4,821)                                              │
│  "Welcome back"    en / strings.xml                          │
│  "Post a photo"    en / strings.xml                          │
│  "Share a reel"    en / strings.xml                          │
│  ...                                                          │
│                                                               │
│  Fonts (18)                                                   │
│  Proxima Nova Regular    ProximaNova-Regular.ttf              │
│  Proxima Nova Bold       ProximaNova-Bold.ttf                 │
└────────────────────────────────────────────────────────────────┘
```

---

### SCREEN 6: Network

**Layout:** Table with filter + detail drawer

```
┌────────────────────────────────────────────────────────────────┐
│  Network Endpoints                         [Search...    🔍]   │
│  ──────────────────────────────────────────────────────────── │
│  38 endpoints found across 12 files                           │
│                                                               │
│  DOMAIN                    PATH               FOUND IN        │
│  api.instagram.com         /v1/feed/          ApiService.java │
│  api.instagram.com         /v1/users/{id}     ApiService.java │
│  graph.facebook.com        /me/friends        FBHelper.java   │
│  crashlytics.com           /report            CrashKit.java   │
│  doubleclick.net           /ads/              AdManager.java  │
│  ...                                                          │
│                                                               │
│  ── Grouped by Domain ──────────────────────────────────────  │
│  api.instagram.com     (22 endpoints)                        │
│  graph.facebook.com    (8 endpoints)                         │
│  crashlytics.com       (3 endpoints)                         │
│  doubleclick.net       (5 endpoints)                         │
└────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- Endpoints extracted by scanning decompiled source for URL patterns (regex)
- Also scans `res/values/strings.xml` for hardcoded URLs
- Click row → side drawer shows exact source file + line number + full URL

---

### SCREEN 7: Permissions

**Layout:** Categorized list with severity badges

```
┌────────────────────────────────────────────────────────────────┐
│  Permissions (42)                                              │
│  [All] [Dangerous 🔴] [Normal 🟢] [Signature 🔵]             │
│  ──────────────────────────────────────────────────────────── │
│                                                               │
│  🔴 DANGEROUS (12)                                            │
│  READ_CONTACTS          Access contact list                   │
│  CAMERA                 Take photos and videos                │
│  ACCESS_FINE_LOCATION   Precise GPS location                  │
│  READ_MEDIA_IMAGES      Access photo library                  │
│  RECORD_AUDIO           Microphone access                     │
│  ...                                                          │
│                                                               │
│  🟢 NORMAL (24)                                               │
│  INTERNET               Network access                        │
│  VIBRATE                Vibrate device                        │
│  RECEIVE_BOOT_COMPLETED Run on device startup                 │
│  ...                                                          │
│                                                               │
│  🔵 SIGNATURE (6)                                             │
│  ...                                                          │
└────────────────────────────────────────────────────────────────┘
```

---

### SCREEN 8: Dependencies

**Layout:** Categorized SDK cards

```
┌────────────────────────────────────────────────────────────────┐
│  Dependencies (134 detected)                                   │
│  ──────────────────────────────────────────────────────────── │
│                                                               │
│  🔥 Analytics & Tracking (8)                                  │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐          │
│  │ Firebase     │ │ Mixpanel     │ │ Amplitude    │          │
│  │ Analytics    │ │ Analytics    │ │              │          │
│  │ v21.3.0      │ │ v5.8.0       │ │ v1.10.0      │          │
│  └──────────────┘ └──────────────┘ └──────────────┘          │
│                                                               │
│  🌐 Networking (5)                                            │
│  ┌──────────────┐ ┌──────────────┐                           │
│  │ Retrofit     │ │ OkHttp       │                           │
│  │ v2.9.0       │ │ v4.11.0      │                           │
│  └──────────────┘ └──────────────┘                           │
│                                                               │
│  🖼️  Image Loading (3)                                        │
│  💳 Payments (2)                                              │
│  🔔 Push Notifications (3)                                    │
│  🛡️  Security & Auth (4)                                      │
│  💥 Crash Reporting (2)                                       │
│  🎯 Ads (6)                                                   │
│  📦 Other (101)                                               │
└────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- SDK fingerprinting: match class package prefixes against hardcoded JSON SDK registry
- Registry format: `{ "com.google.firebase": { name: "Firebase", category: "Analytics", ... } }`
- Unknown packages show under "Other" with raw package name

---

### SCREEN 9: Report

**Layout:** Full rendered report with export options

```
┌────────────────────────────────────────────────────────────────┐
│  Full Report — com.instagram.android v312.0.0                  │
│  Generated: 2025-08-14 14:32                                   │
│  ─────────────────────────────────────  [Export PDF] [Copy MD] │
│                                                               │
│  ## Executive Summary                                         │
│  Instagram (com.instagram.android) is a social media app     │
│  targeting Android 8.0+. It uses 134 third-party libraries,  │
│  requests 12 dangerous permissions, and exposes 38 API       │
│  endpoints. Heavy investment in analytics (Firebase,         │
│  Mixpanel, Amplitude) suggests an A/B testing culture.       │
│                                                               │
│  ## App Info          ## Permissions      ## Network         │
│  ...                  ...                 ...                │
│                                                               │
│  ## Dependencies      ## Structure        ## Notable Findings│
│  ...                  ...                 ...                │
└────────────────────────────────────────────────────────────────┘
```

---

## 4. DATA PIPELINE — PROCESSING ARCHITECTURE

```
APK File Input
     │
     ▼
┌─────────────────┐
│  ApkProcessor   │  (Coroutine scope, sequential steps)
│  CoroutineScope │
└────────┬────────┘
         │
         ├──▶ Step 1: UnzipApk()         → /tmp/apklens/{hash}/raw/
         ├──▶ Step 2: RunApkTool()       → /tmp/apklens/{hash}/decoded/
         ├──▶ Step 3: ParseManifest()    → ManifestModel
         ├──▶ Step 4: RunDex2Jar()       → classes.jar
         ├──▶ Step 5: RunJadx()          → /tmp/apklens/{hash}/sources/
         ├──▶ Step 6: ScanResources()    → AssetsModel
         ├──▶ Step 7: ScanNetworkUrls()  → NetworkModel
         ├──▶ Step 8: ScanPermissions()  → PermissionsModel
         ├──▶ Step 9: MatchSDKs()        → DependenciesModel
         └──▶ Step 10: BuildReport()     → AnalysisResult (sealed class)
                                                │
                                                ▼
                                        SQLDelight DB (cache)
```

---

## 5. DATA MODELS (Kotlin)

```kotlin
// Root result stored in DB and held in memory
data class AnalysisResult(
    val id: String,                       // UUID
    val apkPath: String,
    val analyzedAt: Long,
    val appInfo: AppInfo,
    val manifest: ManifestModel,
    val structure: StructureModel,
    val assets: AssetsModel,
    val network: NetworkModel,
    val permissions: List<Permission>,
    val dependencies: List<Dependency>,
)

data class AppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val minSdk: Int,
    val targetSdk: Int,
    val fileSizeBytes: Long,
    val md5: String,
)

data class ManifestModel(
    val rawXml: String,
    val activities: List<ComponentEntry>,
    val services: List<ComponentEntry>,
    val receivers: List<ComponentEntry>,
    val providers: List<ComponentEntry>,
    val intentFilters: List<IntentFilter>,
)

data class ComponentEntry(
    val name: String,
    val exported: Boolean,
    val attributes: Map<String, String>,
)

data class StructureModel(
    val totalClasses: Int,
    val packages: List<PackageNode>,    // tree structure
)

data class PackageNode(
    val name: String,
    val classes: List<ClassEntry>,
    val children: List<PackageNode>,
)

data class ClassEntry(
    val simpleName: String,
    val fullName: String,
    val type: ClassType,  // enum: ACTIVITY, FRAGMENT, SERVICE, ADAPTER, OTHER
    val sourceFilePath: String?,
)

data class NetworkModel(
    val endpoints: List<NetworkEndpoint>,
)

data class NetworkEndpoint(
    val domain: String,
    val path: String,
    val fullUrl: String,
    val foundInFile: String,
    val lineNumber: Int?,
)

data class Permission(
    val name: String,
    val protectionLevel: ProtectionLevel,  // enum: NORMAL, DANGEROUS, SIGNATURE
    val description: String,
)

data class Dependency(
    val packagePrefix: String,
    val name: String,
    val version: String?,
    val category: DependencyCategory,  // enum: ANALYTICS, NETWORKING, IMAGE, PAYMENTS, ADS, etc.
    val knownSdk: Boolean,
)

data class AssetsModel(
    val images: List<AssetFile>,
    val fonts: List<AssetFile>,
    val layouts: List<AssetFile>,
    val rawFiles: List<AssetFile>,
    val strings: List<StringEntry>,
)

data class AssetFile(
    val name: String,
    val relativePath: String,
    val sizeBytes: Long,
    val type: String,
)

data class StringEntry(
    val key: String,
    val value: String,
    val locale: String,
)
```

---

## 6. NAVIGATION ARCHITECTURE

Using **Decompose** library:

```kotlin
// Root component
class RootComponent(componentContext: ComponentContext) {
    sealed class Child {
        object Welcome : Child()
        data class Processing(val apkPath: String) : Child()
        data class Analysis(val result: AnalysisResult) : Child()
        object History : Child()
        object Settings : Child()
    }

    // Navigation stack
    val stack: Value<ChildStack<*, Child>>
}

// Analysis sub-navigation (left nav tabs)
class AnalysisComponent(componentContext: ComponentContext, val result: AnalysisResult) {
    sealed class Tab {
        object Overview : Tab()
        object Manifest : Tab()
        object Structure : Tab()
        object SourceCode : Tab()
        object Assets : Tab()
        object Network : Tab()
        object Permissions : Tab()
        object Dependencies : Tab()
        object Report : Tab()
    }
    val activeTab: Value<Tab>
    fun selectTab(tab: Tab)
}
```

---

## 7. EXTERNAL TOOL INTEGRATION

### apktool (CLI subprocess)
```kotlin
suspend fun runApkTool(apkPath: String, outputDir: String): Result<Unit> {
    val process = ProcessBuilder(
        "java", "-jar", "apktool.jar", "d", apkPath, "-o", outputDir, "-f"
    ).start()
    return withContext(Dispatchers.IO) {
        val exit = process.waitFor()
        if (exit == 0) Result.success(Unit) else Result.failure(...)
    }
}
```

### jadx (library, not CLI)
```kotlin
// Add jadx-core to dependencies
// io.github.skylot:jadx-core:1.4.7
val jadx = JadxDecompiler(JadxArgs().apply {
    inputFiles = listOf(File(jarPath))
    outDir = File(outputDir)
})
jadx.load()
jadx.save()
```

### Bundled tools strategy
- Bundle `apktool.jar` inside app resources
- Bundle `dex-tools` (dex2jar) inside app resources
- Extract to `~/.apklens/tools/` on first run
- Check Java availability; show error if missing

---

## 8. VISUAL DESIGN SYSTEM

**Theme:** Dark-first, developer tool aesthetic

```kotlin
object AppColors {
    val background = Color(0xFF0F1117)       // near-black
    val surface = Color(0xFF1A1D27)          // card background
    val surfaceHover = Color(0xFF222536)
    val accent = Color(0xFF6C63FF)           // electric purple
    val accentGreen = Color(0xFF4ADE80)      // success / normal permission
    val accentRed = Color(0xFFFF4D6D)        // dangerous permission
    val accentBlue = Color(0xFF60A5FA)       // signature / info
    val textPrimary = Color(0xFFE8E8F0)
    val textSecondary = Color(0xFF8B8FA8)
    val divider = Color(0xFF2A2D3E)
    val codeBackground = Color(0xFF141722)
}

object AppTypography {
    val displayFont = FontFamily(Font("JetBrainsMono-Regular.ttf")) // for code
    val uiFont = FontFamily(Font("Inter-Regular.ttf"))              // for UI
}
```

**Component library to build:**
- `SectionCard` — surface-colored card with title + content slot
- `NavItem` — left nav item with icon + label + selected state
- `BadgePill` — colored pill for permission levels / categories
- `CodeViewer` — scrollable syntax-highlighted code block
- `FileTree` — recursive expandable tree component
- `SearchBar` — unified search input used across screens
- `ProgressPipeline` — step-by-step progress display (Processing screen)
- `DataTable` — sortable/filterable table for network endpoints etc.
- `ExportButton` — triggers PDF/Markdown report generation

---

## 9. REPORT EXPORT

### PDF Export
- Use `Apache PDFBox` (JVM library) to generate PDF
- Walk through `AnalysisResult` and render each section as structured PDF pages
- Include: app icon (if found in assets), summary stats, all sections

### Markdown Export
- Simple string builder rendering `AnalysisResult` to `.md`
- Useful for pasting into Notion / GitHub issues / AI prompts (V2 prep)

---

## 10. HISTORY (SQLDelight)

```sql
-- analysis_sessions table
CREATE TABLE AnalysisSession (
    id TEXT PRIMARY KEY,
    package_name TEXT NOT NULL,
    app_name TEXT NOT NULL,
    version_name TEXT NOT NULL,
    analyzed_at INTEGER NOT NULL,
    result_json TEXT NOT NULL    -- full AnalysisResult serialized as JSON
);
```

- On app open: load recent sessions from DB, display on Welcome screen
- Clicking history item: deserialize JSON → restore full AnalysisResult → navigate to Analysis screen
- No re-processing needed

---

## 11. FILE / DIRECTORY STRUCTURE

```
apklens/
├── composeApp/
│   └── src/desktopMain/kotlin/
│       ├── main.kt                          # entry point
│       ├── App.kt                           # root Compose + theme setup
│       ├── navigation/
│       │   ├── RootComponent.kt
│       │   └── AnalysisComponent.kt
│       ├── ui/
│       │   ├── theme/
│       │   │   ├── AppColors.kt
│       │   │   └── AppTypography.kt
│       │   ├── components/
│       │   │   ├── SectionCard.kt
│       │   │   ├── NavItem.kt
│       │   │   ├── BadgePill.kt
│       │   │   ├── CodeViewer.kt
│       │   │   ├── FileTree.kt
│       │   │   ├── SearchBar.kt
│       │   │   ├── DataTable.kt
│       │   │   └── ExportButton.kt
│       │   └── screens/
│       │       ├── WelcomeScreen.kt
│       │       ├── ProcessingScreen.kt
│       │       ├── OverviewScreen.kt
│       │       ├── ManifestScreen.kt
│       │       ├── StructureScreen.kt
│       │       ├── SourceCodeScreen.kt
│       │       ├── AssetsScreen.kt
│       │       ├── NetworkScreen.kt
│       │       ├── PermissionsScreen.kt
│       │       ├── DependenciesScreen.kt
│       │       └── ReportScreen.kt
│       └── domain/
│           ├── models/                      # all data classes above
│           ├── pipeline/
│           │   ├── ApkProcessor.kt          # orchestrator
│           │   ├── ApkToolRunner.kt
│           │   ├── JadxRunner.kt
│           │   ├── ManifestParser.kt
│           │   ├── ResourceScanner.kt
│           │   ├── NetworkScanner.kt
│           │   ├── PermissionScanner.kt
│           │   └── SdkMatcher.kt
│           ├── export/
│           │   ├── PdfExporter.kt
│           │   └── MarkdownExporter.kt
│           └── db/
│               └── HistoryRepository.kt
├── shared/
│   └── src/commonMain/kotlin/
│       └── (shared models if needed later for V2 mobile companion)
└── resources/
    ├── tools/
    │   ├── apktool.jar
    │   └── dex-tools/
    └── sdk-registry.json                    # SDK fingerprint database
```

---

## 12. BUILD & DEPENDENCIES (build.gradle.kts)

```kotlin
commonMain.dependencies {
    implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
    implementation("com.arkivanov.decompose:decompose:2.2.2")
    implementation("com.arkivanov.decompose:extensions-compose:2.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("app.cash.sqldelight:runtime:2.0.2")
}

desktopMain.dependencies {
    implementation(compose.desktop.currentOs)
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("io.github.skylot:jadx-core:1.4.7")
    implementation("io.github.skylot:jadx-input-smali:1.4.7")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")
}
```

---

## 13. V2 PREPARATION NOTES (for AI agent awareness)

V1 output is intentionally structured to feed V2. Specifically:
- `AnalysisResult` will be serialized to JSON and sent to Claude API in V2
- `MarkdownExporter` output will be used as AI context document
- `NetworkModel` + `StructureModel` will feed the "flow reconstruction" prompt in V2
- History DB schema is forward-compatible — add `ai_analysis_json` column later
- Keep `sdk-registry.json` as an external file — V2 will enrich it with product descriptions

---

*End of V1 Technical Plan*
