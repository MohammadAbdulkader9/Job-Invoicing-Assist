## Job and Invoicing Assist 
Job-Invoicing-Assist is an Android application developed as a team project to support wheel loader operators and small machine-owning companies in planning work, registering work details, and generating invoices for customers.

The application runs on an Android tablet and is designed to simplify work planning, time tracking, material, service distance, weight registration, and invoice creation.

---

## Project Overview

The goal of this project is to provide a user-friendly solution that allows operators to:

- Create and manage work plans
- Register work-related data such as:
  - Time
  - Distance
  - Weight
  - Materials
  - Services
- View work plan items as structured cards
- Generate and send invoices with the entered data for relevant work

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
│       └── dao	            # Data access objects
│       └── repositories    # repositories  
│
├── vm 	                    # View models (Workplan ViewModel, Invoice ViewModel )
│
├── ui / activities         # Activities and UI logic
│
├── adapters                # RecyclerView adapters
│
└── res
    └── layout              # XML layouts
```

Each work plan item type (Time, Material, Service, Distance, Weight) is handled separately to keep the logic clear and extensible.

---

## Work Plan Items

Currently supported work plan item types:

- **Time Entry**
- **Material**
- **Service**
- **Distance**
- **Weight**

Each item:
- Can be added through the Work Plan Details screen
- Is displayed as a card in a list
- Is stored in Firebase Firestore under its own collection

---

## Simple work flow process
1- Open the application.

2- Create or Select an operator profile (access Profile from the Bottom Navigation Bar).

3- After selecting a Profile navigate to home screen using the navigation bar.

4- Click on Create Work Plan.

5- Enter the required details (Client Name, Work Title, Work Description, Work Location).

6- After entering the details click on Create.

7- The application will navigate to the recently created work plan screen (Work Plan Details).

8- In the Work Plan Detail screen you will be able to Edit the work plan, change the status of the work.
-	Open: indicates the work plan is created.
-	In Progress: indicates the work is ongoing.
-	Closed: indicates the work plan is done.

9- In the Work Plan Detail screen you will be able to add work plan item, each work plan item indicates what type of work is done and you can mix items as well.

“Assuming we work time based”

10- From Home Page click on Time Tracking.

11- Time Tracking consists of a stopwatch timer where the user can create time instance to record the amount of time spent on a work.

12- Click on the ‘+’ icon (Floating Button on the bottom right hand side).

13- Enter a Title for the Timer, and click Create.

14- You can Start the timer by clicking on the Play button and pause by clicking the Pause button, the Reset by clicking the Reset button.

15- When the work is done you can Stop the Timer.

16- From the Home Screen click on View Work Plans.

17- From the Work Plans screen you can see all work plans, dynamic Search bar and Filter by is implemented which you can use to search and filter your work plans.

18- Click on the work plan you created.

19- set the Status to Closed, for the record.

20- Click on Time Entry, the field will expand, and you either enter the time manually or click on Import From Timer button.

21- After clicking of the Import From Timer, you will get a list of all timer instances, select the instance you created for this work.

22- The Time Entry fields will automatically be filed, but you need to enter the Hour Rate, then click on Add Timer Entry button.

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

### Testing
- Acceptance tests derived directly from user stories
- Manual acceptance testing conducted in multiple test sessions
- Test cases documented and maintained in an external Excel file
- Core functionalities tested incrementally during development
- Failed tests documented and added back to the product backlog as future work

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

- Using a real 3rd party financial system instead a mock API integration.
- Add functionality print the invoice as a receipt.
- Offline create and manage work plans

---

## Team

This project is developed collaboratively as part of a Mälardalen University Software Engineering 2: Project Teamwork DVA313 24057 HT2025 course.
