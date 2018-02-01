package com.webcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.utils.model.News;


public class ABSCBNCrawler implements CrawlerInterface
{
	private static final String ROOTURL = "http://news.abs-cbn.com";
	private News model;
	static int page;
	private HashSet<String> links;
	
	public ABSCBNCrawler() 
	{
		this.model = new News();
		page = 1;
		links = new HashSet<String>();
	}
	
	public void scrapeNews(String currentUrl)
	{
		if (!links.contains(currentUrl)) {
			try {
				if (links.add(currentUrl)) {
					System.out.println(currentUrl);
				}

				Document document = Jsoup.connect(currentUrl).get();
				Elements articles = document.getElementsByTag("article");
				String url = "";
				for (Element article : articles)
				{
					url = ROOTURL + (article.getElementsByTag("a").get(0).attr("href"));
					
					try 
					{
						this.model.setTitle( this.scrapeTitle(url) );
						this.model.setContent( this.scrapeContent(url) );
						System.out.println(this.model.getTitle());
						writeToFile();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					try 
					{
						TimeUnit.SECONDS.sleep(1);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				
				page++;
				currentUrl = ROOTURL + "/maiinit-na-balita?page=" + page;
				this.scrapeNews(currentUrl);
				
			} catch (IOException e) {
				System.err.println("For '" + currentUrl + "': " + e.getMessage());
			}
		}
	}
	
	private void writeToFile()
	{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
		File file = new File(s+"\\abs-cbn.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(this.model.getTitle() + " __:__ " + this.model.getContent());
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String scrapeContent(String url) throws IOException 
	{
		String contents = "";
		Document art = Jsoup.connect(url).get();
		Element content;
		try {
			content = art.getElementsByClass("article-content").get(0);
		} catch (Exception e) {
			content = art.getElementsByClass("media-caption").get(0);
		}
	
		for (Element par : content.getElementsByTag("p"))
			contents += par.text();
			
		return contents;
	}
	
	public String scrapeTitle(String url) throws IOException 
	{
		Document art = Jsoup.connect(url).get();
		String title = art.getElementsByClass("news-title").get(0).text();
		return title;
	}
	public static void main (String args[]) {

		ABSCBNCrawler c = new ABSCBNCrawler();
		c.scrapeNews("http://news.abs-cbn.com/maiinit-na-balita?page=1");
	}
	
}
	