package com.ebody.bip.core.domain.intelligence.heuristics

import com.ebody.bip.core.domain.intelligence.model.HeuristicResult

fun interface BipHeuristic {
    suspend fun evaluate(): HeuristicResult?
}