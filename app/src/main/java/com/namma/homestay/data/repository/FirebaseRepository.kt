package com.namma.homestay.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.namma.homestay.data.model.DailyMenu
import com.namma.homestay.data.model.HomeProfile
import com.namma.homestay.data.model.LocalGuide
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // ✅ Firebase Storage REMOVED — not available on free Spark plan
    // Images are now saved as Base64 strings directly inside Firestore
    // This works 100% on the free plan

    private val PROFILE_ID = "my_home"

    suspend fun saveProfile(profile: HomeProfile) {
        db.collection("home_profiles").document(PROFILE_ID).set(profile).await()
    }

    suspend fun getProfile(): HomeProfile? {
        val snapshot = db.collection("home_profiles").document(PROFILE_ID).get().await()
        return snapshot.toObject(HomeProfile::class.java)
    }

    // ✅ NEW: Convert image URI → compressed Base64 string
    // Instead of uploading to Storage, we shrink the image and save it as text in Firestore
    fun convertImageToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return ""

        // Step 1: Decode the image from the URI
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        if (originalBitmap == null) return ""

        // Step 2: Resize to max 600x600 to keep the size small enough for Firestore
        val maxSize = 600
        val width = originalBitmap.width
        val height = originalBitmap.height
        val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height, 1f)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // Step 3: Compress to JPEG at 60% quality
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val imageBytes = outputStream.toByteArray()

        // Step 4: Convert to Base64 string and return
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    suspend fun addMenuItem(item: DailyMenu) {
        val ref = db.collection("daily_menu").document()
        val newItem = item.copy(id = ref.id, addedAt = System.currentTimeMillis())
        ref.set(newItem).await()
    }

    fun getMenuRef() = db.collection("daily_menu").orderBy("addedAt", Query.Direction.DESCENDING)

    fun getInquiriesRef() = db.collection("inquiries").orderBy("timestamp", Query.Direction.DESCENDING)

    fun getGuidesRef() = db.collection("local_guides")

    suspend fun addGuide(guide: LocalGuide) {
        val ref = db.collection("local_guides").document()
        val newGuide = guide.copy(id = ref.id)
        ref.set(newGuide).await()
    }
}