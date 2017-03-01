package stock;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;

import utils.WebUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockWithSpringTest {
	
	@Autowired
	public StockForLINEServices services;
//	@Test
	public void testGetUrlCookies(){
		System.out.println(WebUtils.getCookieForAPI("http://mis.twse.com.tw/stock/index.jsp", null));
	}
	
	@Test
	public void testToJSON(){
		String jsonString = services.getStockDetails("1504");
		System.out.println(jsonString);
		
	}
}
