package mobappdev.example.sportsense.utils

import android.content.Context
import android.util.Log
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
        Log.d("TFLiteModel", "Android Model Input Data: ${inputData.joinToString(", ")}")

        val inputBuffer = ByteBuffer.allocateDirect(inputData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        inputBuffer.put(inputData)
        inputBuffer.rewind()  // Viktigt: Reset bufferten innan inferens

        val outputBuffer = ByteBuffer.allocateDirect(21 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        interpreter?.run(inputBuffer, outputBuffer)

        val outputArray = FloatArray(21)
        outputBuffer.rewind()  // Viktigt: Reset output innan läsning
        outputBuffer.get(outputArray)

        Log.d("TFLiteModel", "Android Model Output: ${outputArray.joinToString(", ")}")

        return outputArray
    }

    fun close() {
        interpreter?.close()
    }
}