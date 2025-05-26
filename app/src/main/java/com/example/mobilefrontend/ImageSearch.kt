package com.example.mobilefrontend

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilefrontend.databinding.FragmentImageSearchBinding
import com.example.mobilefrontend.itemCard.AdapterClass
import com.example.mobilefrontend.itemCard.DataClass
import com.example.mobilefrontend.repository.ApiResult
import com.example.mobilefrontend.viewmodels.CardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.min

class ImageSearch : Fragment() {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!

    private val cardModel: CardViewModel by viewModel()
    // CameraX variables
    private lateinit var adapter: AdapterClass
    private val dataList = ArrayList<DataClass>()
    private val originalDataList = ArrayList<DataClass>()
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
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

        // Setup RecyclerView
        binding.rvCardList.layoutManager = LinearLayoutManager(context)
        binding.rvCardList.setHasFixedSize(true)

        // Initialize adapter (set isGrid to false for list mode, true for grid mode)
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardModel.cardState.collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val cards = result.data ?: emptyList()

                            // Convert first card safely to DataClass
                            val mappedCard = cards.firstOrNull()?.let {
                                DataClass(
                                    it.id,
                                    it.image_url,
                                    it.name,
                                    "Paldea Evolved", // Replace with actual set name if needed
                                    it.rarity,
                                    it.code
                                )
                            }

                            if (mappedCard != null) {
                                // Populate original list for filtering
                                originalDataList.clear()
                                originalDataList.add(mappedCard)

                                // Copy all to dataList (visible list)
                                dataList.clear()
                                dataList.addAll(originalDataList)

                                adapter.notifyDataSetChanged()
                            } else {
                                Toast.makeText(context, "No cards found.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is ApiResult.Loading -> {
                            // Show loading UI (optional)
                        }
                        is ApiResult.Error -> {
                            Log.e("Home", "Error: ${result.message}")
                        }
                        null -> {}
                    }
                }
            }
        }
    }

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
                    val frameOverlay = binding.overlayView


                    val originalBitmap = BitmapFactory.decodeFile(savedPath)

                    // Get dimensions of the overlay view
                    val viewWidth = frameOverlay.width.toFloat()
                    val viewHeight = frameOverlay.height.toFloat()

                    // These match the rectangle drawn in FrameOverlayView
                    val frameWidth = viewWidth * 0.7f
                    val frameHeight = frameWidth * (8.8f / 6.3f)
                    val left = (viewWidth - frameWidth) / 2
                    val top = (viewHeight - frameHeight) / 2
                    val right = left + frameWidth
                    val bottom = top + frameHeight

                    // Scale those to the actual bitmap size
                    val bitmapWidth = originalBitmap.width.toFloat()
                    val bitmapHeight = originalBitmap.height.toFloat()
                    val scaleX = bitmapWidth / viewWidth
                    val scaleY = bitmapHeight / viewHeight

                    val cropLeft = (left * scaleX).toInt()
                    val cropTop = (top * scaleY).toInt()
                    val cropWidth = ((right - left) * scaleX).toInt()
                    val cropHeight = ((bottom - top) * scaleY).toInt()

                    // Clamp to avoid errors
                    val safeCropWidth = min(cropWidth, originalBitmap.width - cropLeft)
                    val safeCropHeight = min(cropHeight, originalBitmap.height - cropTop)

                    // Do the crop
                    val croppedBitmap = Bitmap.createBitmap(originalBitmap, cropLeft, cropTop, safeCropWidth, safeCropHeight)


                    // Save the cropped image
                    val croppedFile = File(
                        getOutputDirectory(),
                        "CROPPED_" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
                    )
                    saveBitmapToFile(croppedBitmap, croppedFile)
                    val croppedImagePath = croppedFile.absolutePath
                    Log.d("ImageSearch", "Cropped image saved at: $croppedImagePath")
                    Toast.makeText(requireContext(), "Cropped image saved: $croppedImagePath", Toast.LENGTH_SHORT).show()
                    performImageSearch(croppedImagePath)
                }
            }
        )
    }


    fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    /**
     * Perform an image search using the captured image.
     */
    private fun performImageSearch(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            Log.d("ImageSearch", "Image file path: ${imageFile.absolutePath}")
            cardModel.searchCards(imageFile)  // Call the real image upload method
        } else {
            Log.e("ImageSearch", "Image file not found at path: $imagePath")
        }
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
