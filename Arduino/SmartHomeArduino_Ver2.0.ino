#include <ArduinoJson.h>
#include <Servo.h>
#include <DHT.h>
#include <HttpClient.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <string>
#include "Servo.h"

#define DHTPIN 12
#define DHTTYPE DHT22
#define no_dust 0.50
#define SERVOPIN 9
#define AIROUT_PIN 4  //환기팬(환기청정기)
#define AIRCLEANER_PIN 32  //실링팬(공기청정기)
#define USE_NETWORK 1
#define USE_BLUETOOTH 1
#define DEBUG 1

//===============미세먼지 변수들==============================
float voMeasured = 0;
float dustDensity;
int measurePin = A4; // 센서의 5번핀 먼지를 측정 아두이노 A4번에 연결
int ledPower = A5;
#define RGB_R 23 
#define RGB_G 35 
#define RGB_B 36
//==========================================================
float temperature, humidity;
String jsondata;
String data;
const unsigned long requestInterval = 3000;  // 요구 시간 딜레이(30 sec)
int angle = 0;
int timeout = 0; 
char sData[64] = { 0x00, };
String controlArr[20] = "";
String beforeData = "";
String beforeControlArr[20] = {"windowUp","2","angle","2","heater","2","heater_temp","0","ac","2","ac_temp","0","airCleaner","2","airOut","2","door","2"};
//========================================================
#include "WiFiEsp.h"
char ssid[] = "BaeWha_WiFi";  // your network SSID (name)
char pass[] = "baewha2018";  // your network password
int status = WL_IDLE_STATUS;  // the Wifi radio's status
WiFiEspServer server_f(400);
//========================================================

DHT dht(DHTPIN, DHTTYPE);
Servo servo; 
LiquidCrystal_I2C lcd(0x27, 16, 2);

IPAddress hostIp(172,16,160,37);
//IPAddress hostIp(3, 39, 194, 213);
int SERVER_PORT = 8081;
WiFiEspClient client;

void printLCD(int col, int row , char *str) {
    for(int i=0 ; i < strlen(str) ; i++){
      lcd.setCursor(col+i , row);
      lcd.print(str[i]);
    }
}

void printWifiStatus(){
#if 1 // #if DEBUG
  // print the SSID of the network you're attached to
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
#endif

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  delay(10);
#if 1 // #if DEBUG
  Serial.print("IP Address: ");
  Serial.println(ip);
#endif
   char ipno2[26] ;
   sprintf(ipno2, "%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
     printLCD(0, 1, ipno2);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  
#if 1 // #if DEBUG
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
#endif
}

void setup() {
  pinMode(AIROUT_PIN, OUTPUT);
  pinMode(AIRCLEANER_PIN, OUTPUT);
  pinMode(ledPower,OUTPUT); //LED RGB
  pinMode(RGB_R,OUTPUT); // LED Red 난방 가동시
  pinMode(RGB_G,OUTPUT);
  pinMode(RGB_B,OUTPUT); // LED green 냉방 가동시
  pinMode(31, OUTPUT);  //doorlock
  servo.attach(9); // 서보모터 : 창문
  Serial.begin(115200);
  Serial2.begin(115200);

  digitalWrite(ledPower,LOW);
  //digitalWrite(RGB_R,LOW);
  //digitalWrite(RGB_G,LOW);
  //digitalWrite(RGB_B,LOW);
  
  lcd.init();
  lcd.backlight();
  dht.begin();
  printLCD(0, 0, "SmartHome");
  printLCD(0, 1, "NETWORKING...");  

#if USE_NETWORK
  WiFi.init(&Serial2);

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
     #if DEBUG
    Serial.println("WiFi shield not present");
   #endif
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
     #if DEBUG
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
   #endif
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }
   #if DEBUG
  Serial.println("You're connected to the network");
   #endif
  printWifiStatus(); // display IP address on LCD
  delay(2000);
   
  server_f.begin(); 
#endif
  
  #if DEBUG
  Serial.println("START");
  #endif

  digitalWrite(ledPower,LOW);
  digitalWrite(AIROUT_PIN, LOW);
  digitalWrite(AIRCLEANER_PIN, LOW);
}

