package mobappdev.example.sportsense.ui.screens

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import mobappdev.example.sportsense.data.SensorData

@Composable
fun HRGraphScreen(sensorData: List<SensorData>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Heart Rate Graph",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { createLineChart(context, sensorData) },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

fun createLineChart(context: Context, sensorData: List<SensorData>): LineChart {
    val chart = LineChart(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        description = Description().apply { text = "Heart Rate over Time" }
        setBackgroundColor(Color.WHITE)
    }

    val hrEntries = sensorData.mapIndexed { index, data ->
        Entry(index.toFloat(), data.heartRate.toFloat())
    }

    val lineDataSet = LineDataSet(hrEntries, "Heart Rate").apply {
        color = Color.RED
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 4f
    }

    chart.data = LineData(lineDataSet)
    chart.invalidate() // Uppdatera grafen
    return chart
}
