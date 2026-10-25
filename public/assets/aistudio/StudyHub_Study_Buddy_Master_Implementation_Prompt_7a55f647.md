# STUDYHUB — STUDY BUDDY
## Complete Master Implementation Prompt for Google AI Studio / Gemini

Act as a principal Android engineer, senior product designer, UX architect, AI application architect, data engineer, security engineer, and QA lead.

Your mission is to implement **Study Buddy**, a deeply context-aware personal AI study companion inside my **existing StudyHub Android application**. This is not a new app, a generic chatbot, a static UI mockup, or a disconnected AI demo. Integrate it into the current codebase, using existing architecture, design system, navigation, local data, models, repositories, and coding patterns.

Study Buddy should feel like a premium study partner that understands the student's actual StudyHub context—syllabus, study plans, calendar, focus sessions, habits, goals, progress, and revision workload—and uses relevant information to provide personalized, actionable, grounded help.

**Core principle: context-aware, not merely conversation-aware.** Use real data when available, acknowledge missing information, and never invent subjects, progress, deadlines, habits, or history.

The feature is intended to become premium later. For now, build and test the actual experience using Google AI Studio and a Gemini model available under the user's current free-tier access. Keep the architecture provider-configurable so the model/provider can be changed later.

---

# 1. AUDIT THE EXISTING PROJECT BEFORE CHANGING IT

Inspect the codebase first and identify:
- Language, Android UI framework, architecture, navigation, dependency injection, async patterns, and testing stack.
- Existing theme system, colors, typography, spacing, shapes, icons, reusable components, and light/dark behavior.
- The five existing tabs: Home, Syllabus, Focus, Calendar, Tools.
- Existing syllabus/topic, calendar/plan, focus-session, Stats, goal, revision, and settings data sources where present.
- Existing persistence, entities, repositories, use cases, ViewModels/state holders, and model definitions.
- How user data is created, updated, persisted, and read.
- How full-screen destinations are opened from Home.

Do not assume a data model, repository, feature, or dependency exists until you verify it. Use the existing sources of truth. If a data source is absent, gracefully handle that absence instead of fabricating data or building a competing system. Do not delete, rename, or rewrite unrelated features.

## Non-negotiable preservation rules
- Keep the existing five bottom-navigation tabs exactly: **Home, Syllabus, Focus, Calendar, Tools**.
- **Do not add a sixth bottom-navigation tab.**
- Do not rebuild StudyHub or redesign unrelated screens.
- Do not create duplicate syllabus, Calendar, focus-history, Stats, revision, or planning systems.
- Preserve current behavior, stored data, and existing navigation.

---

# 2. PRODUCT DEFINITION

## What Study Buddy should do
Help students understand their current study situation, ask questions about syllabus topics, decide what to prioritize, plan realistically, interpret progress, recover from missed plans without guilt, prepare for exams using verified dates and syllabus data, understand study statistics, and turn advice into useful app actions when existing StudyHub features support them.

## What it must not be
- A generic chatbot with no access to relevant StudyHub context.
- A fake AI interface powered only by hardcoded replies.
- A replacement for existing StudyHub features.
- An autonomous system that silently changes Calendar or student data.
- A source of fabricated study hours, grades, deadlines, completed chapters, or history.
- A system that uploads the entire database or every conversation on every request.

---

# 3. UI PLACEMENT — OPTION A

Use **Home + Syllabus integration with a dedicated Study Buddy destination screen**. Keep the five-tab bottom navigation unchanged.

## A. Home entry card
Add a compact, polished **Study Buddy** card to the existing Home screen.

Requirements:
- Match StudyHub's established design system and component conventions.
- Make it premium and useful, not a large advertisement.
- Include the title “Study Buddy,” a concise description such as “Your personal study companion,” and a clear **Ask Study Buddy** / **Open Study Buddy** action.
- Show a personalized greeting or suggestion only when supported by actual data.
- Optionally show one or two real context chips, such as revision items due or the next recorded exam, only if those facts exist and are correctly calculated.
- If the student has little/no data, show a welcoming cold-start state and general example questions without pretending to know personal facts.
- Keep the card compact so it does not overwhelm Home or push important existing content too far down.
- Respect existing light/dark theme behavior and responsive layouts.

