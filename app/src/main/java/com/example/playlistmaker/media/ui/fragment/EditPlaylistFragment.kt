package com.example.playlistmaker.media.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class EditPlaylistFragment : Fragment() {

    private var playlistId: Long? = null

    private var _binding: FragmentEditPlaylistBinding? = null

    private val binding get() = _binding!!

    private val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    private var imageUriName: String? = null
    private var playlistName: String? = null
    private var playlistDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlaylistBinding.inflate(layoutInflater, container, false)
        playlistId = requireArguments().getLong(ARGS_EDIT_PLAYLIST)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylist(playlistId!!)
        initTextWatchers()

        viewModel.getPlaylistInfo.observe(viewLifecycleOwner) { playlist ->
            setupPlaylistInfo(playlist)
        }

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
        binding.materialToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.editPlaylistButton.setOnClickListener { saveEditPlaylist() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupPlaylistInfo(playlist: Playlist) {
        if (playlist.image == null) {
            binding.playlistImage.setImageResource(R.drawable.placeholder_icon)
        } else {
            binding.playlistImage.setImageURI(loadImageUri(playlist.image))
            imageUriName = playlist.image
        }
        binding.etPlaylistName.setText(playlist.name)
        binding.etDescription.setText(playlist.description)
    }

    private fun initTextWatchers() {
        val playlistNameTxtWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    binding.etPlaylistName.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_border_not_empty
                    )
                    binding.tvPlaylistName.visibility = View.VISIBLE
                    binding.editPlaylistButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_background_color
                        )
                    )
                    playlistName = p0.toString()
                } else {
                    binding.etPlaylistName.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_border_empty
                    )
                    binding.tvPlaylistName.visibility = View.GONE
                    binding.editPlaylistButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.theme_color_edittext
                        )
                    )
                    playlistName = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etPlaylistName.addTextChangedListener(playlistNameTxtWatcher)

        val playlistDescriptionTxtWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    binding.etDescription.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_border_not_empty
                    )
                    binding.tvDescription.visibility = View.VISIBLE

                    playlistDescription = p0.toString()
                } else {
                    binding.etDescription.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edittext_border_empty
                    )
                    binding.tvDescription.visibility = View.GONE
                    playlistDescription = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etDescription.addTextChangedListener(playlistDescriptionTxtWatcher)
    }

    private fun saveEditPlaylist() {
        if (!playlistName.isNullOrEmpty()) {
            viewModel.editPlaylist(
                imageUriName,
                playlistName!!,
                playlistDescription

            )
            findNavController().navigateUp()
        }
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

    private fun loadImageUri(uriImage: String): Uri {
        val filePath =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, uriImage)
        return file.toUri()
    }

    companion object {
        private const val ARGS_EDIT_PLAYLIST = "ARGS_EDIT_PLAYLIST"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_EDIT_PLAYLIST to playlistId)
    }
}