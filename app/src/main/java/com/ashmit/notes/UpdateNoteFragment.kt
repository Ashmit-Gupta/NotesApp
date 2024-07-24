package com.ashmit.notes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateNoteFragment : Fragment() {
    private lateinit var noteTitleEditText: EditText
    private lateinit var noteContentEditText: EditText
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    private var noteId: String? = null // Add this to identify the note to be updated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_update_note, container, false)
        // Inflate the layout for this fragment
        noteTitleEditText = view.findViewById(R.id.updateNotesTitle)
        noteContentEditText = view.findViewById(R.id.updateNotesContent)

        // Retrieve data from arguments
        val title = arguments?.getString("title")
        val content = arguments?.getString("content")
        noteId = arguments?.getString("noteId")

        // Display data in EditText fields
        noteTitleEditText.setText(title!!.trim())
        noteContentEditText.setText(content!!.trim())

        // Set up save button
         requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("DEBUG" , "In the FAB click !!")
                updateNote()
            }
        })
//            Log.d("DEBUG", "In the FAB click !!")
//            updateNote()

        return view
    }
    private fun updateNote() {
        val updatedTitle = noteTitleEditText.text.toString().trim()
        val updatedContent = noteContentEditText.text.toString().trim()

        if (noteId != null) {
            db.collection("user").document(userId).collection("notes").document(noteId!!)
                .update("title", updatedTitle, "content", updatedContent)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Note updated!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error updating note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(requireContext(), "The noteId is null !! , ${noteId}", Toast.LENGTH_SHORT).show()
        }
    }


}