## B. Dedicated full-screen chat destination
Tapping the Home card opens a dedicated Study Buddy screen using the app's existing navigation pattern.

Include:

### Top bar
- Back navigation consistent with the app.
- Study Buddy title and restrained icon/identity.
- Conversation history/new-chat control where suitable.
- Optional context/privacy information if it fits cleanly.
- Avoid unnecessary controls and clutter.

### Personalized welcome
- Use a short grounded greeting when relevant data exists.
- Suitable context includes real upcoming Calendar plans, actual incomplete syllabus topics, actual due revision items, or recent focus sessions.
- If context is unavailable, use a neutral welcome such as “What would you like help studying today?”
- Never fabricate a personalized greeting.

### Suggested question chips
Show a small adaptive set, for example:
- “What should I study next?”
- “Help me plan today.”
- “Explain my progress.”
- “Help me revise a topic.”
- “I missed my study plan. What now?”
- “Help me prepare for my next exam.”

Prioritize suggestions based on real data when available. Otherwise use generic suggestions. Tapping a suggestion should behave predictably and must not accidentally send duplicate messages.

### Conversation area
- Clearly distinguish user and assistant messages.
- Make long responses readable with comfortable line spacing and sensible content width.
- Render supported Markdown safely if compatible with the existing stack.
- Use headings/lists/bold only where useful; avoid walls of text.
- Preserve stable message IDs and message order.
- Scroll to the newest message without repeatedly overriding the user's manual scrolling.
- Distinguish loading, streaming (only if genuinely supported), failure, retry, and completed states.
- Never expose raw JSON or internal prompts to the student.

### Message composer
- Fixed at the bottom and compatible with keyboard/system insets.
- Multi-line input that grows to a reasonable maximum.
- Clear send/loading/enabled states and keyboard send action where supported.
- Reject empty messages and prevent duplicate sends.
- Preserve the draft when a request fails.
- Support long text without breaking the screen.
- Provide accessible labels and touch targets.
- If appropriate, briefly explain that relevant StudyHub information may be used to answer.

### Message actions
- Add copy-response functionality if it fits existing patterns.
- Add retry for failed requests.
- Optional lightweight helpful/not-helpful feedback.
- Show action buttons only when they perform a real supported action.
- No decorative/inert buttons.

## C. Conversation history
Use a lightweight list, drawer, or existing navigation pattern that fits StudyHub.

- Persist conversations and messages using existing storage conventions.
- Store stable IDs, created/updated timestamps, title, and message order.
- Generate a short title from the first meaningful question or safe summary.
- Support starting a new conversation, reopening a previous one, and deleting with confirmation.
- Handle empty/loading/error states.
- Do not mix unrelated conversations.
- Do not send every previous conversation to Gemini by default.
- For long chats, use bounded recent turns or a concise summary.
- If cross-conversation memory is added, keep it grounded, inspectable, and clearable where feasible.

## D. Syllabus contextual entry
Where it fits naturally, add a restrained **Ask Study Buddy about this topic** action.

- Pass the selected topic/subject's verified ID and name into the conversation.
- Reuse the existing syllabus data; never create a duplicate record.
- Do not pretend to have read a textbook, notes, PDF, or course material unless its content is actually available.
- Avoid cluttering every syllabus row with multiple buttons; prefer an existing contextual/overflow action pattern.

Structure the feature so contextual entry points from Focus, Calendar, Stats, or revision can be added later, but do not redesign those screens now. These entry points must open the same Study Buddy experience, not separate chatbot implementations.

---

# 4. PREMIUM DESIGN QUALITY

The feature should feel modern, calm, focused, polished, and premium.

