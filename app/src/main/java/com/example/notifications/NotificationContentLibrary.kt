package com.example.notifications

enum class NotificationCategory {
    FUNNY_ROASTS,
    DISTRACTION_NUDGES,
    MOTIVATION,
    SYLLABUS_CHAPTERS,
    TASKS_PLANNING,
    EXAM_ALERTS,
    FOCUS_BREAKS,
    STREAKS_PROGRESS,
    HEALTHY_HABITS,
    GENTLE_COMEBACK,
    NOTES_LEARNING,
    EXAM_STRATEGY,
    REVISION_SCIENCE,
    CHAPTER_MASTERY,
    ADMISSION_READINESS,
    STUDY_ENVIRONMENT,
    SUBJECT_THINKING,
    LEARNING_FACTS,
    RESILIENCE
}

enum class NotificationActionRoute {
    SYLLABUS,
    FOCUS,
    TASKS,
    NOTES,
    GOALS,
    CALENDAR,
    SUBJECTS,
    PROGRESS,
    DISMISS
}

data class NotificationTemplate(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val body: String,
    val tone: String = "HUMOROUS",
    val actionRoute: NotificationActionRoute = NotificationActionRoute.FOCUS,
    val priority: Int = 2, // 1: Low, 2: Medium, 3: High
    val requiresExamData: Boolean = false,
    val requiresTasks: Boolean = false,
    val requiresChapters: Boolean = false,
    val requiresNotes: Boolean = false
)

object NotificationContentLibrary {

