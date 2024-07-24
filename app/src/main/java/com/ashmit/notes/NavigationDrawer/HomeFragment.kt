package com.ashmit.notes.NavigationDrawer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashmit.notes.CreateNoteFragment
import com.ashmit.notes.Helper.DialogBox
import com.ashmit.notes.NotesModel
import com.ashmit.notes.R
import com.ashmit.notes.RecyclerViewAdapter
import com.ashmit.notes.UpdateNoteFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var notesArrayList = ArrayList<NotesModel>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.homeFragRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter and set it to the RecyclerView
        recyclerViewAdapter = RecyclerViewAdapter(notesArrayList)
        recyclerView.adapter = recyclerViewAdapter

        recyclerViewAdapter.setOnItemClickListener(object:RecyclerViewAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val selectedNote = notesArrayList[position]
                val editNoteFrag = UpdateNoteFragment().apply {
                    arguments = Bundle().apply {
                        putString("noteId" , selectedNote.noteId)
                        putString("title" , selectedNote.title)
                        putString("content" , selectedNote.content)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_Container, editNoteFrag)
                    .addToBackStack(null)
                    .commit()
            }
        })

        recyclerViewAdapter.setOnItemLongClickListener(object : RecyclerViewAdapter.onItemLongClickListener {
            //dialog box

            override fun onItemLongClick(position: Int) {
                DialogBox(requireContext()).customDialogBox(requireContext(), "Delete" , "Are your sure you want to delete this note ?" , R.drawable.baseline_delete_24) {

                    val selectedNote = notesArrayList[position]
                    deleteNote(selectedNote.noteId!! , position)
                }
            }

        })

        fetchNotes()


        val fab: FloatingActionButton = view.findViewById(R.id.fabAddItem)
        fab.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_Container, CreateNoteFragment())
                    .addToBackStack(null)
                    .commit()
        }

        return view
    }

//    add ondestroy

    private fun fetchNotes() {
        db.collection("user").document(userId).collection("notes").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    notesArrayList.clear() // Clear existing data before adding new ones
                    for (data in result.documents) {
                        Log.d("Debug", "Starting")
                        val notes: NotesModel? = data.toObject(NotesModel::class.java)?.apply {
                            noteId = data.id
                        }
                        Log.d("Debug", notes.toString())
                        if (notes != null) {
                            Log.d("Debug", "In the if")
                            notesArrayList.add(notes)
                            Log.d("Debug", notesArrayList[0].toString())
                        }
                    }
                    // Notify adapter about data changes
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNote(noteId :String , position :Int){
        db.collection("user").document(userId).collection("notes").document(noteId).delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Note Delete Successfully", Toast.LENGTH_SHORT).show()
                notesArrayList.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Failed to Delete", Toast.LENGTH_SHORT).show()
            }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            notesArrayList = it.getStringArrayList("notes")
//        }
    }

    //for data passing
//    companion object {
//        private const val ARG_PARAM1 = "param1"
//        private const val ARG_PARAM2 = "param2"
//
//        @JvmStatic
//        fun newInstance(notes:ArrayList<NotesModel>) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(notesArrayList, notes)
//                }
//            }
//    }
}