- Use StudyHub's current design system as the source of truth.
- Maintain clear hierarchy, alignment, spacing, readability, and restrained emphasis.
- Avoid excessive gradients, glows, shadows, nested cards, and decorative effects.
- Do not overuse emoji or use emoji as a replacement for the app's icon set.
- Avoid making every assistant message a huge card.
- Use subtle, purposeful motion only where it helps.
- Respect reduced-motion/accessibility settings where applicable.
- Ensure keyboard and system bars never cover the composer or latest message.
- Test small screens, long subject names, long responses, and large font settings.
- Support existing light/dark theme behavior.
- Avoid fake charts, fake statistics, or decorative data visualizations.
- Premium quality must come from detail, consistency, usability, and reliability—not effects alone.

---

# 5. GEMINI PROVIDER AND DEVELOPMENT SETUP

Use Google AI Studio with a Gemini model currently available under the user's free-tier access for development/testing.

- Verify current SDK/API compatibility, model identifier, request/response format, and provider requirements before implementing.
- Do not assume a model name or free quota will remain available forever.
- Centralize the provider/model configuration.
- Handle rate limits, quota exhaustion, network errors, timeouts, unavailable models, safety blocks, and malformed responses.
- Do not promise unlimited free usage or exact quota values.
- Do not silently switch to a paid model or service.
- Preserve conversation history and draft text if the provider fails.
- Keep the provider configurable so a different model or secure production provider can replace Gemini without rewriting UI/domain logic.
- Use a fake provider for deterministic tests.

## Provider boundary
Use the existing architecture. Add only the abstractions needed for maintainability, such as an AI provider interface, Gemini implementation, request/response models, context builder, send-message use case/repository, error mapper, and configuration. Avoid unnecessary boilerplate.

## API key security — mandatory
- **Never hardcode a real Gemini API key in Android source, resources, committed BuildConfig, or the APK.**
- Never commit secrets to version control.
- Never log API keys, authorization headers, sensitive prompts, or student data.
- A key in a client APK can be extracted; obfuscation does not make it secret.
- For a local prototype, use a clearly documented development-only configuration consistent with current provider guidance. Do not describe client-side storage as production-secure.
- Design the provider boundary so production can use a secure backend/proxy for credentials, authentication, quotas, and access control.
- Do not introduce paid backend infrastructure unless it already exists or is explicitly available.
- If a safe direct integration is not possible in this environment, make the limitation explicit and provide a functional fake provider for UI/flow testing rather than exposing a production secret.
- Never put credentials in prompts, logs, analytics, crash reports, or conversation storage.

Centralize model ID, generation settings, request timeout, retry policy, context limits, and provider selection. Use bounded retries only for transient errors; do not retry quota errors in a tight loop.

---

# 6. THE CONTEXT ENGINE — THE CORE DIFFERENTIATOR

The most important part is not the chat UI; it is giving the AI the right, verified StudyHub information for each question.

## A. Read real data from existing repositories
Use relevant categories only when they exist:

1. **Study profile:** actual syllabus subjects, explicit saved goals/preferences, and configured availability if stored.
2. **Syllabus/progress:** actual subjects, topics, completion states, stored progress values and their definitions, and due revision status where available. Never infer mastery from completion alone.
3. **Calendar:** real upcoming tasks/plans, actual dates/times/status, and conflicts only where supported by data. A planned task is not a completed task.
4. **Focus history:** actual completed sessions, durations, timestamps, linked subject/topic when available. Follow existing definitions for pauses, cancelled sessions, and timer recovery.
5. **Stats:** reuse existing calculated statistics and the same definitions as the Stats screen. Include the relevant time range; avoid contradictory duplicate calculations.
6. **Revision/planner:** actual due/overdue items, suggestions, and approved plans if implemented. Distinguish a suggestion from a task already added to Calendar.
7. **Exams/deadlines:** only stored dates. If no date is recorded, say so.

Do not assume these sources exist. Verify them and degrade gracefully.

