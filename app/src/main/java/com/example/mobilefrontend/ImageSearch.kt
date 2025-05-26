package com.example.mobilefrontend

import android.Manifest
import android.app.Dialog // 🔧 added
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable // 🔧 added
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView // 🔧 added
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilefrontend.databinding.FragmentImageSearchBinding
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageSearch : Fragment() {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterClass
    private val dataList = ArrayList<DataClass>()
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQUEST_CODE_PERMISSIONS = 10
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    // 🔧 Loading dialog reference
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigation()
        hideSystemBars()

        binding.rvCardList.layoutManager = LinearLayoutManager(context)
        binding.rvCardList.setHasFixedSize(true)

        adapter = AdapterClass(dataList, isGrid = false) { selectedCard ->
            val action = SearchDirections.actionSearchToCardDetail(
                selectedCard.dataId,
                selectedCard.dataImage,
                selectedCard.dataCardName,
                selectedCard.dataCardSet,
                selectedCard.dataCardRarity,
                selectedCard.dataCardCode,
                false
            )
            findNavController().navigate(action)
        }
        binding.rvCardList.adapter = adapter

        loadSampleData()

        binding.returnBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery selection not implemented", Toast.LENGTH_SHORT).show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun loadSampleData() {
        dataList.clear()
        dataList.add(
            DataClass(
                dataId = 1,
                dataImage = "R.drawable.samplecard",
                dataCardName = "Meowscarada",
                dataCardSet = "Paldea Evolved",
                dataCardRarity = "Rare",
                dataCardCode = "PAL-001"
            )
        )
        adapter.notifyDataSetChanged()
    }

    private fun hideBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                .build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("ImageSearch", "Camera binding failed", exc)
                Toast.makeText(requireContext(), "Camera binding failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // 🔧 Show loading before capture
        showLoadingDialog()

        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    // 🔧 Hide loading on error
                    hideLoadingDialog()
                    Log.e("ImageSearch", "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // 🔧 Hide loading on success
                    hideLoadingDialog()
                    val savedPath = photoFile.absolutePath
                    Toast.makeText(requireContext(), "Photo captured: $savedPath", Toast.LENGTH_SHORT).show()
                    Log.d("ImageSearch", "Photo saved at: $savedPath")
                    loadSampleData()
                }
            }
        )
    }

    private fun getOutputDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return downloadsDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun hideSystemBars() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Let the window draw behind system bars so we can hide them.
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                // Hide both status and navigation bars.
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                // Allow them to be temporarily revealed with a swipe.
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
        }
    }

    private fun showSystemBars() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Restore the default window drawing behavior.
            window.setDecorFitsSystemWindows(true)
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 🔧 Clean up loading dialog
        hideLoadingDialog()
        showSystemBars()
        showBottomNavigation()
        cameraExecutor.shutdown()
        _binding = null
    }

    // 🔧 Utility to show the loading dialog with animated drawable
    private fun showLoadingDialog() {
        if (loadingDialog?.isShowing == true) return

        loadingDialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog?.setContentView(view)
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val loadingImage = view.findViewById<ImageView>(R.id.loading_image)
        val animationDrawable = loadingImage.drawable as? AnimationDrawable
        animationDrawable?.start()

        loadingDialog?.show()
    }

    // 🔧 Utility to hide the dialog
    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
