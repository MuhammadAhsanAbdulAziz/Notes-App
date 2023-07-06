package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notesapp.api.NoteApi
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteApi: NoteApi) {

    private val _noteResponseLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    private val _statusLiveData = MutableLiveData<NetworkResult<String>>()

    val noteResponseLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _noteResponseLiveData
    val statusLiveData: LiveData<NetworkResult<String>>
        get() = _statusLiveData

    suspend fun getNotes() {
        _noteResponseLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.getNotes()
        handleResponse(response)
    }

    suspend fun createNote(noteRequest: NoteRequest){
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.addNotes(noteRequest)
        handleResponse(response,"Note Created")

    }

    suspend fun deleteNote(noteId: String){
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.deleteNotes(noteId)
        handleResponse(response,"Note Deleted")
    }

    suspend fun updateNote(noteId : String, noteRequest: NoteRequest){
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = noteApi.updateNotes(noteId,noteRequest)
        handleResponse(response,"Note Updated")
    }

    private fun handleResponse(response: Response<NoteResponse>,message : String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(message))
        } else {
            _statusLiveData.postValue(NetworkResult.Success("Something went wrong"))
        }
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