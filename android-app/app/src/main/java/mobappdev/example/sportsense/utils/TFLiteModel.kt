package mobappdev.example.sportsense.utils

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteModel(context: Context) {
    private var interpreter: Interpreter? = null

    init {
        try {
            interpreter = Interpreter(loadModelFile(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Ladda modellen från assets/movement_model.tflite
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("movement_model.tflite")
        val inputStream = fileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Kör inferens på ett nytt sensorvärde
    fun predict(inputData: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(600 * 7 * 4) // 600 time steps, 7 features, 4 bytes per float
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(inputData)

        val outputBuffer = ByteBuffer.allocateDirect(21 * 4) // 21 klasser (eller hur många du har)
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter?.run(inputBuffer, outputBuffer)

        val outputArray = FloatArray(21) // Justera beroende på antal klasser
        outputBuffer.asFloatBuffer().get(outputArray)
        return outputArray
    }

    fun close() {
        interpreter?.close()
    }
}