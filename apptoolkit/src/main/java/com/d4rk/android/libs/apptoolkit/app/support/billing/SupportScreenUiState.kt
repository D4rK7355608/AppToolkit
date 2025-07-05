package com.d4rk.android.libs.apptoolkit.app.support.billing

import com.android.billingclient.api.ProductDetails

data class SupportScreenUiState(
    val error: String? = null,
    val products: List<ProductDetails> = emptyList()
)
