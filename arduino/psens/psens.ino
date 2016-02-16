#include <NewPing.h>

#define R_LED 2
#define Y_LED 3
#define G_LED 4
#define ECHO_PIN     11
#define TRIGGER_PIN  12
#define MAX_DISTANCE 500
#define DELAY 10

struct history {
  unsigned int cur;
  unsigned int hist[100];
  unsigned int hsize;
  history(unsigned int hsize = 10) : 
  cur(0), hist({
    0  }
  ),hsize(hsize) {
  }
  void add(int i){
    hist[cur] = i;
    cur = (cur + 1) % hsize;  
  }
  int avg(){
    int sum = 0;
    int divider = hsize;
    for (int i = 0; i < hsize; i++) {
      if (hist[i%hsize] == 0) {
        divider--;        
      }
      else{
        sum = sum + hist[i%hsize];      
      }
    }
    return sum / divider;
  }
};

NewPing sonar(TRIGGER_PIN,ECHO_PIN,MAX_DISTANCE);
history *h = new history(50);

void setup(){
  pinMode(R_LED, OUTPUT);
  pinMode(G_LED, OUTPUT);
  pinMode(Y_LED, OUTPUT);
  //Serial.begin(115200);
}

void loop(){
  delay(DELAY);
  h->add(sonar.ping_cm());
  int distance = h->avg();
  //Serial.print(distance);
  //Serial.println(" cm");


  if (distance == -1){
    nothing(); 
  }
  else if (distance > 100){
    green();  
  }
  else if (distance > 5){
    yellow();   
  }
  else{
    red();     
  }
}

void nothing() {
  digitalWrite(G_LED, HIGH);
  digitalWrite(Y_LED, HIGH);  
  digitalWrite(R_LED, HIGH);
}

void green(){
  digitalWrite(G_LED, HIGH);
  digitalWrite(Y_LED, LOW);  
  digitalWrite(R_LED, LOW);
}

void yellow(){
  digitalWrite(G_LED, LOW);
  digitalWrite(Y_LED, HIGH);  
  digitalWrite(R_LED, LOW);
}

void red(){
  digitalWrite(G_LED, LOW);
  digitalWrite(Y_LED, LOW);  
  digitalWrite(R_LED, HIGH);
}




