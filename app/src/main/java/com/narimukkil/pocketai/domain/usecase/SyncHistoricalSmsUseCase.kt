package com.narimukkil.pocketai.domain.usecase

import com.narimukkil.pocketai.sms.sync.SmsSyncManager
import javax.inject.Inject

class SyncHistoricalSmsUseCase @Inject constructor(
    private val smsSyncManager: SmsSyncManager
) {
    suspend operator fun invoke(days: Int = 30): Int {
        return smsSyncManager.syncHistoricalSms(days)
    }
}
