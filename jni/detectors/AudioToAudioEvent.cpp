#include <AudioToAudioEvent.h>

#define ANALYSER_PITCH_ALGO AnalyserPitchAlgo_Sphinx_Yin

#include <android/log.h>
	#ifndef LOG_TAG
		#define LOG_TAG "JP LOG:"
		#endif

		#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
		#include <time.h>
		#include <ctime>
		#include <sstream>
  #include <string>
AudioToAudioEvent::AudioToAudioEvent(int sample_rate)
{
	int i;
	
	this->sample_rate = sample_rate;
	this->a4note = 69;
	this->a4freq = 440;
	
	this->note_active = 0;

	analyser = new_analyser_param(sample_rate);
	detector = new_onsetdetector_param(sample_rate);
	pitch_estimator = new_analyser_pitch_param(sample_rate, ANALYSER_PITCH_ALGO);
	
	in_signal.size = 0;
	out_partframe_mix.size = 0;
	out_wholeframe_rect_mix.size = 0;
	out_wholeframe_win_mix.size = 0;
	out_wholeframe_fft_mix.size = 0;
	out_wholeframe_fft_mix_amplitude.size = 0;
		
	AudioEvent local_audio_event;
	audio_events_raw.clear();
	articulation_newnote_delay_max = 10;
	articulation_newnote_delay_current = 4;
	for (i=0; i<articulation_newnote_delay_max + 3; i++) {
		audio_events_raw.push_back(local_audio_event);
	}
	
	memset(&active_audio_event, 0, sizeof(AudioEvent));
	
	articulation_leftright_threshold = 12; // dB
	articulation_muting_threshold = 7.5;	// dB
	articulation_muting_amplitude_threshold = 9.0;	// dB
	articulation_glitch_threshold = 24; // dB
	articulation_ratiofeatures_delay = 4+1;
	articulation_pitchchange_delay = 3+3;	
}

AudioToAudioEvent::~AudioToAudioEvent()
{
	if (analyser) { delete_analyser_param(analyser); }
	if (detector) { delete_onsetdetector_param(detector); }
	if (pitch_estimator) { delete_analyser_pitch_param(pitch_estimator); }
	
	if (in_signal.size) { fx_v_free(&in_signal); }
	if (out_partframe_mix.size) { fx_v_free(&out_partframe_mix); }
	if (out_wholeframe_rect_mix.size) { fx_v_free(&out_wholeframe_rect_mix); }
	if (out_wholeframe_win_mix.size) { fx_v_free(&out_wholeframe_win_mix); }
	if (out_wholeframe_fft_mix.size) { cx_v_free(&out_wholeframe_fft_mix); }
	if (out_wholeframe_fft_mix_amplitude.size) { fx_v_free(&out_wholeframe_fft_mix_amplitude); }	
}