float getDust(){
  digitalWrite(ledPower,LOW);
  delayMicroseconds(100);
  voMeasured = analogRead(measurePin);
  delayMicroseconds(100);
  digitalWrite(ledPower,HIGH);
  delayMicroseconds(100);

  dustDensity = (0.17 * (voMeasured * (5.0 / 1024)) - 0.76) * 1000;
  if(dustDensity >= 0 && dustDensity <= 250){
    return dustDensity;
  }
  else{
    Serial.println("Dust Error");
    return 0.0;
  }
}

void getTemp_Humid(){
  if(dht.readHumidity() >= 0 && dht.readHumidity() <= 100){
    humidity = dht.readHumidity();
  }
  if(dht.readTemperature() >= -40 && dht.readTemperature() <= 80){
    temperature = dht.readTemperature();
  }
}

String parseJson(){
  //JSON 인코딩
  jsondata ="";
  DynamicJsonDocument doc(1024);
  Serial.println(temperature);
  Serial.println(humidity);
  Serial.println(dustDensity);
  doc["temp"] = temperature;
  doc["humid"] = humidity;
  doc["pm"] = dustDensity;

  serializeJson(doc, jsondata);
  Serial.println(jsondata);
  return jsondata;
}

void connectToServer(){
  if(client.connect(hostIp, SERVER_PORT)){
    data = parseJson();
    //데이터 전송
    client.print(F("POST /sensor/set-data"));
    client.print(F(" HTTP/1.1\r\n"));
    client.print(F("Cache-Control: no-cache\r\n"));
    client.print(F("Host: localhost:8081\r\n"));
    client.print(F("User-Agent: Arduino\r\n"));
    client.print(F("Content-Type: application/json;charset=UTF-8\r\n"));
    client.print(F("Content-Length: "));
    client.println(data.length());
    client.println();
    client.println(data);
    client.print(F("\r\n\r\n"));
    }
    else{
      Serial.println("Connection failed");
    }
}

void fromServer(){
  if(client.connected()){
      String aData = "";
      aData = client.readStringUntil("\r\n");
      Serial.println("automatic control from server");
      Serial.println();
      Serial.println(aData);
      Serial.println();
      
      if(aData.indexOf("1a") != -1){
        control(1);
      }else if(aData.indexOf("2b") != -1){
        control(2);
      }else if(aData.indexOf("3c") != -1){
        control(3);
      }else if(aData.indexOf("4d") != -1){
        control(4);
      }
      client.flush();
      client.stop(); 
  }
}  

//데이터 수신(1: 환/실 ON, 2: 환 ON 실 OFF, 3: 환 OFF 실 ON, 4: 환/실 OFF)
void control(int c){
  switch(c){
    case 1:
      Serial.println(c);
      Serial.println("control : 1");
      digitalWrite(AIROUT_PIN, HIGH);
      digitalWrite(AIRCLEANER_PIN, HIGH);
      break;
    case 2:
      Serial.println(c);
      Serial.println("control : 2");      
      digitalWrite(AIROUT_PIN, HIGH);
      digitalWrite(AIRCLEANER_PIN, LOW);
      break;
    case 3:
      Serial.println(c);
      Serial.println("control : 3");
      digitalWrite(AIROUT_PIN, LOW);
      digitalWrite(AIRCLEANER_PIN, HIGH);
      break;
    case 4:
      Serial.println(c);
      Serial.println("control : 4");
      digitalWrite(AIROUT_PIN, LOW);
      digitalWrite(AIRCLEANER_PIN, LOW);
      break;
    default:
      Serial.println("No device found to control.");
      delay(1000);
      client.stop();
  }
  Serial.println("End of auto Control.");
}

