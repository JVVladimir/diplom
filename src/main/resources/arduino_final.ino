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

#define INIT_W_LEAD 1
#define INIT_W_SLAVE -1
#define INIT_X 2
#define TRAIN 3
#define SYNC_DONE 4

Random *random2;
TreeParityMachine *tpm1;
TPMTrainer *trainer;

byte command;

short* input;
short out;
short out2;

short* key;
short len;
bool isSlave = false;

void flushDelay();

void setup() {
  Serial.begin(115200);
  random2 = new Random();
  random2->setBounds(-L, L);
  tpm1 = new TreeParityMachine(n, k, random2);
  trainer = new TPMTrainer();
}

void loop() {
  if (Serial.available() > 0) {
    StaticJsonDocument<500> docT;
    StaticJsonDocument<1400> docAnswer;
    JsonArray data;
    JsonArray data2;
    DeserializationError error = deserializeJson(docT, Serial);
    if (error) {
      Serial.println("error");
      flushDelay();
      return;
    }
    command = docT["command"];
    switch (command) {
      case INIT_W_LEAD:
        isSlave = false;
        tpm1->regenerate(random2);
        docAnswer["resultCode"] = 100;
        serializeJson(docAnswer, Serial);
        flushDelay();
        break;
      case INIT_W_SLAVE:
        isSlave = true;
        tpm1->regenerate(random2);
        docAnswer["resultCode"] = 100;
        serializeJson(docAnswer, Serial);
        flushDelay();
        break;
      case INIT_X:
        input = random2->getIntsCastedToDouble(n);
        docAnswer["resultCode"] = 100;
        data = docAnswer.createNestedArray("vector");
        for (int i = 0; i < n; i++) {
          data.add(input[i]);
        }
        out = tpm1->getOutput(input);
        docAnswer["out"] = out;
        serializeJson(docAnswer, Serial);
        flushDelay();
        delete []input;
        break;
      case TRAIN:
        input = new short[n];
        for (int i = 0; i < n; i++) {
          input[i] = docT["vector"][i];
        }
        out = docT["out"];
        trainer->synchronize(tpm1, input, out);
        if (isSlave) {
          out2 = tpm1->getOutput(input);
        }
        else {
          delete []input;
          input = random2->getIntsCastedToDouble(n);
          docAnswer["resultCode"] = 100;
          data = docAnswer.createNestedArray("vector");
          for (int i = 0; i < n; i++) {
            data.add(input[i]);
          }
          out2 = tpm1->getOutput(input);
        }
        docAnswer["out"] = out2;
        docAnswer["memory"] = freeMemory();
        serializeJson(docAnswer, Serial);
        flushDelay();
        delete []input;
        break;
      case SYNC_DONE:
        docAnswer["resultCode"] = 100;
        key = tpm1->getSecretKey();
        data2 = docAnswer.createNestedArray("weight");
        for (int i = 0; i < n * k; i++) {
          data2.add(key[i]);
        }
        serializeJson(docAnswer, Serial);
        flushDelay();
        delete []key;
        break;
    }
  }
}

void flushDelay() {
  Serial.flush();
  delay(t);
}