## B. Relevance-based retrieval
Do not send the entire database or all categories every time.

For each request:
1. Classify the intent into a small set such as topic/syllabus, planning, stats/progress, focus habits, revision, exam preparation, or general study help.
2. Select only relevant data categories.
3. Fetch current facts from repositories rather than trusting stale chat context.
4. Include enough information to answer, but enforce context/token limits.
5. Apply deterministic selection and truncation rules.
6. Include timestamps or “last updated” data when materially relevant.
7. Represent unavailable data as unavailable, not as a guessed value.
8. Distinguish “no data exists” from “data retrieval failed.”
9. Exclude unrelated personal data.
10. Avoid repeatedly sending large static payloads when a compact current representation is sufficient.

Use deterministic local routing/retrieval for known StudyHub facts. The model should reason about selected facts, not invent or reconstruct them.

## C. Structured context envelope
Use typed models/DTOs where appropriate. Keep these concepts separate:
- `verified_facts`: retrieved StudyHub facts.
- `data_availability`: available, unavailable, or failed-to-load categories.
- `time_reference`: current date/time and timezone source when available.
- `conversation_context`: bounded recent turns and optional summary.
- `entry_point_context`: selected topic/task/session that opened the chat.
- `allowed_actions`: validated operations the model may request.
- `context_freshness`: when relevant facts were fetched.

Avoid brittle string concatenation spread throughout the app.

## D. Truthfulness rules
The assistant must:
- Never invent the student's name, subjects, chapters, hours, grades, deadlines, goals, streaks, preferences, or behavior.
- Never claim it inspected a textbook/file unless the content was actually provided.
- Never claim an action succeeded until the app confirms success.
- Never present estimates as exact measurements.
- Ask a focused question when essential information is missing.
- Distinguish unavailable, not recorded, failed to load, and not applicable.
- Explain the evidence behind personalized advice when useful.
- Avoid claiming that correlation proves why a student performed a certain way.

Example:
- Good: “Your Calendar currently shows two study plans for today.”
- Bad: “You always struggle in the afternoon,” unless there is reliable, relevant evidence; even then, phrase cautiously.

## E. Freshness and performance
- Refresh relevant facts for each new question.
- Do not use stale Calendar/Syllabus values after they change.
- Keep context retrieval off the main thread.
- Handle concurrent data changes and repository failures.
- Avoid unnecessary repeated queries.

---

# 7. CONVERSATION MEMORY

## Short-term memory
- Send bounded recent turns or a concise rolling summary.
- Preserve important clarifications and the meaning of the newest question.
- Never mix conversations.
- Keep turns in correct order.
- Summarize/truncate long chats safely without silently losing the newest question.
- Never send the entire conversation archive by default.

## Optional longer-term personalization
If cross-conversation memory is implemented:
- Prefer explicit preferences and user-approved summaries.
- Separate saved preferences, verified StudyHub facts, and AI-generated hypotheses.
- Do not form permanent personality judgments from one message.
- Do not infer sensitive traits.
- Do not save sensitive information as a profile by default.
- Do not present an AI-generated memory as a verified fact.
- Offer a clear way to review, clear, or disable optional personalization if feasible.
- If this adds excessive risk/scope, ship robust per-conversation context first and leave a clean extension point.

## Persistence
- Persist user messages and completed assistant replies reliably.
- Represent pending, completed, and failed states clearly.
- Do not save internal prompts/secrets as user-visible messages.
- Prevent repeated taps/retries from creating duplicate messages.
- Preserve drafts where feasible after failures or process recreation.

---

# 8. SAFE APP ACTIONS AND TOOL CALLING

Where supported by existing features, Study Buddy may request a validated app action rather than merely describe it.

Possible actions, only if the underlying feature exists:
- Open a syllabus topic.
- Open a Calendar plan.
- Open a Stats section.
- Start Focus with a selected subject/topic.
- Prepare a draft study plan.
- Prepare a revision suggestion.
- Navigate to a relevant screen.

