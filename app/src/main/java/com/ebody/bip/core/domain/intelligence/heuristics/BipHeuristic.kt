package com.ebody.bip.core.domain.intelligence.heuristics

import com.ebody.bip.core.domain.intelligence.model.BipRiskLevel

/**
 * Contrato base para uma regra heurística do Agente BIP.
 * Retorna um nível de risco caso a regra seja acionada, ou null se a condição não se aplicar.
 */
fun interface BipHeuristic {
    suspend fun evaluate(): BipRiskLevel?
}