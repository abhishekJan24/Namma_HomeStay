package com.namma.homestay.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.namma.homestay.data.model.DailyMenu
import com.namma.homestay.data.model.HomeProfile
import com.namma.homestay.data.model.Inquiry
import com.namma.homestay.data.model.LocalGuide
import com.namma.homestay.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ✅ Changed from ViewModel to AndroidViewModel so we can access Context
// Context is needed to read image files from the device
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()
    private val context = application.applicationContext

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    // ---------------- PROFILE ----------------

    private val _profile = MutableLiveData<HomeProfile>()
    val profile: LiveData<HomeProfile> = _profile

    // ---------------- MENU ----------------

    private val _menuItems = MutableLiveData<List<DailyMenu>>()
    val menuItems: LiveData<List<DailyMenu>> = _menuItems

    // ---------------- INQUIRIES ----------------

    private val _inquiries = MutableLiveData<List<Inquiry>>()
    val inquiries: LiveData<List<Inquiry>> = _inquiries

    // ---------------- GUIDES ----------------

    private val _guides = MutableLiveData<List<LocalGuide>>()
    val guides: LiveData<List<LocalGuide>> = _guides

    private var menuListener: ListenerRegistration? = null
    private var inquiryListener: ListenerRegistration? = null
    private var guidesListener: ListenerRegistration? = null

    init {
        loadProfile()
        listenToMenu()
        listenToInquiries()
        listenToGuides()
    }

    // ---------------- PROFILE ----------------

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProfile()?.let {
                    _profile.value = it
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveProfile(profile: HomeProfile, roomUri: Uri?, toiletUri: Uri?, farmUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ✅ No Firebase Storage — convert each image to Base64 on background thread
                var roomImageData = profile.roomImageUrl
                var toiletImageData = profile.toiletImageUrl
                var farmImageData = profile.farmImageUrl

                withContext(Dispatchers.IO) {
                    roomUri?.let {
                        roomImageData = repository.convertImageToBase64(context, it)
                    }
                    toiletUri?.let {
                        toiletImageData = repository.convertImageToBase64(context, it)
                    }
                    farmUri?.let {
                        farmImageData = repository.convertImageToBase64(context, it)
                    }
                }

                val updatedProfile = profile.copy(
                    roomImageUrl = roomImageData,
                    toiletImageUrl = toiletImageData,
                    farmImageUrl = farmImageData
                )

                repository.saveProfile(updatedProfile)
                _profile.value = updatedProfile
                _successMessage.value = "Profile saved successfully!"

            } catch (e: Exception) {
                _errorMessage.value = "Failed to save profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------------- MENU ----------------

    private fun listenToMenu() {
        menuListener = repository.getMenuRef()
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull {
                    it.toObject(DailyMenu::class.java)
                }
                _menuItems.value = list
            }
    }

    fun addMenuItem(name: String, price: String) {
        viewModelScope.launch {
            try {
                val menuItem = DailyMenu(itemName = name, price = price)
                repository.addMenuItem(menuItem)
                _successMessage.value = "Menu item added"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add menu item"
            }
        }
    }

    // ---------------- INQUIRIES ----------------

    private fun listenToInquiries() {
        inquiryListener = repository.getInquiriesRef()
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull {
                    it.toObject(Inquiry::class.java)
                }
                _inquiries.value = list
            }
    }

    // ---------------- GUIDES ----------------

    private fun listenToGuides() {
        guidesListener = repository.getGuidesRef()
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull {
                    it.toObject(LocalGuide::class.java)
                }
                _guides.value = list
            }
    }

    fun addGuide(name: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ✅ No Firebase Storage — convert guide image to Base64
                var imageData = ""
                withContext(Dispatchers.IO) {
                    imageUri?.let {
                        imageData = repository.convertImageToBase64(context, it)
                    }
                }

                repository.addGuide(
                    LocalGuide(
                        name = name,
                        description = description,
                        imageUrl = imageData  // stores Base64 string
                    )
                )
                _successMessage.value = "Guide added!"

            } catch (e: Exception) {
                _errorMessage.value = "Failed to add guide: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------------- COMMON ----------------

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        menuListener?.remove()
        inquiryListener?.remove()
        guidesListener?.remove()
    }
}