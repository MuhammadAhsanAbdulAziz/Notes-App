package com.example.notesapp.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notesapp.api.NoteApi
import com.example.notesapp.api.UserApi
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.models.UserRequest
import com.example.notesapp.models.UserResponse
import com.example.notesapp.utils.Constants.TAG
import com.example.notesapp.utils.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteApi: NoteApi) {

    private val _noteResponseLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()

    val noteResponseLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _noteResponseLiveData

    suspend fun getNotes() {
        val response = noteApi.getNotes()
        handleResponse(response)
    }

    private fun handleResponse(response: Response<List<NoteResponse>>) {
        if (response.isSuccessful && response.body() != null) {
            _noteResponseLiveData.postValue(NetworkResult.Success(response.body()))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _noteResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _noteResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}