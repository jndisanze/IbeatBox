q
		/*
		 * Copyright (C) 2010 The Android Open Source Project
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
		 *
		 */

		/* This is a JNI example where we use native methods to play sounds
		 * using OpenSL ES. See the corresponding Java source file located at:
		 *
		 */

		#include <assert.h>
		#include <jni.h>
		#include <string.h>
		#include <pthread.h>
		#include <stdio.h>
		#include <stdlib.h>
		#include<iostream>
		#include <cstring>
		#include<fstream>
		#include <string>
		#include <sstream>
		#include <cstdlib>
		#include <sys/stat.h>
		#include <map>
		#include <sqlite3.h>
		#include "mysqlite.h"
		#include <vector>
		#include <time.h>
		#include <ctime>
		#include <sstream>
  #include <string>
		using namespace std;
		#include <android/log.h>
		#include "include/Voicer.h"
		#include "include/Stk.h"
		#include "include/Instrmnt.h"
		#include "include/Noise.h"
		#include "include/Sitar.h"
	//STK Instrument Classes
#include "include/Clarinet.h"
#include "include/BlowHole.h"
#include "include/Saxofony.h"
#include "include/Flute.h"
#include "include/Brass.h"
#include "include/BlowBotl.h"
#include "include/Bowed.h"
#include "include/Plucked.h"
#include "include/StifKarp.h"
#include "include/Sitar.h"
#include "include/Mandolin.h"
#include "include/Rhodey.h"
#include "include/Wurley.h"
#include "include/TubeBell.h"
#include "include/HevyMetl.h"
#include "include/PercFlut.h"
#include "include/BeeThree.h"
#include "include/FMVoices.h"
#include "include/VoicForm.h"
#include "include/Moog.h"
#include "include/Simple.h"
#include "include/Drummer.h"
#include "include/BandedWG.h"
#include "include/Shakers.h"
#include "include/ModalBar.h"
#include "include/Mesh2D.h"
#include "include/Resonate.h"
#include "include/Whistle.h"
#include "AudioToAudioEvent.h"
		#ifndef LOG_TAG
		#define LOG_TAG "JP LOG:"
		#endif

		#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

		// for native audio
		#include <SLES/OpenSLES.h>
		#include <SLES/OpenSLES_Android.h>
        #include <unistd.h>
		
		// for native asset manager
		#include <sys/types.h>
		#include <android/asset_manager.h>
		#include <android/asset_manager_jni.h>
		//semaphore
		#include <semaphore.h>
		// pre-recorded sound clips, both are 8 kHz mono 16-bit signed little endian
		#define MAX_NUMBER_INTERFACES 5
		#define MAX_NUMBER_INPUT_DEVICES 3
		#define POSITION_UPDATE_PERIOD 1000 /* 1 sec */


		static const char hello[] =
		#include "hello_clip.h"
		;

		static const char android[] =
		#include "android_clip.h"
		;
		int myValue = 0;
		// engine interfaces
		static SLObjectItf engineObject = NULL;
		static SLEngineItf engineEngine;

		// output mix interfaces
		static SLObjectItf outputMixObject = NULL;
		static SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;

		// buffer queue player interfaces
		static SLObjectItf bqPlayerObject = NULL;
		static SLPlayItf bqPlayerPlay;
		static SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
		static SLEffectSendItf bqPlayerEffectSend;
		static SLMuteSoloItf bqPlayerMuteSolo;
		static SLVolumeItf bqPlayerVolume;

		// aux effect on the output mix, used by the buffer queue player
		static const SLEnvironmentalReverbSettings reverbSettings =
			SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

		// URI player interfaces
		static SLObjectItf uriPlayerObject = NULL;
		static SLPlayItf uriPlayerPlay;
		static SLSeekItf uriPlayerSeek;
		static SLMuteSoloItf uriPlayerMuteSolo;
		static SLVolumeItf uriPlayerVolume;

		// file descriptor player interfaces
		static SLObjectItf fdPlayerObject = NULL;
		static SLPlayItf fdPlayerPlay;
		static SLSeekItf fdPlayerSeek;
		static SLMuteSoloItf fdPlayerMuteSolo;
		static SLVolumeItf fdPlayerVolume;

		// recorder interfaces
		static SLObjectItf recorderObject = NULL;
		static SLRecordItf recorderRecord;
		static SLAndroidSimpleBufferQueueItf recorderBufferQueue;

		// synthesized sawtooth clip
		#define SAWTOOTH_FRAMES 8000
		static short sawtoothBuffer[SAWTOOTH_FRAMES];

		// 10 seconds of recorded audio at 16 kHz mono, 16-bit signed little endian
		static int minute = 20; 
		#define SAMPLE_RATE (16000)
		#define BLOCK_SIZE (256)
		#define RECORDER_FRAMES (BLOCK_SIZE)
		#define LOOPING_FRAMES (BLOCK_SIZE)
		#define TOTAL_RECORDER_FRAMES (SAMPLE_RATE*20)
		//static int TOTAL_RECORDER_FRAMES =8000*minute
		static short recorderBuffer1[RECORDER_FRAMES];
		static short stkBuffer[RECORDER_FRAMES];
		//const static stk::StkFloat* samples;
		static short recorderBuffer2[RECORDER_FRAMES];
		static short stkRecBuffer[RECORDER_FRAMES];
		static short recorderBuffer3[RECORDER_FRAMES];
		static short t[RECORDER_FRAMES];
		static short dataCopy[LOOPING_FRAMES];
		static short recorderBuffer_TOTAL[TOTAL_RECORDER_FRAMES];
		//static short stkBuffer_TOTAL[TOTAL_RECORDER_FRAMES];
		static short loopBuffer_TOTAL[TOTAL_RECORDER_FRAMES];
		static unsigned recorderSize = 0;
		static unsigned bufferNumber = 3;
		static unsigned countBufferCurrent=0;
		static short *recorderbufferCurrent = 0;
		static short * recorderbufferCurrent_audioProcessing=0;
		static SLmilliHertz recorderSR;
		static int record_play_realtime = 0;
		const static int size = 5000;
		static int countPlayer;
		static short metroBufferHight[RECORDER_FRAMES];
		static short metroBufferLow[RECORDER_FRAMES];
		static short metroBufferNull[RECORDER_FRAMES];
		static short mixBuffer[RECORDER_FRAMES];
		static int countSound=0;
		static int COUNTLOOPSOUND =0;
		static int COUNTLOOPSOUNDVOICE =0;
		static stk::Instrmnt *instrmnt=0;
		static int  numberInstrmnt=0;
  //static stk::Clarinet *instrmnt;
		//semaphore
		sem_t mutex;

		// signature rythmique
		static int mesure = 1;
		static int rythmeBPM = 120;
		static int timeSignatureNum = 4;
		static int timeSignatureDen = 4;
		static double beat = 60.0/rythmeBPM;
		static int sizeBeat =(TOTAL_RECORDER_FRAMES/10.0)*beat;
		static int nextbeat = 0;
		static int countBeat = 1;
		static int countBar = 1;
		static int countBufferPlay = 0;
		static int beatChange = 0;
		static int sizeFractionBeat = sizeBeat/4.;
		static int countFractionBeat = 1;

		// sound recoder in hash map and from file register
		map <string,vector<short> > sound_list;
		//static short PLAYING_BUFFER[TOTAL_RECORDER_FRAMES];
		map <string,int > size_sound;
		// pointer and size of the next player buffer to enqueue, and number of remaining buffers
		static short *nextBuffer;
		static unsigned nextSize;
		static int nextCount;
		static int cancel_sound = 0;
		static bool LOOP_IS_RECORDING=false;
		static bool LOOP_IS_PLAYING=false;
		static bool IS_REALTIME = false;
		static bool IS_LOOPING = false;
		static bool IS_STOPPED = false;
		static string NAME_PLAYING = "";
		static int STATE_REPEAT = 5;
		static short myData[TOTAL_RECORDER_FRAMES];
		static vector<string> soundVoice;
		static vector<string> sound;
 
// Detectors GLOBAL VARIABLES
int order_resolution = -4;
int logscale_ratio = 1;
int ola = 2;
int decramping = 0;
double timeDetector=0;

//int numberInstrmnt =0;
 
// Detectors VARIABLES
int sample_rate = SAMPLE_RATE;
int block_size = BLOCK_SIZE;
SongPosition position;
Audio audio;
vector<AudioEvent>* audio_events;
AudioToAudioEvent* audio_to_audio_event;


 