void AudioToAudioEvent::setParameters(int _analyser_frame_length)
{
	a4note = 69;
	a4freq = 440;
	
	framerate = 0;
	
	analyser_window_type = 7;
	analyser_frame_length = _analyser_frame_length;
	analyser_frame_hop = 2;
	analyser_frame_hop = analyser_frame_length / analyser_frame_hop;
	
	detectorhpon = 1;
	detectorlpon = 0;
	detectorhpcutoff = 0;		// maybe to be adjusted
	detectorlpcutoff = 4000; // 20000;
	detectorthreshold = -48; // Don't modify
	detectorsensitivity = 0.5; //
	detectorguard = 25;
	detectormode = 1;
		
	detector_onsetsds_whiten_whtype = 0;
	detector_onsetsds_odf_hpcutoff = 0;
	detector_onsetsds_odf_maxfreq = 4000; // 16000;
	detector_onsetsds_odf_logfreqscale = 0;
	detector_onsetsds_odf_perceptual = 0;
	//int detector_onsetsds_odf_singleorvector;
	detector_onsetsds_odf_rectify = 1;
	detector_onsetsds_odf_smoothref = 1;
	detector_onsetsds_odf_lagfordeviation = 1;
	detector_onsetsds_odf_type = 6;
	detector_onsetsds_detector_type = 0;
	detector_onsetsds_detector_threshold = 0.1;// default: 0.06 // detector threshold to modify and call set_onsetdetector_param_sensitivity
	
	pitch_algo = ANALYSER_PITCH_ALGO;				// 2 = AnalyserPitchAlgo_Aubio_Yin
	pitch_range_low = 75; //75
	pitch_range_high = 1500;
	pitch_yin_threshold = 0.1;  //  default: 0.3
	
	set_analyser_samplerate(analyser, sample_rate);
	
	set_analyser_param_window(analyser, analyser_window_type);
	
	set_analyser_param_framelength(analyser, analyser_frame_length, analyser_frame_hop);
	
	set_onsetdetector_samplerate(detector, sample_rate);
	
	set_onsetdetector_param(detector, framerate, detectorhpon, detectorlpon, detectorhpcutoff, detectorlpcutoff,
							detectorthreshold, detectorguard);
	
	set_onsetdetector_param_framelength(detector, analyser_frame_length, analyser_frame_hop);
	
	set_onsetdetector_param_algo(detector, DetectorAlgo_Onsetsds);
		
	set_onsetdetector_param_sensitivity(detector, detector_onsetsds_detector_threshold); //detectorsensitivity);
	
	set_onsetdetector_param_onsetsds(detector, detector_onsetsds_whiten_whtype,
									 detector_onsetsds_odf_maxfreq,
									 detector_onsetsds_odf_logfreqscale,
									 detector_onsetsds_odf_perceptual,
									 //detector_onsetsds_odf_singleorvector,
									 detector_onsetsds_odf_rectify,
									 detector_onsetsds_odf_smoothref,
									 detector_onsetsds_odf_lagfordeviation,
									 detector_onsetsds_odf_type,
									 detector_onsetsds_detector_type);	
	
	set_onsetdetector_mode(detector, detectormode);
	
	set_analyser_pitch_samplerate(pitch_estimator, sample_rate);
	
	set_analyser_pitch_algo(pitch_estimator, pitch_algo);
	
	set_analyser_pitch_param_framelength(pitch_estimator, analyser_frame_length, analyser_frame_hop);
	
	set_analyser_pitch_param_pitchrange(pitch_estimator, pitch_range_low, pitch_range_high);
	
	set_analyser_pitch_param_yinthreshold(pitch_estimator, pitch_yin_threshold);
}

void AudioToAudioEvent::setParametersSampleRate(float sample_rate)
{
	this->sample_rate = sample_rate;
	set_analyser_samplerate(analyser, sample_rate);
	set_onsetdetector_samplerate(detector, sample_rate);
	set_analyser_pitch_samplerate(pitch_estimator, sample_rate);
}

void AudioToAudioEvent::setParametersA4(float a4) {
	if (a4>0) {
		this->a4freq = a4;
	}
}

