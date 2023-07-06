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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.noteAdapter
import com.example.notesapp.databinding.FragmentMainBinding
import com.example.notesapp.models.NoteResponse
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()
    private lateinit var noteAdapter: noteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater,container,false)
        noteAdapter = noteAdapter(::onNoteClicked)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObersers()

        noteViewModel.getNotes()
        binding.noteList.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.noteList.adapter = noteAdapter
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
        }
    }

    private fun bindObersers() {
        noteViewModel.noteResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    noteAdapter.submitList(it.data)
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.error.toString(),Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun onNoteClicked(noteResponse: NoteResponse){
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_mainFragment_to_noteFragment,bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}