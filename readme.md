# 📚 StudySync – AI-Powered Smart Study Planner

## 1. Project Overview

**StudySync** is an Android-based study planning and productivity application designed to help students organize their academic activities, manage subjects, track learning progress, and improve their study habits.

The application combines study planning, subject management, progress tracking, Pomodoro-based time management, and AI-powered study insights in a single platform.

StudySync uses Google's Gemini API to analyze available student study data and generate personalized study recommendations and learning insights.

## 2. Problem Statement

Students often struggle to manage multiple subjects, organize their study schedules, track academic progress, and maintain consistent study habits.

Traditional planning methods do not always provide meaningful insights into a student's learning patterns or help identify what they should study next.

StudySync addresses these challenges by providing a centralized digital platform for academic planning, productivity tracking, and personalized study guidance.

## 3. Objectives

- Simplify subject and topic management.
- Help students organize their study schedules.
- Track completed topics and academic progress.
- Encourage focused learning through a Pomodoro timer.
- Analyze study sessions and learning patterns.
- Generate personalized study recommendations using Gemini AI.
- Provide students with insights into their study consistency and performance.

## 4. Features

### 4.1 Dashboard

The Dashboard provides an overview of the student's academic activities.

Features include:
- Overview of registered subjects.
- Study progress information.
- Recent study sessions.
- Today's study schedule.
- Quick access to study activities.
- Access to AI-powered study recommendations.

### 4.2 Subject Management

Students can create and manage their academic subjects.

Features include:
- Add new subjects.
- Specify examination dates.
- Set target scores.
- View individual subject details.
- Track subject-wise progress.
- Delete subjects.

### 4.3 Topic Management

Students can organize each subject into individual topics.

Features include:
- Add topics under specific subjects.
- Assign difficulty levels: Easy, Medium, and Hard.
- Mark topics as completed or incomplete.
- Delete topics.
- Automatically calculate subject progress based on completed topics.

Topic information is stored using the Room database.

### 4.4 Study Planner

The Planner helps students organize their academic activities and study schedules.

It provides a centralized place to manage planned study activities.

### 4.5 Pomodoro Timer

The Pomodoro feature encourages focused study sessions using time-management techniques.

It helps students maintain concentration and record their study activity.

### 4.6 Study Analytics

The Analytics section provides information about the student's study activity.

It includes:
- Study consistency.
- Total registered subjects.
- Completed topics.
- Overall topic progress.
- Total recorded study time.
- Subject-wise study activity.

### 4.7 AI Study Recommendations

StudySync integrates Google's Gemini API to provide personalized study recommendations.

The AI recommendation engine considers available information such as:

- Subject names.
- Examination dates.
- Target scores.
- Subject progress.
- Completed and incomplete topics.
- Recent study sessions.
- Total recorded study time.

Based on the available information, the AI generates a recommendation containing:

- Recommended subject.
- Recommended topic.
- Suggested study duration.
- Reason for the recommendation.
- Priority level.

### 4.8 Study DNA – AI Learning Insights

Study DNA is an AI-powered feature designed to help students understand their learning patterns.

It analyzes available academic and study-session information and generates insights under the following categories:

1. Study Consistency
2. Subject Focus
3. Topic Progress
4. What Is Working
5. Recommended Next Step
6. Improvement Suggestion

The insights are generated using the student's recorded data rather than relying on generic study advice.

**Note:** The quality of the generated insights depends on the availability and accuracy of the recorded student data.

### 4.9 Profile Management

The Profile section provides access to the student's profile and academic information.

## 5. Technology Stack

| Component | Technology |
|---|---|
| Programming Language | Kotlin |
| Development Environment | Android Studio |
| UI Development | Android XML |
| Architecture | MVVM |
| Local Database | Room Database |
| Asynchronous Operations | Kotlin Coroutines |
| Reactive Data | Kotlin Flow |
| AI Integration | Google Gemini API |
| JSON Processing | JSONObject |
| Version Control | Git and GitHub |