void AudioToAudioEvent::processAudio(Audio *audio, SongPosition position)
{
	int is_attack;
	int is_release;
	int is_attack_left;
	int is_attack_right;
	
	int is_attack_muted;
	int is_attack_pitchchange;
	int is_attack_glitch;
	
	int time_delay;
	int sampleframes = audio->sampleframes;
	
	int wholeframe_length, partframe_length;
	float onset_function;
	
	audio_events.clear();
	
	if (in_signal.size!=sampleframes) {
		if (in_signal.size) {
			fx_v_free(&in_signal);
		}
		in_signal = fx_v_create(sampleframes); 
	}
	
	memcpy(in_signal.el, (audio->data)[0], sampleframes*sizeof(float));
		
	analyser_process_spectral_improved_newblock(analyser, &in_signal, 0, 0);
	
	get_analyser_framelength(analyser, &wholeframe_length, &partframe_length);
	
	if (out_partframe_mix.size!=partframe_length) {
		if (out_partframe_mix.size) {
			fx_v_free(&out_partframe_mix);
		}
		out_partframe_mix = fx_v_create(partframe_length);
	}
	if (out_wholeframe_rect_mix.size!=wholeframe_length) {
		if (out_wholeframe_rect_mix.size) {fx_v_free(&out_wholeframe_rect_mix);}
		out_wholeframe_rect_mix = fx_v_create(wholeframe_length);
		if (out_wholeframe_win_mix.size) {fx_v_free(&out_wholeframe_win_mix);}
		out_wholeframe_win_mix = fx_v_create(wholeframe_length);
		if (out_wholeframe_fft_mix.size) {cx_v_free(&out_wholeframe_fft_mix);}
		out_wholeframe_fft_mix = cx_v_create(wholeframe_length);
		if (out_wholeframe_fft_mix_amplitude.size) {fx_v_free(&out_wholeframe_fft_mix_amplitude);}
		out_wholeframe_fft_mix_amplitude = fx_v_create(wholeframe_length);
	}	
		
	int ret = 1;
	float local_analysis_sample_rate;
	int eaten_samples;
	
	int rets = 0;
	double acc1=0;
	double acc2=0; 
	double acc3=0; 
	ostringstream str1;
	clock_t start, end;
 start = clock();
	while (ret) {
		//analyse à accumuler l
		LOGI("a");
		ret = analyser_process_spectral_improved(analyser, &in_signal, 0, 0,
												 &out_partframe_mix, &out_wholeframe_rect_mix,
												 &out_wholeframe_win_mix, &out_wholeframe_fft_mix,
												 &eaten_samples);
		
		local_analysis_sample_rate = sample_rate / out_partframe_mix.size;
		end = clock();
		acc1 += ((double)(end-start)/CLOCKS_PER_SEC)*100; 
		str1<<"analyser_process_spectral \t"<<acc1<<"\n";
		LOGI((str1.str()).c_str()); 
		if (ret) {

			time_delay = onsetdetector_get_delay(detector); // if fixed algo delay is know, then this can be retracted
															// because the plugin can advertise it.
			
			memset(&audio_features, 0, sizeof(audio_features));
			memset(&midi_features, 0, sizeof(MIDIFeatures));			
			memset(&audio_event, 0, sizeof(AudioEvent));
			//detector on set
			ostringstream str2;
			clock_t start1, end1;
   start1 = clock();
			process_onsetdetector(detector, &out_partframe_mix, &out_wholeframe_fft_mix, &audio_features);
			end1 = clock();
			acc2 += ((double)(end1-start1)/CLOCKS_PER_SEC)*100; 
		 str2<<"process_onsetdetector \t"<<acc2<<"\n";
		 LOGI((str2.str()).c_str()); 
			get_onset_function(detector, &onset_function);
			ostringstream str3;
			clock_t start2, end2;
   start2 = clock();
			//prosses hauteur pitch
			process_analyser_pitch(pitch_estimator, &out_partframe_mix, &out_wholeframe_fft_mix, &audio_features);
			end2 = clock();
			acc3 += ((double)(end2-start2)/CLOCKS_PER_SEC)*100; 
		 str3<<"process_analyser_pitch \t"<<acc3<<"\n";
		 LOGI((str3.str()).c_str());
			for (int i=audio_events_raw.size()-1;i>0;i--) {
				audio_events_raw[i] = audio_events_raw[i-1];
			}
			
			if (audio_features.noteonoff_set) {
				if (audio_features.noteonoff==NoteOnOffType_On) {
					audio_event.attack = 1;
				}
				else if (audio_features.noteonoff==NoteOnOffType_Off) {
					audio_event.release = 1;
				}
			}
			if (audio_features.pitch_set) {
				audio_event.pitch = audio_features.pitch;
			}
			if (audio_features.energy_set) {
				audio_event.amplitude = audio_features.energy;
			}
			audio_event.note = round (12.0 * log(audio_event.pitch/this->a4freq) / log (2.0) + this->a4note);
            audio_event.noteFloat = ( float ) ( 12.0 * log( audio_event.pitch / this->a4freq ) / log ( 2.0 ) + ( float )this->a4note ); 
			audio_event.timestamp_s = position.timestamp_s + (float)rets * (float)out_partframe_mix.size / (float)sample_rate;
			audio_event.timestamp_mtc = position.timestamp_mtc;
			audio_event.timestamp_start_s = audio_event.timestamp_s;
			audio_event.timestamp_start_mtc = audio_event.timestamp_mtc;
			
			rets++;
			
			// DRAFT CODE - FEATURE EXTRACTOR
			// SD TODO - this should be moved to analyser / detector code
			float maxamplitude = 0;
			for (int i=0; i<out_partframe_mix.size; i++) {
				if (fabs(out_partframe_mix.el[i].f)>maxamplitude) {
					maxamplitude = fabs(out_partframe_mix.el[i].f);
				}
			}
			maxamplitude = 20.0 * log10(maxamplitude);
			audio_event.maxamplitude = maxamplitude;
			audio_event.spectralcentroid = 0;
			float fftamplitudesum;
			fftamplitudesum = 0;
			for (int i=0;i<out_wholeframe_fft_mix.size/2+1;i++) {
				out_wholeframe_fft_mix_amplitude.el[i].f = sqrt(out_wholeframe_fft_mix.el[i].re.f*out_wholeframe_fft_mix.el[i].re.f
																+ out_wholeframe_fft_mix.el[i].im.f*out_wholeframe_fft_mix.el[i].im.f);
			}
			for (int i=0;i<out_wholeframe_fft_mix.size/2+1;i++) {
				fftamplitudesum += out_wholeframe_fft_mix_amplitude.el[i].f;
				audio_event.spectralcentroid += (float)i * sample_rate / analyser_frame_length / fftamplitudesum * out_wholeframe_fft_mix_amplitude.el[i].f;
			}
			// PITCH RATIO
			audio_event.pitch_ratio = audio_event.pitch / audio_events_raw[articulation_ratiofeatures_delay].pitch;
			// LOG ENERGY RATIO
			audio_event.log_energy_ratio = audio_event.amplitude - audio_events_raw[articulation_ratiofeatures_delay].amplitude; // dB
			// LOG CENTER OF GRAVITY RATIO
			audio_event.log_spectralcentroid_ratio = log10(audio_event.spectralcentroid / audio_events_raw[articulation_ratiofeatures_delay].spectralcentroid);
			float max_spectralcentroid;
			int maxpos_spectralcentroid;
			float min_spectralcentroid;
			max_spectralcentroid = 0;
			maxpos_spectralcentroid = articulation_newnote_delay_current;
			min_spectralcentroid = sample_rate;
			for (int i=articulation_newnote_delay_current-2;i<=articulation_newnote_delay_current+1;i++) {
				if (audio_events_raw[i].spectralcentroid>max_spectralcentroid) {
					max_spectralcentroid = audio_events_raw[i].spectralcentroid;
					maxpos_spectralcentroid = i;
				}
			}
			for (int i=articulation_newnote_delay_current-1;i<articulation_newnote_delay_current+3;i++) {
				if (audio_events_raw[i].spectralcentroid<min_spectralcentroid) {
					min_spectralcentroid = audio_events_raw[i].spectralcentroid;
				}
			}
			audio_event.pitch_ratio_extreme = audio_events_raw[max(0,maxpos_spectralcentroid-2)].pitch
			/ audio_events_raw[min(int(audio_events_raw.size())-1,maxpos_spectralcentroid+2)].pitch;
			audio_event.log_energy_ratio_extreme = audio_events_raw[max(0,maxpos_spectralcentroid-2)].amplitude
			- audio_events_raw[min(int(audio_events_raw.size())-1,maxpos_spectralcentroid+2)].amplitude;
			audio_event.log_spectralcentroid_ratio_extreme = log10(audio_events_raw[max(0,maxpos_spectralcentroid-2)].spectralcentroid
																   / audio_events_raw[min(int(audio_events_raw.size())-1,maxpos_spectralcentroid+2)].spectralcentroid);
			// BUFFER
			audio_events_raw[0] = audio_event;
						
			is_release = audio_event.release;
			
			is_attack = 0;
            			
			// PITCH CHANGE TRIGGER ATTACK
			if (note_active) {
				if ( (audio_events_raw[0].fret >= 0) && (audio_events_raw[0].fret != audio_events_raw[articulation_newnote_delay_current].fret) ) {
					int tmp_attack = 0;
					for (int i=0;i<=articulation_newnote_delay_current;i++) {
						if (audio_events_raw[i].attack==1) {
							tmp_attack = 1;
						}
					}
					if (!tmp_attack) {
						audio_events_raw[0].attack = 1;
					}
				}
			}
			
			// LEGATO - NON-LEGATO DETECTION
			float energy_max = -96;
			float energy_maxamplitude = -96;
			float energy_min = 0;
			is_attack_left = 0;
			is_attack_right = 0;
			if (audio_events_raw[articulation_newnote_delay_current].attack==1) {
				for (int i=0;i<=articulation_newnote_delay_current;i++) {
					if (audio_events_raw[i].amplitude>energy_max) {
						energy_max = audio_events_raw[i].amplitude;
					}
					if (audio_events_raw[i].maxamplitude>energy_maxamplitude) {
						energy_maxamplitude = audio_events_raw[i].maxamplitude;
					}
				}
				for (int i=articulation_newnote_delay_current+1;i<audio_events_raw.size();i++) {
					if (audio_events_raw[i].amplitude<energy_min) {
						energy_min = audio_events_raw[i].amplitude;
					}
				}
				if (energy_max-energy_min > articulation_leftright_threshold) {
					is_attack_right = 1;
				}
				else {
					is_attack_left = 1;
				}
                ////////is_attack_right = 1;
			}
			
			// PITCH CHANGE DETECTION
			is_attack_pitchchange = 0;
			if (audio_events_raw[0].pitch && audio_events_raw[articulation_pitchchange_delay].pitch) {                
				if ( ( audio_events_raw[ 0 ].pitch / audio_events_raw[ articulation_pitchchange_delay ].pitch > pow( 2.0,0.66 / 12.0 ))
					|| (audio_events_raw[ 0 ].pitch/audio_events_raw[ articulation_pitchchange_delay ].pitch < pow( 2.0,-0.66 / 12.0 )) ) {
					is_attack_pitchchange = 1;					
				}
			}

			// MUTE DETECTION
			is_attack_muted = 0;
			if (is_attack_left) {
				is_attack_muted = 0;
				if (energy_max-audio_events_raw[0].amplitude > articulation_muting_threshold
					|| energy_maxamplitude-audio_events_raw[0].maxamplitude > articulation_muting_amplitude_threshold
					|| energy_max-audio_events_raw[1].amplitude > articulation_muting_threshold 
					|| energy_maxamplitude-audio_events_raw[1].maxamplitude > articulation_muting_amplitude_threshold) {
					is_attack_muted = 1;
				}	
			}
			
			// GLITCH DETECTION
			is_attack_glitch = 0;
			if (audio_events_raw[articulation_newnote_delay_current].maxamplitude-audio_events_raw[0].maxamplitude > articulation_glitch_threshold) {
				is_attack_glitch = 1;
			}
			if (energy_max-audio_events_raw[0].amplitude <= -48) { // can be even higher the -48 probably
				is_attack_glitch = 1;
			}
			
			// FINAL DECISIONS
			is_attack = 0;
			
			if ( ( audio_events_raw[0].pitch!=0 ) && ( !is_attack_glitch ) ) {
				if (is_attack_right) {
					is_attack = 1;
				}
				else if ( is_attack_left ) {
					if ( !is_attack_muted ) {
						if ( is_attack_pitchchange ) { 
							is_attack = 1;
						}
					}
				}
                else if ( is_attack_pitchchange ) {	
                    if ( !is_attack_muted ) {
                        if ( is_attack_pitchchange ) { 
                            is_attack = 1;
                         }
                    }					
                }
			}
			
			// WRITING
			if ( is_attack == 1 ) {
				if ( note_active == 1 ) {
					audio_event.gstring = active_audio_event.gstring;
					audio_event.fret = active_audio_event.fret;
					audio_event.timestamp_start_s = active_audio_event.timestamp_s;
					audio_event.timestamp_start_mtc = active_audio_event.timestamp_mtc;
					audio_event.duration_s = audio_event.timestamp_s - active_audio_event.timestamp_s;
					audio_event.duration_mtc = audio_event.timestamp_mtc - active_audio_event.timestamp_mtc;					
					audio_event.attack = 0;
					audio_event.release = 1;
					audio_events.push_back(audio_event);					
					note_active = 0;
				}
				audio_event.pitch = audio_events_raw[0].pitch;
				audio_event.gstring = audio_events_raw[articulation_newnote_delay_current-2].gstring;
				audio_event.fret = audio_events_raw[0].fret;
                audio_event.pluckingPoint = audio_events_raw[0].pluckingPoint;
   				audio_event.timestamp_start_s = audio_events_raw[ articulation_newnote_delay_current ].timestamp_s;
				audio_event.timestamp_start_mtc = audio_events_raw[ articulation_newnote_delay_current ].timestamp_mtc;				
				audio_event.duration_s = 0;
				audio_event.duration_mtc = 0;
				audio_event.attack = 1;
				audio_event.release = 0;				
				note_active = 1;
				active_audio_event = audio_event;
				audio_events.push_back( audio_event );
			}
			else if ( ( note_active == 1 ) && ( is_release == 1 ) ) {
				audio_event.gstring = active_audio_event.gstring;
				audio_event.fret = active_audio_event.fret;
				audio_event.timestamp_start_s = active_audio_event.timestamp_s;
				audio_event.timestamp_start_mtc = active_audio_event.timestamp_mtc;
				audio_event.duration_s = audio_event.timestamp_s - active_audio_event.timestamp_s;
				audio_event.duration_mtc = audio_event.timestamp_mtc - active_audio_event.timestamp_mtc;					
				audio_event.attack = 0;
				audio_event.release = 1;
                audio_event.pitchdeviation = (float)0;
				note_active = 0;
				audio_events.push_back(audio_event);
			}
			else {
				audio_event.attack = 0;
				audio_event.release = 0;
				audio_events.push_back(audio_event);
			}
			
		}
		
	}
	
}

