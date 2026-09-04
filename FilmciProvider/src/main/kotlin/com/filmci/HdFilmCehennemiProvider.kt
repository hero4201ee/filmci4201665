package com.filmci

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class HdFilmCehennemiProvider : MainAPI() { // All providers must be an instance of MainAPI
    override var mainUrl = "https://www.hdfilmcehennemi.nl" 
    override var name = "HdFilmCehennemi"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override var lang = "tr"

    override val hasMainPage = true

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/arama/$query/"
        val document = app.get(url).document
        
        return document.select("div.movie-poster").mapNotNull {
            val title = it.selectFirst("h3.title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val image = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = image
            }
        }
    }
    
    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: return null
        val poster = document.selectFirst("div.poster img")?.attr("src")
        val plot = document.selectFirst("div.summary")?.text()
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = plot
        }
    }
    
    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        // Implement video extraction logic here
        // Usually involves finding iframe src and using an Extractor
        return true
    }
}