void rr(String device, int value){
  
  if(value>0){ //동작
    if(device=="windowUp") {
      Serial.println("device: " + device + " (ON)" + " angle: " + value);
      servo.write(value);
      //delay(5000);
    }
    if(device=="heater") {
      Serial.println("device: " + device + " (ON)" + " heater_temp: " + value);
      digitalWrite(RGB_R,HIGH);
      digitalWrite(RGB_G,LOW);
      digitalWrite(RGB_B,LOW);
      Serial.println("Heater On, Angle: " + value);
      //lcd.setCursor(0, 0);
      //lcd.print("Heater On, Angle: " + value);
      // Lcd에 온도값 출력
      // LED Red On.
    }if(device=="ac") {
      Serial.println("device: " + device + " (ON)" + " ac_temp: " + value);
      digitalWrite(RGB_R,LOW);
      digitalWrite(RGB_G,LOW);
      digitalWrite(RGB_B,HIGH);
      Serial.println("Aircon On, Angle: " + value);
      //lcd.setCursor(0, 0);
      //lcd.print("Aircon On, Angle: " + value);
      // Lcd에 온도값 출력
      // LED Blue On.
    }
    if(device=="airCleaner") { //실링팬
      Serial.println("device: " + device + " (ON)");
      digitalWrite(AIRCLEANER_PIN, HIGH);
    }
    if(device=="airOut") { //환기팬
      Serial.println("device: " + device + " (ON)");
      digitalWrite(AIROUT_PIN, HIGH);
    }
    if(device=="door") {
      Serial.println("device: " + device + " (ON)");
      digitalWrite(31 , HIGH);
    }
  }
  else if(value==0){ //OFF
    if(device=="windowUp") {
      Serial.println("device: " + device + " (OFF)");
      servo.write(0);
      //delay(5000);
    }
    if(device=="heater") {
      digitalWrite(RGB_R,LOW);
      Serial.println("device: " + device + " (OFF)");
    }
    if(device=="ac") {
      digitalWrite(RGB_B,LOW);
      Serial.println("device: " + device + " (OFF)");
    }
    if(device=="airCleaner") { //실링팬
      Serial.println("device: " + device + " (OFF)");
      digitalWrite(AIRCLEANER_PIN, LOW);
    }
    if(device=="airOut") { //환기팬
      Serial.println("device: " + device + " (OFF)");
      digitalWrite(AIROUT_PIN, LOW);
    }
    if(device=="door") {
      Serial.println("device: " + device + " (OFF)");
      digitalWrite(31 , LOW);
    }
  }
}

void rControl(String arr[20]) {
  // windowUp, 1, angle, 90, heater, 0, heater_temp, 0, ac, 1, ac_temp, 18, airCleaner, 0, airOut, 0, door, 0

  int index = 0;
  while(index<21){

    // { windowUp, 1, angle, 90, heater, 0, heater_temp, 0, ac, 1, ac_temp, 18 } 인 경우
    if(index>=0 && index<=11){
      if(index==0 || index%2==0){
        //Serial.println(arr[index] + arr[index+1]);
        if(arr[index+1]=="1"){ //ON
          int cc = arr[index+3].toInt();
          //Serial.println("d: " + arr[index] + ", Cdata: " + cc);
          rr(arr[index],cc);
        }else if(arr[index+1]=="0"){ //OFF
          rr(arr[index],0);
        }
      }//end of if

    // { airCleaner, 0, airOut, 0, door, 0 } 인 경우
    }else if(index>=12){
      if(index%2==0){
        if(arr[index+1]=="1"){ //ON
          rr(arr[index],1);
        }else if(arr[index+1]=="0"){ //OFF
          Serial.println("d: " + arr[16] + ", Cdata: " + arr[17] );
          rr(arr[index],0);
        }
      }
    }
    index++;
  }//end of while();
}// end of rControl();

