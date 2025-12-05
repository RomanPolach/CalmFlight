package com.anxiousflyer.peacefulflight.utils

/**
 * Extension functions for easy TTS voice logging
 */

/**
 * Quick way to log all voices from TtsManager
 */
fun TtsManager.logAllVoices() {
    val logger = TtsVoiceLogger()
    logger.logAllVoices(getTts())
}

/**
 * Log voices grouped by language
 */
fun TtsManager.logVoicesByLanguage() {
    val logger = TtsVoiceLogger()
    logger.logVoicesByLanguage(getTts())
}

/**
 * Log only ubest offline voices
 */
fun TtsManager.logBestOfflineVoices() {
    val logger = TtsVoiceLogger()
    logger.logBestOfflineVoices(getTts())
}

/**
 * Log voices for specific language (e.g., "en", "es", "fr")
 */
fun TtsManager.logVoicesForLanguage(language: String) {
    val logger = TtsVoiceLogger()
    logger.logVoicesForLanguage(getTts(), language)
}

/**
 * Example usage in your code:
 * 
 * // In your Activity/Fragment where you have TtsManager:
 * ttsManager.logAllVoices()
 * ttsManager.logVoicesByLanguage()
 * ttsManager.logBestOfflineVoices()
 * ttsManager.logVoicesForLanguage("en")
 * 
 * // Or use the logger directly:
 * val logger = TtsVoiceLogger()
 * logger.logAllVoices(ttsManager.tts)
 */
