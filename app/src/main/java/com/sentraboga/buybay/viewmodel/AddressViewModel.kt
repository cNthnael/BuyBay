package com.sentraboga.buybay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sentraboga.buybay.data.Address
import com.sentraboga.buybay.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _adNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _adNewAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address){
        val valInput = validateInput(address)
        if (valInput){
            viewModelScope.launch {
                _adNewAddress.emit(Resource.Loading())
            }
            firestore.collection("user").document(auth.uid!!)
                .collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _adNewAddress.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _adNewAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _error.emit("Semua form wajib diisi!")
            }
        }
    }

    private fun validateInput(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }

}