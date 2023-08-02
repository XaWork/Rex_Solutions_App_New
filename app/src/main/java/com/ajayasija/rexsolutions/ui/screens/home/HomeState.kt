package com.ajayasija.rexsolutions.ui.screens.home

import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lead: InspectionLeadModel? = null,
    val pendingLeads: Int = 0,
)
