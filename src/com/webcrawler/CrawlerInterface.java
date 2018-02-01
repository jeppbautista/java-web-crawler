package com.webcrawler;
import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.utils.model.News;

public interface CrawlerInterface {
	void scrapeNews(String url);
	String scrapeContent(String url) throws IOException;
	String scrapeTitle(String url) throws IOException ;

}
