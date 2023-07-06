package com.example.notesapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.adapter.noteAdapter
import com.example.notesapp.databinding.FragmentMainBinding
import com.example.notesapp.databinding.FragmentNoteBinding
import com.example.notesapp.models.NoteRequest
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.models.UserRequest
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding : FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private var note : NoteResponse? = null
    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        getData()

        binding.btnDelete.setOnClickListener{
            note?.let {
                noteViewModel.deleteNote(it!!._id)
            }
        }
        binding.btnSubmit.setOnClickListener {
            if(note != null) {
                noteViewModel.updateNote(note!!._id,getData())
            }
            else{
                noteViewModel.createNote(getData())
            }

        }

        bindObersers()
    }

    private fun getData(): NoteRequest {
        val title = binding.txtTitle.text.toString()
        val description = binding.txtDescription.text.toString()
        return NoteRequest(description,title)
    }

    private fun setInitialData() {
        val jsonNote = arguments?.getString("note")
        if(jsonNote != null){
            note = Gson().fromJson(jsonNote,NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.description)
            }
        }
        else{
            binding.btnDelete.isVisible = false
            binding.addEditText.text = "Add Note"
        }
    }

    private fun bindObersers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {
                }

                is NetworkResult.Loading -> {
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}