vector<AudioEvent>* AudioToAudioEvent::getAudioEvent()
{
	return &audio_events;
}

/*
 
// GLOBAL VARIABLES
int order_resolution = -4;
int logscale_ratio = 1;
int ola = 2;
int decramping = 0;
 
// VARIABLES
int sample_rate = 16000;
int block_size = 128;
SongPosition position;
Audio audio;
vector<AudioEvent>* audio_events;
AudioToAudioEvent* audio_to_audio_event;

// INIT
audio.numchannels = 1;
audio.sampleframs = block_size;
audio.samplerate = sample_rate;
audio.data = new float*[1];
audio.data[0] = new float[block_size];
audio_to_audio_event = new AudioToAudioEvent(sample_rate);
audio_to_audio_event->setParameters(block_size);
audio_to_audio_event->setParametersSampleRate (sample_rate);
audio_to_audio_event->setParametersA4 (440);
 
// CALLBACK
for (i=0;i<block_size;i++) {
  audio.data[0][i] = recorderbuffer[i];
} 
audio_to_audio_event->processAudio(Audio *audio, position);
audio_events = audio_to_audio_event->getAudioEvent();
for (i=0;i<audio_events->size();i++) {
  if ( (*local_audio_events)[i].attack ) {
	// noteOn
	// (*local_audio_events)[i].pitch
    // (*local_audio_events)[i].amplitude
  }
  else if ( (*local_audio_events)[i].release ) {
	// noteOff
  }
  else if ( (*local_audio_events)[i].active ) {
    // changePitch
  }
}
 
// CLOSE
delete audio_to_audio_event;
 
*/
