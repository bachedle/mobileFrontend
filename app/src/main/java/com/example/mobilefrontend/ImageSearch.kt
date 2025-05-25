package com.example.mobilefrontend

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobilefrontend.databinding.FragmentImageSearchBinding
import com.example.mobilefrontend.viewmodels.CardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageSearch : Fragment() {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!

    private val cardModel: CardViewModel by viewModel()

    // CameraX variables
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQUEST_CODE_PERMISSIONS = 10
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the bottom navigation bar and system bars
        hideBottomNavigation()
        hideSystemBars()

        // Return button to navigate back
        binding.returnBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // Camera functionality – check permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Capture button in the camera container triggers image capture
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }

        // Gallery button (optional, if you want to implement gallery selection)
        binding.galleryButton.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery selection not implemented", Toast.LENGTH_SHORT).show()
        }

        // Initialize the camera executor for background operations
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /**
     * Hide the BottomNavigationView
     */
    private fun hideBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.GONE
    }

    /**
     * Show the BottomNavigationView
     */
    private fun showBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    /**
     * Start the CameraX preview.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configure the preview use case with a target aspect ratio
            val preview = Preview.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                .build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Initialize ImageCapture for taking photos
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                .build()

            // Select the default back camera
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

    /**
     * Capture an image with CameraX.
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create an output file with a time-stamped name
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
                    Log.e("ImageSearch", "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedPath = photoFile.absolutePath
                    Toast.makeText(requireContext(), "Photo captured: $savedPath", Toast.LENGTH_SHORT).show()
                    Log.d("ImageSearch", "Photo saved at: $savedPath")

                    // Trigger image search with the captured image
                    performImageSearch(savedPath)
                }
            }
        )
    }

    /**
     * Perform an image search using the captured image.
     */
    private fun performImageSearch(imagePath: String) {
        val imageFile = File(imagePath)
        cardModel.getCards(keyword = "Meowscarada") // Placeholder
        findNavController().popBackStack()
    }

    // Helper: Check whether camera permission has been granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    // Helper: Returns an output directory in the phone's Downloads folder for saved photos
    private fun getOutputDirectory(): File {
        // This method uses the public Downloads directory. (For Android 10+, consider using MediaStore.)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return downloadsDir
    }

    // Handle permission results
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

    /**
     * Hide the system bars for an immersive experience.
     */
    private fun hideSystemBars() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    /**
     * Restore the system bars and BottomNavigationView.
     */
    private fun showSystemBars() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsetsController.BEHAVIOR_DEFAULT)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the system bars and bottom navigation
        showSystemBars()
        showBottomNavigation()
        cameraExecutor.shutdown()
        _binding = null
    }
}
