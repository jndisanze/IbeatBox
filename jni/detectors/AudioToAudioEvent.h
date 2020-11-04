#ifndef __AudioToAudioEvent__
#define __AudioToAudioEvent__

#include <iostream>
#include <vector>
#include <string>
#include <math.h>

#include <purplemetaproc.h>

#define ANALYSER_PITCH_ALGO AnalyserPitchAlgo_Sphinx_Yin

struct SongPosition {
	float timestamp_s;
	float timestamp_mtc;
	int time_signature_num;
	int time_signature_den;
    int time_signature_ternary;
    float unit_of_time_value;
	int bpm;
	int subdivision;
};

struct Audio {
	float **data;
	int numchannels;
	int sampleframes;
	int samplerate;
};

struct AudioEvent {
    //
	float timestamp_s; 
	float timestamp_mtc;			// quantize timestamp in beat time code
    //
	float timestamp_start_s; 
	float timestamp_start_mtc;			// quantize timestamp in beat time code
	//
	float pitch, amplitude, maxamplitude;
	float spectralcentroid;
	float pitch_ratio;
	float pitch_ratio_extreme;
	float log_energy_ratio;
	float log_energy_ratio_extreme;
	float log_spectralcentroid_ratio;
	float log_spectralcentroid_ratio_extreme;
	//
	float pitchdeviation;
    int note;
    float noteFloat;
    bool attack, release;
	int articulation;
    float hammer, pulloff, slide, bend, bendDiscrete, bendSign, bendValidAttack, normalNote;
    bool dead;
    int harmonic; 
    float palmMuting, notPalmMuting;
    float pluckingPoint;
    int gstring;
    int fret;
	float duration_s;
    float duration_mtc;				// quantize duration in beat time code
    bool hammerStart, hammerStop, pulloffStart, pulloffStop, slideStart, slideStop, bendStart, bendStop, bendValue, prebendValue, bendSustainStart, bendSustainStop, slurStart, slurStop, palmmuteStart, palmmuteStop;
    //
	bool active;
};

class AudioToAudioEvent
{
public:
	
	AudioToAudioEvent( int sample_rate );
	~AudioToAudioEvent();
	
	void setParameters ( int _analyser_frame_length );
	void setParametersSampleRate ( float sample_rate );
	void setParametersA4 ( float a4 );

    void processAudio(Audio *audio, SongPosition position);
	vector<AudioEvent>* getAudioEvent();

protected:
	
	AnalyserParam		*analyser;
	OnsetDetectorParam	*detector;
	AnalyserPitchParam  *pitch_estimator;
	
	int a4note;
	float a4freq;
		
    int note_active;
	
    int block_size;
	int sample_rate;
	float framerate;

	int analyser_window_type;
	float analyser_frame_length;
	float analyser_frame_hop;
	float detectorhpon;
	float detectorlpon;
	float detectorhpcutoff;
	float detectorlpcutoff;
	float detectorthreshold;
	float detectorsensitivity;
	float detectorguard;
	float detectormode;
	int detector_onsetsds_whiten_whtype;
	float detector_onsetsds_odf_hpcutoff;
	float detector_onsetsds_odf_maxfreq;
	int detector_onsetsds_odf_logfreqscale;
	int detector_onsetsds_odf_perceptual;
	//int detector_onsetsds_odf_singleorvector;
	int detector_onsetsds_odf_rectify;
	int detector_onsetsds_odf_smoothref;
	int detector_onsetsds_odf_lagfordeviation;
	int detector_onsetsds_odf_type;
	int detector_onsetsds_detector_type;
	float detector_onsetsds_detector_threshold;
	int	pitch_algo;
	float pitch_range_low;
	float pitch_range_high;
	float pitch_yin_threshold;
	
	// Additional Parameters for Articulations
	int articulation_newnote_delay_max;
	int articulation_newnote_delay_current;
	float articulation_leftright_threshold;
	float articulation_muting_threshold;
	float articulation_muting_amplitude_threshold;
	float articulation_glitch_threshold;
	float articulation_ratiofeatures_delay;
    int articulation_pitchchange_delay;
	
	fx_vector in_signal;
	fx_vector out_partframe_mix;
	fx_vector out_wholeframe_rect_mix;
	fx_vector out_wholeframe_win_mix;
	cx_vector out_wholeframe_fft_mix;
	fx_vector out_wholeframe_fft_mix_amplitude;
	AudioFeatures	    audio_features;
	MIDIFeatures		midi_features;
	vector<AudioEvent>	audio_events_raw;
	AudioEvent			audio_event;
	AudioEvent			active_audio_event;
	vector<AudioEvent>	audio_events;
};

#endif
