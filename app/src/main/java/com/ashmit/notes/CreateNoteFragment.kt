package com.ashmit.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.firestore

class CreateNoteFragment : Fragment() {
    // Inflate the layout for this fragment
    private lateinit var view: View
//
//    private lateinit var title :EditText
//    private lateinit var content: EditText
//    private lateinit var fab :FloatingActionButton
    private lateinit var title :String
    private lateinit var content: String
    private lateinit var fab :FloatingActionButton

    //for adindg the notes to the db
    private var database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_note, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show an exit confirmation dialog
                title  = view.findViewById<EditText?>(R.id.notesTitle).text.toString().trim()
                content = view.findViewById<EditText?>(R.id.notesContent).text.toString().trim()
                if(!check()){
                    val notesHashMap = hashMapOf(
                        "title" to title,
                        "content" to content
                    )
                    val userid = FirebaseAuth.getInstance().currentUser!!.uid
                    database.collection("user").document(userid).collection("notes").add(notesHashMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Note Created Successfully", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()

                        }.addOnFailureListener{
                            Toast.makeText(requireContext(), "Failed to add data ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    fragmentManager?.popBackStack()
                }
            }
        }
        )
        return view
    }

    private fun check() : Boolean{
        if(content.isEmpty() && title.isEmpty()){
            return true
        }else{
            return false
        }
    }
}