Rules:
1. Start with read-only context and navigation actions.
2. Never let generated text directly mutate the database.
3. Use typed action requests and an allowlist of action names.
4. Validate every ID against actual local data.
5. Validate duration, date/time, subject/topic, and conflicts before writing.
6. Any create/update/reschedule/delete operation requires a preview and explicit user confirmation.
7. Never silently change Calendar plans.
8. Confirm success only after the operation returns success.
9. Make operations idempotent where possible to avoid duplicates.
10. Reject malformed or unauthorized action arguments safely.
11. Never allow arbitrary code, SQL, shell commands, file operations, or unrestricted URLs.
12. Keep navigation actions separate from data-changing actions.
13. Do not implement every possible tool in v1. Implement a small, dependable set that truly works.

If an action is unsupported, explain the limitation; do not show a fake button.

---

# 9. RESPONSE QUALITY AND STUDY COACHING

Study Buddy should be a practical study companion, not a verbose motivational bot.

- Friendly, respectful, encouraging, and nonjudgmental.
- Match the student's language where feasible: if they write in Bangla/Banglish, respond naturally in Bangla/Banglish; if English, respond in English unless asked otherwise.
- Use clear explanations and actionable next steps.
- Keep simple answers short; organize complex answers into useful sections.
- Explain why advice fits the verified context.
- Never shame the student for missed plans.
- Never guarantee grades, exam outcomes, or learning results.
- Do not pretend certainty.

### Expected behavior examples

**“What should I study next?”**
Check actual Calendar plans, due revisions, incomplete topics, and priorities if available. Give a small reasoned recommendation. If essential data is missing, ask briefly or label general advice as general.

**“Plan my day.”**
Use real tasks, deadlines, available time, and explicit constraints. If availability is unknown, ask instead of inventing a schedule. Mark output as a proposal; require confirmation before adding/changing Calendar plans.

**“How am I doing?”**
Use the same Stats definitions as StudyHub and specify the time range. Do not invent causes for trends. Offer a practical next step.

**“I missed my plan.”**
Respond without judgment. Check relevant actual tasks/time only if available. Offer a realistic recovery option; do not silently reschedule.

**“Help me revise this topic.”**
Use the selected/verified topic. Be clear whether actual notes/textbook content is available. Do not fabricate course-specific material.

**“When is my next exam?”**
Use only a stored exam date. If none exists, say no exam date is currently recorded.

**General academic questions**
Use the model's general capabilities while separating general knowledge from claims about the student's StudyHub data.

---

# 10. ERROR HANDLING AND RESILIENCE

Handle at minimum:
- Offline/network loss.
- Provider timeout.
- Gemini rate limit/quota exhaustion.
- Model/provider unavailable.
- Configuration/authentication failure.
- Safety refusal.
- Empty or malformed response.
- Context retrieval failure/partial context.
- App backgrounding or process recreation during a request.
- Repeated send taps.
- Empty/long input and long output.
- Conversation persistence failure.
- Deletion/navigation during pending requests.
- Keyboard open/close, screen resize, and rotation where supported.

Requirements:
- Human-readable errors, not raw stack traces/API payloads.
- Preserve the student's draft on failure.
- Provide retry where appropriate.
- Do not retry permanent failures endlessly.
- Do not create duplicate messages/actions.
- Do not label failed retrieval as “no data.”
- Explain when context could not be loaded and allow retry.
- Never run network/heavy database work on the main thread.

---

# 11. PRIVACY AND DATA MINIMIZATION

Treat study records and conversations as private user data.

