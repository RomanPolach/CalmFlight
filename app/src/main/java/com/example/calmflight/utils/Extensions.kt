package com.example.calmflight.utils

import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.HtmlCompat
import com.example.calmflight.model.LearnItem

@Composable
fun LearnItem.answer(): String {
    val context = LocalContext.current
    val text = context.resources.getText(this.answerRes)
    if (text is Spanned) {
        // Convert to HTML to preserve bold tags
        val html = HtmlCompat.toHtml(text, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        // Remove paragraph tags and convert newlines properly
        return html
            .replace("<p dir=\"ltr\">", "")
            .replace("</p>", "<br><br>")
            //  .replace("\n", "<br>")
            .trim()
    }
    return text.toString()
}

