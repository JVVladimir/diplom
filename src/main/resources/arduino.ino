#include <HiddenLayer.h>
#include <HiddenNeuron.h>
#include <NetLayer.h>
#include <Neuron.h>
#include <OutputLayer.h>
#include <OutputNeuron.h>
#include <Random.h>
#include <TPMTrainer.h>
#include <TreeParityMachine.h>

#include <MemoryFree.h>
#include <ArduinoJson.h>

#define L 2
// Число входов в каждый нейрон сети
#define n 8
// Число нейронов на скрутом слое
#define k 16

#define t 5

Random *random2;
TreeParityMachine *tpm1;
TPMTrainer *trainer;
byte* secretKey;

byte command;

short* input;
short out;
short out2;

byte* message;
byte* shifrMessage;
short* key;

void setup() {
Serial.begin(115200);
random2 = new Random();
random2->setBounds(-L, L);
tpm1 = new TreeParityMachine(n, k, random2);
trainer = new TPMTrainer();
Serial.println();
Serial.flush();
}

void loop() {
if (Serial.available() > 0) {
StaticJsonDocument<300> docT;
StaticJsonDocument<1200> docAnswer;
JsonArray data;
DeserializationError error = deserializeJson(docT, Serial);
if (error) {
Serial.println("error");
Serial.flush();
return;
}
command = docT["command"];
switch (command) {
case 1:
tpm1->regenerate(random2);
docAnswer["resultCode"] = 100;
serializeJson(docAnswer, Serial);
Serial.flush();
sendMessage();
break;
case 2:
input = random2->getIntsCastedToDouble(n);
docAnswer["resultCode"] = 100;
data = docAnswer.createNestedArray("vector");
for (int i = 0; i < n; i++) {
data.add(input[i]);
}
out = tpm1->getOutput(input);
docAnswer["out"] = out;
serializeJson(docAnswer, Serial);
Serial.flush();
sendMessage();
delete []input;
break;
case 3:
input = new short[n];
for (int i = 0; i < n; i++) {
input[i] = docT["vector"][i];
}
out = docT["out"];
out2 = trainer->synchronize(tpm1, input, out);
delete []input;
input = random2->getIntsCastedToDouble(n);
docAnswer["resultCode"] = 100;
data = docAnswer.createNestedArray("vector");
for (int i = 0; i < n; i++) {
data.add(input[i]);
}
docAnswer["out"] = out2;
docAnswer["memory"] = freeMemory();
serializeJson(docAnswer, Serial);
Serial.flush();
sendMessage();
delete []input;
break;
case 4:
key = tpm1->getSecretKey();
// secretKey = weightToKey(key, 32);
docAnswer["resultCode"] = 100;
//КЛЮЧ КАК ВЕКТОР ОТСЫЛАТЬ НЕ НАДО, НО ПОКА ЧТОБ ПОСМОТРЕТЬ ОТСЫЛАЕМ
data = docAnswer.createNestedArray("vector");
for (int i = 0; i < n * k; i++) {
data.add(key[i]);
}
serializeJson(docAnswer, Serial);
Serial.flush();
sendMessage();
delete []key;
break;
case 5:
/*message = new byte[bytes - 1];
for (int i = 0; i < bytes - 1; i++) {
message[i] = buf[i + 1];
}
shifrMessage = shifr(message, bytes - 1, secretKey, 32);
byte answer4[bytes];
DynamicJsonDocument doc4(bytes * 8);
answer4[0] = 100;
for (int i = 0; i < bytes - 1; i++) {
answer4[i + 1] = shifrMessage[i];
}
JsonArray arr = doc4.to<JsonArray>();
for (int i = 0 ; i < bytes; i++) {
arr.add(answer4[i]);
}*/


docAnswer["resultCode"] = 100;
data = docAnswer.createNestedArray("key");
for (int i = 0; i < n * k; i++) {
data.add(secretKey[i]);
}
serializeJson(docAnswer, Serial);
Serial.flush();
sendMessage();
delete []message;
delete []shifrMessage;
break;
}
}
}

byte* shifr(byte input[], short lenInput, byte key[], short lenKey) {
byte* res = new byte[lenInput];
for (int i = 0; i < lenInput;

i++) {
res[i] = input[i] ^ key[i % lenKey];
}
return res;
}

byte* weightToKey(short key[], short lenKey) {
byte* mas = new byte[lenKey];
for (int i = 0, j = 0; i < lenKey; i++) {
mas[i] |= key[j];
j++;
mas[i] = mas[i] « 4;
mas[i] |= key[j];
j++;
}
return mas;
}

void sendMessage() {
Serial.flush();
delay(t);
}