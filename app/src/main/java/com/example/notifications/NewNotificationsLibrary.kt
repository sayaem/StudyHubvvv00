package com.example.notifications

object NewNotificationsLibrary {

    val NEW_NOTIFICATIONS: List<NotificationTemplate> = listOf(
        NotificationTemplate(
            id = "N001",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "বই খুলে ঘুমের ডাক 😂",
            body = "বই খুললেই ঘুম এলে বুঝবি, তোর বালিশের সঙ্গে ষড়যন্ত্র চলছে। ১০ মিনিট পড়ে ষড়যন্ত্র ভাঙ!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N002",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "সিলেবাসের দিকে তাকাস না 💀",
            body = "সিলেবাস দেখে ভয় পেলে হবে? পুরো পাহাড় না, আজ শুধু একটা পাথর সরিয়ে দে।",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N003",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "পড়ার মুডের অপেক্ষা?",
            body = "মুড আসার অপেক্ষায় থাকলে মুডই একদিন টপার হয়ে যাবে। শুরু কর, মুড পেছনে আসবে।",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N004",
            category = NotificationCategory.EXAM_ALERTS,
            title = "পানিরও চাপ আছে 😂",
            body = "পরীক্ষা যত কাছে আসে, মনে হয় পানির তাপমাত্রাও তোর চেয়ে কম চাপে আছে!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N005",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "মস্তিষ্ক কি ছুটিতে?",
            body = "শরীর পড়ার টেবিলে, কিন্তু মস্তিষ্ক ঘুরতে গেছে? একটা প্রশ্ন দিয়ে তাকে ফিরিয়ে আন।",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N006",
            category = NotificationCategory.MOTIVATION,
            title = "আজকের ছোট জয় 🏆",
            body = "আজ দশটা প্রশ্ন ঠিকমতো বুঝলে, সেটা কালকের আত্মবিশ্বাসের পুঁজি।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N007",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "বইয়ের সঙ্গে সম্পর্ক",
            body = "বইয়ের দিকে তাকিয়ে থাকলে সম্পর্ক গভীর হয় না। ভেতরের বিষয়গুলোও জানতে হয়!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N008",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "পড়া জমিয়ে রাখিস না",
            body = "পড়া বাকি রেখে দিলে সেটা নিজে নিজে কমে না। আজকের একটা অংশ আজই শেষ কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N009",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "মস্তিষ্কের উপস্থিতি নে",
            body = "খাতায় নাম লিখলেই উপস্থিতি হয় না। পড়ার সময় মাথাটাকেও হাজির রাখ!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N010",
            category = NotificationCategory.MOTIVATION,
            title = "দুই মিনিটের চ্যালেঞ্জ",
            body = "পড়তে ইচ্ছা করছে না? মাত্র দুই মিনিট শুরু কর। অনেক সময় শুরুটাই আসল বাধা।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N011",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "সূত্র মুখস্থ, ব্যবহার কই?",
            body = "সূত্র মুখস্থ করে বসে থাকলে হবে না। কোন প্রশ্নে কোন সূত্র লাগবে, সেটাও অনুশীলন কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N012",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "ভুল মানেই শেষ নয়",
            body = "অনুশীলনে ভুল ধরা পড়া ভালো। পরীক্ষার আগে ভুল ধরতে পারলে নম্বর বাঁচতে পারে।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N013",
            category = NotificationCategory.TASKS_PLANNING,
            title = "আজকের লক্ষ্য ছোট রাখ",
            body = "আজ পুরো অধ্যায় শেষ করার চাপ নিস না। আগে একটা নির্দিষ্ট অংশ শেষ কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N014",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "খাতা খুলে বসে আছিস?",
            body = "কলম হাতে নিয়ে ভাবলেই উত্তর আসবে না। প্রথম প্রশ্নটা নিজে সমাধান করে দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N015",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "পড়ার টেবিলের নাটক 🎭",
            body = "টেবিল গুছানো, কলম গুছানো, পানি গুছানো—এবার পড়াটাও শুরু কর!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N016",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "ভুলে যাওয়া স্বাভাবিক",
            body = "একবার পড়ে সব মনে না থাকলে নিজেকে দোষ দিস না। বই বন্ধ করে মনে করার অনুশীলন কর।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N017",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "নিজের পরীক্ষা নিজে নে",
            body = "বই দেখে সব সহজ লাগে। বই বন্ধ করে তিনটা মূল কথা লিখতে পারিস কি না দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N018",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়া আর পুনরাবৃত্তি",
            body = "নতুন অধ্যায় পড়ছিস, ভালো কথা। কিন্তু পুরোনো পড়া ঝালাই না করলে সেগুলোও হারিয়ে যেতে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N019",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "মনোযোগের খোঁজে 📵",
            body = "পাঁচ মিনিট পরপর ফোন দেখলে মনোযোগও পাঁচ মিনিটের অতিথি হয়ে থাকবে। ফোনটা দূরে রাখ।",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N020",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "অল্প পড়া, আসল পড়া",
            body = "এক ঘণ্টা বইয়ের সামনে বসার চেয়ে বিশ মিনিট মন দিয়ে শেখা বেশি কাজে লাগতে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N021",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "অধ্যায়কে ছোট কর",
            body = "বড় অধ্যায়কে ছোট ছোট অংশে ভাগ কর। ছোট লক্ষ্য শেষ হলে পরেরটা ধর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N022",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "পড়া বুঝেছিস তো?",
            body = "লাইন মুখস্থ করার আগে নিজের ভাষায় বোঝানোর চেষ্টা কর। বুঝতে পারলেই মনে রাখা সহজ হয়।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N023",
            category = NotificationCategory.TASKS_PLANNING,
            title = "সময় কোথায় গেল?",
            body = "পড়ার সময় উধাও হয়ে যায়, কিন্তু ফোনে সময় উড়ে যায় আরও দ্রুত। আজ সময়টা মেপে দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N024",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "ভুলের খাতা বানাস",
            body = "যে প্রশ্নে ভুল করিস, সেটা লিখে রাখ। একই ভুল বারবার করলে নম্বরও বারবার পালাবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N025",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "পড়া শুরু করার কৌশল",
            body = "প্রথমে বই, খাতা আর কলম সামনে রাখ। শুরু করার বাধা কমলে পড়ায় ঢোকা সহজ হয়।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N026",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "মনে রাখার পরীক্ষা",
            body = "একটা সংজ্ঞা পড়ে বই বন্ধ কর। নিজের ভাষায় বলতে পারলে বুঝবি শেখাটা হচ্ছে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N027",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়া কি শুধু দাগানো?",
            body = "পুরো বই রঙিন করে ফেললেই শেখা হয় না। দাগানো অংশ মনে করে বলতে পারাটাই আসল।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N028",
            category = NotificationCategory.MOTIVATION,
            title = "আজ একটু এগিয়ে যা",
            body = "অন্যের পড়ার গতির সঙ্গে নিজের তুলনা করিস না। গতকালের চেয়ে একটু এগোলেই লাভ।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N029",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "কঠিন প্রশ্নের মুখোমুখি",
            body = "কঠিন প্রশ্ন দেখেই পালিয়ে যাস না। আগে কী জানতে চেয়েছে, সেটা আলাদা করে বুঝে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N030",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "পড়ার মাঝে বিরতি",
            body = "একটানা পড়ে মাথা ভারী হলে ছোট বিরতি নে। বিরতির পর নির্দিষ্ট সময়ে আবার ফিরিস।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N031",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "ক্যালকুলেটর সব জানে?",
            body = "ক্যালকুলেটর হিসাব করতে পারে, কিন্তু কোন পদ্ধতি লাগবে সেটা বুঝতে তোকে শিখতেই হবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N032",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "উত্তরটা কত নম্বরের?",
            body = "প্রশ্নে যত নম্বর, উত্তরে ততটুকু প্রয়োজনীয় ব্যাখ্যা দে। অপ্রয়োজনীয় কথা লিখে সময় নষ্ট করিস না।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N033",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "আগে প্রশ্ন পড়",
            body = "বহুনির্বাচনী প্রশ্নে ‘নয়’ বা ‘ব্যতীত’ শব্দটা মিস করলেই উত্তর উল্টে যেতে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N034",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "অনুমান করে দাগাস না",
            body = "ভুল উত্তরে নম্বর কাটলে অন্ধ অনুমান বিপদ ডেকে আনতে পারে। আগে নিয়মটা বুঝে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N035",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "সময়ের সঙ্গে বন্ধুত্ব",
            body = "বাড়িতে সময় ধরে প্রশ্ন সমাধান কর। পরীক্ষার হলে সময়ের চাপ তখন কম অচেনা লাগবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N036",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "শেষের পাঁচ মিনিট",
            body = "পরীক্ষায় কিছু সময় উত্তর যাচাইয়ের জন্য রাখ। একক, চিহ্ন আর বাদ পড়া অংশ দেখে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N037",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "নিউটনের সূত্র মনে আছে?",
            body = "স্থির বস্তু স্থির থাকতে চায়—তোর পড়া শুরু না করার অবস্থাটাও বেশ স্থির দেখাচ্ছে! এবার নড়।",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N038",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "সূত্রের পেছনের গল্প",
            body = "সূত্র শুধু মুখস্থ করিস না। কোন ধারণা থেকে এসেছে বুঝলে নতুন প্রশ্নেও কাজে লাগবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N039",
            category = NotificationCategory.TASKS_PLANNING,
            title = "পড়ার পরিকল্পনা বনাম পড়া",
            body = "পরিকল্পনা সুন্দর হলেই নম্বর আসে না। পরিকল্পনার অন্তত একটা কাজ আজ শেষ কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N040",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "ঘুমের সঙ্গে যুদ্ধ নয়",
            body = "ঘুম পেলে নিজেকে মেশিন ভাবিস না। নিয়মিত ঘুম আর মনোযোগী পড়া দুটোই দরকার।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N041",
            category = NotificationCategory.ADMISSION_READINESS,
            title = "ভর্তি পরীক্ষার বাস্তবতা",
            body = "শুধু কত ঘণ্টা পড়েছিস, সেটাই সব নয়। কতটা মনে রাখতে আর প্রয়োগ করতে পারিস, সেটাও জরুরি।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N042",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "মক টেস্টের ভয়",
            body = "অনুশীলনী পরীক্ষায় কম নম্বর মানেই শেষ নয়। ভুলগুলো ঠিক করার সুযোগ হিসেবেই দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N043",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "দুর্বল অধ্যায় পালাচ্ছে?",
            body = "যে অধ্যায়টা বারবার এড়িয়ে যাচ্ছিস, আজ সেটার সবচেয়ে সহজ অংশ দিয়ে শুরু কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N044",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "নিজের ভুলকে চিনে নে",
            body = "ভুল উত্তর শুধু নম্বর কমায় না, দুর্বল জায়গাটাও দেখিয়ে দেয়। সেটা কাজে লাগা।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N045",
            category = NotificationCategory.TASKS_PLANNING,
            title = "সবকিছু একদিনে নয়",
            body = "এক দিনে পুরো সিলেবাস জেতার দরকার নেই। নিয়মিত ছোট অগ্রগতিই বড় ফল গড়ে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N046",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়া বুঝিয়ে বল",
            body = "কাউকে শেখানোর মতো করে বিষয়টা ব্যাখ্যা কর। যেখানে আটকে যাবি, সেখানেই আরও শেখা দরকার।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N047",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "প্রশ্নের ধরন চিনে নে",
            body = "আগের বছরের প্রশ্ন দেখলে প্রশ্নের ধরন বোঝা যায়। তবে শুধু পুরোনো প্রশ্নের ওপর নির্ভর করিস না।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N048",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "সূত্রের তালিকা",
            body = "যেসব সূত্র বারবার ভুলে যাস, সেগুলো এক জায়গায় লিখে নিয়মিত মনে করার চেষ্টা কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N049",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "একবার পড়েই শেষ?",
            body = "একবার পড়ে পরিচিত লাগতে পারে, কিন্তু বই বন্ধ করে মনে করতে পারাই আসল পরীক্ষা।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N050",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "মাথার ভেতর গুছিয়ে নে",
            body = "অধ্যায়ের মূল ধারণাগুলো দিয়ে ছোট মানচিত্র বানালে বিষয়গুলোর সম্পর্ক বোঝা সহজ হয়।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N051",
            category = NotificationCategory.TASKS_PLANNING,
            title = "আজকের তিনটি কাজ",
            body = "আজকের জন্য তিনটি নির্দিষ্ট কাজ লিখে নে। সব শেষ না হলেও অগ্রগতি পরিষ্কার থাকবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N052",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "ফোনটা একটু দূরে",
            body = "ফোন চোখের সামনে থাকলে মন সেদিকে যেতে পারে। পড়ার সময় নাগালের বাইরে রাখার চেষ্টা কর।",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N053",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "মনোযোগ ফিরিয়ে আন",
            body = "মন অন্যদিকে চলে গেছে বুঝলেই নিজেকে দোষ দিস না। পরের লাইনটা মন দিয়ে পড়ে আবার শুরু কর।",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N054",
            category = NotificationCategory.TASKS_PLANNING,
            title = "কঠিন বিষয় আগে?",
            body = "যে সময়ে মন সবচেয়ে সতেজ থাকে, তখন কঠিন বিষয়টা ধরার চেষ্টা কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N055",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "ছোট বিরতি, পরিষ্কার মাথা",
            body = "বিরতিতে একটু হাঁটাহাঁটি কর। বিরতিটা যেন নতুন করে দীর্ঘ ফোন-ভ্রমণ না হয়ে যায়!",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N056",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "পড়ার জায়গা তৈরি",
            body = "দরকারি বই আর খাতা আগে গুছিয়ে রাখ। পড়ার মাঝখানে জিনিস খুঁজতে সময় কম যাবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N057",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "নম্বরের হিসাব",
            body = "কোন অংশে বেশি নম্বর, সেটা জেনে সময় ভাগ কর। তবে কম নম্বরের গুরুত্বপূর্ণ ধারণাও বাদ দিস না।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N058",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "উত্তর সাজিয়ে লেখ",
            body = "বড় উত্তরে মূল কথা, ব্যাখ্যা আর উদাহরণ আলাদা করে লিখলে পরীক্ষক বুঝতে সুবিধা পান।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N059",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "একক ভুলিস না",
            body = "সংখ্যার উত্তর ঠিক হলেও একক ভুল হলে সমস্যা হতে পারে। হিসাবের শেষে একক যাচাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N060",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "চিহ্নের ছোট বিপদ",
            body = "যোগ-বিয়োগের চিহ্নে ছোট ভুল পুরো হিসাব বদলে দিতে পারে। শেষবার চিহ্নগুলো দেখে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N061",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "আগে সহজ নম্বর",
            body = "পরীক্ষায় যে প্রশ্নগুলো ভালো পারিস, সেগুলো দিয়ে আত্মবিশ্বাস আর সময়—দুটোই সামলাতে পারিস।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N062",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "আটকে গেলে থাম",
            body = "একটি প্রশ্নে বেশি সময় আটকে থাকিস না। চিহ্ন দিয়ে পরেরটায় যা, পরে ফিরে আসিস।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N063",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "প্রশ্নের সব অংশ",
            body = "প্রশ্নে ক, খ, গ থাকলে সব অংশের উত্তর দিয়েছিস কি না শেষে মিলিয়ে দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N064",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "পরীক্ষার ব্যাগ",
            body = "পরীক্ষার আগের রাতে প্রবেশপত্র, কলম আর দরকারি জিনিস গুছিয়ে রাখ। সকালে অযথা দৌড়াতে হবে না।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N065",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "শেষ রাতের ম্যারাথন",
            body = "পরীক্ষার আগের রাতে সব নতুন করে শেখার চেষ্টা চাপ বাড়াতে পারে। গুরুত্বপূর্ণ বিষয়গুলো ঝালাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N066",
            category = NotificationCategory.TASKS_PLANNING,
            title = "পড়া আর বিরতির হিসাব",
            body = "কতক্ষণ পড়বি আর কখন বিরতি নিবি, আগে ঠিক করে নে। এতে সময়ের ওপর নিয়ন্ত্রণ বাড়ে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N067",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "মনে রাখার সহজ উপায়",
            body = "তথ্যকে পরিচিত উদাহরণ বা গল্পের সঙ্গে মিলিয়ে দেখ। অর্থপূর্ণ সম্পর্ক মনে রাখতে সাহায্য করতে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N068",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "ফ্ল্যাশকার্ডের পরীক্ষা",
            body = "কার্ডের এক পাশে প্রশ্ন, অন্য পাশে উত্তর রাখ। আগে নিজে উত্তর দিয়ে তারপর মিলিয়ে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N069",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "আজ পুরোনোটা ঝালাই",
            body = "নতুন বিষয়ের পাশাপাশি আগের দিনের পড়া কয়েক মিনিট মনে করে দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N070",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "তিন দিনের পরীক্ষা",
            body = "তিন দিন আগে পড়া বিষয়টা বই না দেখে মনে করার চেষ্টা কর। কোথায় ভুলছিস বুঝতে পারবি।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N071",
            category = NotificationCategory.RESILIENCE,
            title = "মন খারাপের দিনেও",
            body = "আজ সব পড়তে না পারলেও একটা ছোট কাজ শেষ কর। নিয়মিত ফিরে আসাটাই গুরুত্বপূর্ণ।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N072",
            category = NotificationCategory.RESILIENCE,
            title = "নিজেকে ছোট করিস না",
            body = "অন্যের অগ্রগতি দেখে নিজের চেষ্টা বাতিল করিস না। তোর পরের পদক্ষেপটা ঠিক কর।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N073",
            category = NotificationCategory.MOTIVATION,
            title = "তুলনা নয়, উন্নতি",
            body = "গত সপ্তাহে যা পারতিস না, আজ তার কতটা পারিস দেখে নিজের উন্নতি মাপ।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N074",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "পড়ার গতি নয়",
            body = "দ্রুত পৃষ্ঠা শেষ করাই লক্ষ্য নয়। মূল ধারণা বুঝেছিস কি না, সেটাই যাচাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N075",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "মনোযোগের ছোট দৌড়",
            body = "পনেরো মিনিটের জন্য একটা স্পষ্ট লক্ষ্য ঠিক কর। সময় শেষ হলে কী শিখলি, দেখে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N076",
            category = NotificationCategory.TASKS_PLANNING,
            title = "লক্ষ্যটা লিখে রাখ",
            body = "‘আজ পড়ব’ না লিখে কোন অধ্যায়ের কোন অংশ শেষ করবি, সেটা নির্দিষ্ট করে লিখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N077",
            category = NotificationCategory.MOTIVATION,
            title = "অজুহাতের ছুটি শেষ",
            body = "সবকিছু ঠিকঠাক হওয়ার অপেক্ষা করিস না। এখন যা আছে, তা দিয়েই শুরু কর।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N078",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "একটা প্রশ্নই যথেষ্ট",
            body = "শুরু করতে কষ্ট হলে একটা প্রশ্ন সমাধান কর। তারপর পরের কাজটা ঠিক কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N079",
            category = NotificationCategory.MOTIVATION,
            title = "আত্মবিশ্বাসের প্রমাণ",
            body = "আত্মবিশ্বাস শুধু ভাবনা থেকে নয়, প্রস্তুতির প্রমাণ থেকেও আসে। আজ কিছু প্রশ্ন সমাধান কর।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N080",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "প্রস্তুতি যাচাই কর",
            body = "কোন বিষয় ভালো জানিস আর কোনটা ঝাপসা, লিখে রাখ। পরের পড়ার সময়টা ঠিক করতে সুবিধা হবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N081",
            category = NotificationCategory.TASKS_PLANNING,
            title = "ঘড়ির দিকে তাকাস না",
            body = "বারবার সময় দেখার বদলে নির্দিষ্ট সময়ের লক্ষ্য ঠিক কর। সময় শেষে কাজটা কতদূর হলো দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N082",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়া শেষের সংক্ষিপ্তসার",
            body = "পড়া শেষে তিনটি মূল কথা লিখে রাখ। পরেরবার পুনরাবৃত্তি দ্রুত হবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N083",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "সংজ্ঞার সঙ্গে উদাহরণ",
            body = "সংজ্ঞা পড়ার পর একটা উদাহরণ নিজে তৈরি কর। ধারণাটা বুঝেছিস কি না বোঝা যাবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N084",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "উত্তর না দেখে লিখ",
            body = "আগে নিজের মতো উত্তর লেখ, তারপর বইয়ের সঙ্গে মিলিয়ে ভুলগুলো ঠিক কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N085",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "ভুল উত্তর কেন ভুল?",
            body = "সঠিক উত্তর জানার পাশাপাশি অন্য বিকল্পগুলো কেন ভুল, সেটাও বোঝার চেষ্টা কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N086",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "আজকের দুর্বল জায়গা",
            body = "যে তিনটি বিষয় সবচেয়ে কঠিন লাগছে, সেগুলো লিখে রাখ। একে একে কাজ কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N087",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "সঠিক সময়ে পুনরাবৃত্তি",
            body = "একবার পড়ে দীর্ঘদিন ফেলে রাখিস না। বিরতি দিয়ে আবার মনে করার চেষ্টা কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N088",
            category = NotificationCategory.TASKS_PLANNING,
            title = "ক্যালেন্ডারে পড়া রাখ",
            body = "পড়ার সময়টা ক্যালেন্ডারে লিখে রাখলে অন্য কাজের সঙ্গে সামলানো সহজ হতে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N089",
            category = NotificationCategory.TASKS_PLANNING,
            title = "অল্প অল্প করে এগো",
            body = "বড় লক্ষ্যকে দৈনিক কাজে ভাগ কর। ছোট কাজগুলো শেষ হলে অগ্রগতি জমতে থাকবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N090",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "প্রশ্ন করতেই শিখ",
            body = "কোনো ধারণা বুঝতে সমস্যা হলে নির্দিষ্ট প্রশ্ন লিখে রাখ। পরে শিক্ষক বা বন্ধুর কাছে পরিষ্কার করে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N091",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়ার সঙ্গী",
            body = "বন্ধুর সঙ্গে পালা করে একে অন্যকে প্রশ্ন কর। শুধু উত্তর শুনে নয়, নিজে ব্যাখ্যা দিয়েও শেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N092",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "দলবদ্ধ পড়ার নিয়ম",
            body = "একসঙ্গে পড়লে আগে লক্ষ্য ঠিক কর। লক্ষ্য না থাকলে পড়ার দল গল্পের দলে পরিণত হতে পারে!",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N093",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "পড়া কি বুঝতে পারছিস?",
            body = "একটি বিষয় এক মিনিটে বোঝানোর চেষ্টা কর। বারবার আটকে গেলে সেই অংশ আবার পড়।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N094",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "নিজের প্রশ্ন বানাস",
            body = "অধ্যায় পড়ে নিজেই পাঁচটা প্রশ্ন তৈরি কর। এতে গুরুত্বপূর্ণ তথ্য খুঁজে বের করার অভ্যাস হবে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N095",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "স্মৃতি যাচাই",
            body = "বই বন্ধ করে অধ্যায়ের শিরোনামগুলো মনে করার চেষ্টা কর। কোন অংশ বাদ পড়েছে বুঝতে পারবি।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N096",
            category = NotificationCategory.RESILIENCE,
            title = "পরীক্ষার আগে শান্ত থাক",
            body = "সবকিছু একসঙ্গে ভাবলে চাপ বাড়ে। এখন যে কাজটা করতে পারিস, সেটাতেই মন দে।",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N097",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "একটা ভুল, একটা শিক্ষা",
            body = "ভুল প্রশ্নের পাশে কেন ভুল হয়েছে লিখে রাখ। শুধু সঠিক উত্তর দেখে এগিয়ে যাস না।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N098",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "হিসাব আবার দেখ",
            body = "গণিতের উত্তর পেলে সম্ভব হলে উল্টো হিসাব করে যাচাই কর। ছোট ভুল ধরা পড়তে পারে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N099",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "পড়ার ফল মাপ",
            body = "কত পৃষ্ঠা পড়েছিস না গুনে, বই বন্ধ করে কতটা মনে করতে পারিস সেটা যাচাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N100",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "আজকের পুনরাবৃত্তি",
            body = "আজ নতুন পড়ার আগে গতকালের বিষয়টা পাঁচ মিনিট মনে করে দেখ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N101",
            category = NotificationCategory.ADMISSION_READINESS,
            title = "ভর্তি প্রস্তুতির বাস্তবতা",
            body = "ভর্তি পরীক্ষার প্রস্তুতিতে শুধু পড়া নয়, সময় ধরে প্রশ্ন সমাধানের অভ্যাসও দরকার।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N102",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "ভুলের তালিকা ঝালাই",
            body = "মক টেস্টে ভুল হওয়া প্রশ্নগুলো আবার সমাধান কর। একই ভুল কমানোই উন্নতির লক্ষণ।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N103",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "প্রশ্নের ভাষা বুঝ",
            body = "প্রশ্নে ‘ব্যাখ্যা কর’, ‘তুলনা কর’ আর ‘নির্ণয় কর’—প্রতিটির চাহিদা আলাদা।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N104",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "সব প্রশ্ন এক রকম নয়",
            body = "কিছু প্রশ্ন তথ্য চায়, কিছু প্রশ্ন প্রয়োগ চায়। প্রশ্নের ধরন বুঝে উত্তর সাজা।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N105",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "সময় ভাগ করে নে",
            body = "পরীক্ষার মোট সময় আর প্রশ্নের সংখ্যা দেখে সময়ের একটা পরিকল্পনা বানিয়ে নে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N106",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "শেষ মুহূর্তের তালিকা",
            body = "পরীক্ষার আগে সূত্র, সংজ্ঞা আর বারবার ভুল হওয়া বিষয়গুলো আলাদা করে ঝালাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N107",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "নিজের গতি যাচাই",
            body = "সময় ধরে একটা পুরোনো প্রশ্নপত্র সমাধান কর। কোন অংশে বেশি সময় যাচ্ছে, বুঝতে পারবি।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N108",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "প্রস্তুতি মানে অনুশীলন",
            body = "পড়া শেষ মানেই প্রস্তুতি শেষ নয়। প্রশ্ন সমাধান করে নিজের শেখা যাচাই কর।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N109",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "আজই একটা অধ্যায়",
            body = "অনেক কিছু বাকি থাকলেও একটা অধ্যায়ের নির্দিষ্ট অংশ শেষ কর। অগ্রগতি চোখে পড়বে।",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N110",
            category = NotificationCategory.MOTIVATION,
            title = "শুরু কর, তারপর গতি",
            body = "নিখুঁত পরিকল্পনার অপেক্ষা বাদ দে। আজকের প্রথম কাজটা এখনই শেষ কর।",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N111",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Book khule sleep mode 😂",
            body = "Boi khultei ghum ashle bujhbi pillow-er sathe conspiracy cholche. 10 minute pore conspiracy break kor!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N112",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Syllabus dekhe panic?",
            body = "Puro mountain ekbare uthte hobe na. Ajke ekta pathor sorai de, mane ekta topic finish kor.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N113",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Mood-er jonno wait?",
            body = "Study mood-er wait korte korte mood-i topper hoye jabe. Start kor, mood pore join korbe.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N114",
            category = NotificationCategory.EXAM_ALERTS,
            title = "Panir-o pressure ase 😂",
            body = "Exam joto kache ashe, mone hoy panir temperature-o tor cheye kom pressure-e ase!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N115",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Brain ki chhutite?",
            body = "Body study table-e, brain tour-e? Ekta question solve kore brain-ke back an.",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N116",
            category = NotificationCategory.MOTIVATION,
            title = "Chhoto win, boro confidence",
            body = "Ajke 10 ta question bujhe korle, kalke confidence-er account-e deposit porbe.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N117",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Boi-er sathe relationship",
            body = "Boi-er dike takiye thakle relationship deep hoy na. Bhitorer topic-gulao jante hoy!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N118",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Backlog nijey kombe na",
            body = "Porar baki ongsho nijer theke vanish korbe na. Ajker ekta part ajkei finish kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N119",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Brain-er attendance ne",
            body = "Khatai naam likhle attendance hoy na. Porar shomoy matha-o present rakh!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N120",
            category = NotificationCategory.MOTIVATION,
            title = "2-minute challenge",
            body = "Porte iccha kortese na? Just 2 minute start kor. Onek shomoy start koratai main boss level.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N121",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Formula mukhosto, use koi?",
            body = "Formula mukhosto kore boshe thakle hobe na. Kon question-e kon formula lagbe, sheta practice kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N122",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Bhul mane game over na",
            body = "Practice-e bhul dhora pora bhalo. Exam-er age bhul dhorte parle marks bachbe.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N123",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ajker target chhoto rakh",
            body = "Ajkei pura chapter shesh korar pressure nis na. Age ekta specific part complete kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N124",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Khata khule boshe achish?",
            body = "Pen hate niye bhable answer ashbe na. First question-ta nije solve kore dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N125",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Study table-er natok 🎭",
            body = "Table clean, pen clean, pani ready—ekhon poratao start kor!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N126",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Bhule jawa normal",
            body = "Ekbar pore shob mone na thakle nijeke blame koris na. Book bondho kore recall practice kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N127",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Nijer exam nije ne",
            body = "Boi dekhe shob easy lage. Book bondho kore 3 ta main point likhte parish kina dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N128",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Porashona ar revision",
            body = "Notun chapter porchos, bhalo. Kintu purono pora revise na korle oigulao memory theke logout korte pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N129",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Focus-er khoje 📵",
            body = "5 minute porpor phone check korle focus-o 5 minute-er guest hoye thakbe. Phone dure rakh.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N130",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "Kom pora, real pora",
            body = "Ek ghonta boi-er shamne boshar cheye 20 minute mon diye shekha beshi useful hote pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N131",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Chapter-ke chhoto kor",
            body = "Boro chapter-ke chhoto section-e vag kor. Ekta shesh hole next level-e ja.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N132",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Bujhchos toh?",
            body = "Line mukhosto korar age nijer language-e explain kor. Bujhle mone rakha easier hoy.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N133",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Time gelo koi?",
            body = "Porar shomoy time vanish kore, phone-e abar time rocket speed-e ure. Ajke nijer time track kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N134",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Mistake-er khata ban",
            body = "Je question-e bhul koris, likhe rakh. Same mistake repeat korle marks-o repeat kore palabe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N135",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "Start korar setup",
            body = "Boi, khata, pen agei ready rakh. Start-er obstacle komle poray dhuka easy hoy.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N136",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Memory test de",
            body = "Ekta definition pore book bondho kor. Nijer moto bolte parle bujhbi learning hocche.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N137",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Porashona mane highlight?",
            body = "Pura boi colorful korlei learning hoy na. Highlight kora part mone kore bolte parish kina dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N138",
            category = NotificationCategory.MOTIVATION,
            title = "Aj ektu agai ja",
            body = "Onner study speed-er sathe nijeke compare koris na. Gotokaler cheye ektu agaleo progress.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N139",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Hard question-er face kor",
            body = "Tough question dekhei palabi na. Age question-ta ashole ki chacche, sheta identify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N140",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Break nite dosh nai",
            body = "Ekta stretch por matha heavy hole short break ne. Break shesh hole abar fixed time-e fire ay.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N141",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Calculator shob jane?",
            body = "Calculator calculation kore, kintu kon method lagbe sheta bujhte tokei shikhte hobe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N142",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Answer koy marks-er?",
            body = "Question-e jot marks, answer-e tototuku relevant explanation de. Extra golpo likhe time noshto koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N143",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Question age por",
            body = "MCQ-te NOT ba EXCEPT miss korlei answer ulta hoye jete pare. Keyword-ta age catch kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N144",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Guess kore mark koris na",
            body = "Negative marking thakle blind guess risky. Age exam-er marking rule bujhe ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N145",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Timer-er sathe practice",
            body = "Basay time dhore question solve kor. Exam hall-er time pressure tokhon kom unfamiliar lagbe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N146",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Last 5 minute",
            body = "Exam-e kichu time answer check-er jonno rakh. Unit, sign ar missed part verify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N147",
            category = NotificationCategory.FUNNY_ROASTS,
            title = "Newton-er law mone ase?",
            body = "Inertia bole object rest-e thakte chay. Tor pora start na korar obosthao besh stable dekhacche—ekhon move!",
            tone = "HUMOROUS",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N148",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Formula-r pichoner logic",
            body = "Formula shudhu mukhosto koris na. Concept-ta bujhle unfamiliar question-eo apply korte parbi.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N149",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Plan vs actual pora",
            body = "Study plan sundor holei marks ashbe na. Plan theke ajke at least ekta task complete kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N150",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Ghum-er sathe juddho na",
            body = "Ghum pele nijeke machine bhabis na. Regular sleep ar focused study duita-i important.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N151",
            category = NotificationCategory.ADMISSION_READINESS,
            title = "Admission-er reality",
            body = "Admission preparation-e shudhu reading na, time dhore question solve korar habit-o dorkar.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N152",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Mock test-er bhoy",
            body = "Mock test-e low score mane finish na. Mistake-gula fix korar chance hisebe dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N153",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Weak chapter palacche?",
            body = "Je chapter-ta bar bar avoid korchos, ajke oitar easiest part diye start kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N154",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Nijer mistake chin",
            body = "Wrong answer shudhu marks komay na, weak spot-o show kore. Ei information-ta use kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N155",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ek dine shob na",
            body = "Ek dine full syllabus conquer korte hobe na. Regular small progress mile boro result hoy.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N156",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Teach kore dekh",
            body = "Kauke shekhanor moto topic-ta explain kor. Jekhane atke jabi, oikhanei aro learning dorkar.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N157",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Previous question dek",
            body = "Previous year-er question dekhe pattern bujhte parish. Tobe shudhu old question-er upor depend koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N158",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "Formula list ready?",
            body = "Je formula-gula bar bar bhule jas, ek jaygay likhe regular recall practice kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N159",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Ekbar pore done?",
            body = "Ekbar pore familiar lagte pare, but book bondho kore recall korte paratai real test.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N160",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Brain-er moddhe organize kor",
            body = "Chapter-er main concept diye chhoto mind map banale topic-gular connection bujha easy hoy.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N161",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ajker 3 ta task",
            body = "Ajker jonno 3 ta specific task likhe ne. Shob na holeo progress clear thakbe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N162",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Phone-ta ektu dure",
            body = "Phone chokher shamne thakle attention okhane jete pare. Study time-e reach-er baire rakh.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N163",
            category = NotificationCategory.DISTRACTION_NUDGES,
            title = "Focus abar fire an",
            body = "Mind onno dike geche bujhle nijeke blame koris na. Next line-ta mon diye pore abar start kor.",
            tone = "WITTY",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N164",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Hard topic kokhon?",
            body = "Je shomoy matha shobcheye fresh thake, tokhon difficult topic-ta tackle korar try kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N165",
            category = NotificationCategory.HEALTHY_HABITS,
            title = "Short break, fresh mind",
            body = "Break-e ektu hatahati kor. Break-ta jeno abar long phone tour hoye na jay!",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N166",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "Study setup ready",
            body = "Dorkarer boi ar khata agei guchhiye rakh. Majhkhane jinis khujte time kom jabe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N167",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Marks-er calculation",
            body = "Kon section-e beshi marks, sheta jene time divide kor. Tobe low-mark important concept-o skip koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N168",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Answer-ta organize kor",
            body = "Long answer-e main point, explanation ar example alada korle examiner-er bujhte easy hoy.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N169",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Unit bhulish na",
            body = "Number thik holeo unit miss korle problem hote pare. Calculation-er sheshe unit check kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N170",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Sign-er chhoto danger",
            body = "Plus-minus-er chhoto mistake puro calculation change korte pare. Last-e sign-gula verify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N171",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Easy marks age ne",
            body = "Exam-e je question-gula bhalo parish, ogula diye confidence ar time duita-i manage korte parish.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N172",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Atke gele move on",
            body = "Ekta question-e beshi time atke thakish na. Mark kore next-e ja, pore abar fire ashish.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N173",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Question-er shob part",
            body = "Question-e A, B, C ba multiple part thakle sheshe check kor shobgular answer disos kina.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N174",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Exam bag ready",
            body = "Exam-er ager rate admit card, pen ar dorkarer jinis guchhiye rakh. Shokale unnecessary sprint dite hobe na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N175",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Last-night marathon",
            body = "Exam-er ager rate shob notun kore shekhar try pressure barate pare. Important topic revise kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N176",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Study-break balance",
            body = "Koto khon porbi ar kokhon break nibi age decide kor. Time-er upor control barbe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N177",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Memory trick",
            body = "Information-ke familiar example ba story-r sathe connect kor. Meaningful connection mone rakhte help korte pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N178",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Flashcard test",
            body = "Card-er ek pashe question, onno pashe answer rakh. Age nijer answer de, tarpor check kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N179",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Aj old topic revise",
            body = "Notun porar pasapashi gotokal-er topic-ta koyek minute recall kore dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N180",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "3-day memory test",
            body = "Tin din age pora topic-ta book chara mone korar try kor. Kothay gap ache bujhte parbi.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N181",
            category = NotificationCategory.RESILIENCE,
            title = "Bad day-o count kore",
            body = "Aj shob porte na parle-o ekta small task finish kor. Regularly fire ashatay important.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N182",
            category = NotificationCategory.RESILIENCE,
            title = "Nijeke chhoto koris na",
            body = "Onner progress dekhe nijer effort cancel koris na. Tor next step-ta decide kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N183",
            category = NotificationCategory.MOTIVATION,
            title = "Compare na, improve",
            body = "Gotokale ja parish na, aj tar koto tuku parish dekhe nijer progress measure kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N184",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Speed shob na",
            body = "Taratari page shesh korai goal na. Main concept bujhechis kina, sheta test kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N185",
            category = NotificationCategory.FOCUS_BREAKS,
            title = "15-minute focus race",
            body = "15 minute-er jonno ekta clear target ne. Time shesh hole ki shikhli, check kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N186",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Target-ta likhe rakh",
            body = "“Aj porbo” na likhe kon chapter-er kon part shesh korbi, specific kore likh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N187",
            category = NotificationCategory.MOTIVATION,
            title = "Excuse-er chhuti shesh",
            body = "Shob perfect howar wait koris na. Ekhon ja ache, sheta diyei start kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N188",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Ekta question enough",
            body = "Start korte kosto hole ekta question solve kor. Tarpor next task decide kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N189",
            category = NotificationCategory.MOTIVATION,
            title = "Confidence-er proof",
            body = "Confidence shudhu feeling theke na, preparation-er evidence thekeo ashe. Aj koyekta question solve kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N190",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Preparation check kor",
            body = "Kon topic bhalo janish ar konটা blurry, likhe rakh. Next study session plan korte easy hobe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N191",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Ghori check kom kor",
            body = "Bar bar time dekhar bodole fixed time target ne. Sheshe kaj koto dur holo dekhe ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N192",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Study sesher summary",
            body = "Porar sheshe 3 ta main point likhe rakh. Next revision faster hobe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N193",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Definition-er sathe example",
            body = "Definition porar por nijer ekta example banas. Concept-ta bujhechis kina clear hobe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N194",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Answer na dekhe likh",
            body = "Age nijer moto answer likh, tarpor book-er sathe compare kore mistake fix kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N195",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Wrong option keno wrong?",
            body = "Correct answer janar sathe sathe baki option-gula keno wrong, shetao bujhar try kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N196",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Ajker weak spot",
            body = "Je 3 ta topic shobcheye tough lagche, list kor. Ekta ekta kore tackle kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N197",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Right time-e revision",
            body = "Ekbar pore onek din fele rakhish na. Gap diye abar recall korar try kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N198",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Calendar-e study time",
            body = "Study time calendar-e likhe rakhle onno kajer sathe manage kora easier hote pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N199",
            category = NotificationCategory.TASKS_PLANNING,
            title = "Little by little",
            body = "Boro goal-ke daily task-e vag kor. Chhoto task-gula finish hole progress jomte thakbe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N200",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Question korte shikh",
            body = "Concept bujhte problem hole specific question likhe rakh. Teacher ba friend-er kachhe clear kore ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N201",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Study partner challenge",
            body = "Friend-er sathe ekjon arekjon-ke question kor. Shudhu answer shunish na, nijeyo explain kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N202",
            category = NotificationCategory.STUDY_ENVIRONMENT,
            title = "Group study-er rule",
            body = "Ekshathe porar age goal fix kor. Goal na thakle study group gossip group hoye jete pare!",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N203",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "1-minute explanation",
            body = "Ekta topic ek minute-e explain korar try kor. Bar bar atke gele oi part abar por.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N204",
            category = NotificationCategory.CHAPTER_MASTERY,
            title = "Nijer question banas",
            body = "Chapter pore nijei 5 ta question create kor. Important information khuje ber korar habit hobe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N205",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Memory audit",
            body = "Book bondho kore chapter-er heading-gula recall kor. Kon section miss hoyeche bujhte parbi.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N206",
            category = NotificationCategory.RESILIENCE,
            title = "Exam-er age calm thak",
            body = "Shob kichu ekshathe bhable pressure barbe. Ekhon je kajta korte parish, oitate focus kor.",
            tone = "CARING",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N207",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Ekta mistake, ekta lesson",
            body = "Wrong question-er pashe keno bhul hoyeche likh. Correct answer dekhei move on koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N208",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Calculation abar check",
            body = "Math-er answer pele possible hole reverse calculation kore verify kor. Chhoto mistake dhora porte pare.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.TASKS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N209",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Learning measure kor",
            body = "Koto page porli sheta na gune, book bondho kore koto tuku recall korte parish test kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N210",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Ajker revision",
            body = "Aj notun porar age gotokal-er topic-ta 5 minute recall kore dekh.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N211",
            category = NotificationCategory.ADMISSION_READINESS,
            title = "Admission prep reality",
            body = "Admission preparation-e shudhu reading na, time dhore question solve korar habit-o dorkar.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N212",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Mistake list revise kor",
            body = "Mock test-e wrong howa question-gula abar solve kor. Same mistake komano-i progress.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N213",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Question-er language bujh",
            body = "“Explain”, “Compare” ar “Calculate”—prottek command-er demand alada. Keyword dhore answer de.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N214",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Shob question same na",
            body = "Kichu question fact chay, kichu application chay. Question-er type bujhe answer organize kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N215",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Time divide kore ne",
            body = "Exam-er total time ar question count dekhe time allocation plan banaye ne.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.CALENDAR,
            priority = 2
        ),
        NotificationTemplate(
            id = "N216",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Final revision list",
            body = "Exam-er age formula, definition ar bar bar bhul howa topic-gula alada kore revise kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N217",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Nijer speed test kor",
            body = "Time dhore ekta old question paper solve kor. Kon section-e beshi time jacche, bujhte parbi.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N218",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "Preparation mane practice",
            body = "Reading shesh mane preparation shesh na. Question solve kore nijer learning test kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N219",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Aj ekta chapter",
            body = "Onk kichu baki thakleo ekta chapter-er specific part finish kor. Progress chokhe porbe.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N220",
            category = NotificationCategory.MOTIVATION,
            title = "Start kor, speed ashbe",
            body = "Perfect plan-er wait bad de. Ajker first task-ta ekhoni complete kor.",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N221",
            category = NotificationCategory.REVISION_SCIENCE,
            title = "Brain-er cache clear kor",
            body = "Same page bar bar porar bodole book bondho kore recall kor. Nijer memory-r actual status bujhte parbi.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N222",
            category = NotificationCategory.EXAM_STRATEGY,
            title = "MCQ-te detective ho",
            body = "Option-gula dekhei answer select koris na. Question-er keyword ar condition age identify kor.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N223",
            category = NotificationCategory.SYLLABUS_CHAPTERS,
            title = "Backlog-er sathe negotiation",
            body = "Backlog-er sathe daily ekta topic-er deal kor. Ekdin-e shob clear korte giye abar freeze koris na.",
            tone = "INFORMATIONAL",
            actionRoute = NotificationActionRoute.SYLLABUS,
            priority = 2
        ),
        NotificationTemplate(
            id = "N224",
            category = NotificationCategory.MOTIVATION,
            title = "Future-you thank you bolbe",
            body = "Ajker 20 minute focused study future-you-er stress komate pare. Ekhon ekta task start kor!",
            tone = "MOTIVATIONAL",
            actionRoute = NotificationActionRoute.FOCUS,
            priority = 2
        )
    )
}
