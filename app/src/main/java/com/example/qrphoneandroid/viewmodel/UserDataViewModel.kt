package com.example.qrphoneandroid.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.example.qrphoneandroid.model.UserData
import com.example.qrphoneandroid.service.QRCodeService
import com.example.qrphoneandroid.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDataViewModel(application: Application) : AndroidViewModel(application) {

    private val storageService = StorageService(application)
    private val qrCodeService = QRCodeService()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _isDataSaved = MutableStateFlow(false)
    val isDataSaved: StateFlow<Boolean> = _isDataSaved.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    init {
        val loaded = storageService.loadUserData()
        if (loaded != null) {
            _userData.value = loaded
            _isDataSaved.value = true
        }
    }

    fun saveData(firstName: String, lastName: String, phoneNumber: String, email: String) {
        val sanitizedFirst = firstName.trim().take(50)
        val sanitizedLast = lastName.trim().take(50)
        val sanitizedPhone = phoneNumber.trim().take(20)
        val sanitizedEmail = email.trim().take(100).takeIf { it.isNotEmpty() }

        if (sanitizedFirst.isEmpty()) {
            _validationError.value = "El nombre es obligatorio"
            return
        }
        if (sanitizedLast.isEmpty()) {
            _validationError.value = "El apellido es obligatorio"
            return
        }
        val phoneRegex = Regex("^\\+?[0-9\\s\\-().]{7,20}$")
        if (!phoneRegex.matches(sanitizedPhone)) {
            _validationError.value = "Número de teléfono inválido"
            return
        }
        if (sanitizedEmail != null) {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            if (!emailRegex.matches(sanitizedEmail)) {
                _validationError.value = "Correo electrónico inválido"
                return
            }
        }

        val data = UserData(sanitizedFirst, sanitizedLast, sanitizedPhone, sanitizedEmail)
        storageService.saveUserData(data)
        _userData.value = data
        _isDataSaved.value = true
        _validationError.value = null
    }

    fun deleteData() {
        storageService.deleteUserData()
        _userData.value = null
        _isDataSaved.value = false
        _validationError.value = null
    }

    fun editData() {
        _isDataSaved.value = false
        _validationError.value = null
    }

    fun cancelEdit() {
        if (_userData.value != null) {
            _isDataSaved.value = true
            _validationError.value = null
        }
    }

    fun generateQRCode(): Bitmap? {
        val data = _userData.value ?: return null
        val content = buildString {
            append(data.firstName)
            append("\n")
            append(data.lastName)
            append("\n")
            append(data.phoneNumber)
            data.email?.let {
                append("\n")
                append(it)
            }
        }
        if (content.length > 500) return null
        return qrCodeService.generateQRCode(content)
    }

    fun clearValidationError() {
        _validationError.value = null
    }
}
