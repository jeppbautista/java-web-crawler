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
public class BALITACrawler implements CrawlerInterface 
{

	private static final String ROOTURL = "http://balita.net.ph/category/balita-main/";
	private News model;
	static int page;
	private HashSet<String> links;
	
	public BALITACrawler()
	{
		this.model = new News();
		page = 1;
		links = new HashSet<String>();
	}

	@Override
	public void scrapeNews(String currentUrl) 
	{
		if (!links.contains(currentUrl))
		{
			try {
				if (links.add(currentUrl))
				{
					System.out.println(currentUrl);
				}
				Document document = Jsoup.connect(currentUrl).get();
				Element container = document.getElementById("container");
				Elements articles = container.getElementsByClass("archiveposts");
				String url = "";
				for (Element article : articles)
				{
					url = article.getElementsByTag("a").get(0).attr("href");
					
					this.model.setTitle( this.scrapeTitle(url) );
					this.model.setContent( this.scrapeContent(url) );
					System.out.println(this.model.getContent());
					writeToFile();
					
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
				currentUrl = ROOTURL + "/page/" + page;
				this.scrapeNews(currentUrl);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public String scrapeContent(String url) throws IOException 
	{
		String contents = "";
		Document art = Jsoup.connect(url).get();
		Element content = art.getElementsByClass("entry").get(0);
		Elements pars = content.getElementsByTag("p");
		int i = 0;
		for (Element par : pars)
		{
			if (i!=0)
				contents += par.text();
			i++;
		}
		
		return contents;
	}

	@Override
	public String scrapeTitle(String url) throws IOException 
	{
		Document art = Jsoup.connect(url).get();
		String title = art.getElementsByTag("h1").text();
		return title;
	}
	
	private void writeToFile()
	{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		
		File file = new File(s+"\\Balita-net.txt");
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
	
	public static void main(String[] args)
	{
		BALITACrawler c = new BALITACrawler();
		c.scrapeNews(ROOTURL);
	}

}