    private val BASE_NOTIFICATIONS: List<NotificationTemplate> = listOf(
        // Category A — Funny Study Roasts (N001 - N015)
        NotificationTemplate(
            id = "N001",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Bhai, syllabus ki tor ex? 💀",
            body = "Eto din dhore avoid kortesis! Ajke ekta chapter-er sathe settle kore fel.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2,
            requiresChapters = true
        ),
        NotificationTemplate(
            id = "N002",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Book khule allergy hoy naki? 📚",
            body = "Matro 10 minute por. Tarpor chaile nijeke genius vabish.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N003",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Tor future loading... 2% 🔄",
            body = "Ektu porashona korle progress bar-tao move korbe, boss.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N004",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Bhai, bed ki exam dibe? 🛏️",
            body = "Aram kore shuye thakle marks ashbe na. Uth, ekta task finish kor.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N005",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Motivation-er jonno wait kortesis? 😂",
            body = "Motivation vacation-e. Tui 10 minute start kor, mood pore ashbe.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N006",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Tor pen-er retirement hoye gelo ✍️",
            body = "Bechara koto din kichu lekhe na. Ajke take ekta kaj de.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N007",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Chapter-ra tor naam-e missing diary korse 🕵️",
            body = "Ekta pending chapter open kor. Oder bari fire aste de.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2,
            requiresChapters = true
        ),
        NotificationTemplate(
            id = "N008",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Scroll korte PhD hoye geche 📱",
            body = "Ebar 15 minute porashonay doctorate-er first step ne.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N009",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Bhai, brain airplane mode-e? ✈️",
            body = "Choto ekta study session diye connection abar on kor.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N010",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Exam-er sathe situationship cholche? 😭",
            body = "Serious commitment-er time hoyeche. Ajker study goal-ta finish kor.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N011",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Tor chair tor upor disappointed 🪑",
            body = "Koto khon dhore khali pore ache! 15 minute boshe por.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N012",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Calculator-o bored hoye geche 🧮",
            body = "Math-er ekta problem solve kor. Calculator-ke kaj-e ferot de.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SUBJECTS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N013",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Future you court case korbe ⚖️",
            body = "“Ajke porte parto!” — ei evidence-ta ajkei delete kore de.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N014",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Bhai, excuse-er stock sesh? 😂",
            body = "Excuse na, ajke ekta choto task complete korar screenshot-worthy moment chai.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N015",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Bookshelf-er sathe long-distance relationship 📚",
            body = "Dure theke bhalobasha enough na. Ekta boi khule bos.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),

        // Category B — Social Media Distraction Nudges (N016 - N030)
        NotificationTemplate(
            id = "N016",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Scroll-er rabbit hole-e dhukchis? 🐇",
            body = "Ekhon back button chap. 10 minute Focus challenge accept kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N017",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Reel sesh? Naki next 47 ta baki? 💀",
            body = "Ektu break theke abar porashonay fire ay. Ekta choto task choose kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N018",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Algorithm tor topper hote dibe na 😂",
            body = "Feed tor attention chai. Tui nijer goal-ke 15 minute de.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N019",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Notification-er pichone douracchis? 🏃",
            body = "Ebar ekta notification-er bodole ekta chapter finish korar chase de.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N020",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Phone-er sathe break-up korbi? 📱💔",
            body = "Permanent na. Matro 15 minute dure rakh, tarpor abar dekha hobe.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N021",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Internet ache, concentration kothay? 😂",
            body = "Ekta distraction-free session start kor. Choto win-o win.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N022",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Feed-er episode sesh hoy na keno? 🎬",
            body = "Ebar nijer syllabus-er next episode play kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N023",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Dopamine-er delivery eshe geche 📦",
            body = "Quick scroll-er bodole ekta task complete kore satisfying tick ne.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N024",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Phone-ke ektu chuti de 😴",
            body = "Screen down, book up. 10 minute holeo focus kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N025",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Online status green, syllabus status? 💀",
            body = "Syllabus-er progress-o green korar jonno ekta pending chapter dhor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N026",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "“Just 5 minutes” cinematic universe 😂",
            body = "Sei 5 minute-er sequel bondho kore ekta real study session start kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N027",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Scroll-er streak bhangbi na? 🔥",
            body = "Scroll streak na, study streak build korar challenge ne.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N028",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Feed tor attendance ney na 📱",
            body = "Tor goal ney. Ajke nijer goal-er kache attendance de.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N029",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Brain-er tab onek khola? 🧠",
            body = "Ekta tab rakh—ekhon sudhu ekta task. Baki pore.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N030",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Offline hero howar time 🦸",
            body = "Phone silent kore 15 minute por. Nijer attention-er boss tui.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),

        // Category C — Motivation and Comeback (N031 - N045)
        NotificationTemplate(
            id = "N031",
            category = NotificationCategory.MOTIVATION,
            title = "Choto start, boro comeback 💪",
            body = "Ajke 10 minute diye shuru kor. Perfect day-er jonno wait korish na.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N032",
            category = NotificationCategory.MOTIVATION,
            title = "Tor progress zero na 🌱",
            body = "Ekhane fire asha-o progress. Ebar ekta choto task choose kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N033",
            category = NotificationCategory.MOTIVATION,
            title = "Slow progress-o progress 🐢",
            body = "Speed niye tension na. Consistency-r ekta choto step ne.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N034",
            category = NotificationCategory.MOTIVATION,
            title = "Future you high-five pathaise ✋",
            body = "Ajker ekta completed task future-er pressure ektu komabe.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N035",
            category = NotificationCategory.MOTIVATION,
            title = "Comeback arc start korbi? 🎬",
            body = "Hero-ra ek dine shob pare na. Ajke ekta chapter finish kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N036",
            category = NotificationCategory.MOTIVATION,
            title = "Tor effort count kore ❤️",
            body = "Result ekhono na ashleo effort meaningful. Ajke abar ektu try kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N037",
            category = NotificationCategory.MOTIVATION,
            title = "Ekta tick mark-er power ☑️",
            body = "Ekta task complete kor. Momentum nijer moto build hote de.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N038",
            category = NotificationCategory.MOTIVATION,
            title = "Perfect routine lagbe na ✨",
            body = "Ekhon je time ache, oi time-e ekta choto session kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N039",
            category = NotificationCategory.MOTIVATION,
            title = "Restart button tor kachei 🔁",
            body = "Agertar din jemon-i jak, ekhon theke abar start kora jay.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N040",
            category = NotificationCategory.MOTIVATION,
            title = "Future topper loading... 🔋",
            body = "Battery bachanor moto focus-o bachate hoy. 15 minute nijeke de.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N041",
            category = NotificationCategory.MOTIVATION,
            title = "Ajker effort, kalke confidence 🌟",
            body = "Ekta topic revise kor. Nijer upor trust build kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N042",
            category = NotificationCategory.MOTIVATION,
            title = "Tui parbi, but ekta condition 😎",
            body = "Shuru korte hobe. Ajker easiest task-ta finish kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N043",
            category = NotificationCategory.MOTIVATION,
            title = "Choto win celebrate kor 🎉",
            body = "Ekta goal complete korle nijeke credit dite bhulish na.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N044",
            category = NotificationCategory.MOTIVATION,
            title = "Pressure-ke plan-e convert kor 🗺️",
            body = "Chinta komate ajker kaj-gulo choto step-e vag kore ne.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N045",
            category = NotificationCategory.MOTIVATION,
            title = "Ekdom late hoye jayni ❤️",
            body = "Nijeke blame na kore ekta realistic next step ne.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),

        // Category D — Syllabus and Chapter Reminders (N046 - N060)
        NotificationTemplate(
            id = "N046",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Pending chapter-er roll call 📋",
            body = "Syllabus khule dekhe ne kon chapter-ta next-e complete kora jay.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N047",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Ekta chapter, ekta victory 🏆",
            body = "Ajke ekta chapter-er meaningful portion shesh korar target ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N048",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Revision-er appointment ache 📚",
            body = "Age pora topic-ta ektu revise kor. Memory-ke refresh de.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N049",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Syllabus map check kor 🗺️",
            body = "Kon topic complete, kon topic baki—ajke progress update kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N050",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Ekta topic ekhono waiting room-e ⏳",
            body = "Pending topic-ta open kore 10 minute porar challenge ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N051",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Notes-er sathe reunion 💌",
            body = "Purono notes khule dekhe ne ki revise kora dorkar.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2,
            requiresNotes = true
        ),
        NotificationTemplate(
            id = "N052",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Chapter boss fight incoming 🎮",
            body = "Tough topic-ta choto section-e vag kore start kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N053",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Ajke ki shikhlis? 🧠",
            body = "Pora topic theke 3 ta key point recall korar try de.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N054",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Subject rotation time 🔄",
            body = "Routine-e thakle next subject-e shift korar somoy hoyeche.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SUBJECTS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N055",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Progress update, boss! 📈",
            body = "Ajker pora chapter-er progress save kora hoyeche kina check kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N056",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Difficult topic-ke ghost koris na 👻",
            body = "Topic-ta niye stuck hole notes dekhe ekta choto step solve kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N057",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Revision-er mini mission 🎯",
            body = "Ajke ekta purono topic theke nijeke 5 ta question kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N058",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Chapter finish line kothay? 🏁",
            body = "Baki section-gulo check kore next realistic milestone set kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N059",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Subject-er sathe check-in 📝",
            body = "Ajker subject goal-ta ekhono baki thakle ekta manageable part finish kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SUBJECTS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N060",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Memory refresh, not factory reset 😂",
            body = "Pora topic-ta abar recall kor. Bar bar revise korle retention-e help hote pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),

        // Category E — Tasks and Planning (N061 - N072)
        NotificationTemplate(
            id = "N061",
            category = NotificationCategory.TASKS_PLANNING,
            title = "To-do list tor dike takiye ache 👀",
            body = "Ajker task-gulo dekhe easiest meaningful task-ta diye shuru kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2,
            requiresTasks = true
        ),
        NotificationTemplate(
            id = "N062",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Plan chara pora = GPS chara trip 🗺️",
            body = "Ajker jonno ekta clear study target set kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N063",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Task choto kor, excuse na 😂",
            body = "Boro kaj-ke 15-minute step-e vag kore start kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N064",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Calendar-er sathe peace treaty 🤝",
            body = "Upcoming deadline check kore realistic study slot plan kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N065",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ajker mission select kor 🎯",
            body = "Ekta priority task choose kor. Multitasking-er circus lagbe na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N066",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Deadline-er sathe eye contact 👀",
            body = "Kachakachi deadline thakle plan-ta ekbar review kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N067",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Task complete = tiny dopamine 🎉",
            body = "Kono task shesh kore thakle status update kore nijer progress track kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N068",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Routine update kora hoyeche? 🗓️",
            body = "Plan change hole calendar-o update kor, future tui confused hobe na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N069",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ajker top priority konta? 🥇",
            body = "Shob kichu ekshathe na. Ekhon shobcheye important ekta kaj choose kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N070",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Choto deadline reminder ⏰",
            body = "Tor saved plan-e kono task due thakle ekhon progress check kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N071",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Task-er naam ta clear kor ✍️",
            body = "“Porbo” na likhe specific topic ba chapter set kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N072",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Plan B-o ekta plan 😎",
            body = "Time kom hole target choto kor, puro din-ta cancel koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),

        // Category F — Exam and Deadline Alerts (N073 - N082) [requiresExamData = true]
        NotificationTemplate(
            id = "N073",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Exam countdown check! ⏳",
            body = "Tor saved exam date check kor. Baki preparation-er plan-ta realistic rakh.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N074",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Deadline kachakachi, captain! 🚦",
            body = "Tor saved deadline-er remaining kaj-gulo review kor.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N075",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Exam plan-er health check 🩺",
            body = "Exam-er age kon subject ba topic-e beshi time lagbe, plan kore ne.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N076",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Admit-card mission check 🎫",
            body = "Relevant exam-er official notice o required document status check kor, jodi applicable hoy.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N077",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Revision window miss koris na 📖",
            body = "Exam date-er sathe match kore revision slot rakhte bhulish na.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N078",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Mock deadline, real discipline 🎯",
            body = "Tor saved study plan-e deadline thakle seta review kore next step ne.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N079",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Exam prep-er mini audit 🧾",
            body = "Syllabus progress dekhe remaining topics-er priority set kor.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N080",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Important date check kore ne 📅",
            body = "Saved exam ba admission deadline thakle date-ta verify kore plan update kor.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N081",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Preparation-er gap khuje ber kor 🔎",
            body = "Kon topic-e confidence kom, seta identify kore ekta revision task set kor.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 3,
            requiresExamData = true
        ),
        NotificationTemplate(
            id = "N082",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Deadline alert, drama na 🚦",
            body = "Date verified thakle remaining preparation-er ekta practical step ne.",
            tone = "ALERT",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 3,
            requiresExamData = true
        ),

        // Category G — Focus Sessions and Breaks (N083 - N092)
        NotificationTemplate(
            id = "N083",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "15-minute boss mode 😎",
            body = "Ekta choto distraction-free session start kor. Shudhu ekta task.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N084",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Focus mode: activated? 🎮",
            body = "Session start korar por unnecessary app switching komanor try kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N085",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Ekta task, full attention 🎯",
            body = "Ekhon multitasking na kore ekta kaj-e focus kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N086",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Brain-o break chai 🧠",
            body = "Long study session-er por short break ne, pani kha, chokh relax kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N087",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Break sesh hole comeback 🔔",
            body = "Break timer sesh hole next study block-e fire ay, jodi ready thakis.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N088",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Focus session-er tiny victory 🏆",
            body = "Session complete hole nijer effort acknowledge kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N089",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Desk setup ready? 🪑",
            body = "Pani, notes, pen ready kore distraction komiye start kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N090",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Focus-er age mini checklist ☑️",
            body = "Ekta task, proyojonio materials, ar ekta realistic timer—done!",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N091",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Chokh-keo ektu bhalobash ❤️",
            body = "Screen use korle majhe majhe dure takao, blink koro, ar comfort check koro.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N092",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Session sesh? Review-o important 📝",
            body = "Ki shikhlis, next ki korbi—duita line note kore rakh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),

        // Category H — Streaks and Progress (N093 - N100)
        NotificationTemplate(
            id = "N093",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Nijer progress dekhe ne 📈",
            body = "Study history check kor. Choto improvement-o valuable.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.PROGRESS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N094",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Streak-er jonno pressure na ❤️",
            body = "Gap hoye geleo problem nei. Ajke abar nijer moto shuru kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N095",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Ajker effort save kor ☑️",
            body = "Session ba task complete kore thakle progress update kore ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.PROGRESS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N096",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Level up-er time 🎮",
            body = "Ajker goal-er ekta step finish korle nijer progress aro clear hobe.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N097",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Tui koto dur eshechis dekhe ne 🌱",
            body = "Purono progress-er sathe compare kore next realistic target set kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.PROGRESS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N098",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Consistency-er small flex 😎",
            body = "Ajke ekta planned study action complete kor. Nijer effort count kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N099",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Goal check, boss! 🎯",
            body = "Goal realistic ache kina review kor, proyojone adjust kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.GOALS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N100",
            category = NotificationCategory.STREAKS_PROGRESS,
            title = "Achievement unlock-er age effort 🔓",
            body = "Badge-er cheye learning important. Ajke ekta meaningful task kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.PROGRESS,
            priority = 2
        ),

        // Category I — Healthy Study Habits (N101 - N106)
        NotificationTemplate(
            id = "N101",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Pani kheye ne, scholar 💧",
            body = "Onekkhon porle ektu pani kha ar nijer comfort check kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.DISMISS,
            priority = 1
        ),
        NotificationTemplate(
            id = "N102",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Stretch break, boss 🧘",
            body = "Onekkhon boshe thakle ektu dariye stretch kor, jodi possible hoy.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.DISMISS,
            priority = 1
        ),
        NotificationTemplate(
            id = "N103",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Sleep is not a side quest 😴",
            body = "Ghum-er time sacrifice na kore realistic study plan rakh.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 1
        ),
        NotificationTemplate(
            id = "N104",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Brain-er maintenance time 🧠",
            body = "Break, sleep, food, ar manageable study block—balance-o important.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.DISMISS,
            priority = 1
        ),
        NotificationTemplate(
            id = "N105",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Marathon na, smart session 🏃",
            body = "Kaj-ke choto block-e vag kor. Nijer energy onujayi pace rakh.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 1
        ),
        NotificationTemplate(
            id = "N106",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Desk theke mini reset 🌿",
            body = "Onekkhon porle short break niye abar comfortable hoye bos.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.DISMISS,
            priority = 1
        ),

        // Category J — Gentle Comeback and Inactivity (N107 - N112)
        NotificationTemplate(
            id = "N107",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "Hey, abar dekha holo ❤️",
            body = "Kichudin gap holeo shob sesh na. Ajke ekta choto step ne.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N108",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "No guilt, just next step 🌱",
            body = "Agertar din niye nijeke blame na kore ekhon theke shuru kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N109",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "Study buddy ekhanei ache 🤝",
            body = "Ready hole ekta topic choose kor. Choto kore start korlei hobe.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N110",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "Comeback-er jonno permission lage na 🔁",
            body = "Ajkei abar shuru kora jay. Ekta easy task choose kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N111",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "Routine ektu off track? 🗺️",
            body = "Nijer current schedule dekhe ekta manageable study slot set kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N112",
            category = NotificationCategory.GENTLE_COMEBACK,
            title = "One step is enough for now ❤️",
            body = "Ajke shob finish korte hobe na. Ekta meaningful step ne.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),

        // Category K — Notes and Learning (N113 - N120)
        NotificationTemplate(
            id = "N113",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Notes-er makeover time ✍️",
            body = "Ajker important concept-ta nijer vashay note kore rakh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N114",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Nijeke teacher baniye de 🎓",
            body = "Pora concept-ta nijeke ba imaginary student-ke explain korar try kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N115",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Active recall challenge 🧠",
            body = "Notes na dekhe 3 ta point mone korar try kor, tarpor verify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N116",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Question mark-er power ❓",
            body = "Je jaygay confused, seta question hisebe note kore rak.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N117",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Ajker learning-er receipt 🧾",
            body = "Ki shikhlis, ki baki—duita point record kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N118",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Ekta concept, nijer language 📚",
            body = "Difficult concept-ta simple language-e explain kore dekhe ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        ),
        NotificationTemplate(
            id = "N119",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Confusion-ke bookmark kor 🔖",
            body = "Stuck hole topic-ta mark kor, pore targeted help ba revision ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N120",
            category = NotificationCategory.NOTES_LEARNING,
            title = "Review-er mini mission 🔎",
            body = "Purono notes theke ekta important idea recall kore verify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.NOTES,
            priority = 2
        )
    )

    val ALL_NOTIFICATIONS: List<NotificationTemplate> = BASE_NOTIFICATIONS + NewNotificationsLibrary.NEW_NOTIFICATIONS

    fun findById(id: String): NotificationTemplate? {
        return ALL_NOTIFICATIONS.find { it.id == id }
    }
}