## 6. Application Architecture

StudySync follows the Model-View-ViewModel (MVVM) architectural pattern.

### Presentation Layer
Responsible for displaying application screens and handling user interactions.

Examples:
- DashboardFragment
- SubjectDetailsFragment
- AnalyticsFragment
- PlannerFragment
- ProfileFragment

### ViewModel Layer
Responsible for managing UI-related data and application operations.

Examples:
- SubjectViewModel
- DashboardViewModel
- AnalyticsViewModel
- AIViewModel

### Repository Layer
Acts as an intermediary between the ViewModels and the database.

The SubjectRepository handles:
- Subject operations.
- Topic operations.
- Study-session operations.
- Academic progress updates.

### Data Layer
Uses Room Database to store and retrieve application data locally.

Main database entities:
- Subject
- Topic
- StudySession

### AI Layer
Responsible for preparing student data and communicating with the Gemini API.

Main components:
- AIService
- GeminiAIService
- AIRepository
- StudyContextBuilder
- AIViewModel

## 7. Database Structure

StudySync uses Room Database for local data persistence.

### Subject Entity

Stores academic subject information.

Fields:
- id
- name
- examDate
- targetScore
- progress

### Topic Entity

Stores individual topics associated with subjects.

Fields:
- id
- subjectId
- topicName
- difficulty
- completed

### StudySession Entity

Stores information about recorded study sessions.

Fields:
- id
- subjectId
- subjectName
- topicName
- date
- startTime
- duration
- completed

The `subjectId` field in the Topic entity associates each topic with its corresponding subject.

## 8. Topic Management Workflow

The topic management process follows these steps:

1. The student opens the Subjects section.
2. The student selects a subject.
3. The SubjectDetailsFragment displays the selected subject.
4. The student selects Add Topic.
5. The student enters a topic name and selects its difficulty.
6. The application creates a Topic object containing the subject ID.
7. SubjectViewModel receives the topic.
8. SubjectRepository inserts the topic into Room Database through TopicDao.
9. The topic list is retrieved using the corresponding subject ID.
10. The UI updates with the latest topic information.

Subject progress is recalculated whenever topics are added, deleted, or marked as completed.

The progress calculation is:

**Progress (%) = (Completed Topics / Total Topics) × 100**

## 9. AI Processing Workflow

The AI integration follows this workflow:

1. The student requests an AI recommendation or Study DNA analysis.
2. AIViewModel receives the request.
3. AIRepository requests the student's study context.
4. StudyContextBuilder retrieves subjects, topics, and recent study sessions.
5. The collected information is converted into a structured StudyContext.
6. GeminiAIService prepares a prompt using the collected information.
7. The request is sent to Google's Gemini API.
8. Gemini processes the prompt and generates a response.
9. The response is processed and returned to the application.
10. AIViewModel updates the UI with the generated result or an appropriate error message.

AI requests are triggered explicitly by the student rather than automatically whenever the Analytics screen opens.

## 10. Project Folder Structure

