package com.example.sensorlab

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    private var temperature by mutableStateOf<Float?>(null)
    private var angularVelocity by mutableStateOf<AngularVelocity?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            MaterialTheme {
                SensorScreen(
                    temperature = temperature,
                    temperatureAvailable = temperatureSensor != null,
                    angularVelocity = angularVelocity,
                    gyroscopeAvailable = gyroscopeSensor != null
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        temperature = null
        angularVelocity = null
        temperatureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                val celsius = event.values[0]
                // Some virtual devices emit an invalid placeholder before the first reading.
                if (celsius.isFinite() && celsius >= -273.15f) {
                    temperature = celsius
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val (x, y, z) = event.values
                angularVelocity = AngularVelocity(x, y, z)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

private data class AngularVelocity(val x: Float, val y: Float, val z: Float)

@Composable
private fun SensorScreen(
    temperature: Float?,
    temperatureAvailable: Boolean,
    angularVelocity: AngularVelocity?,
    gyroscopeAvailable: Boolean
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Android Sensor Monitor", style = MaterialTheme.typography.headlineMedium)

            SensorValueCard("Ambient Temperature") {
                when {
                    !temperatureAvailable -> Text("Temperature sensor is not available.")
                    temperature == null -> Text("Waiting for sensor reading...")
                    else -> Text("%.1f °C".format(temperature))
                }
            }

            SensorValueCard("Gyroscope") {
                when {
                    !gyroscopeAvailable -> Text("Gyroscope is not available.")
                    angularVelocity == null -> Text("Waiting for sensor reading...")
                    else -> {
                        Text("X: %.3f rad/s".format(angularVelocity.x))
                        Text("Y: %.3f rad/s".format(angularVelocity.y))
                        Text("Z: %.3f rad/s".format(angularVelocity.z))
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorValueCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}
