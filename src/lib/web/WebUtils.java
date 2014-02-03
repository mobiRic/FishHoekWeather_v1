package lib.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

/**
 * Web Service helper methods.
 */
public class WebUtils
{

	/**
	 * Generic web service call.
	 * 
	 * @param request
	 *            {@link HttpGet} or {@link HttpPost} that defines the request to fetch.
	 * @return result of the call, or an error string
	 */
	public static String getHttpResponse(HttpRequestBase request)
	{
		StringBuilder sb = new StringBuilder();
		try
		{

			DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();

			if (statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				BufferedReader bReader =
						new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				String line = null;
				while ((line = bReader.readLine()) != null)
				{
					sb.append(line);
				}
			}
			else
			{
				sb.append(reason);
			}
		}
		catch (UnsupportedEncodingException ex)
		{
		}
		catch (ClientProtocolException ex1)
		{
		}
		catch (IOException ex2)
		{
		}
		return sb.toString();
	}
}
