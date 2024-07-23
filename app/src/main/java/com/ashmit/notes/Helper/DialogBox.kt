package com.ashmit.notes.Helper

import android.content.Context
import androidx.appcompat.app.AlertDialog

class DialogBox (val context : Context){
//this is the normal Dialog box with 2 button yes and no
     fun showDialogBox( title :String , message : String ,onConfirm: () -> Unit ){
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
//            .setIcon(icon)
            .setCancelable(false)
            .setPositiveButton("Yes"){
                    _,_->
                    onConfirm()
            }
            .setNegativeButton("No"){
                    dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    //creating a custom alert dialog box , where the user will enter his password to confirm that he wants to delete his account
     fun customDialogBox(
        context: Context,
        title: String,
        message: String,
        icon: Int,
        onConfirm: () -> Unit ,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(icon)
        builder.setPositiveButton("Yes") { _, _ ->
            onConfirm()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}