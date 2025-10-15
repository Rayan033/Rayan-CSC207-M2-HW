package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {


    @Override
    public List<String> getSubBreeds(String breedName) throws BreedNotFoundException {
        try {
            String apiUrl = "https://dog.ceo/api/breed/" + breedName + "/list";
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");


            int status = con.getResponseCode();
            if (status != 200) {
                throw new BreedNotFoundException("Breed not found or API error. HTTP status: " + status);
            }


            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            con.disconnect();


            JSONObject json = new JSONObject(response.toString());
            String statusMsg = json.getString("status");
            if (!statusMsg.equals("success")) {
                throw new BreedNotFoundException("API did not return success status.");
            }


            JSONArray subBreedsJson = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < subBreedsJson.length(); i++) {
                subBreeds.add(subBreedsJson.getString(i));
            }
            return subBreeds;


        } catch (IOException | JSONException e) {
            throw new BreedNotFoundException("Failed to fetch or parse sub-breeds: " + e.getMessage());
        }
    }
}