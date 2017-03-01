package stock;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.WebUtils;

public class StockTest {

	@Test
	public void test() {
		WebUtils webUtils = new WebUtils();
//		System.out.println(webUtils.getUrl("http://mis.twse.com.tw/stock/index.jsp", null));
		System.out.println(webUtils.getCookieForAPI("http://mis.twse.com.tw/stock/index.jsp", null));
	}

}
