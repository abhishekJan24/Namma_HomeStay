# HomeStay - Homestay Management App

## How to Build & Run in Android Studio

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 (bundled with Android Studio)
- Android SDK API 34
- A physical Android device (API 24+) or emulator

### Steps

1. **Open the project**
   - Launch Android Studio
   - Click `File → Open`
   - Navigate to and select the `HomeStay` project folder
   - Click **OK**

2. **Wait for Gradle sync**
   - Android Studio will automatically download all required dependencies
   - Internet connection is required during first setup
   - Wait until “Gradle Sync Finished” appears

3. **Connect Firebase**
   - Create a Firebase project
   - Add your Android app
   - Download `google-services.json`
   - Place it inside:

   ```text
   app/google-services.json
   ```

4. **Run the application**
   - Connect an Android device using USB Debugging
   - OR create an emulator from `Tools → Device Manager`
   - Click the green ▶ Run button
   - Select your device and launch the app

---

# App Features

| Feature | Description |
|---|---|
| 🏠 Home Profile | Add and manage homestay details |
| 📸 Image Upload | Upload room, toilet, and farm images |
| 🍽 Daily Menu | Add and update daily food menus |
| 📩 Guest Inquiries | Receive and manage customer inquiries |
| 🗺 Local Guide | Provide local guide information |
| 🔄 Realtime Sync | Live updates using Firebase Firestore |

---

# Technologies Used

- Kotlin
- Android Studio
- Firebase Firestore
- Firebase Storage
- MVVM Architecture
- LiveData & ViewModel
- RecyclerView
- Fragments & Bottom Navigation

---

# Project Structure

```text
app/src/main/
├── java/com/namma/homestay/
│   ├── MainActivity.kt
│   ├── ui/
│   ├── adapters/
│   ├── model/
│   ├── repository/
│   ├── viewmodel/
│   └── firebase/
└── res/
    ├── layout/
    ├── drawable/
    ├── values/
    └── menu/
```

---

# About the Project

HomeStay is a Firebase-powered Android application designed for homestay owners to manage properties and guest services efficiently. The app allows users to upload property images, manage menus, handle guest inquiries, and provide local guide information using a clean interface and real-time Firebase synchronization.
