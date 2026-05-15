package com.example.nammapustaka.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.nammapustaka.NammaPustakaApp
import com.example.nammapustaka.databinding.FragmentIssueReturnBinding
import com.example.nammapustaka.scanner.BarcodeCameraAnalyzer
import com.example.nammapustaka.viewmodel.AppViewModelFactory
import com.google.mlkit.vision.barcode.BarcodeScanning
import kotlinx.coroutines.launch

class IssueReturnFragment : Fragment() {

    private var _binding: FragmentIssueReturnBinding? = null
    private val binding get() = _binding!!

    private val vm: IssueReturnViewModel by viewModels {
        AppViewModelFactory.from(requireActivity().application as NammaPustakaApp)
    }

    private var analyzer: BarcodeCameraAnalyzer? = null

    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) startCamera() else {
            Toast.makeText(requireContext(), "Camera required", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIssueReturnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        val ok = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        if (ok) startCamera() else requestCamera.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(requireContext())
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            val scanner = BarcodeScanning.getClient()
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val a = BarcodeCameraAnalyzer(scanner) { raw ->
                requireActivity().runOnUiThread { onQr(raw) }
            }
            analyzer = a
            analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), a)
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, selector, preview, analysis)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun onQr(raw: String) {
        analyzer?.stop()
        lifecycleScope.launch {
            val book = vm.findBook(raw) ?: run {
                Toast.makeText(requireContext(), "Unknown QR", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@launch
            }
            val active = vm.activeTransaction(book.bookId)
            if (active != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Return book?")
                    .setMessage(book.title)
                    .setPositiveButton("Return") { _, _ ->
                        vm.returnBook(book.bookId) { r ->
                            Toast.makeText(requireContext(), r.fold(onSuccess = { "Returned" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> findNavController().popBackStack() }
                    .show()
            } else {
                val repo = (requireActivity().application as NammaPustakaApp).repository
                val students = repo.listStudents()
                if (students.isEmpty()) {
                    Toast.makeText(requireContext(), "No students registered", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    return@launch
                }
                val names = students.map { it.name }.toTypedArray()
                AlertDialog.Builder(requireContext())
                    .setTitle("Issue to student")
                    .setItems(names) { _, which ->
                        val student = students[which]
                        vm.issue(book.bookId, student.userId, vm.defaultDueDate()) { r ->
                            Toast.makeText(requireContext(), r.fold(onSuccess = { "Issued" }, onFailure = { it.message ?: "Error" }), Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> findNavController().popBackStack() }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        analyzer?.stop()
        super.onDestroyView()
        _binding = null
    }
}
