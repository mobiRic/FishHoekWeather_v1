package mobiric.fhbsc.weather.model;

import lib.gson.MyGson;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a single weather reading data point as received from the server.
 */
public class WeatherReading
{
	/*
	 * JSON string is: {"windSpeed":"25 knots", "windDir":"135&#176;", "windGust":"31 knots",
	 * "windGustDir":"135&#176;", "barometer":"1010.5 mbar", "outTemp":"20.4&#176;C",
	 * "outTempMin":"19.0&#176;C", "outTempMax":"21.5&#176;C"}
	 */

	@SerializedName("Time")
	public String time;
	public String windSpeed;
	public String windDir;
	public String windGust;
	public String windGustDir;
	public String barometer;
	public String outTemp;
	public String outTempMin;
	public String outTempMax;
	public String rainRate = "0mm";
	public String rainRateMin;
	public String rainRateMax;

	@Override
	public String toString()
	{
		return MyGson.PARSER.toJson(this);
	}

}
