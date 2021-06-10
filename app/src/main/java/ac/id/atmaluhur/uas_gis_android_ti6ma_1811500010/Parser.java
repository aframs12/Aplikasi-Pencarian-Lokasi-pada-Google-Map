package ac.id.atmaluhur.uas_gis_android_ti6ma_1811500010;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {
    public List<HashMap<String, String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try{
            Log.d("Place", "parse");
            jsonObject = new JSONObject((String) jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        }catch (Exception e){
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return  getPlaces(jsonArray);
    }
    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray){
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");
        for(int i = 0; i < placesCount; i++){
            try{
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");
            }catch (JSONException e){
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return placesList;
    }
    private HashMap<String, String> getPlace(JSONObject googlePlaceJson){
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String Longitude = "";
        String reference = "";

        Log.d("getPlace", "Entered");

        try{
            if (!googlePlaceJson.isNull("name")){
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")){
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            Longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", Longitude);
            googlePlaceMap.put("reference", reference);
            Log.d("getPlace", "Putting Places");
        }catch (JSONException e){
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }
        return  googlePlaceMap;
    }
}