void Split(String sData, char cSeparator)
{  
  int nCount = 0;
  int nGetIndex = 0 ;

  //임시저장
  String sTemp = "";

  //원본 복사
  String sCopy = sData;
  int index = 0;
  while(true)
  {
    //구분자 찾기
    nGetIndex = sCopy.indexOf(cSeparator);
    
    //리턴된 인덱스가 있나?
    if(-1 != nGetIndex)
    {
      //데이터 넣고 출력.
      sTemp = sCopy.substring(0, nGetIndex);
      controlArr[index] = sTemp;
      Serial.print(controlArr[index] +", ");
      index++;
    
      //뺀 데이터 만큼 잘라낸다.
      sCopy = sCopy.substring(nGetIndex + 1);
    }
    else
    {
      //없으면 마무리 한다.
      Serial.println( sCopy );
      break;
    }
    //다음 문자로~
    ++nCount;
  }

}
void remoteControl() { //원격제어 참조 및 제어 코드
  if(client.connect(hostIp, SERVER_PORT)){
    Serial.println("----- Connecting...Server for remoteControl -----");
    client.println("GET /sensor/get-control-data HTTP/1.1");
    client.println("Host: localhost:8081");
    client.println("Connection: close");
    client.println(); // end HTTP header  

    String rData = client.readStringUntil("\r\n");
    int index = rData.indexOf("windowUp");
    String data = rData.substring(index);
    if(beforeData != data){
      Split(data , ',');
      beforeData = data;
      rControl(controlArr);
    }
    //rControl(controlArr);
    
    client.flush();
    client.stop();  
    delay(3000); //3초 뒤 서버와 연결을 끊고 재연결 시도
  }else {
    Serial.println("remote Connection failed");
  }
} // end of remoteControl()

void loop() { 
   timeout += 1; //처음과 그 이후 구분하기 위해 존재함.
   
   if(timeout == 1){ //처음
    getDust();
    getTemp_Humid();

    lcd.clear();
    memset(sData, 0x00, 64);
    sprintf(sData, "temp %02dC humi %02d%%", (int)temperature, (int)humidity);
    printLCD(0, 0, sData);
    memset(sData, 0x00, 64);
    sprintf(sData, "1 dust: %d", (int)dustDensity);
    printLCD(0, 1, sData);
    
    int i = 0; //기준 시간 정의
    
    //원격제어
    do{
      if(i == 5){
        Serial.println("----- after 30cnt / first remoteControl -----");
        remoteControl();
      }
      
      if(i == 30){
        //자동제어
        Serial.println("----- after 5min / first connect start -----");
        connectToServer(); //데이터 전송
        Serial.println("----- start first autoControl -----");
        //Serial.println("----- first Data send and get -----");
        fromServer(); //데이터 수신
      }
      i++;
    }while(i <= 30);     
    client.flush();
    client.stop();
    
   }else { //2번째 실행부터 경우
      float h = humidity; //기존 온도
      float t = temperature; //기존 습도
      float d = dustDensity; //기존 미세먼지 농도

      getTemp_Humid(); //온습도 새로 측정
      getDust(); //미세먼지 새로 측정

      lcd.clear();
      memset(sData, 0x00, 64);
      sprintf(sData, "temp %02dC humi %02d%%", (int)temperature, (int)humidity);
      printLCD(0, 0, sData);
      memset(sData, 0x00, 64);
      sprintf(sData, "2 dust: %d", (int)dustDensity);
      printLCD(0, 1, sData);
      
      int i = 0; //기준 시간 정의
      //원격제어
      do{
        if(i % 30 == 0){
          Serial.println("----- after 30cnt / Not first remoteControl -----");
          remoteControl();
        }
        if(i % 500 == 0){ //시연 시 500cnt, if(i % 5000 == 0)
          //자동제어
          if(humidity != h || temperature != t || dustDensity != d){ 
          //기존값과 새로 읽은 값 상이한지 비교
          Serial.println("----- after 5min / Not first connect start -----");
          connectToServer(); //데이터 전송
          //delay(30000);// 30초 대기
          Serial.println("----- start not first autoControl -----");
          fromServer(); //데이터 수신
          }
        }
        i++;
        client.flush();
        client.stop();
      }while(true); 
      }// end of if     
   }
   
   //원격제어 코드
   //remoteControl();
   
   //client.flush();
   //client.stop();
   //delay(100);  // waiting 2sec