- Send only context necessary for the current question.
- Do not upload the whole database, full syllabus, complete focus history, or all conversations by default.
- Exclude unrelated identifiers and sensitive fields.
- Do not log message contents or context by default.
- Do not expose secrets in logs, crash reports, analytics, screenshots, or repository files.
- Follow current provider security recommendations.
- Review the current Google AI Studio/Gemini free-tier data handling and usage terms before testing with real student data; do not assume development-tier data has production privacy guarantees.
- Do not represent a prototype as production-ready.
- Make clear in code/docs which context is sent to the provider and when.
- Provide conversation deletion; delete associated local messages/summaries where applicable.
- Avoid saving AI-derived profile claims unless the user explicitly chooses to save them.
- If accounts/sync exist, enforce authorization server-side and never rely only on client-side visibility.

---

# 12. PREMIUM READINESS — NO PAYMENTS YET

Study Buddy will eventually be a premium subscription feature; the existing StudyHub features remain free.

For this development stage:
- Do not integrate billing/payment SDKs, checkout, purchase flows, live entitlement verification, or payment screens.
- Do not charge users or imply a payment has occurred.
- Do not gate or remove existing free features.
- Keep the Study Buddy feature boundary clean for future entitlement checks.
- If needed, use a clearly marked development-only access mechanism; never pretend it is real subscription verification.
- Avoid hardcoded premium booleans scattered throughout the app.
- A small injectable feature-access interface is acceptable if it fits the architecture.
- Production entitlement enforcement must be secure and cannot trust a client-only flag.
- Do not build a fake paywall unless requested later.

The goal now is the real Study Buddy experience, not payment infrastructure.

---

# 13. ANDROID QUALITY, ACCESSIBILITY, AND PERFORMANCE

- Follow existing lifecycle and architecture patterns.
- Use lifecycle-aware state collection and cancellation.
- Avoid leaking Activity/View/lifecycle references.
- Keep state predictable across recomposition/re-rendering.
- Use stable IDs.
- Preserve state across configuration changes and persist important data across process recreation.
- Avoid main-thread I/O.
- Bound context and response sizes.
- Avoid redundant queries and expensive recompositions.
- Provide content descriptions and accessible labels.
- Ensure contrast, touch targets, font scaling, long text, and keyboard insets are usable.
- Respect system bars and navigation gestures.
- Avoid unnecessary dependencies; check compatibility.
- Use structured concurrency and cancel obsolete requests when appropriate.
- Leaving a screen must not accidentally resend a request.
- Do not persist a transient loading state as a completed answer.

---

# 14. TESTING REQUIREMENTS

Use the project's current testing stack.

## Unit tests
Test:
- Intent classification/context selection.
- Mapping of actual StudyHub facts.
- Missing data versus failed retrieval.
- Context limits and truncation.
- Conversation ordering and isolation.
- Context/prompt construction.
- Provider error mapping and retry policy.
- Duplicate-send prevention.
- Malformed model responses.
- Tool/action argument validation and confirmation requirements.
- Date/time handling where relevant.
- No fake data in empty states.
- Provider abstraction using a fake implementation.

## Repository/integration tests
Where supported, test:
- Conversation create/read/delete.
- Message persistence and ordering.
- Success and failure/retry without duplicates.
- Deleting related summaries.
- Reading real Syllabus, Calendar, Focus, Stats, and revision data through existing repositories.
- Home/Syllabus navigation and context handoff.

## UI/manual tests
Verify:
- Home card placement and compactness.
- Exactly five bottom-navigation tabs.
- Card opens the dedicated screen.
- Welcome state, suggestions, history, composer, and retry work.
- Keyboard never covers the composer/latest message.
- Long text and large fonts work.
- Empty, loading, error, and quota-exhausted states are understandable.
- Syllabus topic entry passes the correct topic.
- Theme matches StudyHub.
- Screen recreation does not corrupt or duplicate messages.
- No inert controls.
- No unrelated feature regression.

Use a fake AI provider for deterministic tests; automated tests must not require a live Gemini call.

---

# 15. IMPLEMENTATION ORDER

