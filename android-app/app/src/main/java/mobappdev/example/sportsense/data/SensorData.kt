package mobappdev.example.sportsense.data

// Data klass för att hantera IMU- och hjärtfrekvensdata
data class SensorData(
    val timestamp: Long,
    val heartRate: Int,
    val accelX: Float,
    val accelY: Float,
    val accelZ: Float,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float
)