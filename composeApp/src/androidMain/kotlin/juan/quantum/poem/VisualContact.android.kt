package juan.quantum.poem

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.display.DisplayManager
import android.os.SystemClock
import android.util.Log
import android.view.Display
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
actual fun VisualContactEffect(
    updateEvent: MutableSharedFlow<Unit>,
    modifier: Modifier,
    isDebugMode: Boolean
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraWithFaceDetection(context, updateEvent, modifier, isDebugMode)
    }
}

@Composable
private fun CameraWithFaceDetection(
    context: Context,
    updateEvent: MutableSharedFlow<Unit>,
    modifier: Modifier = Modifier,
    isDebugMode: Boolean = false
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    var faceLandmarkerResult by remember { mutableStateOf<FaceLandmarkerResult?>(null) }
    var observerPresent by remember { mutableStateOf(false) }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val faceLandmarker = remember {
        val baseOptions = BaseOptions.builder().setModelAssetPath("face_landmarker.task").build()
        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumFaces(1)
            .setOutputFaceBlendshapes(true)
            .setResultListener { result, _ ->
                faceLandmarkerResult = result
                
                val landmarks = result.faceLandmarks()
                val blendshapes = result.faceBlendshapes()
                
                val isWatching = if (landmarks.isNotEmpty() && blendshapes.isPresent && blendshapes.get().isNotEmpty()) {
                    val categories = blendshapes.get()[0]
                    
                    val leftBlink = categories.find { it.categoryName() == "eyeBlinkLeft" }?.score() ?: 1f
                    val rightBlink = categories.find { it.categoryName() == "eyeBlinkRight" }?.score() ?: 1f
                    
                    leftBlink < 0.3f || rightBlink < 0.3f
                } else {
                    false
                }

                if (isWatching != observerPresent) {
                    observerPresent = isWatching
                    if (!isWatching) {
                        coroutineScope.launch { updateEvent.emit(Unit) }
                    }
                }
            }
            .setErrorListener { error ->
                Log.e("VisualContactEffect", "Face Landmarker Error: $error")
            }
            .build()
        FaceLandmarker.createFromOptions(context, options)
    }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lifecycleOwner, isDebugMode) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    detectLiveStream(imageProxy, faceLandmarker)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        
        try {
            if (isDebugMode) {
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
            } else {
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalyzer)
            }
        } catch (exc: Exception) {
            Log.e("VisualContactEffect", "Use case binding failed", exc)
        }
    }

    if (isDebugMode) {
        Box(modifier = modifier) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                faceLandmarkerResult?.let { result ->
                    val (canvasWidth, canvasHeight) = size
                    val landmarksList = result.faceLandmarks()
                    
                    if (landmarksList.isNotEmpty()) {
                        val landmarks = landmarksList[0]
                        val eyeConnections = FaceLandmarker.FACE_LANDMARKS_LEFT_EYE + 
                                         FaceLandmarker.FACE_LANDMARKS_RIGHT_EYE +
                                         FaceLandmarker.FACE_LANDMARKS_LEFT_IRIS +
                                         FaceLandmarker.FACE_LANDMARKS_RIGHT_IRIS
                        
                        eyeConnections.forEach { connection ->
                            val startLandmark = landmarks[connection.start()]
                            val endLandmark = landmarks[connection.end()]
                            
                            drawLine(
                                color = Color.Cyan,
                                start = Offset(
                                    x = startLandmark.x() * canvasWidth,
                                    y = startLandmark.y() * canvasHeight
                                ),
                                end = Offset(
                                    x = endLandmark.x() * canvasWidth,
                                    y = endLandmark.y() * canvasHeight
                                ),
                                strokeWidth = 2f
                            )
                        }
                    }
                }
            }
            
            Text(
                text = if (observerPresent) "WATCHING" else "NOT WATCHING",
                color = if (observerPresent) Color.Green else Color.Red,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            faceLandmarker?.close()
            cameraProviderFuture.get().unbindAll()
        }
    }
}

private fun detectLiveStream(imageProxy: ImageProxy, faceLandmarker: FaceLandmarker?) {
    imageProxy.use { proxy ->
        val rotation = proxy.imageInfo.rotationDegrees
        val width = proxy.width
        val height = proxy.height
        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        proxy.planes[0].buffer.rewind()
        bitmapBuffer.copyPixelsFromBuffer(proxy.planes[0].buffer)

        val matrix = Matrix().apply {
            postRotate(rotation.toFloat())
            postScale(-1f, 1f, width.toFloat(), height.toFloat())
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, width, height, matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        faceLandmarker?.detectAsync(mpImage, frameTime)
    }
}
