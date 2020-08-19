package me.noaz.testplugin.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.noaz.testplugin.maps.CustomLocation;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    /**
     * Retrives a string list from an array in a json object, the name of the array is given by jsonKey.
     * @param jsonObjectToRetrieveFrom The json object to retrive the list from
     * @param jsonKey The key for the array.
     * @return A list of string values for each entry in the retrived array.
     */
    public static List<String> jsonArrayToStringList(JsonObject jsonObjectToRetrieveFrom, String jsonKey) {
        List<String> list = new ArrayList<>();

        JsonArray jsonArray = getJsonArrayFromKey(jsonObjectToRetrieveFrom, jsonKey);

        if(jsonArray != null) {
            for (JsonElement element : jsonArray) {
                list.add(element.getAsJsonPrimitive().getAsString());
            }
        }

        return list;
    }

    /**
     * Creates a json array from a given list
     * @param list The list to transofrm to a json array
     * @return A JsonArray with the elements from the given list.
     */
    public static JsonArray stringListToJsonArray(List<String> list) {
        JsonArray jsonArray = new JsonArray();

        for(String element : list) {
            jsonArray.add(element);
        }

        return jsonArray;
    }

    /**
     * @param objectToRetrieveFrom The object to retrieve the array from
     * @param jsonKey The keyword for the array
     * @return A JsonArray retrieved from the object using the key
     */
    public static JsonArray getJsonArrayFromKey(JsonObject objectToRetrieveFrom, String jsonKey) {
        JsonArray jsonArray;
        try {
            jsonArray = objectToRetrieveFrom.getAsJsonArray(jsonKey);
        } catch(Exception e) {
            jsonArray = new JsonArray();
        }

        return jsonArray;
    }

    public static List<CustomLocation> getJsonObjectAsListOfCustomLocations(JsonArray arrayToRetrieveFrom) {
        JsonArray jsonArrayContainingArrays = arrayToRetrieveFrom.getAsJsonArray();

        List<CustomLocation> listOfIntegerArrays = new ArrayList<>();

        for(JsonElement elementContainingCoordinates : jsonArrayContainingArrays) {
            JsonArray elementContainingCoordinatesAsJsonArray = elementContainingCoordinates.getAsJsonArray();
            double x = elementContainingCoordinatesAsJsonArray.get(0).getAsDouble();
            double y = elementContainingCoordinatesAsJsonArray.get(1).getAsDouble();
            double z = elementContainingCoordinatesAsJsonArray.get(2).getAsDouble();

            listOfIntegerArrays.add(new CustomLocation(x,y,z));
        }

        return listOfIntegerArrays;
    }
}
