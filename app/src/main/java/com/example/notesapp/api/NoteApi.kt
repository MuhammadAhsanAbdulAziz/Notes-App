package com.example.notesapp.api

import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.models.UserRequest
import com.example.notesapp.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApi {

    @GET("/notes")
    suspend fun getNotes() : Response<List<NoteResponse>>

    @POST("/notes")
    suspend fun addNotes(@Body noteRequest: NoteRequest) : Response<NoteResponse>

    @PUT("/notes/{noteId}")
    suspend fun updateNotes(@Path("noteId") noteId:String, @Body noteRequest: NoteRequest) : Response<NoteResponse>

    @DELETE("/notes/{noteId}")
    suspend fun deleteNotes(@Path("noteId") noteId:String) : Response<NoteResponse>

}