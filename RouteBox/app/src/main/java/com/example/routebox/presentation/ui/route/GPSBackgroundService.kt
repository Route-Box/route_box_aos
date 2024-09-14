package com.example.routebox.presentation.ui.route

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.routebox.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

// RouteWriteActivity에서 권한을 먼저 물어보기 때문에 아래에서는 권한 요청을 하지 않도록 구현!
@SuppressLint("MissingPermission")
// Service는 사용자와 상호작용 하지 않고, 백그라운드에서 작업을 수행할 수 있도록 하는 요소!
class GPSBackgroundService: Service() {

    private lateinit var notificationBuilder: NotificationCompat.Builder

    // Service와 Client 사이에 인터페이스 역할!!
    override fun onBind(p0: Intent?): IBinder? {
        Log.d("LOCATION-TEST", "결과 넣기")
        TODO("Not yet implemented")
    }

    // 다른 화면에서 startService를 했을 때 호출되는 함수!!
    // So, 작업하고자 하는 내용을 밑에다 작성
    // 상황에 따라 아래 함수와 함께, 작업을 멈추기 위한 stop 함수가 필요하다면 해당 함수도 함께 만들어주어야 한다!
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            SERVICE_START -> serviceStart()
            SERVICE_STOP -> serviceStop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ForegroundServiceType")
    private fun serviceStart() {
        setNotification()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // GPS 및 네트워크 권한 확인
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(!isGpsEnabled && !isNetworkEnabled) {
            Toast.makeText(this, ContextCompat.getString(this, R.string.check_gps_internet_permission), Toast.LENGTH_SHORT).show()
        }

        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = 60000 // 앱에서 선호하는 위치 업데이트 수신 간격
            fastestInterval = 60000 // 앱이 위치를 업데이트 할 수 있는 가장 빠른 간격
        }

        // 내부는 Network Provider가 정확도가 높고, 외부는 GPS Provider가 정확도가 더 높음!
        // 이 둘 사이에서 더욱 정확한 위치를 알아내기 위해 사용하는 것이 FusedLocationProvider!
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        // SDK 34 이상일 경우, Service의 Type 명시!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(1, notificationBuilder.build())
        }
    }

    // TODO: 코드 다시 확인
    private fun serviceStop() {
        stopForeground(true)
        stopSelf()
    }

    private fun setNotification() {
        // 위치 알림 띄워주기
        Log.d("LOCATION_SERVICE", "setNotification")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android SDK 26 이상부터는 NotificationChannel을 통해 Notification을 Build!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder = NotificationCompat.Builder(this, "location")
        } else {
            notificationBuilder = NotificationCompat.Builder(this)
        }

        notificationBuilder
            .setContentTitle("당신의 루트를 기록하고 있습니다.")
            .setSmallIcon(R.drawable.ic_logo_alarm)
            .setOngoing(true)
    }

    // 인식된 위치 정보를 받아오는 부분
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation != null) {
                val latitude = locationResult.lastLocation!!.latitude
                val longitude = locationResult.lastLocation!!.longitude
                Log.d("LOCATION_SERVICE", "$latitude, $longitude")
            }
        }
    }

    companion object {
        const val SERVICE_START = "SERVICE_START"
        const val SERVICE_STOP = "SERVICE_STOP"
    }
}