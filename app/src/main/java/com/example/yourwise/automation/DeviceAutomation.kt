package com.example.yourwise.automation

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.media.AudioManager
import android.os.Build
import android.app.NotificationManager

class DeviceAutomation(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun executeCommand(command: String) {
        when {
            command == "wifi_on" -> enableWifi()
            command == "wifi_off" -> disableWifi()
            command == "bluetooth_on" -> enableBluetooth()
            command == "bluetooth_off" -> disableBluetooth()
            command.startsWith("brightness_") -> setBrightness(command.substringAfter("_").toIntOrNull() ?: 128)
            command.startsWith("volume_") -> setVolume(command.substringAfter("_").toIntOrNull() ?: 5)
            command == "airplane_on" -> enableAirplaneMode()
            command == "airplane_off" -> disableAirplaneMode()
        }
    }

    private fun enableWifi() {
        val intent = Intent(Settings.ACTION_WIFI_IP_SETTINGS)
        context.startActivity(intent)
    }

    private fun disableWifi() {
        val intent = Intent(Settings.ACTION_WIFI_IP_SETTINGS)
        context.startActivity(intent)
    }

    private fun enableBluetooth() {
        val intent = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
        context.startActivity(intent)
    }

    private fun disableBluetooth() {
        // Requires Bluetooth admin permission
        try {
            val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter?.disable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBrightness(value: Int) {
        try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setVolume(level: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0)
    }

    private fun enableAirplaneMode() {
        try {
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 1)
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            intent.putExtra("state", true)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableAirplaneMode() {
        try {
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            intent.putExtra("state", false)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}