package com.company;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;

public class Main {
    protected String type;
    protected String urlMain;
    //protected typeUrl;

    public Main(String main) {
    }

    public Main() {
    }

    public static void main(String[] args) throws Exception {
        // write your code here
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Please enter Super Hero you are looking for: ");
        String searchHero = inputScanner.nextLine();

        BufferedReader reader = new BufferedReader(new FileReader("keys.txt"));

        String publicKey = reader.readLine();
        String privateKey = reader.readLine();

        Random rnd = new Random();
        int ts = rnd.nextInt(10000000) + 10000000;
        System.out.println(ts);

        String hashMe = ts + privateKey + publicKey;

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = hashMe.getBytes();
        byte[] hashBytes = md.digest(keyBytes);

        String hash = String.format("%032X", new BigInteger(1, hashBytes)); //http://stackoverflow.com/questions/5470219/get-md5-string-from-message-digest
        System.out.println(hash.toLowerCase());
        hash = hash.toLowerCase();

        // Now have ts and hash, and can make API call.

        String baseurl = "https://gateway.marvel.com:443/v1/public/characters?name=" +searchHero + "&ts=%s&apikey=%s&hash=%s";
        String url = String.format(baseurl, ts, publicKey, hash);

        System.out.println(url);

        AsyncHttpClient c = new DefaultAsyncHttpClient();
        c.prepareGet(url).execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) throws Exception {

                try {
                    JSONObject jsonResponse = new JSONObject(response.getResponseBody());
                    JSONArray resultArray = jsonResponse.getJSONObject("data").getJSONArray("results");
                    //System.out.println(jsonResponse);

                    // System.out.println(resultObject);

                    for (int x = 0; x < resultArray.length(); x++) {
//
                        JSONObject character = resultArray.getJSONObject(x);
                        //System.out.println(character);
                        System.out.println(character.getString("name"));
                        System.out.println(character.getString("description"));
                        
                    }

                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

    }


    
}