// Detectors CLOSE
//delete audio_to_audio_event;
 

		
		
		
		
		
		
		
/// stk choose instrument///////////////
extern"C"
int voiceByNumber(int number, stk::Instrmnt **instrument)
{
  int temp = number;
  if      (number==0)  *instrument = new stk::Clarinet(10.0);
  else if (number==1)  *instrument = new stk::BlowHole(10.0);
  else if (number==2)  *instrument = new stk::Saxofony(10.0);
  else if (number==3)  *instrument = new stk::Flute(10.0);
  else if (number==4)  *instrument = new stk::Brass(10.0);
  else if (number==5)  *instrument = new stk::BlowBotl;
  else if (number==6)  *instrument = new stk::Bowed(10.0);
  else if (number==7)  *instrument = new stk::Plucked(5.0);
  else if (number==8)  *instrument = new stk::StifKarp(5.0);
  else if (number==9)  *instrument = new stk::Sitar(5.0);
  else if (number==10) *instrument = new stk::Clarinet(10.0);//stk::Mandolin(5.0);

  else if (number==11) *instrument = new stk::Clarinet(10.0);//stk::Rhodey;
  else if (number==12) *instrument = new stk::Clarinet(10.0);//stk::Wurley;
  else if (number==13) *instrument = new stk::Clarinet(10.0);//stk::TubeBell;
  else if (number==14) *instrument = new stk::Clarinet(10.0);//stk::HevyMetl;
  else if (number==15) *instrument = new stk::Clarinet(10.0);//stk::PercFlut;
  else if (number==16) *instrument = new stk::Clarinet(10.0);//stk::BeeThree;
  else if (number==17) *instrument = new stk::Clarinet(10.0);//stk::FMVoices;

  else if (number==18) *instrument = new stk::Clarinet(10.0);//stk::VoicForm;
  else if (number==19) *instrument = new stk::Clarinet(10.0);//stk::Moog;
  else if (number==20) *instrument = new stk::Clarinet(10.0);//stk::Simple;
  else if (number==21) *instrument = new stk::Clarinet(10.0);//stk::Drummer;
  else if (number==22) *instrument = new stk::Clarinet(10.0);//new stk::BandedWG;
  else if (number==23) *instrument = new stk::Clarinet(10.0);//new stk::Shakers;
  else if (number==24) *instrument = new stk::Clarinet(10.0);//new stk::ModalBar;
  else if (number==25) *instrument = new stk::Mesh2D(10, 10);
  else if (number==26) *instrument = new stk::Resonate;
  else if (number==27) *instrument = new stk::Whistle;

  else if(number==0){
    LOGI("\ninstrument number instr ===== 0!\n");
    temp = -1;
  }
  else {
    LOGI("\nUnknown instrument or program change requested!\n");
    temp = -1;
  }
  
  return temp;
}
extern"C"
int  tick( unsigned int nBufferFrames){

   ostringstream strs,nb;
   string str;
   strs<<"value instru "<<numberInstrmnt;
  //LOGI((strs.str()).c_str());
  stk::StkFrames output(nBufferFrames, 1);
  //LOGI("mi tic");
  instrmnt->tick(output);
  //LOGI("fin tic");
    for(unsigned int i=0; i<nBufferFrames; i++){
      stkBuffer[i] = (short) (output[i]*((1<<15)-1)); 
  }
  return 0;
}
		//class loop using for loop sample
		class Loop
		{
		public:
			int getSoundNumber() { 
				return this->soundNumber;
			} 
			int getSoundVoiceNumber() { 
				return this->soundVoiceNumber;
			} 
			void setSoundLoope(string uid){
			  this->soundLoope.push_back(uid);
				 this->soundStateLoope.push_back(false);
				 this->soundStateActive.push_back(false);
				 this->soundStateRec.push_back(false);
				 this->currentSize.push_back(0);
				 soundNumber++;
			}
			void setSoundVoiceLoope(string uid){
				this->soundVoiceLoope.push_back(uid);
				this->voiceStateLoope.push_back(false);
				this->voiceStateRec.push_back(false);
				this->voiceStateActive.push_back(false);
				this->currentSize.push_back(0);
				soundVoiceNumber++;
			}
			
			vector<string> getSoundLoope(){
				return this->soundLoope;
			}
			vector<string> getSoundVoiceLoope(){
				return this->soundVoiceLoope;
			}
			void eraseSoundLoope(string uid){
				int i;
				for(i=0;i<(this->soundLoope).size();i++){
						if(this->soundLoope[i].compare(uid)==0){
							this->soundLoope.erase (this->soundLoope.begin()+i);
							this->soundStateLoope.erase (this->soundStateLoope.begin()+i);
							this->soundStateRec.erase (this->soundStateRec.begin()+i);
							this->soundStateActive.erase (this->soundStateActive.begin()+i);
							this->currentSize.erase(this->currentSize.begin()+i);
							i=(this->soundLoope).size();
							soundNumber--;
						}
				}	
			}
			void eraseSoundVoiceLoope(string uid){
				int i;
				for(i=0;i<(this->soundVoiceLoope).size();i++){
						if(this->soundVoiceLoope[i].compare(uid)==0){
							this->soundVoiceLoope.erase (this->soundVoiceLoope.begin()+i);
							this->voiceStateLoope.erase (this->voiceStateLoope.begin()+i);
							this->voiceStateRec.erase (this->voiceStateRec.begin()+i);
							this->voiceStateActive.erase (this->voiceStateActive.begin()+i);
							this->currentSize.erase(this->currentSize.begin()+i);
							i=(this->soundVoiceLoope).size();
							soundVoiceNumber--;
						}
				}	
			}
			void setSoundStateLoop(string uid,bool state){
				  int j =indexStateSound(uid);
					 if(j>=0 && j < soundStateLoope.size())
						   soundStateLoope[j]=state;
					 else {
						  int k = indexStateVoice(uid);
						  if(k>=0 && k < soundStateLoope.size())
						   voiceStateLoope[k]=state;
					 }      
			 
			}
			void setSoundStateActive(string uid,bool state){
				 int j =indexStateSound(uid);
					 if(j>=0 && j < soundStateActive.size())
						   soundStateActive[j]=state;
					   else {
						  int k = indexStateVoice(uid);
						  if(k>=0 && k < soundStateLoope.size())
						   voiceStateActive[k]=state;
					 }       
			}
			void setSoundStateRec(string uid,bool state){
				  LOGI("native code setSoundStateRec : uid i");	
				  int j =indexStateSound(uid);
					 if(j>=0 && j < soundStateRec.size()){
						   soundStateRec[j]=state;LOGI("setSoundStateRec : uid in  is no voice");}
					  else {
						  int k = indexStateVoice(uid);
							ostringstream oss;
							oss<<"k="<<k;
							LOGI(oss.str().c_str());
						  if(k>=0 && k < voiceStateRec.size()){
							LOGI((uid+"true").c_str());
							voiceStateRec[k]=state; 
							}
					 }           
			}
			bool isActive(string uid){
				int j =indexStateSound(uid);
					 if(j>=0 && j < soundStateActive.size())
						   return soundStateActive[j];
					  else {
						  int k = indexStateVoice(uid);
						  if(k>=0 && k < voiceStateActive.size())
							 return voiceStateActive[k];
					 }        
				return false;
			}
			bool isLooping(string uid){
				int j =indexStateSound(uid);
					 if(j>=0 && j < soundStateLoope.size())
						   return soundStateLoope[j];
					  else {
						  int k = indexStateVoice(uid);
						  if(k>=0 && k < voiceStateLoope.size())
							   return voiceStateLoope[k];
					 }        
				return false;
			}
			int getCurrentSize(string uid){
				int j =indexStateSound(uid);
					 if(j>=0 && j < currentSize.size())
						   return currentSize[j];
					  else {
						  int k = indexStateVoice(uid);
						  if(k>=0 && k < currentSize.size())
							   return currentSize[k];
					 }        
				return -1;
			}
			void setCurrentSize(string uid,int i){
				 int j =indexStateSound(uid);
					 if(j>=0)
						   currentSize[j]=i;
					  else {
						  int k = indexStateVoice(uid);
						  if(k>=0)
						   currentSize[k]=i;
					 } 
			}
			int indexStateSound(string uid){
				int i;
				for(i=0;i<soundLoope.size();i++){
					if(soundLoope[i].compare(uid)==0)
						return i;
				}
				return -1;
		}	    
		int indexStateVoice(string uid){
			  int i;
				 for(i=0;i<soundVoiceLoope.size();i++){
					LOGI("indexStateVoice");
					if(soundVoiceLoope[i].compare(uid)==0)
						return i;
				}
				return -1;
			}
			bool isVoiceActive(){
				 int i;
				 for(i=0;i<soundVoiceLoope.size();i++){
					if(voiceStateActive[i])
						return voiceStateActive[i];
					 }
				 return false;       
			}
			bool isSoundActive(){
				 int i;
				 for(i=0;i<soundLoope.size();i++){
					if(soundStateActive[i])
						return true;
					 }
				 return false;       
			}
			bool isVoiceRec(){
				 int i;
				 for(i=0;i<soundVoiceLoope.size();i++){
					if(voiceStateRec[i])
						return true;
					 }
				 return false;       
			}
			string getVoiceRecordable(){
				 int i;	
				 for(i=0;i<soundVoiceLoope.size();i++){
					if(voiceStateRec[i])
						return soundVoiceLoope[i];
					 }
				 return "";       
			}
			bool voiceToSound(string uid){
				 int k = indexStateVoice(uid);
				 if(k>0){
					bool loop = voiceStateLoope[k];
					bool rec = voiceStateRec[k];
					bool active = voiceStateActive[k];
					eraseSoundVoiceLoope(uid);
					setSoundLoope(uid);
					setSoundStateLoop(uid,loop);
					setSoundStateRec(uid,rec);
					setSoundStateActive(uid,active);
					return true;
				 }
				return false;	
			}
			bool soundToVoice(string uid){
				 int k = indexStateSound(uid);
				 if(k>0){
					bool loop = soundStateLoope[k];
					bool rec = soundStateRec[k];
					bool active = soundStateActive[k];
					eraseSoundLoope(uid);
					setSoundVoiceLoope(uid);
					setSoundStateLoop(uid,loop);
					setSoundStateRec(uid,rec);
					setSoundStateActive(uid,active);
					return true;
				 }
				return false;	
			}
			void erase(){
			 soundLoope.clear();
			 soundVoiceLoope.clear();
			 currentSize.clear();
			 soundStateLoope.clear();
			 soundStateRec.clear();
			 soundStateActive.clear();
			 voiceStateLoope.clear();
			 voiceStateRec.clear();
			 voiceStateActive.clear();
			}
		private:
			 vector<string> soundLoope;
			 vector<string> soundVoiceLoope;
			 vector<int> currentSize;
			 vector<bool> soundStateLoope;
			 vector<bool> soundStateRec;
			 vector<bool> soundStateActive;
			 vector<bool> voiceStateLoope;
			 vector<bool> voiceStateRec;
			 vector<bool> voiceStateActive;
			 int soundNumber;
			 int soundVoiceNumber;
			 string voiceActive;
			 
		}myLoop;

		// synthesize a mono sawtooth wave and place it into a buffer (called automatically on load)
		__attribute__((constructor)) static void onDlOpen(void)
		{
			unsigned i;

			for (i = 0; i < SAWTOOTH_FRAMES; ++i) {
				sawtoothBuffer[i] = 32768 - ((i % 100) * 660);
			}
		}


		// get current recorderbuffer using mutex
		short *  getCurrentRecorderBuffered(int who){
			ostringstream oss;
			 oss<<sizeof(recorderBuffer1)<<"\t"<<who;
			//LOGI(("value counter: "+oss.str()).c_str());
			 sem_wait(&mutex);
				 //PRODUCTEUR bqRecordCallback
				if((countBufferCurrent % bufferNumber) == 0){
					recorderbufferCurrent = recorderBuffer1;
					countBufferCurrent++;
					//LOGI(" mutex 1");
				}
				else if((countBufferCurrent % bufferNumber) == 1){
					recorderbufferCurrent =recorderBuffer2;
					countBufferCurrent++;
					//LOGI("mutex 2");
				}
				else{
					recorderbufferCurrent = recorderBuffer3;
					countBufferCurrent++;
					//LOGI(" mutex 3");
				}
				sem_post(&mutex);
			return recorderbufferCurrent;
		}
		//stop music
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_stopMusic(JNIEnv* env, jclass clazz){
		   IS_STOPPED = true;
		}
		extern "C"
  void Java_be_umons_ibeatbox_main_NativeAudio_setInstrument(JNIEnv* env, jclass clazz,jint instr){
     numberInstrmnt = instr; 
     if((instr>=0 && instr<10) || (instr>=25 && instr<=27)){
        voiceByNumber(numberInstrmnt,&instrmnt);
         
            
     } 
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getTimeSignatureDen(JNIEnv* env, jclass clazz){
		   return timeSignatureDen;
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getTimeSignatureNum(JNIEnv* env, jclass clazz){
			return timeSignatureNum;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setTimeSignatureDen(JNIEnv* env, jclass clazz,jint sign){
		   timeSignatureDen = sign;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setTimeSignatureNum(JNIEnv* env, jclass clazz,jint sign){
			timeSignatureNum = sign;
		}
		//bmp from java
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setBMP(JNIEnv* env, jclass clazz,jint bpm){
			rythmeBPM = bpm;
			double min = minute;
			sizeBeat = (TOTAL_RECORDER_FRAMES/(min))*(60.0/rythmeBPM);
		}

		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getBMP(JNIEnv* env, jclass clazz){
			return rythmeBPM;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setMinute(JNIEnv* env, jclass clazz,jint min){
			minute = min;
			int d =(8000*minute);
			//TOTAL_RECORDER_FRAMES =d;
			//TOTAL_RECORDER_FRAMES += d;
		}

		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getMinute(JNIEnv* env, jclass clazz){
			return minute;
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getBeatvalue(JNIEnv* env, jclass clazz){
			return countBeat;	
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getBarvalue(JNIEnv* env, jclass clazz){
			return countBar;	
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getMesure(JNIEnv* env, jclass clazz){
			return mesure;	
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setMesure(JNIEnv* env, jclass clazz,jint mes){
			mesure = mes;	
		}
		extern "C"
		void initCounterRythm(){
		   countBeat=1;
		   countBar=1;
		   countFractionBeat = 1;
		}
		// this callback handler is called every time a buffer finishes playing
		void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context){
			assert(bq == bqPlayerBufferQueue);
			assert(NULL == context);
			SLresult result;
			if(IS_REALTIME && !IS_STOPPED){
			 /////////////////////////////////STK//////////////////
			 tick(RECORDER_FRAMES);	
			 ///////////////////////////////////////////////////
			
				//short * t =getCurrentRecorderBuffered(0);
				memcpy(t,recorderbufferCurrent,RECORDER_FRAMES*sizeof(short));
				//result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,
				//	recorderbufferCurrent,RECORDER_FRAMES*sizeof(short));
				int i;
				int stateBuffer = countBufferPlay*RECORDER_FRAMES;
				countBufferPlay ++;
				for(i=0;i<RECORDER_FRAMES;i++){
					//if(0){
					if((stateBuffer + i) < (nextbeat + RECORDER_FRAMES) && (stateBuffer+i)>= nextbeat){
					 beatChange = 1;
						if(countBeat == 1)
							mixBuffer[i]=metroBufferHight[i]+t[i]+stkBuffer[i];
						else 
							mixBuffer[i]=metroBufferLow[i]+t[i]+stkBuffer[i];
						}
					else{
					//à ajouté beatbar
						mixBuffer[i]=t[i]+stkBuffer[i];
						if(beatChange==1){
						  beatChange = 0;
						  countBeat = (countBeat + 1)%timeSignatureNum;
						  if(countBeat == 1){
							 countBar = (countBar+1)%timeSignatureDen;
						  }
						  nextbeat +=sizeBeat;
						  
						  //LOGI("increment metronome---------------------------------------------------->");
						}
					}
					 stkRecBuffer[i]=t[i]+stkBuffer[i];
				}
			memcpy(recorderBuffer_TOTAL+recorderSize, stkRecBuffer,RECORDER_FRAMES*sizeof(short));
			recorderSize += RECORDER_FRAMES;			
			//result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,mixBuffer,RECORDER_FRAMES*sizeof(short));
			 	result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,stkBuffer,RECORDER_FRAMES*sizeof(short));	     		     	
			}
			// for streaming playback, replace this test by logic to find and fill the next buffer
			else if (--nextCount > 0 && NULL != nextBuffer && 0 != nextSize) {
				//SLresult result;
				// enqueue another buffer
				result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, nextBuffer, nextSize);
				// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
				// which for this code example would indicate a programming error
				assert(SL_RESULT_SUCCESS == result);
			}
			else if (IS_LOOPING && !IS_STOPPED){
				LOGI("looping are");
				bool voice = myLoop.isVoiceRec();
				//bool voicetosound =true;
					   // on test s'il y a au moins 1 son active
					   if(voice || myLoop.isSoundActive()){
							  LOGI("1");
								  ostringstream oss;
								  int i = 0,j=0,k=0,n=myLoop.getSoundLoope().size();
							  for(i=0;i<LOOPING_FRAMES;i++){
								 LOGI("boucle 1");
									  dataCopy[i]=0;
										for(j=0;j<n;j++){
										   vector<short> tmp = sound_list[myLoop.getSoundLoope()[j]];
												// testons son etat de lecture actuelle	
											int currentSize = myLoop.getCurrentSize(myLoop.getSoundLoope()[j]);	
										   if((i<tmp.size() || i< currentSize)&& 
										   (myLoop.isActive(myLoop.getSoundLoope()[j]) || myLoop.isLooping(myLoop.getSoundLoope()[j])))
												 //dataCopy[i] = dataCopy[i] + tmp[i+recorderSize];
												 dataCopy[i] = dataCopy[i] + tmp[i+currentSize];
												 LOGI("somme sound 1");
										  }
										  //// add voice record
										  if(voice && LOOP_IS_RECORDING && recorderbufferCurrent){
											 LOGI("voice tr 1");
											 //oss  << recorderbufferCurrent[i]<<"\n";
											 //LOGI((oss.str()).c_str());
											 // pack qui est d'étre enregistre possibilite d'avoir decalage
											 dataCopy[i] = dataCopy[i] + recorderbufferCurrent[i];
											 LOGI("voice tr 2");
											 //voicetosound =false;
										  }
								 }
							  //if this is a active and recording is finish 	 
							  if(voice && !LOOP_IS_RECORDING){
												//lecture automatique 
									myLoop.voiceToSound(myLoop.getVoiceRecordable());
									LOGI("fin enregistrement");
									//exit(0);
								}
							   // update currentSize of each sound buffer	 
							  for(j=0;j<n;j++){
								if(myLoop.isLooping(myLoop.getSoundLoope()[j])){	
									int cSize = (myLoop.getCurrentSize(myLoop.getSoundLoope()[j]) + LOOPING_FRAMES)% TOTAL_RECORDER_FRAMES;
									myLoop.setCurrentSize(myLoop.getSoundLoope()[j],cSize);
									 ostringstream oss;
									oss<<"\t"<<myLoop.getCurrentSize(myLoop.getSoundLoope()[j]) ;
									LOGI(("incremnt counter in loop : "+oss.str()).c_str());
								}
								else if(myLoop.isActive(myLoop.getSoundLoope()[j])){
									int cSize = myLoop.getCurrentSize(myLoop.getSoundLoope()[j]) + LOOPING_FRAMES;
									myLoop.setCurrentSize(myLoop.getSoundLoope()[j],cSize);
									ostringstream oss;
									oss<<"\t"<<myLoop.getCurrentSize(myLoop.getSoundLoope()[j]) ;
									LOGI(("incremnt counter in loop : "+oss.str()).c_str());
									}	
								}
								  /// here buffer enregistrement 	
								  memcpy(loopBuffer_TOTAL+recorderSize,dataCopy,LOOPING_FRAMES*sizeof(short));	
							   result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,dataCopy,LOOPING_FRAMES*sizeof(short));   
						 }
						 //si pas de sound active attente de 1 sec
						 else{
							LOGI("no sound active waiting 1 sec");
							sleep(1);
						 }
			}
			else{
						LOGI("end of player or looping");
				}  
		}


		// this callback handler is called every time a buffer finishes recording
		void bqRecorderCallback(SLAndroidSimpleBufferQueueItf bq, void *context)
		{
			assert(bq == bqrecorderBufferQueue);
			assert(NULL == context);
			SLresult result;
			//LOGI("loop voice recodercallback");
			//trouver SLAndroidSimpleBufferQueueItf
			// for streaming recording, here we would call Enqueue to give recorder the next buffer to fill
			// but instead, this is a one-time buffer so we stop recording
			short * recorderbuffer = getCurrentRecorderBuffered(1);
			 ostringstream strs,nb;
			 strs<<numberInstrmnt<<"\n";
  LOGI((strs.str()).c_str());
			/////////////// Analysis buffer ////////////////////
			   // Detectors CALLBACK
			   int i;
for (i=0;i<block_size;i++) {
  audio.data[0][i] = recorderbuffer[i];
  audio.data[0][i] /=((1<<15)-1);
} 
      //clock_t start, end;
      //start = clock();
      audio_to_audio_event->processAudio(&audio, position);
      audio_events = audio_to_audio_event->getAudioEvent();
      //end = clock(); 
      //timeDetector += ((double)(end-start)/CLOCKS_PER_SEC);          
for (i=0;i<audio_events->size();i++) {
//strs<<"parametre:  "<<(*audio_events)[i].attack<<"\t"<<(*audio_events)[i].pitch <<"\t"<<(*audio_events)[i].amplitude<<"\n";
//LOGI((strs.str()).c_str());
  if ( (*audio_events)[i].attack ) { /// note detect
	// noteOn
	 //(*audio_events)[i].pitch
  //(*audio_events)[i].amplitude
   strs<<"parametre evets:  "<<(*audio_events)[i].pitch <<"\t"<<(*audio_events)[i].amplitude<<"\n";
  LOGI((strs.str()).c_str());
  //instrmnt->noteOn(440,0.5);
  instrmnt->noteOn((*audio_events)[i].pitch,(*audio_events)[i].amplitude);
  }
  else if ( (*audio_events)[i].release ) {
	// noteOff
  }
  else if ( (*audio_events)[i].active ) {
    // changePitch
  }
}
//LOGI("fin experience"); 
//exit(0);			   
			//////////////////////////////////////////////////
			//copie du buffer courant dans le grand buffer       
			if(recorderSize<TOTAL_RECORDER_FRAMES-(bufferNumber*RECORDER_FRAMES) && !IS_STOPPED){
				result = (*recorderBufferQueue)->Enqueue(bq,recorderbuffer,
							RECORDER_FRAMES * sizeof(short));
							//LOGI("LOOP_IS_RECORDING true");
							ostringstream oss;
							oss<<"\t"<<recorderSize;
							//LOGI(("value counter: "+oss.str()).c_str());
			}
			else{
			ostringstream strs1;
			strs1 <<timeDetector<<"\n"; 
   LOGI((strs1.str()).c_str());  
			result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_STOPPED);
			//record_play_realtime = 0;
			//LOGI("LOOP_IS_RECORDING stop");
			ostringstream oss;
			oss<<"\t"<<recorderSize;
			LOGI(("value counter time: "+oss.str()).c_str());
			IS_REALTIME = false;
			LOOP_IS_RECORDING  = false;
			initCounterRythm();
			}
			if (SL_RESULT_SUCCESS == result) {
				//recorderSize = RECORDER_FRAMES * sizeof(short);
				recorderSR = SL_SAMPLINGRATE_8;
				if(SAMPLE_RATE==8000)
				   recorderSR=SL_SAMPLINGRATE_8;
				else if(SAMPLE_RATE==16000) 
				     recorderSR=SL_SAMPLINGRATE_16;   
			}
		}
		extern "C"
		void  Java_be_umons_ibeatbox_main_NativeAudio_stopRecord(JNIEnv* env, jclass clazz){
			IS_STOPPED = true;
		}
		
		extern "C"
		class SoundTraitemnt
		{
		public:
			int testSoundBorne() const { 
			int i;
			for(i=0;i<128;i++){
			   if(this->recorder[i]>4000 || this->recorder[i]<(-4000)){
				  return 1;
			   }
			}
			return 0; } 
			void setSound(short *value){this->recorder = value;}
		private:
			 short * recorder;
		}mysound;
		
		
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_SoundTraitemnt(JNIEnv* env, jclass clazz){
					 SoundTraitemnt mysound ;
					 short * t =getCurrentRecorderBuffered(0);
					 mysound.setSound(t);
			   return mysound.testSoundBorne();
		}
		// create the engine and output mix objects
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_createEngine(JNIEnv* env, jclass clazz)
		{
			SLresult result;
			// create engine
			result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
			assert(SL_RESULT_SUCCESS == result);

			// realize the engine
			result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
			assert(SL_RESULT_SUCCESS == result);

			// get the engine interface, which is needed in order to create other objects
			result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
			assert(SL_RESULT_SUCCESS == result);

			// create output mix, with environmental reverb specified as a non-required interface
			const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
			const SLboolean req[1] = {SL_BOOLEAN_FALSE};
			result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);
			assert(SL_RESULT_SUCCESS == result);

			// realize the output mix
			result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
			assert(SL_RESULT_SUCCESS == result);

			// get the environmental reverb interface
			// this could fail if the environmental reverb effect is not available,
			// either because the feature is not present, excessive CPU load, or
			// the required MODIFY_AUDIO_SETTINGS permission was not requested and granted
			result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
					&outputMixEnvironmentalReverb);
			if (SL_RESULT_SUCCESS == result) {
				result = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
						outputMixEnvironmentalReverb, &reverbSettings);
			}
			// ignore unsuccessful result codes for environmental reverb, as it is optional for this example

		}
 
		// create buffer queue audio player
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_createBufferQueueAudioPlayer(JNIEnv* env,
				jclass clazz)
		{
			SLresult result;

			// configure audio source
			SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
			SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
				SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
				SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};


			SLDataSource audioSrc = {&loc_bufq, &format_pcm};

			// configure audio sink
			SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
			SLDataSink audioSnk = {&loc_outmix, NULL};

			// create audio player
			const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND,
					/*SL_IID_MUTESOLO,*/ SL_IID_VOLUME};
			const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
					/*SL_BOOLEAN_TRUE,*/  SL_BOOLEAN_TRUE};
			result = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audioSrc, &audioSnk,
					3, ids, req);
			assert(SL_RESULT_SUCCESS == result);

			// realize the player
			result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
			assert(SL_RESULT_SUCCESS == result);

			// get the play interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
			assert(SL_RESULT_SUCCESS == result);

			// get the buffer queue interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
					&bqPlayerBufferQueue);
			assert(SL_RESULT_SUCCESS == result);

			// register callback on the buffer queue
			result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
			assert(SL_RESULT_SUCCESS == result);

			// get the effect send interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_EFFECTSEND,
					&bqPlayerEffectSend);
			assert(SL_RESULT_SUCCESS == result);

		#if 0   // mute/solo is not supported for sources that are known to be mono, as this is
			// get the mute/solo interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_MUTESOLO, &bqPlayerMuteSolo);
			assert(SL_RESULT_SUCCESS == result);
		#endif

			// get the volume interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_VOLUME, &bqPlayerVolume);
			assert(SL_RESULT_SUCCESS == result);

			// set the player's state to playing
			result = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
			assert(SL_RESULT_SUCCESS == result);

		}

  /**
  
		// create buffer queue audio player
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_createBufferQueueAudioPlayer(JNIEnv* env,
				jclass clazz)
		{
			SLresult result;

			// configure audio source
			SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
			SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_16,
				SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
				SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
     if(SAMPLE_RATE==8000)
				       format_pcm.samplesPerSec=SL_SAMPLINGRATE_8;
				 else if(SAMPLE_RATE==16000) 
				       format_pcm.samplesPerSec=SL_SAMPLINGRATE_16;   

			SLDataSource audioSrc = {&loc_bufq, &format_pcm};

			// configure audio sink
			SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
			SLDataSink audioSnk = {&loc_outmix, NULL};

			// create audio player
			const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_EFFECTSEND,
					/*SL_IID_MUTESOLO,*/ /*SL_IID_VOLUME};
			const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
					/*SL_BOOLEAN_TRUE,*/ /*SL_BOOLEAN_TRUE};
			result = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audioSrc, &audioSnk,
					3, ids, req);
			assert(SL_RESULT_SUCCESS == result);

			// realize the player
			result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
			assert(SL_RESULT_SUCCESS == result);

			// get the play interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bqPlayerPlay);
			assert(SL_RESULT_SUCCESS == result);

			// get the buffer queue interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE,
					&bqPlayerBufferQueue);
			assert(SL_RESULT_SUCCESS == result);

			// register callback on the buffer queue
			result = (*bqPlayerBufferQueue)->RegisterCallback(bqPlayerBufferQueue, bqPlayerCallback, NULL);
			assert(SL_RESULT_SUCCESS == result);

			// get the effect send interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_EFFECTSEND,
					&bqPlayerEffectSend);
			assert(SL_RESULT_SUCCESS == result);

		#if 0   // mute/solo is not supported for sources that are known to be mono, as this is
			// get the mute/solo interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_MUTESOLO, &bqPlayerMuteSolo);
			assert(SL_RESULT_SUCCESS == result);
		#endif

			// get the volume interface
			result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_VOLUME, &bqPlayerVolume);
			assert(SL_RESULT_SUCCESS == result);

			// set the player's state to playing
			result = (*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
			assert(SL_RESULT_SUCCESS == result);

		}
  */
		// create URI audio player
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_createUriAudioPlayer(JNIEnv* env, jclass clazz,
				jstring uri)
		{

			SLresult result;

			// convert Java string to UTF-8
			const char *utf8 = env->GetStringUTFChars(uri, NULL);
			assert(NULL != utf8);

			// configure audio source
			// (requires the INTERNET permission depending on the uri parameter)
			SLDataLocator_URI loc_uri = {SL_DATALOCATOR_URI, (SLchar *) utf8};
			SLDataFormat_MIME format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
			SLDataSource audioSrc = {&loc_uri, &format_mime};

			// configure audio sink
			SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
			SLDataSink audioSnk = {&loc_outmix, NULL};

			// create audio player
			const SLInterfaceID ids[3] = {SL_IID_SEEK, SL_IID_MUTESOLO, SL_IID_VOLUME};
			const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
			result = (*engineEngine)->CreateAudioPlayer(engineEngine, &uriPlayerObject, &audioSrc,
					&audioSnk, 3, ids, req);
			// note that an invalid URI is not detected here, but during prepare/prefetch on Android,
			// or possibly during Realize on other platforms
			assert(SL_RESULT_SUCCESS == result);

			// release the Java string and UTF-8
			env->ReleaseStringUTFChars(uri, utf8);

			// realize the player
			result = (*uriPlayerObject)->Realize(uriPlayerObject, SL_BOOLEAN_FALSE);
			// this will always succeed on Android, but we check result for portability to other platforms
			if (SL_RESULT_SUCCESS != result) {
				(*uriPlayerObject)->Destroy(uriPlayerObject);
				uriPlayerObject = NULL;
				return JNI_FALSE;
			}

			// get the play interface
			result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_PLAY, &uriPlayerPlay);
			assert(SL_RESULT_SUCCESS == result);

			// get the seek interface
			result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_SEEK, &uriPlayerSeek);
			assert(SL_RESULT_SUCCESS == result);

			// get the mute/solo interface
			result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_MUTESOLO, &uriPlayerMuteSolo);
			assert(SL_RESULT_SUCCESS == result);

			// get the volume interface
			result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_VOLUME, &uriPlayerVolume);
			assert(SL_RESULT_SUCCESS == result);

			return JNI_TRUE;
		}


		// set the playing state for the URI audio player
		// to PLAYING (true) or PAUSED (false)
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setPlayingUriAudioPlayer(JNIEnv* env,
				jclass clazz, jboolean isPlaying)
		{
			SLresult result;

			// make sure the URI audio player was created
			if (NULL != uriPlayerPlay) {

				// set the player's state
				result = (*uriPlayerPlay)->SetPlayState(uriPlayerPlay, isPlaying ?
					SL_PLAYSTATE_PLAYING : SL_PLAYSTATE_PAUSED);
				assert(SL_RESULT_SUCCESS == result);

			}

		}


		// set the whole file looping state for the URI audio player
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setLoopingUriAudioPlayer(JNIEnv* env,
				jclass clazz, jboolean isLooping)
		{
			SLresult result;

			// make sure the URI audio player was created
			if (NULL != uriPlayerSeek) {

				// set the looping state
				result = (*uriPlayerSeek)->SetLoop(uriPlayerSeek, (SLboolean) isLooping, 0,
						SL_TIME_UNKNOWN);
				assert(SL_RESULT_SUCCESS == result);

			}

		}


		// expose the mute/solo APIs to Java for one of the 3 players

		static SLMuteSoloItf getMuteSolo()
		{
			if (uriPlayerMuteSolo != NULL)
				return uriPlayerMuteSolo;
			else if (fdPlayerMuteSolo != NULL)
				return fdPlayerMuteSolo;
			else
				return bqPlayerMuteSolo;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setChannelMuteUriAudioPlayer(JNIEnv* env,
				jclass clazz, jint chan, jboolean mute)
		{
			SLresult result;
			SLMuteSoloItf muteSoloItf = getMuteSolo();
			if (NULL != muteSoloItf) {
				result = (*muteSoloItf)->SetChannelMute(muteSoloItf, chan, mute);
				assert(SL_RESULT_SUCCESS == result);
			}
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setChannelSoloUriAudioPlayer(JNIEnv* env,
				jclass clazz, jint chan, jboolean solo)
		{
			SLresult result;
			SLMuteSoloItf muteSoloItf = getMuteSolo();
			if (NULL != muteSoloItf) {
				result = (*muteSoloItf)->SetChannelSolo(muteSoloItf, chan, solo);
				assert(SL_RESULT_SUCCESS == result);
			}
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getNumChannelsUriAudioPlayer(JNIEnv* env, jclass clazz)
		{
			SLuint8 numChannels;
			SLresult result;
			SLMuteSoloItf muteSoloItf = getMuteSolo();
			if (NULL != muteSoloItf) {
				result = (*muteSoloItf)->GetNumChannels(muteSoloItf, &numChannels);
				if (SL_RESULT_PRECONDITIONS_VIOLATED == result) {
					// channel count is not yet known
					numChannels = 0;
				} else {
					assert(SL_RESULT_SUCCESS == result);
				}
			} else {
				numChannels = 0;
			}
			return numChannels;
		}

		// expose the volume APIs to Java for one of the 3 players

		static SLVolumeItf getVolume()
		{
			if (uriPlayerVolume != NULL)
				return uriPlayerVolume;
			else if (fdPlayerVolume != NULL)
				return fdPlayerVolume;
			else
				return bqPlayerVolume;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setVolumeUriAudioPlayer(JNIEnv* env, jclass clazz,
				jint millibel)
		{
			SLresult result;
			SLVolumeItf volumeItf = getVolume();
			if (NULL != volumeItf) {
				result = (*volumeItf)->SetVolumeLevel(volumeItf, millibel);
				assert(SL_RESULT_SUCCESS == result);
			}
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setMuteUriAudioPlayer(JNIEnv* env, jclass clazz,
				jboolean mute)
		{
			SLresult result;
			SLVolumeItf volumeItf = getVolume();
			if (NULL != volumeItf) {
				result = (*volumeItf)->SetMute(volumeItf, mute);
				assert(SL_RESULT_SUCCESS == result);
			}
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_enableStereoPositionUriAudioPlayer(JNIEnv* env,
				jclass clazz, jboolean enable)
		{
			SLresult result;
			SLVolumeItf volumeItf = getVolume();
			if (NULL != volumeItf) {
				result = (*volumeItf)->EnableStereoPosition(volumeItf, enable);
				assert(SL_RESULT_SUCCESS == result);
			}
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setStereoPositionUriAudioPlayer(JNIEnv* env,
				jclass clazz, jint permille)
		{
			SLresult result;
			SLVolumeItf volumeItf = getVolume();
			if (NULL != volumeItf) {
				result = (*volumeItf)->SetStereoPosition(volumeItf, permille);
				assert(SL_RESULT_SUCCESS == result);
			}
		}

		// enable reverb on the buffer queue player
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_enableReverb(JNIEnv* env, jclass clazz,
				jboolean enabled){
			SLresult result;
			// we might not have been able to add environmental reverb to the output mix
			if (NULL == outputMixEnvironmentalReverb) {
				return JNI_FALSE;
			}
			result = (*bqPlayerEffectSend)->EnableEffectSend(bqPlayerEffectSend,
					outputMixEnvironmentalReverb, (SLboolean) enabled, (SLmillibel) 0);
			// and even if environmental reverb was present, it might no longer be available
			if (SL_RESULT_SUCCESS != result) {
				return JNI_FALSE;
			}

			return JNI_TRUE;
		}


		// select the desired clip and play count, and enqueue the first buffer if idle
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_selectClip(JNIEnv* env, jclass clazz, jint which,
				jint count)
		{	(*bqPlayerPlay)->SetPlayState(bqPlayerPlay, SL_PLAYSTATE_PLAYING);
			short *oldBuffer = nextBuffer;
			switch (which) {
			case 0:     // CLIP_NONE
				nextBuffer = (short *) NULL;
				nextSize = 0;
				break;
			case 1:     // CLIP_HELLO
				nextBuffer = (short *) hello;
				nextSize = sizeof(hello);
				break;
			case 2:     // CLIP_ANDROID
				nextBuffer = (short *) android;
				nextSize = sizeof(android);
				break;
			case 3:     // CLIP_SAWTOOTH
				nextBuffer = recorderBuffer_TOTAL;
				nextSize = TOTAL_RECORDER_FRAMES;
				break;
			case 4:     // CLIP_PLAYBACK
				// we recorded at 16 kHz, but are playing buffers at 8 Khz, so do a primitive down-sample
				/**myValue = myValue+1;
				if (recorderSR == SL_SAMPLINGRATE_16) {
					unsigned i;
					for (i = 0; i < recorderSize; i += 2) {
						recorderBuffer_TOTAL[i/2] = recorderBuffer_TOTAL[i];
					}
					recorderSR = SL_SAMPLINGRATE_8;
					recorderSize /= 2;

				}*/
				//recorderSR = SL_SAMPLINGRATE_8;
				   //LOGI(("playing :"+NAME_PLAYING).c_str());
				//nextBuffer = sound_list[NAME_PLAYING];
				//nextSize = TOTAL_RECORDER_FRAMES;//recorderSize;
				break;
			default:
				nextBuffer = NULL;
				nextSize = 0;
				break;
			}
			nextCount = count;
			if (nextSize > 0) {
				//LOGI("PLAYING");
				// here we only enqueue one buffer because it is a long clip,
				// but for streaming playback we would typically enqueue at least 2 buffers to start
				SLresult result;
				result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, nextBuffer, nextSize*sizeof(short));
				if (SL_RESULT_SUCCESS != result) {
					return JNI_FALSE;
				}
			}

			return JNI_TRUE;
		}


		// create asset audio player
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_createAssetAudioPlayer(JNIEnv* env, jclass clazz,
				jobject assetManager, jstring filename)
		{
			SLresult result;

			// convert Java string to UTF-8
			const char *utf8 = env->GetStringUTFChars(filename, NULL);
			assert(NULL != utf8);

			// use asset manager to open asset by filename
			AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
			assert(NULL != mgr);
			AAsset* asset = AAssetManager_open(mgr, utf8, AASSET_MODE_UNKNOWN);

			// release the Java string and UTF-8
			env->ReleaseStringUTFChars(filename, utf8);

			// the asset might not be found
			if (NULL == asset) {
				return JNI_FALSE;
			}

			// open asset as file descriptor
			off_t start, length;
			int fd = AAsset_openFileDescriptor(asset, &start, &length);
			assert(0 <= fd);
			AAsset_close(asset);

			// configure audio source
			SLDataLocator_AndroidFD loc_fd = {SL_DATALOCATOR_ANDROIDFD, fd, start, length};
			SLDataFormat_MIME format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
			SLDataSource audioSrc = {&loc_fd, &format_mime};

			// configure audio sink
			SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
			SLDataSink audioSnk = {&loc_outmix, NULL};

			// create audio player
			const SLInterfaceID ids[3] = {SL_IID_SEEK, SL_IID_MUTESOLO, SL_IID_VOLUME};
			const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
			result = (*engineEngine)->CreateAudioPlayer(engineEngine, &fdPlayerObject, &audioSrc, &audioSnk,
					3, ids, req);
			assert(SL_RESULT_SUCCESS == result);

			// realize the player
			result = (*fdPlayerObject)->Realize(fdPlayerObject, SL_BOOLEAN_FALSE);
			assert(SL_RESULT_SUCCESS == result);

			// get the play interface
			result = (*fdPlayerObject)->GetInterface(fdPlayerObject, SL_IID_PLAY, &fdPlayerPlay);
			assert(SL_RESULT_SUCCESS == result);

			// get the seek interface
			result = (*fdPlayerObject)->GetInterface(fdPlayerObject, SL_IID_SEEK, &fdPlayerSeek);
			assert(SL_RESULT_SUCCESS == result);

			// get the mute/solo interface
			result = (*fdPlayerObject)->GetInterface(fdPlayerObject, SL_IID_MUTESOLO, &fdPlayerMuteSolo);
			assert(SL_RESULT_SUCCESS == result);

			// get the volume interface
			result = (*fdPlayerObject)->GetInterface(fdPlayerObject, SL_IID_VOLUME, &fdPlayerVolume);
			assert(SL_RESULT_SUCCESS == result);

			// enable whole file looping
			result = (*fdPlayerSeek)->SetLoop(fdPlayerSeek, SL_BOOLEAN_TRUE, 0, SL_TIME_UNKNOWN);
			assert(SL_RESULT_SUCCESS == result);
			return JNI_TRUE;
		}


		// set the playing state for the asset audio player
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setPlayingAssetAudioPlayer(JNIEnv* env,
				jclass clazz, jboolean isPlaying)
		{
			SLresult result;

			// make sure the asset audio player was created
			if (NULL != fdPlayerPlay) {

				// set the player's state
				result = (*fdPlayerPlay)->SetPlayState(fdPlayerPlay, isPlaying ?
					SL_PLAYSTATE_PLAYING : SL_PLAYSTATE_PAUSED);
				assert(SL_RESULT_SUCCESS == result);

			}

		}

/**
		// create audio recorder
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_createAudioRecorder(JNIEnv* env, jclass clazz)
		{
			SLresult result;
     LOGI("create 1");
			// configure audio source
			SLDataLocator_IODevice loc_dev = {SL_DATALOCATOR_IODEVICE, SL_IODEVICE_AUDIOINPUT,
					SL_DEFAULTDEVICEID_AUDIOINPUT, NULL};
			SLDataSource audioSrc = {&loc_dev, NULL};
			
		   LOGI("create 2");
			// configure audio sink
			SLDataLocator_AndroidSimpleBufferQueue loc_bq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
			LOGI("create 3");
			SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
				SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
				SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
				LOGI("create 4");
				if(SAMPLE_RATE==8000){
				    LOGI("create 4.5");
				   format_pcm.samplesPerSec=SL_SAMPLINGRATE_8;
				    LOGI("create 5");
				   }
				else if(SAMPLE_RATE==16000){ 
				      LOGI("create ");
				       format_pcm.samplesPerSec=SL_SAMPLINGRATE_16;
				       LOGI("create 6"); 
				      }    
			SLDataSink audioSnk = {&loc_bq, &format_pcm};
    
			// create audio recorder
			// (requires the RECORD_AUDIO permission)
			const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
			const SLboolean req[1] = {SL_BOOLEAN_TRUE};
			//debut semaphore
			sem_init(&mutex, 0, 1);
			result = (*engineEngine)->CreateAudioRecorder(engineEngine, &recorderObject, &audioSrc,
					&audioSnk, 1, id, req);
			if (SL_RESULT_SUCCESS != result) {
				return JNI_FALSE;
			}

			// realize the audio recorder
			result = (*recorderObject)->Realize(recorderObject, SL_BOOLEAN_FALSE);
			if (SL_RESULT_SUCCESS != result) {
				return JNI_FALSE;
			}

			// get the record interface
			result = (*recorderObject)->GetInterface(recorderObject, SL_IID_RECORD, &recorderRecord);
			assert(SL_RESULT_SUCCESS == result);

			// get the buffer queue interface
			result = (*recorderObject)->GetInterface(recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
					&recorderBufferQueue);
			assert(SL_RESULT_SUCCESS == result);

			// register callback on the buffer queue
			result = (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, bqRecorderCallback,
					NULL);
			assert(SL_RESULT_SUCCESS == result);

			return JNI_TRUE;
		}

*/

		// create audio recorder
		extern "C"
		jboolean Java_be_umons_ibeatbox_main_NativeAudio_createAudioRecorder(JNIEnv* env, jclass clazz)
		{
			SLresult result;

			// configure audio source
			SLDataLocator_IODevice loc_dev = {SL_DATALOCATOR_IODEVICE, SL_IODEVICE_AUDIOINPUT,
					SL_DEFAULTDEVICEID_AUDIOINPUT, NULL};
			SLDataSource audioSrc = {&loc_dev, NULL};
			
		   
			// configure audio sink
			SLDataLocator_AndroidSimpleBufferQueue loc_bq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
			SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
				SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
				SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
			SLDataSink audioSnk = {&loc_bq, &format_pcm};

			// create audio recorder
			// (requires the RECORD_AUDIO permission)
			const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
			const SLboolean req[1] = {SL_BOOLEAN_TRUE};
			//debut semaphore
			sem_init(&mutex, 0, 1);
			result = (*engineEngine)->CreateAudioRecorder(engineEngine, &recorderObject, &audioSrc,
					&audioSnk, 1, id, req);
			if (SL_RESULT_SUCCESS != result) {
				return JNI_FALSE;
			}

			// realize the audio recorder
			result = (*recorderObject)->Realize(recorderObject, SL_BOOLEAN_FALSE);
			if (SL_RESULT_SUCCESS != result) {
				return JNI_FALSE;
			}

			// get the record interface
			result = (*recorderObject)->GetInterface(recorderObject, SL_IID_RECORD, &recorderRecord);
			assert(SL_RESULT_SUCCESS == result);

			// get the buffer queue interface
			result = (*recorderObject)->GetInterface(recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
					&recorderBufferQueue);
			assert(SL_RESULT_SUCCESS == result);

			// register callback on the buffer queue
			result = (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, bqRecorderCallback,
					NULL);
			assert(SL_RESULT_SUCCESS == result);

			return JNI_TRUE;
		}
		// set the recording state for the audio recorder
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_startRecording(JNIEnv* env, jclass clazz)
		{
		 LOGI("start record");
			SLresult result;
			//for __android_log_print(ANDROID_LOG_INFO, "YourApp", "formatted message");
			// in case already recording, stop recording and clear buffer queue
			result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_STOPPED);
			assert(SL_RESULT_SUCCESS == result);
			result = (*recorderBufferQueue)->Clear(recorderBufferQueue);
			result = (*bqPlayerBufferQueue) -> Clear(bqPlayerBufferQueue);
			assert(SL_RESULT_SUCCESS == result);
			LOGI("start record 1");
    /////////////////////Metronome Hight and low///////////////////////////
    	int k;
			for(k=0;k<RECORDER_FRAMES;k++){
				metroBufferHight[k] = (rand()%8000)-4000;
				metroBufferLow[k] = metroBufferHight[k]/2;
				metroBufferNull[k] = 0;
			}
    /////////////////////////// STK /////////////////////////////
   /** 
    char *instruments[]={"Clarinet", "BlowHole", "Saxofony", "Flute", "Brass",
"BlowBotl", "Bowed", "Plucked", "StifKarp", "Sitar", "Mandolin",
"Rhodey", "Wurley", "TubeBell", "HevyMetl", "PercFlut",
"BeeThree", "FMVoices", "VoicForm", "Moog", "Simple", "Drummer",
"BandedWG", "Shakers", "ModalBar", "Mesh2D", "Resonate", "Whistle"
	}; */
	  //LOGI("avt onset detector");
	  ///////////////////////onset detector////////////////////////////////////
	  //Detectors INIT
	  ostringstream strs,nb;
	   strs << numberInstrmnt <<"\n"; 
    LOGI((strs.str()).c_str());
   audio.numchannels = 1;
   audio.sampleframes = block_size;
   audio.samplerate = sample_rate;
   audio.data = new float*[1];
   LOGI("start record2");
   (audio.data)[0] = new float[block_size];
    LOGI("onset detector 1"); 
   audio_to_audio_event = new AudioToAudioEvent(sample_rate); 
   LOGI("onset detector 2"); 
   audio_to_audio_event->setParameters(block_size);
   LOGI("onset detector 3"); 
   audio_to_audio_event->setParametersSampleRate (sample_rate);
   LOGI("onset detector 4"); 
   audio_to_audio_event->setParametersA4 (440);
   LOGI("onset detector 5"); 
    ///////////////////////onset detector//////////////////////////////////////
    	 //LOGI("fin onset detector"); 
    if(instrmnt !=0){
       delete instrmnt;
       instrmnt=0;
       //LOGI("delete instr");
       }  	  
    stk::Stk::setSampleRate( sample_rate );
    stk::Stk::setRawwavePath( "rawwaves/" );
    //instrmnt = new stk::Clarinet(10.0);
    stk::StkFrames frame(RECORDER_FRAMES,1);
    /**
    for(k=0;k<=27;k++){
      ////LOGI("1");
      clock_t start, end;
      ostringstream strs,nb;
      string str;
      if((k>=0 && k<10) || (k>=25 && k<=27)){
      voiceByNumber(k,&instrmnt);
      instrmnt->noteOn(125,0.1);
      ////LOGI("2");
      start = clock();
      int l;
      for(l=0;l<25;l++){
         
        instrmnt->tick(frame);
        ////LOGI("3 for");
       } 
       instrmnt->noteOff(0.1);
        ////LOGI("4");
      end = clock();
      if(instrmnt !=0){
       delete instrmnt;
       instrmnt=0;
       ////LOGI("5");
       }              
      double timeDetector= ((double)(end-start)/CLOCKS_PER_SEC)*100;
      strs << k <<"\t"<<instruments[k]<<"\t"<<timeDetector<<"%"; 
      LOGI((strs.str()).c_str());
      }
     }
     //exit(0);*/
     voiceByNumber(numberInstrmnt,&instrmnt);
     stk::StkFloat droneFreqs[3] = { 261.6,277.2, 196.0 };
     instrmnt->noteOn(droneFreqs[0],0.1);
     instrmnt->noteOn(droneFreqs[1],0.1);
    /////////////////////////// STK ///////////////////////////////+
			// the buffer is not valid for playback yet
			recorderSize = 0;

			// enqueue an empty buffer to be filled by the recorder
			 //(for streaming recording, we would enqueue at least 2 empty buffers to start things off)
			IS_REALTIME = true;
			IS_LOOPING = false;
			IS_STOPPED = false;
			countPlayer = 0;
			// play before
			//short metro[RECORDER_FRAMES];q
		result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, metroBufferHight,RECORDER_FRAMES*sizeof(short));
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer1,
					RECORDER_FRAMES * sizeof(short));
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer2,
						RECORDER_FRAMES * sizeof(short));
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer3,
							RECORDER_FRAMES * sizeof(short));
			// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
			// which for this code example would indicate a programming error
			assert(SL_RESULT_SUCCESS == result);

			// start recording
			result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_RECORDING);
			assert(SL_RESULT_SUCCESS == result);

		}


		// shut down the native audio system
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_shutdown(JNIEnv* env, jclass clazz)
		{

			// destroy buffer queue audio player object, and invalidate all associated interfaces
			if (bqPlayerObject != NULL) {
				(*bqPlayerObject)->Destroy(bqPlayerObject);
				bqPlayerObject = NULL;
				bqPlayerPlay = NULL;
				bqPlayerBufferQueue = NULL;
				bqPlayerEffectSend = NULL;
				bqPlayerMuteSolo = NULL;
				bqPlayerVolume = NULL;
			}

			// destroy file descriptor audio player object, and invalidate all associated interfaces
			if (fdPlayerObject != NULL) {
				(*fdPlayerObject)->Destroy(fdPlayerObject);
				fdPlayerObject = NULL;
				fdPlayerPlay = NULL;
				fdPlayerSeek = NULL;
				fdPlayerMuteSolo = NULL;
				fdPlayerVolume = NULL;
			}

			// destroy URI audio player object, and invalidate all associated interfaces
			if (uriPlayerObject != NULL) {
				(*uriPlayerObject)->Destroy(uriPlayerObject);
				uriPlayerObject = NULL;
				uriPlayerPlay = NULL;
				uriPlayerSeek = NULL;
				uriPlayerMuteSolo = NULL;
				uriPlayerVolume = NULL;
			}

			// destroy audio recorder object, and invalidate all associated interfaces
			if (recorderObject != NULL) {
				(*recorderObject)->Destroy(recorderObject);
				recorderObject = NULL;
				recorderRecord = NULL;
				recorderBufferQueue = NULL;
			}

			// destroy output mix object, and invalidate all associated interfaces
			if (outputMixObject != NULL) {
				(*outputMixObject)->Destroy(outputMixObject);
				outputMixObject = NULL;
				outputMixEnvironmentalReverb = NULL;
			}

			// destroy engine object, and invalidate all associated interfaces
			if (engineObject != NULL) {
				(*engineObject)->Destroy(engineObject);
				engineObject = NULL;
				engineEngine = NULL;
			}
		}
		extern "C"
		int saveDataFile(string name,int size,string open){
			FILE * file;
			//memcpy(myData,recorderBuffer_TOTAL,size*sizeof(short));
			LOGI("saving:");
			file = fopen(name.c_str(),open.c_str());
			if (file != NULL){
				   stringstream ss;
				   int i;
				   ss<<size<<":";
				   for(i=0;i<size;i++){
					 myData[i] = recorderBuffer_TOTAL[i];
				  ss << recorderBuffer_TOTAL[i]<<";";
				  }
				 //sound_list[name] =recorderBuffer_TOTAL; 
				 string str;
				 ss >> str;
				 const char* val;
				 val =str.c_str();
				 LOGI((name + " " +str).c_str());
				 fputs (val,file);
				 fclose (file); 
				 return 0;
			  }
			return -1;
		}        	   
		extern "C"
		int getSizeDataSound(string name){
			 int soundSize = 0; 
			 ifstream myfile(name.c_str());
		  if(myfile.is_open()){
			string ligne;
			int tester;
			LOGI("get data file open",name.c_str());
		   while(getline(myfile, ligne)){
				  LOGI("aui1");
				 string value,tmp1=";",tmp2=":";
				 int i,s=0,taille = ligne.size ();
				 for (i = 0 ; i < taille ; ++i){
				 LOGI("aui2");
				 stringstream ss;
				 string str;
				  ss << ligne.at(i);
				  ss >> str;
				  int test = str.compare(tmp2);
				  if(test==0){
					//soundSize = value;
					ss<<value;
					ss>>soundSize;
							size_sound[name] = soundSize;
					value="";
					i = taille;
				   }
				   else
					  value =value +str;
			   }
			}          
		 }
		  return soundSize;
		} 
		int getDataFile(string name){
		 int size =getSizeDataSound(name);
		 //short * mySound =(short*) malloc(sizeof(short) * size); 
		 //short * mySound = new short[size];
		 vector<short> mySound;
		 ifstream myfile(name.c_str());
		  if(myfile.is_open()){
			string ligne;
			int tester;
			int s = 0;
			LOGI("get data file open",name.c_str());
		   while(getline(myfile, ligne))
			 {   //LOGI(ligne.c_str());
				 stringstream ssi,ssi1;
				 LOGI((name + " ").c_str());
				 LOGI(ligne.c_str());
				 string soundSize,value,tmp1=";",tmp2=":";
				 int i,taille = ligne.size ();
				 for (i = 0 ; i < taille ; ++i){
				 stringstream ss;
				 string str;
				  ss << ligne.at(i);
				  ss >> str;
				  int test = str.compare(tmp2);
				  if(test==0){
					soundSize = value;
							 //size_sound[name] = value;
							 LOGI("ok size find");
							 LOGI(value.c_str());
					value="";
				   }
				   else if(str.compare(tmp1)==0){
					 istringstream istr(value);
					 short k;
					 //if(istr >> k && s <TOTAL_RECORDER_FRAMES){
					 if(istr >> k && s < TOTAL_RECORDER_FRAMES){
					  //recorderBuffer_TOTAL[s]=k;
					  mySound.push_back(k);
					  //ssi1<<s<<";\n";
					  //LOGI(ssi1.str().c_str());
					  s++;
					 }
					 else
					 cout << "no";
					 value = "";

				   }
				   else{
				   value =value +str;
				   }
			   }
			   sound_list[name]= mySound;
			  }
			  return 0;
		   }
		 else{
			LOGI(("file no open "+name).c_str());
			return -1;
		 }
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_saveRecordsFile(JNIEnv* env, jclass clazz,jstring soundname){
			const char *utf8 = env->GetStringUTFChars(soundname, NULL);
			string name = utf8;
			 FILE * file;
			//memcpy(myData,recorderBuffer_TOTAL,size*sizeof(short));
			short myData[TOTAL_RECORDER_FRAMES];
			LOGI("saving:");
			file = fopen(name.c_str(),"a");
			if (file != NULL){
				   stringstream ss;
				   int i;
				   ss<<TOTAL_RECORDER_FRAMES<<":";
				   for(i=0;i<TOTAL_RECORDER_FRAMES;i++){
					 myData[i] = recorderBuffer_TOTAL[i];
				  ss << recorderBuffer_TOTAL[i]<<";";
				  }
				 //sound_list[name] =myData; 
				 string str;
				 ss >> str;
				 const char* val;
				 val =str.c_str();
				 LOGI((name + " " +str).c_str());
				 fputs (val,file);
				 fclose (file); 
				 return 0;
			  }
			return -1;
			//return saveDataFile(name,recorderBuffer_TOTAL,TOTAL_RECORDER_FRAMES,"a");

		}
		  extern"C"
				int Java_be_umons_ibeatbox_main_NativeAudio_saveLoopFile(JNIEnv* env, jclass clazz,jstring soundname){
			const char *utf8 = env->GetStringUTFChars(soundname, NULL);
			string name = utf8;
			 FILE * file;
			//memcpy(myData,recorderBuffer_TOTAL,size*sizeof(short));
			short myData[TOTAL_RECORDER_FRAMES];
			LOGI("saving:");
			file = fopen(name.c_str(),"a");
			if (file != NULL){
				   stringstream ss;
				   int i;
				   ss<<TOTAL_RECORDER_FRAMES<<":";
				   for(i=0;i<TOTAL_RECORDER_FRAMES;i++){
					 myData[i] = loopBuffer_TOTAL[i];
				  ss << loopBuffer_TOTAL[i]<<";";
				  }
				 //sound_list[name] =myData; 
				 string str;
				 ss >> str;
				 const char* val;
				 val =str.c_str();
				 LOGI((name + " " +str).c_str());
				 fputs (val,file);
				 fclose (file); 
				 return 0;
			  }
			return -1;
			//return saveDataFile(name,recorderBuffer_TOTAL,TOTAL_RECORDER_FRAMES,"a");

		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_getRecordsFile(JNIEnv* env, jclass clazz,jstring filename){
		  const char *utf8 = env->GetStringUTFChars(filename, NULL);
			string name = utf8;
			int a = getDataFile(name);
			//LOGI("FIN DOWNLOAD DATA");
			NAME_PLAYING = name;
		}
		extern "C"
		int Java_be_umons_ibeatbox_main_NativeAudio_deleteSound(JNIEnv* env, jclass clazz,jstring filename){
		 const char *utf8 = env->GetStringUTFChars(filename, NULL);
		 /**	string name = utf8;
			if(remove(utf8) != 0)
			  LOGI("Error deleting file");
			else
			LOGI("File successfully deleted");  
		 sound_list.erase(name);
		 cancel_sound=1;
		 return cancel_sound;*/
		 return 0;
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_playSound(JNIEnv* env, jclass clazz,jstring soundname){
			const char *utf8 = env->GetStringUTFChars(soundname, NULL);
			string name = utf8;
			int i;
			 IS_STOPPED = false;
			 IS_REALTIME = false;
			 IS_LOOPING = false;
			 if(sound_list.find(name) == sound_list.end()){
				  getDataFile(name);
				  //LOGI("dl early");
			  }
			  //LOGI("dl now");
			   short dataSound[sound_list[name].size()];
			   copy(sound_list[name].begin(),sound_list[name].end(),dataSound);
			   SLresult result;
			   result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,dataSound,80000*sizeof(short));
			   
			}
				extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_startLoopVoice(JNIEnv* env, jclass clazz,jstring uid){
			const char *utf8 = env->GetStringUTFChars(uid, NULL);
			string voicename = utf8;
			IS_LOOPING = true;
			IS_REALTIME = false;
			IS_STOPPED = false;
			LOOP_IS_RECORDING=true;
			SLresult result;
			recorderSize =0;
			//LOGI("loop voice");
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer1,LOOPING_FRAMES * sizeof(short));
			//LOGI("loop voice 1");     
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer2,LOOPING_FRAMES * sizeof(short));
			//LOGI("loop voice 2");
			result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer3,LOOPING_FRAMES* sizeof(short));
			//LOGI("loop voice 3");  
			myLoop.setSoundStateRec(voicename,true);
			// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
			 // which for this code example would indicate a programming error
			assert(SL_RESULT_SUCCESS == result);
			// start recording 
			result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_RECORDING);
			assert(SL_RESULT_SUCCESS == result);	
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_startLoop(JNIEnv* env, jclass clazz){
			if(myLoop.getSoundNumber()>0 || myLoop.getSoundVoiceNumber() >0){
				//start loop
				IS_LOOPING = true;
				IS_REALTIME = false;
				IS_STOPPED = false;
				// get all data sound aviable
				//soundVoice = myLoop.getSoundVoiceLoope();
				sound = myLoop.getSoundLoope();
				
				SLresult result;
			//for __android_log_print(ANDROID_LOG_INFO, "YourApp", "formatted message");
			// in case already recording, stop recording and clear buffer queue
			result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_STOPPED);
			assert(SL_RESULT_SUCCESS == result);
			result = (*recorderBufferQueue)->Clear(recorderBufferQueue);
			assert(SL_RESULT_SUCCESS == result);
			recorderSize =0;
			int i = 0,j=0,k=0,n=sound.size();
			countBufferPlay = 4;
			 ostringstream oss;
			for(i=0;i<LOOPING_FRAMES;i++){
				dataCopy[i] = 0;
			  for(j=0;j<n;j++){
				//somme de buffer
				vector<short> tmp = sound_list[sound[j]];
				if(i<tmp.size() && myLoop.isActive(sound[j]))
					   dataCopy[i] = dataCopy[i] + tmp[i];
					}
				  }
				 //UPDATE currentsize 
				 for(j=0;j<n;j++){
					if(myLoop.isActive(sound[j]))
						myLoop.setCurrentSize(sound[j],LOOPING_FRAMES);	
				}
				recorderSize += LOOPING_FRAMES;
				  bool isVoice = myLoop.isVoiceRec();
				  if(!isVoice){
						//LOGI("no voice isVoice=false ");	
					   result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,dataCopy,LOOPING_FRAMES*sizeof(short)); 
					   } 
				  else{
						result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue,dataCopy,LOOPING_FRAMES*sizeof(short));	
						LOOP_IS_RECORDING = true;
						//LOGI("loop voice");
						result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer1,LOOPING_FRAMES * sizeof(short));
						//LOGI("loop voice 1");     
						result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer2,LOOPING_FRAMES * sizeof(short));
						//LOGI("loop voice 2");
						result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer3,LOOPING_FRAMES* sizeof(short));
						//LOGI("loop voice 3");  
				// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
			   // which for this code example would indicate a programming error
						assert(SL_RESULT_SUCCESS == result);
			   // start recording 
						result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_RECORDING);
						assert(SL_RESULT_SUCCESS == result);
			 }
			}
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_stopLoop(JNIEnv* env, jclass clazz){
			//stk::Instrmnt **instrument;
			//stk::Sitar voicer(8.0);
			if(myLoop.getSoundNumber()>0){
				   IS_LOOPING = false;
				}
			else
				LOGI("Sound map is Empy");
					
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setSoundStateActive(JNIEnv* env, jclass clazz,
		jstring uuid,jboolean isActive){
		const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
		 if(JNI_TRUE == isActive)   
			 myLoop.setSoundStateActive(uid,true);
		 else myLoop.setSoundStateActive(uid,false); 	 	
			
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setSoundStateLoop(JNIEnv* env, jclass clazz,
		jstring uuid,jboolean state){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
			  if(JNI_TRUE == state)   
			 myLoop.setSoundStateLoop(uid,true);
		 else myLoop.setSoundStateLoop(uid,false); 	 
			}
			extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setSoundStateRec(JNIEnv* env, jclass clazz,
		jstring uuid,jboolean state){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
			if(JNI_TRUE == state)   
			 myLoop.setSoundStateRec(uid,true);
		 else myLoop.setSoundStateRec(uid,false);
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_eraseSoundLoope(JNIEnv* env, jclass clazz,
		jstring uuid){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
			sound_list.erase(uid);
			size_sound.erase(uid);
			myLoop.eraseSoundLoope(uid);
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_eraseSoundVoiceLoope(JNIEnv* env, jclass clazz,
		jstring uuid){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
			sound_list.erase(uid);
			myLoop.eraseSoundVoiceLoope(uid);
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setLoopUIDSound(JNIEnv* env, jclass clazz,
		jstring uuid){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string name = utf3;
			if(sound_list.find(name)== sound_list.end())
				getDataFile(name);
				LOGI("before of load loop");    
			myLoop.setSoundLoope(name); 
			LOGI("end of load loop");
		}
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_setVoiceLoopUIDSound(JNIEnv* env, jclass clazz,
		jstring uuid){
			const char *utf3 = env->GetStringUTFChars(uuid, NULL);
			string uid = utf3;
			vector<short> voice (TOTAL_RECORDER_FRAMES,0);
			sound_list[uid]=voice;
			myLoop.setSoundVoiceLoope(uid);
			
		}
		//function call when close android app to save all modifie about register sound
		extern "C"
		void Java_be_umons_ibeatbox_main_NativeAudio_exitApp(JNIEnv* env, jclass clazz){
  //delete data.instrument; 
		myLoop.erase();
			 map<string,vector<short> >::iterator iter;
			  for (iter=sound_list.begin(); iter!=sound_list.end(); ++iter){
				 iter->second.clear();
			  }
			  sound_list.clear();
			//exit(0);  
		}

