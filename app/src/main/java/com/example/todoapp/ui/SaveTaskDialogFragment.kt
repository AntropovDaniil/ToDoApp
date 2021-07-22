package com.example.todoapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R

class SaveTaskDialogFragment: DialogFragment() {

    companion object{
        @JvmStatic val TAG = SaveTaskDialogFragment::class.java.simpleName
        @JvmStatic val REQUEST_KEY = "$TAG:requestKey"
        @JvmStatic val KEY_RESPONSE = "$TAG:keyResponse"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to which))
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialogTitle)
            .setMessage(R.string.dialogMessage)
            .setPositiveButton(R.string.positiveButton, listener)
            .setNegativeButton(R.string.negativeButton, listener)
            .setNeutralButton(R.string.neutralButton, listener)
            .create()
    }
}