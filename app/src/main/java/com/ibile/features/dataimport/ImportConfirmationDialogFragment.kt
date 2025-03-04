package com.ibile.features.dataimport

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.ibile.R
import com.ibile.core.BaseDialogFragment
import com.ibile.core.MvRxEpoxyController
import com.ibile.core.currentContext
import com.ibile.core.simpleController
import com.ibile.features.dataimport.DataImportViewModel.Companion.ViewCommand
import com.ibile.features.main.editfolder.EditFolderViewModel
import com.ibile.features.main.mapfiles.MapFilesViewModel

class ImportConfirmationDialogFragment : BaseDialogFragment() {

    private val viewModel: DataImportViewModel by activityViewModel()
    private val mapFilesViewModel: MapFilesViewModel by fragmentViewModel()
    private val editFolderViewModel: EditFolderViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectSubscribe(DataImportViewModel.State::viewCommand) {
            if (it is ViewCommand.DismissImportConfirmationDialog) {
                Toast.makeText(
                    currentContext,
                    R.string.toast_msg_data_import_started,
                    Toast.LENGTH_SHORT
                ).show()
                val progressBarHandler = ProgressBarHandler(context!!)
                viewModel.handleProgressBar(progressBarHandler)

                this.dismiss()

            }




        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(currentContext)
            .setMessage(getString(R.string.dialog_message_confirm_import))
            .setPositiveButton(R.string.text_yes, null)
            .setNegativeButton(R.string.text_no) { _, _ ->
                viewModel.onClickImportConfirmationDialogNegative()
            }
            .create()
            .apply {
                setOnShowListener { dialog ->
                    (dialog as AlertDialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {
                            viewModel.onClickImportConfirmationDialogPositive(mapFilesViewModel.getCurrentMapFile() ,editFolderViewModel)
                        }
                }
            }
    }

    override fun epoxyController(): MvRxEpoxyController = simpleController {
        /* no-op */
    }
}
