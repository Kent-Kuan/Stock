package stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import utils.WebUtils;

@Service
public class StockForLINEServices {
	private final String TWSE_URL = "http://mis.twse.com.tw/stock/index.jsp";
	private final String TWSE_GETSTOCK_API_URL = "http://mis.twse.com.tw/stock/api/getStockInfo.jsp?json=1&delay=0";
	private final String TOKEN = "QRZlCUr/qxMdrCVZ1ca2etNohm0i0jSQFTqFIg8CfnTHX7AdrannGAJz4YkavOvDj3cQgxXrCIYFGbeSFOKNID8Vv2IXdYOz1O/7bxvfmSrQsBvHee2EmyAfS88MCGzS5sOvIN9gV/29VWl2QVOCWwdB04t89/1O/w1cDnyilFU=";
	
	public String getStockDetails(String stockNum){
		String getStockUrl = TWSE_GETSTOCK_API_URL + "&ex_ch=tse_" + stockNum +".tw" + "&_=" + System.currentTimeMillis();
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Cookie", getCookies(TWSE_URL).toString());
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
	
	public void replyToLINE(JSONObject requestBody){
		JSONArray jsonArray = requestBody.optJSONArray("events");
		JSONObject jsonObject = new JSONObject();
		String replyToken;
		String text;
		for(int i=0, size=jsonArray.length(); i<size; i++){
			jsonObject = jsonArray.optJSONObject(i);
		    replyToken = jsonObject.optString("replyToken");
		    text = extractStockNum(jsonObject.optJSONObject("message").optString("text"));
		    if("".equals(text))break;
		    replyToLINE(replyToken, getStockDetails(text));
		}
	}
	
	public void replyToLINE(String replyToken, String message){
		JSONObject postData = new JSONObject();
		JSONObject messageObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		messageObj.put("type", "text");
		messageObj.put("text", message);
		jsonArray.put(messageObj);
		postData.put("replyToken", replyToken);
		postData.put("messages", jsonArray);
		System.out.println(postData.toString());
		WebUtils.postUrl("https://api.line.me/v2/bot/message/reply", postData, getLINEproperties());
	}
	
	private Map<String, String> getLINEproperties(){
		Map<String, String> porperties = new HashMap<String, String>();
		porperties.put("Content-Type", "application/json");
		porperties.put("Authorization", "Bearer " + TOKEN);
		return porperties;
	}
	
	private String extractStockNum(String text){
		if(text.matches("貓貓,\\d{4}")){
			return text.replaceAll("貓貓,()", "$1");
		}
		return "";
	}
	
	
	
}
