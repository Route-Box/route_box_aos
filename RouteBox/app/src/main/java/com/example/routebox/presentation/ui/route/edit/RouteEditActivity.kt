package com.example.routebox.presentation.ui.route.edit

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.routebox.R
import com.example.routebox.databinding.ActivityRouteEditBinding
import com.example.routebox.domain.model.Route
import com.google.gson.Gson

class RouteEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteEditBinding

    private val viewModel: RouteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_edit)

        binding.apply {
            viewModel = this@RouteEditActivity.viewModel
            lifecycleOwner = this@RouteEditActivity
        }

        setInit()
        initClickListeners()
    }

    private fun setInit() {
        // intent가 넘어왔는지 확인
        intent.getStringExtra("route")?.let { routeJson ->
            val route = Gson().fromJson(routeJson, Route::class.java) // 값이 넘어왔다면 route 인스턴스에 gson 형태로 받아온 데이터를 넣어줌
            viewModel.setRoute(route)

            val navController: NavController by lazy {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.route_edit_container) as NavHostFragment
                navHostFragment.navController
            }

            // Safe Args를 사용하여 action을 생성하고 route 데이터를 전달
            val action = RouteEditFragmentDirections.actionRouteEditFragmentSelf(route)
            navController.navigate(action)
        }
    }

    private fun initClickListeners() {
        binding.routeEditBackIv.setOnClickListener {
            //TODO: 이전 프래그먼트로 이동
        }

        binding.routeEditCloseIv.setOnClickListener {
            // 홈 화면으로 이동
            finish()
        }
    }
}