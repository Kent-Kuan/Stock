package stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import utils.WebUtils;

@Service
public class StockForLINEServices {
	private final String TWSE_URL = "http://mis.twse.com.tw/stock/index.jsp";
	private final String TWSE_GETSTOCK_API_URL = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?json=1&delay=0";
	
	
	public String getStockDetails(String stockNum){
		String getStockUrl = TWSE_GETSTOCK_API_URL + "&ex_ch=tse_" + stockNum +".tw" + "&_=" + System.currentTimeMillis();
		Map<String,List<String>> headers = new HashMap<String,List<String>>();
		headers.put("Cookie", getCookies(TWSE_URL));
		return transferStockDetails(WebUtils.getUrl(getStockUrl, headers),stockNum);
	}
	
	private List<String> getCookies(String urlString){
		return WebUtils.getCookieForAPI(urlString, null);
	}
	
	private String transferStockDetails(String stockDetailsString, String stockNum){
		JSONObject json;
		try {
			json = new JSONObject(stockDetailsString).optJSONArray("msgArray").optJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
			return "Error! JSONException.";
		}
		String stockName = json.optString("n");
		String recentTradePrice = json.optString("z");
		String yesterEndPrice = json.optString("y");
		String diffStr = String.valueOf(Double.valueOf(recentTradePrice)-Double.valueOf(yesterEndPrice));
		String presentStr = String.valueOf(Math.round(Double.valueOf(diffStr)/Double.valueOf(yesterEndPrice)*10000)/100.0);
		StringBuffer sb = new StringBuffer();
		sb.append("[" + stockNum + "]" + stockName + " \n");
		sb.append("最近成交價：" + recentTradePrice + ", \n");
		sb.append("漲跌價" + String.format("%.2f", Float.valueOf(diffStr)) + ", \n");
		sb.append("漲跌百分比：" + presentStr + "% \n");
		return sb.toString();
	}
}
