package com.example.playlistmaker.media.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var imageUriName: String? = null
    private var playlistName: String? = null
    private var playlistDescription: String? = null

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            checkShowDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialToolbar.setNavigationOnClickListener { checkShowDialog() }

        initTextWatchers()

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistImage.setImageURI(uri)
                    imageUriName = imageUriName()
                    saveImageToPrivateStorage(uri, imageUriName!!)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.playlistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callback.isEnabled = false
    }

    private fun initTextWatchers() {
        binding.etPlaylistName.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.etPlaylistName.background = getDrawable(
                    requireContext(),
                    R.drawable.edittext_border_not_empty
                )
                binding.tvPlaylistName.isVisible = true
                binding.createPlaylistButton.setBackgroundColor(
                    getColor(
                        requireContext(),
                        R.color.main_background_color
                    )
                )

                playlistName = text.toString()
                binding.createPlaylistButton.setOnClickListener { createPlaylistButton() }
            } else {
                binding.etPlaylistName.background = getDrawable(
                    requireContext(),
                    R.drawable.edittext_border_empty
                )
                binding.tvPlaylistName.isVisible = false
                binding.createPlaylistButton.setBackgroundColor(
                    getColor(
                        requireContext(),
                        R.color.theme_color_edittext
                    )
                )
            }
        }

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.etDescription.background = getDrawable(
                    requireContext(),
                    R.drawable.edittext_border_not_empty
                )
                binding.tvDescription.isVisible = true

                playlistDescription = text.toString()
            } else {
                binding.etDescription.background = getDrawable(
                    requireContext(),
                    R.drawable.edittext_border_empty
                )
                binding.tvDescription.isVisible = false
            }
        }
    }

    private fun createPlaylistButton() {

        Toast.makeText(
            requireContext(),
            "Плейлист $playlistName создан",
            Toast.LENGTH_SHORT
        ).show()

        viewModel.insertPlaylist(
            playlistName!!,
            playlistDescription,
            imageUriName,
        )

        try {
            findNavController().navigateUp()
        } catch (e: Exception) {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    private fun checkShowDialog() {
        if (!imageUriName.isNullOrEmpty() ||
            !playlistName.isNullOrEmpty() ||
            !playlistDescription.isNullOrEmpty()
        ) {
            showDialog()
        } else {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating_a_playlist))
            .setMessage(getString(R.string.all_unsaved_data_will_be_lost))
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
            .setNegativeButton(getString(R.string.end)) { _, _ ->
                try {
                    findNavController().navigateUp()
                } catch (e: Exception) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri, imageName: String) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, imageName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun imageUriName(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val currentDate = Date(currentTimeMillis)
        val formatter = SimpleDateFormat("ddMMyyyyHHmmss")
        return formatter.format(currentDate).toString()
    }
}