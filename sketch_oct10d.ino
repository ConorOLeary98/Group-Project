#define CUSTOM_SETTINGS
#define INCLUDE_INTERNET_SHIELD
#define INCLUDE_TERMINAL_SHIELD
#define INCLUDE_GPS_SHIELD

#include <OneSheeld.h>
//HttpRequest myRequest("https://group-project-5356f.firebaseio.com/Mic.json");
HttpRequest requestLat("https://group-project-5356f.firebaseio.com/Lat.json");
HttpRequest requestLng("https://group-project-5356f.firebaseio.com/Lng.json");

void setup() {
  // put your setup code here, to run once:
  OneSheeld.begin();
  
  //myRequest.getResponse().setOnError(&onResponseError);
  
  //Internet.setOnError(&onInternetError);  
  
}

void loop() {
  
  //myRequest.addRawData("91");
  //Terminal.println(Internet.performPut(myRequest));

  //delay(15000);

   
  String stringLat = "";  
  String stringLng = "";   
  
  stringLat+=String(int(GPS.getLatitude()))+ "."+String(getDecimal(GPS.getLatitude())); 
  char charLat[stringLat.length()+1];                      
  stringLat.toCharArray(charLat,stringLat.length()+1);     

  stringLng+=String(int(GPS.getLongitude()))+ "."+String(getDecimal(GPS.getLongitude())); 
  char charLng[stringLng.length()+1];                      
  stringLng.toCharArray(charLng,stringLng.length()+1);    


  char buf[20];

  Terminal.println(charLat);
  Terminal.println(charLng);
  requestLat.addRawData(charLat);
  Internet.performPut(requestLat);

  requestLng.addRawData(charLng);
  Internet.performPut(requestLng);
  
  OneSheeld.delay(6000);
}

/* Error handling functions. */
void onResponseError(int errorNumber)
{
  /* Print out error Number.*/
  Terminal.print("Response error:");
  switch(errorNumber)
  {
    case INDEX_OUT_OF_BOUNDS: Terminal.println("INDEX_OUT_OF_BOUNDS");break;
    case RESPONSE_CAN_NOT_BE_FOUND: Terminal.println("RESPONSE_CAN_NOT_BE_FOUND");break;
    case HEADER_CAN_NOT_BE_FOUND: Terminal.println("HEADER_CAN_NOT_BE_FOUND");break;
    case NO_ENOUGH_BYTES: Terminal.println("NO_ENOUGH_BYTES");break;
    case REQUEST_HAS_NO_RESPONSE: Terminal.println("REQUEST_HAS_NO_RESPONSE");break;
    case SIZE_OF_REQUEST_CAN_NOT_BE_ZERO: Terminal.println("SIZE_OF_REQUEST_CAN_NOT_BE_ZERO");break;
    case UNSUPPORTED_HTTP_ENTITY: Terminal.println("UNSUPPORTED_HTTP_ENTITY");break;
    case JSON_KEYCHAIN_IS_WRONG: Terminal.println("JSON_KEYCHAIN_IS_WRONG");break;
  }
}

void onInternetError(int requestId, int errorNumber)
{
  /* Print out error Number.*/
  Terminal.print("Request id:");
  Terminal.println(requestId);
  Terminal.print("Internet error:");
  switch(errorNumber)
  {
    case REQUEST_CAN_NOT_BE_FOUND: Terminal.println("REQUEST_CAN_NOT_BE_FOUND");break;
    case NOT_CONNECTED_TO_NETWORK: Terminal.println("NOT_CONNECTED_TO_NETWORK");break;
    case URL_IS_NOT_FOUND: Terminal.println("URL_IS_NOT_FOUND");break;
    case ALREADY_EXECUTING_REQUEST: Terminal.println("ALREADY_EXECUTING_REQUEST");break;
    case URL_IS_WRONG: Terminal.println("URL_IS_WRONG");break;
  }
}

long getDecimal(float val)
{
  int intPart = int(val);
  long decPart = 10000000*(val-intPart); 
                                    
  if(decPart>0)return(decPart);            
  else if(decPart<0)return((-1)*decPart); 
  else if(decPart=0)return(00);           
}
