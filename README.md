# Campus Connect

**Campus Connect** is an Android application designed to enhance connectivity and collaboration among university students and faculty. The app facilitates seamless networking by allowing users to showcase their skills, find peers with similar interests, and explore internship or research opportunities posted by faculty. Built using Kotlin and Jetpack Compose, the app leverages Firebase for authentication and data storage.

---

## Features

1. **Student Profiles**
   - Students can register and create detailed profiles with sections such as:
     - Name, entry number, branch, and batch.
     - Skills (with fields for skill name, proficiency, and experience).
     - LinkedIn and GitHub links.
   - Profiles can be edited and updated dynamically.

2. **Skill-Based Search**
   - Filter and search students based on skills, proficiency levels, or experience to find suitable collaborators.

3. **Skill Testing System**
   - Integrated quiz module to assess student skills with dynamic difficulty levels and timers.

4. **Chat Functionality**
   - Real-time messaging feature to enable smooth communication between students and faculty.

5. **Security Features**
   - Firebase Authentication ensures secure login and registration.
   - Data stored and managed in Firestore with proper access control.

---

## Screenshots

_Add screenshots of the app's main pages here, such as the registration page, profile page, skill-based search, and chat interface._
![IMG-20241205-WA0006](https://github.com/user-attachments/assets/00f6e8a4-05dd-4407-8dc2-54297de0135b)
![WhatsApp Image 2024-12-05 at 12 10 19_a4eba41c](https://github.com/user-attachments/assets/17753e41-2d52-47d0-9e6d-2a6282eff8f3)
![WhatsApp Image 2024-12-05 at 13 37 20_6dc9e8ec](https://github.com/user-attachments/assets/2bbaa143-7547-4c95-8850-e1d54b226a9b)
![WhatsApp Image 2024-12-05 at 13 37 20_8fb39aa1](https://github.com/user-attachments/assets/4a7a02e9-ad64-4fb8-9174-cf133e3bce55)
![IMG-20241205-WA0007](https://github.com/user-attachments/assets/e1fb3a2b-9c88-4478-9389-012ea28e68dd)
![IMG-20241205-WA0012](https://github.com/user-attachments/assets/2f15af88-ad2b-4aad-82bb-110da2869fb1)
![IMG-20241205-WA0011](https://github.com/user-attachments/assets/f9dbc78e-44b3-4c0c-8eed-9bd7358dc705)
![IMG-20241205-WA0010](https://github.com/user-attachments/assets/2f0098e6-a08a-4413-bbda-f6f3f5d508a0)
![IMG-20241205-WA0009](https://github.com/user-attachments/assets/02eb68f0-7dbb-4b1b-943f-6588f219e9bd)
![IMG-20241205-WA0008](https://github.com/user-attachments/assets/4b9ae8cb-ee65-4bfe-986d-0ea8f7968ddc)
![IMG-20241205-WA0013](https://github.com/user-attachments/assets/95be4732-24f0-4de2-8917-ff2a6bf3ef21)


---

## Tech Stack

- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3 Design)
- **Backend**: Firebase Authentication and Firestore
- **Tools**: Android Studio
- **Additional Libraries**:
  - `com.google.android.material:material:1.10.0`
  - `com.google.firebase:firebase-firestore-ktx`
  - `com.google.firebase:firebase-auth-ktx`

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Akshat2713/campus-connect.git
   ```
2. Open the project in Android Studio.
3. Configure Firebase:
   - Add your Firebase project credentials (`google-services.json`) to the `app/` directory.
4. Build and run the app on an Android emulator or physical device.

---

## Usage

1. **Registration**: New users can register using their university details and create a profile.
2. **Search and Connect**: Use the search feature to find students or faculty based on specific skills or requirements.
3. **Edit Profile**: Update profile information, skills, and social links.
4. **Skill Testing**: Take quizzes to evaluate your expertise in various domains.
5. **Chat**: Initiate conversations for collaboration or guidance.

---

## Database Structure

The app's database follows a Firestore structure with the following key collections:

### `users` Collection
- **Document ID**: `entryNumber`
- Fields:
  - `name`, `email`, `branch`, `batch`, `phoneNumber`
  - `linkedin`, `github`
  - `skills` (Array of objects with fields: `skill`, `proficiency`, `experience`)


### `chats` Collection
- Stores real-time chat data between users.

---

## Future Enhancements

- Integration with third-party APIs like LinkedIn for profile validation.
- Push notifications for new internship postings or messages.
- Gamification elements to encourage skill development.
- Analytics dashboard for faculty to analyze student engagement.

---

## Contribution

Contributions are welcome! Follow these steps:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature description"
   ```
4. Push to the branch:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## Acknowledgments

- **SMVDU**: For providing inspiration and resources to build this app.
- **Firebase**: For its robust backend solutions.
- **Jetpack Compose**: For modern UI development in Android.

---

Feel free to update this README with any additional features or customizations you implement!

