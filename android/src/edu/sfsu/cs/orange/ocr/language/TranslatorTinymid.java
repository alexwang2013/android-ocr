/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.cs.orange.ocr.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import edu.sfsu.cs.orange.ocr.CaptureActivity;

public class TranslatorTinymid {
  private static final String TAG = TranslatorTinymid.class.getSimpleName();
  private static final String API_KEY = " [PUT YOUR API KEY HERE] ";

  private TranslatorTinymid() {  
    // Private constructor to enforce noninstantiability
  }

  // Translate using Google Translate API
  static String translate(String sourceLanguageCode, String targetLanguageCode, String sourceText) {   
    Log.d(TAG, sourceLanguageCode + " -> " + targetLanguageCode);
    
    // Truncate excessively long strings. Limit for Google Translate is 5000 characters
    if (sourceText.length() > 4500) {
      sourceText = sourceText.substring(0, 4500);
    }
    
//    GoogleAPI.setKey(API_KEY);
//    GoogleAPI.setHttpReferrer("https://github.com/rmtheis/android-ocr");
    try {
//      return Translate.DEFAULT.execute(sourceText, Language.fromString(sourceLanguageCode), 
//          Language.fromString(targetLanguageCode));
    	return getTranslatedText(sourceText);
    } catch (Exception e) {
      Log.e(TAG, "Caught exeption in translation request.");
      return Translator.BAD_TRANSLATION_MSG;
    }
  }
  
  static String getTranslatedText(String origText){
	  String retStr=Translator.BAD_TRANSLATION_MSG;
	  String urlServer="Your Server Address";
      try { 
          
			Log.i(TAG," doInBackground urlServer ="+urlServer);
	        HttpClient httpclient = new DefaultHttpClient();
	        // specify the URL you want to post to
			HttpPost httppost = new HttpPost(urlServer);
			
			String strMsg="";
			try {
				// create a list to store HTTP variables and their values
				List nameValuePairs = new ArrayList();
		                // add an HTTP variable and value pair
				nameValuePairs.add(new BasicNameValuePair("origText", origText));								
				//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		                // send the variable and value, in other words post, to the URL
				HttpResponse response = httpclient.execute(httppost);
				
				int responseCode = response.getStatusLine().getStatusCode();
				switch(responseCode) {
				case 200:
				HttpEntity entity = response.getEntity();
				
				    if(entity != null) {
				        strMsg = EntityUtils.toString(entity);			            

				    }
				    break;
				}
			} catch (ClientProtocolException e) {
				// process execption
			} catch (IOException e) {
				// process execption
			}
			
          if(strMsg!=null && strMsg!="")
          {
          	JSONObject json_data = new JSONObject(strMsg);
          	String err_cd=json_data.getString("err_cd");
          	if(err_cd=="1000");
              {
            	  retStr=json_data.getString("result");//succeed
              }
          }            
      } catch (Exception ex) {   
      	System.out.println(ex);

      }  
      finally{
    	  
      }
      return retStr;  
  }

  /**
   * Convert the given name of a natural language into a language code from the enum of Languages 
   * supported by this translation service.
   * 
   * @param languageName The name of the language, for example, "English"
   * @return code representing this language, for example, "en", for this translation API
   * @throws IllegalArgumentException
   */
  public static String toLanguage(String languageName) throws IllegalArgumentException {   
    // Convert string to all caps
    String standardizedName = languageName.toUpperCase();
    
    // Replace spaces with underscores
    standardizedName = standardizedName.replace(' ', '_');
    
    // Remove parentheses
    standardizedName = standardizedName.replace("(", "");   
    standardizedName = standardizedName.replace(")", "");
    
    // Hack to fix misspelling in google-api-translate-java
    if (standardizedName.equals("UKRAINIAN")) {
      standardizedName = "UKRANIAN";
    }
    
    // Map Norwegian-Bokmal to Norwegian
    if (standardizedName.equals("NORWEGIAN_BOKMAL")) {
      standardizedName = "NORWEGIAN";
    }
    
    try {
      return Language.valueOf(standardizedName).toString();
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Not found--returning default language code");
      return CaptureActivity.DEFAULT_TARGET_LANGUAGE_CODE;
    }
  }
}
