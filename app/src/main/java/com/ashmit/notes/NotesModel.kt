package com.ashmit.notes

data class NotesModel(
    var noteId :String?,
    var content: String?,
    var title: String?
){
    // Explicitly defined no-argument constructor
    constructor() : this(null,null, null)
}