1. **Audit:** map the existing architecture, sources of truth, navigation, design system, and tests.
2. **Domain/persistence:** implement the minimum reliable conversation/message models and repository integration using existing conventions.
3. **Context engine:** retrieve relevant verified facts from existing repositories; represent missing/failed data correctly.
4. **Provider:** add a configurable provider interface, Gemini development implementation, bounded requests, and error mapping. Protect credentials and prepare a secure production provider path.
5. **UI:** implement Home card, dedicated chat, suggestions, composer, message rendering, history, and loading/error/retry states.
6. **Contextual entry/actions:** connect the Syllabus topic entry and a small number of real, validated actions where supported. Require confirmation for writes.
7. **Testing/polish:** run available tests/build checks, fix issues, verify navigation and existing features remain intact.

Do not stop at a visual prototype. Connect the UI to real app state and the configured provider—or a functional test fake.

---

# 16. ACCEPTANCE CRITERIA — DEFINITION OF DONE

## UI/navigation
- [ ] Existing five bottom-navigation tabs are unchanged.
- [ ] A compact Study Buddy card exists on Home.
- [ ] The card opens a dedicated full-screen destination.
- [ ] UI matches StudyHub's design system.
- [ ] Composer, keyboard, scrolling, loading, error, and empty states work.
- [ ] Conversation history supports create/reopen/delete as implemented.
- [ ] Syllabus contextual entry passes the correct topic.
- [ ] No visible control is fake or inert.

## AI/context
- [ ] Gemini can be tested in development when configuration is valid.
- [ ] Provider can be replaced without rewriting the UI.
- [ ] Relevant context comes from existing sources of truth.
- [ ] Context is relevant and bounded.
- [ ] Missing data is distinguished from retrieval failures.
- [ ] No fabricated personal study facts.
- [ ] General academic help works without personal context.
- [ ] Conversation context is bounded and isolated.
- [ ] Quota/provider/network errors are handled gracefully.

## Security/privacy
- [ ] No real API key is hardcoded or committed.
- [ ] Sensitive prompts/student data are not logged by default.
- [ ] Only relevant context is sent.
- [ ] Model output cannot execute arbitrary code or directly mutate data.
- [ ] Data-changing actions require preview and explicit confirmation.
- [ ] Future premium access can be integrated without payment flows now.

## Quality
- [ ] App builds successfully.
- [ ] Relevant automated tests pass.
- [ ] Fake-provider tests work deterministically.
- [ ] Existing features and data remain intact.
- [ ] No known duplicate-send, state-loss, or obvious crash issue remains.

Do not mark anything complete unless it was actually implemented and verified.

---

# 17. FINAL IMPLEMENTATION REPORT

After implementation, provide:
1. What was implemented: UI, navigation, persistence, context engine, provider, and actions.
2. Exact files created/modified and their purposes.
3. Which existing repositories/models provide Syllabus, Calendar, Focus, Stats, and revision context.
4. Exact local Gemini configuration steps, without revealing or inventing secrets.
5. Security status: what is acceptable for development, what requires a secure backend before production, and provider/data-use terms that need verification.
6. Exact build/test commands run and their actual results.
7. Known limitations: anything not implemented, unavailable, or unverified.
8. Production next steps: secure provider/backend, privacy review, and real subscription entitlement integration when that phase begins.

Never claim tests/build passed if not run. Never claim an API call works if not verified. Never claim real-data integration while placeholders remain.

---

# FINAL EXECUTION INSTRUCTION

Inspect the existing StudyHub codebase and implement Study Buddy end-to-end within these constraints.

Prioritize:
1. Correct integration with the existing app.
2. Truthful, relevant use of real StudyHub context.
3. Reliable persistence and error handling.
4. Secure, replaceable Gemini integration for development.
5. Premium, polished, accessible UI.
6. Useful, safe contextual actions.
7. Tests and regression protection.

**Do not add a sixth bottom-navigation tab. Do not rebuild StudyHub. Do not fabricate student data. Do not implement payment flows now. Do not hardcode production secrets.**

Build a real, maintainable Study Buddy foundation that can be tested today and responsibly evolved into a premium subscription feature later.
