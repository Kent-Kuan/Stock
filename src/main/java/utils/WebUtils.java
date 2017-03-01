package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class WebUtils {
  private static HttpURLConnection getConnect(String urlString, Map<String, List<String>> porperties){
	  HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection)url.openConnection();
			if(porperties!=null){
				for(Map.Entry<String, List<String>> entry:porperties.entrySet()){
					urlConnection.setRequestProperty(entry.getKey(), entry.getValue().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlConnection;
  }
  
  public static String getUrl(String urlString, Map<String, List<String>> porperties){
	  HttpURLConnection urlConnection = getConnect(urlString, porperties);
	  StringBuffer sb = new StringBuffer();
	  String line ;
	  try {
		BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
		while((line=rd.readLine())!=null){
			sb.append(line);
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	  return sb.toString();
  }
  
  public static List<String> getCookieForAPI(String urlString, Map<String, List<String>> porperties){
	  HttpURLConnection urlConnection = getConnect(urlString, porperties);
	  for(Map.Entry<String, List<String>> headers : urlConnection.getHeaderFields().entrySet()){
		  if("cookie".equalsIgnoreCase(headers.getKey()) || "Set-Cookie".equalsIgnoreCase(headers.getKey())){
			  return headers.getValue();
		  }
	  }
	  throw new NullPointerException("Cookies is NULL!");
  }
}
