# Job-Invoicing-Assist

Job-Invoicing-Assist is an Android application developed as a team project to support wheel loader operators and small machine-owning companies in planning work, registering job details, and generating invoice to their customers.

The application runs on an Android tablet and is designed to simplify work planning, time tracking, material and service registration, and invoice creation.

---

## Project Overview

The goal of this project is to provide a user-friendly mobile solution that allows operators to:

- Create and manage work plans
- Register work-related data such as:
  - Time
  - Distance
  - Weight
  - Materials
  - Services
- View work plan items as structured cards
- Create invoices with the entered data for relevant job

The project is developed incrementally using agile practices.

---

## Technologies Used

- **Android Studio**
- **Java**
- **Firebase Firestore** (cloud data storage)
- **Material Design Components**
- **RecyclerView & CardView**
- **Git & GitHub**
- **Figma** (UI/UX design)

---

## Architecture & Structure

The project follows a layered structure:

```
com.mdu.jobinvoicing
│
├── data
│   └── local
│       └── entities        # Data models (TimeEntry, Material, Service, etc.)
│
├── ui / activities         # Activities and UI logic
│
├── adapters                # RecyclerView adapters
│
└── res
    └── layout              # XML layouts
```

Each work plan item type (Time, Distance, Weight) is handled separately to keep the logic clear and extensible.

---

## Work Plan Items

Currently supported work plan item types:

- **Time Entry**
- **Distance**
- **Weight**

Each item:
- Can be added through the Work Plan Details screen
- Is displayed as a card in a list
- Is stored in Firebase Firestore under its own collection

---

## Development Process

### Agile Methodology
- Scrum-based workflow with sprint planning and reviews
- Kanban-style product backlog board for task tracking

### Collaboration
- GitHub used as the central code repository
- Individual feature branches
- Pull Requests with code reviews before merging
- Pair programming for complex features

### Design
- UI design created in Figma before implementation
- Shared understanding of layouts and navigation before coding

---

## Branching Strategy

- `main` – stable version of the project
- feature branches – one branch per feature or task
  - Example: `homepage-ui`, `workplan-distance-weight`

Changes are only merged into `main` after review.

---

## Documentation & Communication

- **Google Docs** for requirements, design notes, and meeting notes
- **Google Sheets** for time reporting and task tracking
- **Microsoft Teams** for daily communication and meetings
- **Github** for backlog tracking and version control

---

## Setup & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/MohammadAbdulkader9/Job-Invoicing-Assist.git
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical Android device

Note: Firebase configuration requires a valid google-services.json file.

---

## Future Improvements

- Improved synchronization logic
- Email the created invoice

---

## Team

This project is developed collaboratively as part of a Mälardalen University Software Engineering 2: Project Teamwork DVA313 24057 HT2025 course.