```text
StudySync/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/studysync/
│   │       │       │
│   │       │       ├── ai/
│   │       │       │   ├── AIService.kt
│   │       │       │   ├── AIRepository.kt
│   │       │       │   ├── AIRecommendation.kt
│   │       │       │   ├── StudyContext.kt
│   │       │       │   ├── StudyContextBuilder.kt
│   │       │       │   └── GeminiAIService.kt
│   │       │       │
│   │       │       ├── data/
│   │       │       │   ├── AppDatabase.kt
│   │       │       │   ├── Subject.kt
│   │       │       │   ├── Topic.kt
│   │       │       │   ├── StudySession.kt
│   │       │       │   ├── SubjectDao.kt
│   │       │       │   ├── TopicDao.kt
│   │       │       │   ├── StudySessionDao.kt
│   │       │       │   └── SubjectRepository.kt
│   │       │       │
│   │       │       ├── ui/
│   │       │       │   ├── dashboard/
│   │       │       │   ├── subjects/
│   │       │       │   ├── subjectdetails/
│   │       │       │   ├── planner/
│   │       │       │   ├── analytics/
│   │       │       │   └── profile/
│   │       │       │
│   │       │       ├── viewmodel/
│   │       │       │   ├── SubjectViewModel.kt
│   │       │       │   ├── AIViewModel.kt
│   │       │       │   └── AnalyticsViewModel.kt
│   │       │       │
│   │       │       └── MainActivity.kt
│   │       │
│   │       └── res/
│   │           ├── layout/
│   │           ├── drawable/
│   │           ├── color/
│   │           └── values/
│   │
│   └── build.gradle.kts
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

*The structure above represents the main project components. Actual filenames and packages may vary depending on the implementation.*

## 11. Installation and Setup

### Prerequisites

- Android Studio
- Android SDK
- Kotlin support
- A physical Android device or Android Emulator
- Internet connection for Gemini AI features
- Google Gemini API key

### Steps

1. Clone the repository:

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize the project.

4. Configure the Gemini API key.

5. Ensure that the API key is available through the `GEMINI_API_KEY` BuildConfig field.

6. Build and run the application on an Android device or emulator.

**Security recommendation:** For a production release, do not expose a permanent Gemini API key inside the Android application. Use a secure backend to manage AI requests and credentials.

## 12. Testing

The following test scenarios can be used to verify the application's functionality.

| Test Case | Expected Result |
|---|---|
| Add a subject | Subject should be saved |
| Add a topic | Topic should be associated with the selected subject |
| Mark a topic completed | Topic status should update |
| Delete a topic | Topic should be removed |
| Check subject progress | Progress should reflect completed topics |
| Record a study session | Session should be stored |
| Open Analytics | Recorded statistics should be displayed |
| Generate Study DNA insights | AI should return an analysis when the API is available and quota permits |
| Generate study recommendation | AI should return a recommendation when valid topic data and API availability permit |

## 13. Limitations and Considerations

- AI features require internet connectivity.
- Gemini API availability and usage quotas can affect AI response generation.
- AI recommendations depend on the quality of the information stored in the database.
- Empty topic lists limit the ability to generate topic-specific recommendations.
- Study analytics become more meaningful as students record more study sessions.
- AI-generated suggestions should be treated as learning guidance rather than guaranteed academic outcomes.

## 14. Future Enhancements

Potential improvements include:

- AI-generated daily study schedules.
- Calendar synchronization.
- Cloud synchronization and user accounts.
- Study reminders and notifications.
- More detailed learning analytics.
- Subject-wise performance comparisons.
- Exportable study reports.
- Improved offline functionality.
- Secure backend-based AI integration.

## 15. Conclusion

StudySync aims to make academic planning more organized, interactive, and personalized.

By combining subject management, topic tracking, study scheduling, Pomodoro-based productivity, analytics, and Gemini-powered insights, the application provides students with a centralized environment to manage their learning activities.

The project demonstrates the practical integration of Android development, Kotlin, Room Database, MVVM architecture, Kotlin Coroutines, and generative AI into an educational productivity application.

---

**Project Name:** StudySync  
**Category:** Android Application / Educational Technology  
**Platform:** Android  
**Primary Language:** Kotlin  
**AI Integration:** Google Gemini API  
**Database:** Room Database  
**Architecture:** MVVM

![alt text](<WhatsApp Image 2026-11-05 at 8.24.09 PM (1).jpeg>)
 ![alt text](<WhatsApp Image 2026-11-05 at 8.24.08 PM (1).jpeg>) 
 ![alt text](<WhatsApp Image 2026-11-05 at 8.24.08 PM.jpeg>) 
 ![alt text](<WhatsApp Image 2026-11-05 at 8.24.08 PM (2).jpeg>) 
 ![alt text](<WhatsApp Image 2026-11-05 at 8.24.09 PM.jpeg>)

<video controls src="Video Project 5.mp4" title="Title"></video>