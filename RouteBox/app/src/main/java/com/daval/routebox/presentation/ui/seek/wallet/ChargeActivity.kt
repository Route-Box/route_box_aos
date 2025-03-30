package com.daval.routebox.presentation.ui.seek.wallet

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetailsResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.daval.routebox.R
import com.daval.routebox.databinding.ActivityChargeBinding
import com.daval.routebox.domain.model.Point
import com.daval.routebox.presentation.ui.seek.SeekViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChargeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChargeBinding
    private lateinit var billingClient: BillingClient
    // 구매 요청 결과를 위한 리스너
    private lateinit var purchasesUpdatedListener: PurchasesUpdatedListener
    // 구매 가능한 상품(포인트)의 상세 정보를 가진 리스트
    private lateinit var pointDetailList: ProductDetailsResult
    // 구매한 상품의 소모 완료 여부를 확인하기 위한 리스너
    private lateinit var consumeListener: ConsumeResponseListener
    private val viewModel: SeekViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charge)

        binding.apply {
            viewModel = this@ChargeActivity.viewModel
            lifecycleOwner = this@ChargeActivity
        }

        initData()
        initClickListener()
        setInAppPurchase()
    }

    private fun initData() {
        viewModel.getMyInformation()
    }

    private fun initClickListener() {
        binding.icBack.setOnClickListener {
            finish()
        }
    }

    // 구글 플레이와 연결을 위한 기본 세팅
    private fun setInAppPurchase() {
        // 사용자의 구매 관련 업데이트를 받아오는 역할
        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            purchasesUpdated(billingResult, purchases)
        }

        // 일회성 제품을 구매했을 때 소모 완료를 하지 않으면, 새로 구매가 불가능!!
        // 아래 코드는 그 부분을 처리해주는 코드!
        consumeListener = ConsumeResponseListener { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // 서버에 포인트 구매 내역 전송
                viewModel.buyPoints()
            } else {
                // 포인트 재구매 불가능
                Log.d("PURCHASE-TEST", "소모 실패")
            }
        }

        // 결제 라이브러리와 앱 간의 통신을 위한 인터페이스
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        // 구글 플레이와 연결 (비동기)
        billingClient.startConnection(object : BillingClientStateListener {
            // 연결이 성공적으로 완료된 경우
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    CoroutineScope(Dispatchers.IO).launch {
                        getPurchasePoint()
                        CoroutineScope(Dispatchers.Main).launch {
                            purchasePointClickListener()
                        }
                    }
                }
            }
            // 구글과 연결이 끊겼을 때, 재연결을 하기 위해 필요한 함수
            override fun onBillingServiceDisconnected() {
                // TODO: 필요할 경우, 재연결을 위한 내용들 추가
                Log.d("PURCHASE-TEST", "error")
            }
        })
    }

    // 구매 가능한 상품들을 사용자에게 보여주기 위한 함수
    private suspend fun getPurchasePoint() {
        var pointIdList = Point.getPointIdList().map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        // 포인트 리스트 전달
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(pointIdList)

        // 인앱 상품의 세부 정보들을 요청
        withContext(Dispatchers.IO) {
            runCatching {
                pointDetailList = billingClient.queryProductDetails(params.build())
            }.onSuccess {
                Log.d("PURCHASE-TEST", "Success")
            }.onFailure { e ->
                Log.d("PURCHASE-TEST", "Failure $e")
            }
        }
    }

    // 각 포인트들을 눌렀을 때 결제해야 되는 가격 연결
    private fun purchasePointClickListener() {
        // pointIndex는 구글 콘솔에 정렬된 순서대로 지정
        binding.purchase4500.setOnClickListener {
            startPurchaseFlow(Point.POINT4500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT4500)
        }
        binding.purchase9500.setOnClickListener {
            startPurchaseFlow(Point.POINT9500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT9500)
        }
        binding.purchase14500.setOnClickListener {
            startPurchaseFlow(Point.POINT14500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT14500)
        }
        binding.purchase19500.setOnClickListener {
            startPurchaseFlow(Point.POINT19500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT19500)
        }
        binding.purchase24500.setOnClickListener {
            startPurchaseFlow(Point.POINT24500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT24500)
        }
        binding.purchase49500.setOnClickListener {
            startPurchaseFlow(Point.POINT49500.pointIndex)
            viewModel.setPurchasePoint(Point.POINT49500)
        }
    }

    // 구매가 이루어지는 단계
    private fun startPurchaseFlow(pointIndex: Int) {
        // pointIndex를 통해 어떤 포인트 상품을 구매하는지 구분!
        // ProductDetailsParams = 인앱 상품의 상세한 정보를 요청하기 위한 파라미터
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(pointDetailList.productDetailsList!![pointIndex])
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(this, billingFlowParams)
    }
    
    // 구매 시도 후 결과에 따라 처리하는 함수
    private fun purchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // 구매 성공
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams, consumeListener)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // 사용자가 구매를 취소했을 경우 처리
            Log.d("PURCHASE-TEST", "Canceled")
        } else {
            // 이외의 오류 처리
            Log.d("PURCHASE-TEST", "Error")
        }
    }
}