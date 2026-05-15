package com.example.nammapustaka.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.R
import com.example.nammapustaka.databinding.FragmentAddEditBookBinding
import com.example.nammapustaka.utils.QrCodeGenerator
import com.example.nammapustaka.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch
import java.io.File

class AddEditBookFragment : Fragment() {

    private var _binding: FragmentAddEditBookBinding? = null
    private val binding get() = _binding!!

    private val vm: AddEditBookViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    private val bookId: Long by lazy { arguments?.getLong("bookId", 0L) ?: 0L }
    private var photoUri: Uri? = null

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) launchCamera() else Toast.makeText(requireContext(), "Camera denied", Toast.LENGTH_SHORT).show()
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && photoUri != null) {
            Glide.with(this).load(photoUri).into(binding.ivCover)
        }
    }

    private val pickGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            Glide.with(this).load(uri).into(binding.ivCover)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val qr = vm.newQrPayload()
        binding.etQr.setText(qr)
        binding.ivQr.setImageBitmap(QrCodeGenerator.encode(qr, 480))

        lifecycleScope.launch {
            val existing = vm.load(bookId)
            if (existing != null) {
                binding.toolbar.title = getString(R.string.edit_book)
                binding.etTitle.setText(existing.title)
                binding.etAuthor.setText(existing.author)
                binding.etCategory.setText(existing.category)
                binding.etDescription.setText(existing.description)
                binding.etPages.setText(existing.totalPages.toString())
                binding.etQr.setText(existing.qrCode)
                binding.ivQr.setImageBitmap(QrCodeGenerator.encode(existing.qrCode, 480))
                if (!existing.imageUri.isNullOrBlank()) {
                    photoUri = Uri.parse(existing.imageUri)
                    Glide.with(this@AddEditBookFragment).load(photoUri).into(binding.ivCover)
                }
            }
        }

        binding.btnCamera.setOnClickListener {
            val ok = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            if (ok) launchCamera() else requestCamera.launch(Manifest.permission.CAMERA)
        }
        binding.btnGallery.setOnClickListener { pickGallery.launch("image/*") }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text?.toString().orEmpty()
            val author = binding.etAuthor.text?.toString().orEmpty()
            val category = binding.etCategory.text?.toString().orEmpty()
            val desc = binding.etDescription.text?.toString().orEmpty()
            val pages = binding.etPages.text?.toString()?.toIntOrNull() ?: 0
            val qrCode = binding.etQr.text?.toString().orEmpty()
            if (title.isBlank() || author.isBlank() || category.isBlank()) {
                Toast.makeText(requireContext(), "Fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.save(
                bookId = bookId,
                title = title,
                author = author,
                category = category,
                description = desc,
                pages = pages,
                imageUri = photoUri?.toString(),
                qrCode = qrCode
            ) { r ->
                Toast.makeText(requireContext(), r.fold(onSuccess = { "Saved" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_LONG).show()
                if (r.isSuccess) findNavController().popBackStack()
            }
        }
    }

    private fun launchCamera() {
        val file = File.createTempFile("np_cover_", ".jpg", requireContext().cacheDir)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        photoUri = uri
        takePicture.launch(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
