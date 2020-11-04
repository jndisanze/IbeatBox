//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : purplemetaproc.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : purpleproc_config.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

#ifndef _PURPLEPROC_CONFIG_
#define _PURPLEPROC_CONFIG_

#ifdef WIN32

// MS VS8 partial specialization support
#define __STL_CLASS_PARTIAL_SPECIALIZATION

// MS VS8 default for char is signed while it was unsigned in previous versions
// Use /J option in project properties, or define _CHAR_UNSIGNED
// #define _CHAR_UNSIGNED

// MS VS8 detects incorrect iterator use and will assert and display a dialog box at run time in debug mode
// use _HAS_ITERATOR_DEBUGGING to avoid this
// Idem but relatred to stream C++ library 
// basic_istream::read => Consider using basic_istream::_Read_s instead.
// basic_streambuf::sgetn => Consider using basic_streambuf::_Sgetn_s
// basic_streambuf::xgetn => Consider using basic_streambuf::_Xgetn_s
#define _HAS_ITERATOR_DEBUGGING 0
#define _SECURE_SCL 0

// MS is deprecating some ANSI C usage in the name of security.
// This discards the related warnings
//#define _CRT_NONSTDC_NO_DEPRECATE 1
#define _CRT_SECURE_NO_DEPRECATE 1
#pragma warning (disable: 4996)

#endif

#include <assert.h>

#ifdef __cplusplus
namespace std {}
using namespace std;
#endif

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : purplevector.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

#ifndef _FX_ALL_
#define _FX_ALL_

#include <stdio.h>
#if defined(WIN32)
#include <conio.h>
#include <windows.h>
#endif
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
//#include <purpleproc_config.h>

#include <math.h>

#define MAX_FLOAT ((float)3.40282346638528860e+38)
#define MIN_FLOAT ((float)1.17549435e-38)
#define MAX_INT (2147483647)
#define MIN_INT (-2147483647-1)

#define E                     (double)2.7182818284590452354
#define LOG2_E          (double)1.4426950408889634074
#define LOG10_E         (double)0.43429448190325182765
#define PI                  (double)3.14159265358979323846
#define PI_2              (double)1.57079632679489661923
#define PI_4              (double)0.78539816339744830962
#define INV_PI          (double)0.31830988618379067154
#define SQRT_PI         (double)1.772453850905516
#define INV_SQRT_PI       (double)0.5641895835477563
#define INV_SQRT_2PI    (double)0.3989422804014327
#define SQRT_2          (double)1.41421356237309504880
#define SQRT_1_2        (double)0.70710678118654752440
#define LN_2              (double)0.69314718055994530942
#define LN_10             (double)2.30258509299404568402
#define LN_MAX          (double)709.7827128933840

/* Square of a real number. */
#undef sqr
#define sqr(x) ((x) * (x))
/* Exponential base 2. */
#undef exp2
#define exp2(x) exp((x) * LN_2)
/* Exponential base 10. */
#undef exp10
#define exp10(x) exp((x) * LN_10)
/* Rounds a real number to the nearest integer. */
#undef round
#define round(x) ((long int)(2.0*(x)) - (long int) (x))
/* Rounds a real number towards zero. */
#undef trunc
#define trunc(x) ((long int)(x))
/* Returns +1 if RealNumber is > 0, -1 if it is < 0 and 0 otherwise */
#undef sign
#define sign(x) (((x) >= 0 ? 1 : 0) - ((x) <= 0 ? 1 : 0))
/* Returns the fractional part of x */
#undef frac
#define frac(x) ((x) - trunc(x))
/* Returns the abs */
#undef vabs
#define vabs(x) (((x) >= 0) ? (x) : -(x))
/* Returns max of two values */
#define vmax(x, y) ((x) > (y) ? (x) : (y))
/* Returns min of two values */
#define vmin(x, y) ((x) < (y) ? (x) : (y))
#if !defined(WIN32) && !defined(_WIN32_WINCE)
/* Returns max of two values */
//#define max(x, y) ((x) > (y) ? (x) : (y))
/* Returns min of two values */
//#define min(x, y) ((x) < (y) ? (x) : (y))
#endif
/* Some checked functions */
#define Log(x) ((x) <= 0 ? -MAX_FLOAT : log(x))
#define Log10(x) ((x) <= 0 ? -MAX_FLOAT : log10(x))
#define Sqrt(x) ((x) <= 0 ? MIN_FLOAT : sqrt(x))
#define Exp(x) ((x) >= LN_MAX ? MAX_FLOAT : exp(x))

#if defined(_WIN32) || defined(_WIN64)

//#define sec(x)	(1 / cos(x))
//#define csc(x)	(1 / sin(x))
//#define ctan(x)	(1 / tan(x))
//#define asin(x)	(atan(x / sqrt(-x * x + 1)))
//#define acos(x)	(atan(-x / sqrt(-x * x + 1)) + 2 * atan(1))
//#define asec(x)	(2 * atan(1) – atan(sign(x) / sqrt(x * x - 1)))
//#define acsc(x)	(atan(sign(x) / sqrt(x * x - 1)))
//#define acot(x)	(2 * atan(1) - atan(x))
//#define sinh(x)	((exp(x) - exp(-x)) / 2)
//#define cosh(x)	((exp(x) + exp(-x)) / 2)
//#define tanh(x)	((exp(x) - exp(-x)) / (exp(x) + exp(-x)))
//#define sech(x)	(2 / (exp(x) + exp(-x)))
//#define csch(x)	(2 / (exp(x) - exp(-x)))
//#define coth(x)	((exp(x) + exp(-x)) / (exp(x) - exp(-x)))
//#define asinh(x)	(log(x + sqrt(x * x + 1)))
#define acosh(x)	(log(x + sqrt(x * x - 1)))
//#define atanh(x)	(log((1 + x) / (1 - x)) / 2)
//#define asech(x)	(log((sqrt(-x * x + 1) + 1) / x))
//#define acsch(x)	(log((sign(x) * sqrt(x * x + 1) + 1) / x))
//#define acoth(x)	(log((x + 1) / (x - 1)) / 2)

#define fmax max
#define fmin min
#pragma warning (disable:4996)
//#define snprintf sprintf_s

#endif

// ======================================================================== //

typedef enum { no = 0, yes = 1 } flag;

typedef enum
{
	Float = 0,
	Fx32 = 1,
	Fx32Analysis = 2
} Precision;


#ifdef __cplusplus
extern "C" {
#endif
	
	
	typedef struct
	{
		Precision fixed_point;
		int default_exponent;
	} FixedPointParam;
	
	extern FixedPointParam fixed_point_param;
	
	
#ifdef __cplusplus
}
#endif


typedef union
{
	/** float  */
	float f;
	/** 16 bits  */
	short i16;
	/** 32 bits  */
	int i32;
} fx_real;

typedef struct
{
	/** vector size  */
	int size;
	/** vector elements  */
	fx_real *el;
	/** are the values fixed or float ?  */
	Precision fixed_point;
	/** if fixed point, v[i] = el[i] * (2 ** exponent)  */
	int exponent;
} fx_vector;


#ifdef __cplusplus
extern "C" {
#endif
	
	
	fx_vector fx_v_create(int size, int exponent = fixed_point_param.default_exponent);
	void fx_v_free(fx_vector *v);
	void fx_v_reset(fx_vector *v);
	typedef enum { no_test, full_32, test_limit } fx_mul_type;
    void fx_v_mul_v(const fx_vector *v1, const fx_vector *v2, fx_vector *v, fx_mul_type type, int limit);
	void fx_v_div_v(const fx_vector *v1, const fx_vector *v2, fx_vector *v);
	void fx_v_sub_v(const fx_vector *v1, const fx_vector *v2, fx_vector *v);
	void fx_v_sqrt(const fx_vector *v1, fx_vector *v);
	void fx_v_pow2(const fx_vector *v1, fx_vector *v);
	void fx_v_add_v_inplace(fx_vector *v, const fx_vector *v1);
	
	
#ifdef __cplusplus
}
#endif


#define cx_abs(x) sqrt(x.re.f * x.re.f + x.im.f * x.im.f)
#define cx_abs2(x) (x.re.f * x.re.f + x.im.f * x.im.f)
#define cx_angle(x) atan2(x.im.f, x.re.f)

typedef struct
{
	/** real part  */
	fx_real re;
	/** imaginary part  */
	fx_real im;
} fx_complex;

typedef struct
{
	/** vector size  */
	int size;
	/** vector elements  */
	fx_complex *el;
	/** are the values fixed or float ?  */
	Precision fixed_point;
	/** if fixed point, v[i] = el[i] * (2 ** exponent)  */
	int exponent;
} cx_vector;


#ifdef __cplusplus
extern "C" {
#endif
	
	fx_complex cx_mul_cx(fx_complex, fx_complex);
	fx_complex cx_div_cx(fx_complex, fx_complex);
	cx_vector cx_v_create(int size, int exponent = fixed_point_param.default_exponent);
	void cx_v_free(cx_vector*);
	void cx_v_reset(cx_vector*);
	void cx_v_mul_cx_v(cx_vector*, cx_vector*, cx_vector*);
	void cx_v_div_cx_v(cx_vector*, cx_vector*, cx_vector*);

#ifdef __cplusplus
}
#endif


#endif /* _FX_ALL_ */

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : fft_ooura.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

#ifndef _FFTOOURA_H_
#define _FFTOOURA_H_

#include <math.h>
#include <stdio.h>

//#include <kernel/purplevectors.h>
//#include <kernel/kernel.h>
//#include <kernel/fx_complex.h>
//#include <kernel/fx_vector.h>
//#include <kernel/fx_function.h>

#define MAX(x,y) ((x) > (y) ? (x) : (y))

/*
 void cdft(int, int, double *, int *, double *);
 void rdft(int, int, double *, int *, double *);
 void ddct(int, int, double *, int *, double *);
 void ddst(int, int, double *, int *, double *);
 void dfct(int, double *, double *, int *, double *);
 void dfst(int, double *, double *, int *, double *);
 */

void cdft(int, int, float *, int *, float *);
void rdft(int, int, float *, int *, float *);
void ddct(int, int, float *, int *, float *);
void ddst(int, int, float *, int *, float *);
void dfct(int, float *, float *, int *, float *);
void dfst(int, float *, float *, int *, float *);

/*
 void cdft(int, int, double *);
 void rdft(int, int, double *);
 void ddct(int, int, double *);
 void ddst(int, int, double *);
 void dfct(int, double *);
 void dfst(int, double *);
 */

/*
 void cdft(int, int, float *);
 void rdft(int, int, float *);
 void ddct(int, int, float *);
 void ddst(int, int, float *);
 void dfct(int, float *);
 void dfst(int, float *);
 */

typedef struct
{
	int n_fft;
	int order_fft;
	int dir;
	float *a;
	int *ip;
	float *w;
} FFT_R_Param_Ooura;

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : transient.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <kernel/purplevectors.h>
//#include <signal/fft.h>

//#include <purpleproc/fft_ooura.h>
//#include <purpleproc/fft_ipp.h>

//#include <geckosoft.h>

// SD TODO - why no ifdef here?
#if defined(USE_IPP)
#include <ipp.h>
#endif

#define FFT_OOURA

#ifndef _TRANSIENT_H_
#define _TRANSIENT_H_

enum TransientType
{
	kTransientType_EnergyBased			= 0,
	kTransientType_OnsetDetectionBased	= 1
};

typedef struct
{
	// parameters
	int fs;
	int first_sample;
	int first_sample_new;
	float last_gain;
	int   frameshiftint;
	float frameshift;
	float detector_attack_time;
	float detector_release_time;
	float gain_smooth_alpha;
	float gain_smooth_alpha_corrected;
	fx_vector is_release;
	float *taps_fast, *taps_slow;
	float attack_time;
	float attack_gain;
	float release_time;
	float release_gain;
	// Type
	int transient_type;
	// internal buffers
	float max_db;
	int signal_size;
	fx_vector signal;
	fx_vector signal_rect;
	fx_vector detection;
	fx_vector detection_db;
	fx_vector invdetection_db;
	float detection_db_min, detection_db_min_prev;
	float detection_db_max, detection_db_max_prev;
	fx_vector detection_lin;
	fx_vector detection_lin_smooth;
	fx_vector attack_db;
	fx_vector release_db;
	fx_vector refgain_db;
	fx_vector refgainahr_db;
	// New method Nov 2011
	fx_vector gaincurve_db;
	fx_vector gaincurvetarget_db;
	int onset_position_decount;
	//
	fx_vector smoothfactor_db;
	fx_vector gain_db;
	fx_vector gain_db_from_onset;
	fx_vector gain_db_full;
	fx_vector gain_smooth_db;
#if defined(USE_IPP)
	IppsIIRState_32f *ctx;
	int ippiir;
#endif
	// smoothing filter
	float f0;
	fx_vector b, a;
	float y1, x1;
	float g1;
	// detector / attack / release
	float detector_last;
	int detector_hold_countdown;
	float detector_attack_db_per_frame, detector_hold_frames, detector_release_db_per_frame;
	
	float attack_last;
	int attack_hold_countdown;
	float attack_attack_db_per_frame, attack_hold_frames, attack_release_db_per_frame;
	
	float refgainahr_last;
	int refgainahr_hold_countdown;
	float refgainahr_attack_db_per_frame, refgainahr_hold_frames, refgainahr_release_db_per_frame;
	
	float smoothfactor_last;
	int smoothfactor_hold_countdown;
	float smoothfactor_attack_db_per_frame, smoothfactor_hold_frames, smoothfactor_release_db_per_frame;
	
	float release_last;
	int release_hold_countdown;
	float release_attack_db_per_frame, release_hold_frames, release_release_db_per_frame;
	
	// Programmed Gain Curve to be applied when attack detected - New method Nov 2011
	float gaincurve_last;
	int gaincurve_hold_countdown;
	float gaincurve_attack_db_per_frame, gaincurve_hold_frames, gaincurve_release_db_per_frame;
	float onset_position;
	
	//
	float attack_gains;
	float release_gains;
	// other
	int NeedRecompute;
	int ActiveComputing;
	int ActiveCopying;
	
	//
	FILE *debug_output_file;
	
} TransientParam;

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : onsetsds.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

#ifndef _ONSETDS_H_
#define _ONSETDS_H_

//#include <kernel/purplevectors.h>
#include <stdio.h>
#include <string.h>
#if defined (__APPLE__)
#include <stdbool.h>
#endif
#include <math.h>

#ifdef __cplusplus
extern "C" {
#endif
	
	extern int ola;
	
	// SD TODO - get rid of some of these
	
#ifndef PI
#define PI 3.1415926535898f
#endif
#define MINUSPI -3.1415926535898f
#define TWOPI 6.28318530717952646f 
#define INV_TWOPI 0.1591549430919f
	
#define ods_abs(a)  ((a)<0? -(a) : (a))
#define ods_max(a,b) (((a) > (b)) ? (a) : (b))
#define ods_min(a,b) (((a) < (b)) ? (a) : (b))
	
#define ODS_LOG_LOWER_LIMIT 2e-42
#define ODS_LOGOF_LOG_LOWER_LIMIT -96.0154267
#define ODS_ABSINVOF_LOGOF_LOG_LOWER_LIMIT 0.010414993
	
	/**
	 * Types of whitening - may not all be implemented yet.
	 */
	enum onsetsds_wh_types {
		ODS_WH_NONE, ///< No whitening - onsetsds_whiten() becomes a no-op
		ODS_WH_ADAPT_MAX1, ///< Adaptive whitening - tracks recent-peak-magnitude in each bin, normalises that to 1
		ODS_WH_NORMMAX, ///< Simple normalisation - each frame is normalised (independent of others) so largest magnitude becomes 1. Not implemented.
		ODS_WH_NORMMEAN ///< Simple normalisation - each frame is normalised (independent of others) so mean magnitude becomes 1. Not implemented.
	};
	
	/**
	 * Types of onset detection function
	 */
	enum onsetsds_odf_types {
		ODS_ODF_MAG_NOFLUX_NODELTA = 0,
		ODS_ODF_POW_NOFLUX_NODELTA,
		ODS_ODF_LOGMAG_NOFLUX_NODELTA = 2,
		ODS_ODF_MAG_NOFLUX,
		ODS_ODF_POW_NOFLUX,
		ODS_ODF_LOGMAG_NOFLUX = 5,
		ODS_ODF_MAG,
		ODS_ODF_POW,
		ODS_ODF_LOGMAG,
		ODS_ODF_WLOGMAG,
		ODS_ODF_NWLOGMAG,
		ODS_ODF_MKLLOGMAG = 11,
		ODS_ODF_PHASE,
		ODS_ODF_WPHASE,
		ODS_ODF_NWPHASE,
		ODS_ODF_LOGWPHASE,
		ODS_ODF_NLOGWPHASE = 16, /// watch out...
		ODS_ODF_COMPLEX = 17,
		
		
		ODS_ODF_NCOMPLEX,
		ODS_ODF_LOGCOMPLEX,
		ODS_ODF_NLOGCOMPLEX,
		ODS_ODF_POLARCOMPLEX,
		ODS_ODF_NPOLARCOMPLEX,
		ODS_ODF_LOGPOLARCOMPLEX,
		ODS_ODF_NLOGPOLARCOMPLEX
		
		/*
		 ODS_ODF_MAGSUM,   ///< Sum of magnitudes
		 ODS_ODF_POWER,    ///< Power
		 ODS_ODF_HFCPOWER,	  ///< High-frequency content
		 ODS_ODF_L1MAGSF,		  ///< Half-wave rectified L1 Spectral Flux
		 ODS_ODF_L2MAGSF,		  ///< Half-wave rectified L1 Spectral Flux
		 ODS_ODF_L1POWSF,		  ///< Half-wave rectified L1 Spectral Flux
		 ODS_ODF_L2POWSF,		  ///< Half-wave rectified L1 Spectral Flux
		 ODS_ODF_COMPLEX,  ///< Complex-domain deviation
		 ODS_ODF_RCOMPLEX, ///< Complex-domain deviation, rectified (only increases counted)
		 ODS_ODF_PHASE,    ///< Phase deviation
		 ODS_ODF_WPHASE,   ///< Weighted phase deviation
		 ODS_ODF_NWPHASE,  ///< Normalized weighted phase deviation
		 ODS_ODF_MKL       ///< Modified Kullback-Leibler deviation
		 */
	};
	
	enum onsetsds_odf_perceptual {
		ODS_ODF_PERCEPTUAL_NONE,
		ODS_ODF_PERCEPTUAL_HFC,
		ODS_ODF_PERCEPTUAL_EQL
	};
	
	/*
	 enum onsetsds_odf_singleorvector {
	 ODS_ODF_SINGLEORVECTOR_SINGLE_NODELTA,
	 ODS_ODF_SINGLEORVECTOR_SINGLE,
	 ODS_ODF_SINGLEORVECTOR_VECTOR
	 };
	 */
	
	enum onsetsds_detector_types {
		ODS_DETECTOR_THRESHOLD,
		ODS_DETECTOR_DIXONPEAK
	};
	
	void sel_sort(float *array, int length);
	
	inline float onsetsds_phase_rewrap(float phase);
	
	/// The main data structure for the onset detection routine
	typedef struct {
		
		cx_vector input_fft;
		fx_vector input_fft_phase;
		fx_vector input_fft_mag;
		fx_vector input_fft_mag_prev;
		fx_vector input_psp;
		fx_vector input_odf;
		fx_vector input_other;
		fx_vector input_sortbuf;
		
		fx_vector perceptual_weight_none;
		fx_vector perceptual_weight_hfc;
		fx_vector perceptual_weight_eql;
		fx_vector logfreqscale_weight;
		
		// buffers to allow smoothing of reference values for ODF estimation
		fx_vector input_mag_prev_buffer;
		fx_vector *input_fft_mag_prev_buffer;
		fx_vector *input_fft_phase_prev_buffer;
		int input_fft_mag_prev_buffer_size;
		int input_fft_phase_prev_buffer_size;
		fx_vector amplitude_weight;
		
		float fs;
		int fft_order, fft_length, fft_hop, fft_halflength, fft_numbins;
		bool general_logmags;
		
		int hpon, lpon;
		float hpcutoff, lpcutoff;
		
		int whiten_whtype;
		float whiten_relaxtime, whiten_floor, whiten_relaxcoef;
		
		int odf_logfreqscale;
		int odf_logfreqscale_done;
		float odf_maxfreq;
		int odf_perceptual;
		//int odf_singleorvector;
		int odf_rectify;
		int odf_smoothref;
		int odf_lagfordeviation;
		int odf_type;
		float odf_odfparam, odf_normfactor;
		
		float detector_medspan_ms;
		float detector_win_ms;
		int detector_type;
		int detector_medspan;
		float detector_odfvalpost, detector_odfvalpostprev, detector_thresh;
		bool detector_detected, detector_med_odd;
		int detector_win, detector_winwin;
		int detector_bufferlength;
		
		float onset_db;
		
		int minbin, maxbin;
		float autogain;
		
		//
		float odf_raw;
		float odf_normalized;
		
		//
		int detector_candetect;
		float detector_candetect_min;
		
	} OnsetsdsParam;
	
#ifdef __cplusplus
}
#endif

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : eqcell.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <kernel/purplevectors.h>
//#include <signal/fft.h>

/*
 #include <kernel/fx_vector.h>
 #include <kernel/fx_complex.h>
 #include <signal/fft.h>
 #include <signal/filter.h>
 #include <analysis/analysis.h>
 #include <analysis/winweight.h>
 #include <kernel/fx_function.h>
 */

//#include <purpleproc/fft_ooura.h>

#if defined(USE_IPP)
#include <purpleproc/fft_ipp.h>
#endif

//#include <signal/filter.h>

#if defined(USE_ESELLERATE)
#include <geckosoft-ews.h>
#endif

#if defined(WIN32)
#include <support/diverse.h>		// for usleep
#endif

#define FFT_OOURA

#ifndef _EQCELL_H_
#define _EQCELL_H_

enum EQCellModel
{
	EQCellModel_RBJ = 0,
	EQCellModel_UZ,
};

enum EQCellType
{	
	EQCellType_HighPass = 0,
	EQCellType_LowShelf,
	EQCellType_BandPass,
	EQCellType_Notch,
	EQCellType_Peaking,
	EQCellType_HighShelf,
	EQCellType_LowPass,
	EQCellType_HighPass3,
	EQCellType_HighPass6,
	EQCellType_HighPass12,
	EQCellType_HighPass24,
	EQCellType_HighPass48,
	EQCellType_LowPass3,
	EQCellType_LowPass6,
	EQCellType_LowPass12,
	EQCellType_LowPass24,
	EQCellType_LowPass48,
	EQCellType_AllPass,
	EQCellType_LowPass3_AmplitudeCompl,
	EQCellType_LowPass6_AmplitudeCompl,	// AmplitudeCompl meaning 6dB cut at f0
	EQCellType_LowPass12_AmplitudeCompl,
	EQCellType_LowPass24_AmplitudeCompl,
	EQCellType_LowPass48_AmplitudeCompl,
	EQCellType_LowPassVaryslope,
	EQCellType_HighPassVaryslope,
};

/*
 enum EQCellType
 {	
 EQCellType_HighPass = 0,
 EQCellType_LowShelf,
 EQCellType_BandPass,
 EQCellType_Notch,
 EQCellType_Peaking,
 EQCellType_HighShelf,
 EQCellType_LowPass,
 EQCellType_HighPass12,
 EQCellType_HighPass24,
 EQCellType_HighPass48,
 EQCellType_LowPass12,
 EQCellType_LowPass24,
 EQCellType_LowPass48,
 EQCellType_AllPass,
 EQCellType_LowPass3,
 EQCellType_LowPass6,
 EQCellType_HighPass3,
 EQCellType_HighPass6,
 EQCellType_LowPass3_AmplitudeCompl,
 EQCellType_LowPass6_AmplitudeCompl,	// AmplitudeCompl meaning 6dB cut at f0
 EQCellType_LowPass12_AmplitudeCompl,
 EQCellType_LowPass24_AmplitudeCompl,
 EQCellType_LowPass48_AmplitudeCompl,
 EQCellType_LowPassVaryslope,
 EQCellType_HighPassVaryslope,
 };
 */

/*
 enum EQCellType
 {	
 EQCellType_HighPass = 0,
 EQCellType_LowShelf,
 EQCellType_BandPass,
 EQCellType_Notch,
 EQCellType_Peaking,
 EQCellType_HighShelf,
 EQCellType_LowPass,
 EQCellType_HighPass12,
 EQCellType_HighPass24,
 EQCellType_HighPass48,
 EQCellType_LowPass12,
 EQCellType_LowPass24,
 EQCellType_LowPass48,
 EQCellType_AllPass,
 EQCellType_LowPass6_AmplitudeCompl,	//  meaning 6dB cut at f0
 EQCellType_LowPass12_AmplitudeCompl,
 EQCellType_LowPass24_AmplitudeCompl,
 EQCellType_LowPass48_AmplitudeCompl,
 };
 */

/*enum EQCellType
 {	
 EQCellType_LowPass = 0,
 EQCellType_HighPass,
 EQCellType_BandPass,
 EQCellType_Notch,
 EQCellType_Peaking,
 EQCellType_LowShelf,
 EQCellType_HighShelf,
 EQCellType_AllPass,
 };*/

enum EQCellQdependency
{
	EQCellQdependency_1 = 0,
	EQCellQdependency_2,
	EQCellQdependency_3,
	EQCellQdependency_4,
};

typedef struct
{
	// parameters
	int fs;
	float dbgain, f0, q, qa, phase, bw, s;
	int model;
	int type;
	// qdependency
	int qdependency;
	float qdependency_2, qdependency_3, qdependency_4;
	float dbgainmax;
	// internal variables
	float a, w0, alpha;
	float omega, K, Q, V;
	// filter parameters
	unsigned int na, nb;
	double b0, b1, b2, a0, a1, a2;
	double *b_v, *a_v;
	FFT_R_Param_Ooura *fft_param_ooura;
#if defined(USE_IPP)
	FFT_R_Param_ipp *fft_param_ipp;
#endif
	unsigned int fft_order, fft_length;
	fx_vector a_vec, b_vec;
	fx_vector a_vec_base, b_vec_base;
	fx_vector b_vec_fir_c;
	fx_vector b_vec_fir;
	fx_vector temp_vec;
	// SD TODO
	// a_fft and b_fft should be commented too
	//		but kept here because if not, the code does not compile
	cx_vector a_fft, b_fft;
	cx_vector a_fft_log, b_fft_log;
	fx_vector temp_freqz_pow;
	cx_vector freqz_IIR;
	cx_vector freqz_IIR_log;
	fx_vector freqz_IIR_log_pow;
	fx_vector freqz_IIR_log_logpow;
	float *temptab, *temptable;
	int temptablebuilt;
	fx_vector freqz_IIR_pow, freqz_IIR_phase;
	fx_vector freqz_IIR_pow_base;
	cx_vector freqz_FIR, freqz_FIR_delay;
	fx_vector freqz_FIR_pow, freqz_FIR_phase, freqz_FIR_phase_delay;
	fx_vector freqz_FIR_pow_sqrt;
	// filter memory
	fx_vector zi;
	unsigned int zi_size;
	int NeedRecompute;
} EQCellParam;

typedef struct
{
	// parameters
	int fs;
	unsigned int n;
	int firmode;
	EQCellParam **eq_cells;
	int *active;
	// q dependency 
	int qdependency;
	// filter freqz
	unsigned int fft_order, fft_length;
	cx_vector freqz_cell;
	fx_vector freqz_cell_pow, freqz_cell_phase;
	// IIR
	// SD TODO
	// Four lines below should be commented
	// but kept here because if not, the code does not compile
	fx_vector a_vec_iir, b_vec_iir, temp_vec_iir;
	int a_vec_iir_size, b_vec_iir_size;
	cx_vector freqz_iir;
	fx_vector freqz_iir_pow, freqz_iir_phase;
	// FIR
	// SD TODO
	// 1 lines below should be commented
	// but kept here because if not, the code does not compile	
	fx_vector b_vec_fir;
	cx_vector freqz_fir;
	fx_vector freqz_fir_pow, freqz_fir_phase;
	cx_vector freqz_fir_temp;
	int NeedRecompute;
	int ActiveComputing;
	int ActiveCopying;
} EQFullParam;

typedef struct
{
	//
	int fft_length;
	int fft_order;
	// filter design
	EQFullParam *eqfull_param;
	// Spectral implementation of filtering
	// parameters
	float *input_winweight;
	float *input_winweight_rect;
	FFT_R_Param_Ooura *input_fft_param_ooura;
	FFT_R_Param_Ooura *input_ifft_param_ooura;
#if defined(USE_IPP)
	FFT_R_Param_ipp *input_fft_param_ipp;
	FFT_R_Param_ipp *input_ifft_param_ipp;
#endif
	// data
	int input_bufsize;
	int input_bufshift;
	int input_bufpos;
	float *input_buffer;
	int output_bufsize;
	int output_bufpos;
	int output_bufpos_copied;
	float *output_buffer;
	float *input_buffer_delay;
	fx_vector input_frame;
	fx_vector input_window;
	cx_vector input_fft;
	cx_vector output_fft;
	fx_vector output_window_c;
	fx_vector output_window;
	// additional params
	float gain;
	// Demo mode counter
	float process_runtime;
	float process_runtime_end;
	float process_runtime_total;
	float process_dropped;
	//
	float winweight_rescale;
} EQFullFilterParam;

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : a2m.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <kernel/purplevectors.h>

#ifndef _A2M_H_
#define _A2M_H_

enum NoteOnOffType
{	
	NoteOnOffType_None = 0,
	NoteOnOffType_On = 1,
	NoteOnOffType_Off = 2,
};

typedef struct
{
	float time;
	
	int	noteonoff;	// 0 , 1 on, 2 off
	float pitch;
	float energy;
	float timbre;
	float pan;
	
	int	noteonoff_set;
	int pitch_set;
	int energy_set;
	int timbre_set;
	int pan_set;
	
} AudioFeatures;

typedef struct
{
	float time;
	
	int	noteonoff;	// 0 , 1 on, 2 off
	int channel;
	int note;
	int velocity;	// maybe mapped to polyphonic key pressure?
	//
	int pan;		// used to adjust pan
	int pitchbend;	// used to adjust pitch
	int expression; // used to adjust amplitude 
	int timbre;		// used to adjust timbre (well, actually depends on synth modele
	// may be adjusted using key pressure, but not so simple, as synth reaction may vary
	// SD TODO - Solution is to allow use to assign some features to some specific cc manually
	int modwheel;
	
	int	noteonoff_set;
	int channel_set;
	int note_set;
	int velocity_set;
	int pan_set;
	int pitchbend_set;
	int expression_set;
	int timbre_set;	
	int modwheel_set;
	
} MIDIFeatures;

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : analyser.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <kernel/purplevectors.h>
//#include <signal/fft.h>

//#include <purpleproc/fft_ooura.h>
#if defined(USE_IPP)
//#include <purpleproc/fft_ipp.h>
#endif

#if defined(USE_ESELLERATE)
#include <geckosoft-ews.h>
#endif

#if defined(WIN32)
#include <support/diverse.h>		// for usleep
//#include <amp.h>					// for acosh
#endif

#define FFT_OOURA

#ifndef _ANALYSER_H_
#define _ANALYSER_H_

extern "C" {
	// From OOURA Bessel functions implementation
	double dbesi0(double x);
}

// SD NOTE - Ordered from narrower to wider main lobe
enum window_types {
	WINDOW_TYPE_RECTANGULAR = 0,
	WINDOW_TYPE_DOLPH_CHEBYSHEV_ALPHA_1,	// -20 dB
	WINDOW_TYPE_KAISER_ALPHA_2,				// PI*alpha = 2
	WINDOW_TYPE_KAISER_ALPHA_4,
	WINDOW_TYPE_DOLPH_CHEBYSHEV_ALPHA_2,	// -40 dB
	WINDOW_TYPE_HAMMING,
	WINDOW_TYPE_KAISER_ALPHA_6,
	WINDOW_TYPE_HANNING,
	WINDOW_TYPE_DOLPH_CHEBYSHEV_ALPHA_3,
	WINDOW_TYPE_KAISER_ALPHA_8,	
	WINDOW_TYPE_BLACKMAN		
};

typedef struct
{
	//
	int fs;
	int fft_length;
	int hop_length;
	int fft_order;
	int output_copied;
	int i_counter;
	int window_type;
	// SD 2012 Apr - 1 line commented
	// FFT_R_Param_Ooura *chebyshev_fft_param_ooura;
	cx_vector chebyshev_fft_in;
	cx_vector chebyshev_fft_out;
	// signal
	// SD 2012 Apr - 1 line commented
	// fx_vector in_signal, out_signal;
	// Spectral implementation of filtering
	// parameters
	float *input_winweight;
	float *input_winweight_rect;
	// SD 2012 Apr - 2 lines commented
	// FFT_R_Param *input_fft_param;
	// FFT_C_Param *input_ifft_param;
	FFT_R_Param_Ooura *input_fft_param_ooura;
	FFT_R_Param_Ooura *input_ifft_param_ooura;
#if defined(USE_IPP)
	FFT_R_Param_ipp *input_fft_param_ipp;
	FFT_R_Param_ipp *input_ifft_param_ipp;
#endif
	// data
	int input_bufsize;
	int input_bufshift;
	int input_bufpos;
	float *input_buffer;
	int output_bufsize;
	int output_bufpos;
	int output_bufpos_copied;
	float *output_buffer;
	float *input_buffer_delay;
	fx_vector input_frame;
	fx_vector input_window;
	cx_vector input_fft;
	cx_vector output_fft;
	fx_vector output_window_c;
	fx_vector output_window;
	// additional params
	float gain;
	// Demo mode counter
	float process_runtime;
	float process_runtime_end;
	float process_runtime_total;
	float process_dropped;
	//
	float winweight_rescale;
	//
	int wholeframe_length;
	int partframe_length;
} AnalyserParam;

#ifdef __cplusplus
extern "C" {
#endif
	
	AnalyserParam* new_analyser_param(int fs);
	void delete_analyser_param(AnalyserParam *param);
	
	void set_analyser_samplerate(AnalyserParam *param, int fs);
	void set_analyser_param(AnalyserParam *param, float gain);
	void set_analyser_param_window(AnalyserParam *param, int window_type = WINDOW_TYPE_HANNING);
	void something_analyser_param(AnalyserParam *param);
	int  get_analyser_delay(AnalyserParam *param);
	void get_analyser_framelength(AnalyserParam *param,int *wholeframe_length, int *partframe_length);
	void set_analyser_param_framelength(AnalyserParam *param, int wholeframe_length, int partframe_length);
	void reset_analyser(AnalyserParam *param);
	
	void reset_analyser_spectral(AnalyserParam *param);
	void analyser_process_spectral_improved_newblock(AnalyserParam *param, fx_vector *in_signal, fx_vector *out_signal, fx_vector *in_signal_delay);
	int  analyser_process_spectral_improved(AnalyserParam *param, fx_vector *in_signal, fx_vector *out_signal, fx_vector *in_signal_delay,
											fx_vector *out_partframe,
											fx_vector *out_wholeframe_rect,
											fx_vector *out_wholeframe_win,
										   cx_vector *out_wholeframe_fft,
										   int	 *eaten_samples);
	
#ifdef __cplusplus
}
#endif

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : detectors.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <purpleproc/eqcell.h>
//#include <purpleproc/transient.h>
//#include <purplea2m/a2m.h>
//#include <purpleproc/onsetsds.h>

//#define FFT_OOURA

#ifndef _DETECTORS_H_
#define _DETECTORS_H_

enum DetectorAlgo
{
	DetectorAlgo_Legacy = 0,
	DetectorAlgo_Onsetsds
};

enum DetectorMode
{
	DetectorMode_Percussive = 0,
	DetectorMode_Sustained,
	DetectorMode_AutoRoll
};

typedef struct
{
	// algo implementation
	int detector_algo;
	// parameters
	int fs;	
	int framerate;
	int hpon;
	int lpon;
	float hpcutoff;		// 20 - 2000 Hz
	float lpcutoff;		// 200 - 20000 Hz	
	float threshold;	// 0 - -48dB
	float sensitivity;	// 3 - 18 dB (high to low)
	float guard;		// 10 - 100ms
	//
	float guardtimeout;
	float guardtimeoutlag;
	//
	float percussivetimeout;
	float percussivetimeoutlag;
	//
	int detection_curves_position;
	int detection_curves_ratio_pos;
	float detection_db_value;
	float attack_db_value;
	int detected;
	//
	float detectedtimeoutms;
	float detectedtimeout;
	float detectedtimeoutlag;	
	// blocks
	EQCellParam* hp_eqcell;
	EQCellParam* lp_eqcell;
	fx_vector filtered_signal_tmp_hp;
	fx_vector filtered_signal_tmp;
	fx_vector detection_db;
	fx_vector attack_db;
	float onset_db;
	fx_vector detection_signal;
	TransientParam* transient;
	OnsetsdsParam*	onsetsds;
	// memory for display
	fx_vector detection_t_curve;
	fx_vector detection_db_curve;
	fx_vector attack_db_curve;
	fx_vector detection_detected_curve;
	float	  detection_curves_duration;
	int detection_curves_ratio;
	//
	int isnoteon;
	int detector_mode;
} OnsetDetectorParam;

#ifdef __cplusplus
extern "C" {
#endif
	
	OnsetDetectorParam* new_onsetdetector_param(int fs);
	void delete_onsetdetector_param(OnsetDetectorParam *param);
	
	void set_onsetdetector_samplerate(OnsetDetectorParam *param, int fs);
	void set_onsetdetector_param(OnsetDetectorParam *param, float framerate, int hpon, int lpon, float hpcutoff, float lpcutoff,
								 float threshold, float guard, int compute=1);
	void set_onsetdetector_param_framelength(OnsetDetectorParam *param, int wholeframe_length, int partframe_length);
	void set_onsetdetector_param_threshold(OnsetDetectorParam *param, float threshold);
	void set_onsetdetector_param_sensitivity(OnsetDetectorParam *param, float sensitivity);
	void set_onsetdetector_param_algo(OnsetDetectorParam *param, int detector_algo);
	void set_onsetdetector_param_onsetsds(OnsetDetectorParam *param,
										  int whiten_whtype,
										  float odf_maxfreq,
										  int odf_logfreqscale,
										  int odf_perceptual,
										  //int odf_singleorvector,
										  int odf_rectify, int odf_smoothref, int odf_lagfordeviation,
										  int odf_type, int detector_type);
	
	void set_onsetdetector_mode(OnsetDetectorParam *param, int detector_mode);
	void set_onsetdetector_curves_duration(OnsetDetectorParam *param, float detection_curves_duration);	
	void something_onsetdetector_param(OnsetDetectorParam *param);
	float onsetdetector_get_delay(OnsetDetectorParam *param);
	void get_detection_curves(OnsetDetectorParam *param,
							  fx_vector *detection_t_curve, fx_vector *detection_db_curve, fx_vector *attack_db_curve,
							  fx_vector *detection_detected_curve,
							  int *detection_curves_position);
	void get_onset_function(OnsetDetectorParam *param, float *onset_function);
	void reset_onsetdetector(OnsetDetectorParam *param);
	void get_curves_ratio(OnsetDetectorParam *param, float *curves_ratio);
	int process_onsetdetector(OnsetDetectorParam *param,  fx_vector *in_signal, cx_vector *in_signal_fft, AudioFeatures *out_signal);
	
#ifdef __cplusplus
}
#endif

#endif

//-------------------------------------------------------------------------------------------------------
// PURPLE Plug-Ins SDK
//
// Filename     : analyser_pitch.h
//
// © 2007-2013, Stéphane Dupont, University of Mons, All Rights Reserved
//-------------------------------------------------------------------------------------------------------

//#include <kernel/purplevectors.h>
//#include <signal/fft.h>

//#include <purplea2m/a2m.h>

// Aubio Pitch extraction
#if defined(USE_GPL_AUBIO)
#include <aubio.h>
//#include <aubioext.h>
#endif

// Sphinx Pitch extraction - Yin only
#include <sphinxbase/yin.h>

#if defined(USE_ESELLERATE)
#include <geckosoft-ews.h>
#endif

#ifndef _ANALYSER_PITCH_H_
#define _ANALYSER_PITCH_H_

enum AnalyserPitchAlgo
{
	AnalyserPitchAlgo_Aubio_Mcomb = 0,
	AnalyserPitchAlgo_Aubio_Fcomb,
	AnalyserPitchAlgo_Aubio_Yin,
	AnalyserPitchAlgo_Aubio_YinFFT,
	AnalyserPitchAlgo_Aubio_Schmitt,
	AnalyserPitchAlgo_Sphinx_Yin
};

typedef struct
{
	int fs;
	int window_size;
	int overlap_size;
	
	int analyser_pitch_algo;
	
	float pitch;
	
	// AUBIO
#if defined(USE_GPL_AUBIO)
	fvec_t * ibuf;
	/* pitch objects */
	aubio_pitchdetection_t * pitchdet_aubio;
	aubio_pitchdetection_type type_pitch;
	aubio_pitchdetection_mode mode_pitch;
#endif
	
	// SPHINX
	int sphinxflag;
	short * sphinxbuf;
	unsigned short period;
	unsigned short bestdiff;
	yin_t * pitchdet_sphinx;
	
	// GENERAL PARAMETERS
	float yin_threshold;
	float pitch_min;
	float pitch_max;
	
} AnalyserPitchParam;

#ifdef __cplusplus
extern "C" {
#endif
	
	// PUBLIC
	
	AnalyserPitchParam* new_analyser_pitch_param(int fs, int analyser_pitch_algo);
	void delete_analyser_pitch_param(AnalyserPitchParam *param);
	
	void set_analyser_pitch_samplerate(AnalyserPitchParam *param, int fs);
	void set_analyser_pitch_algo(AnalyserPitchParam *param, int analyser_pitch_algo);
	void set_analyser_pitch_param_framelength(AnalyserPitchParam *param, int wholeframe_length, int partframe_length);
	void set_analyser_pitch_param_pitchrange(AnalyserPitchParam *param, float pitch_min, float pitch_max);
	void set_analyser_pitch_param_yinthreshold(AnalyserPitchParam *param, float yin_threshold);
	
	float analyser_pitch_get_delay(AnalyserPitchParam *param);
	
	int process_analyser_pitch(AnalyserPitchParam *param, fx_vector *in_signal, cx_vector *in_signal_fft, AudioFeatures *out_signal, int compute=1);
	
	// PRIVATE
	
#if defined(USE_GPL_AUBIO)
	void set_analyser_pitch_init_aubio(AnalyserPitchParam *param);
#endif
	void set_analyser_pitch_init_sphinx(AnalyserPitchParam *param);
	
	void something_analyser_pitch_param(AnalyserPitchParam *param);
	void reset_analyser_pitch(AnalyserPitchParam *param);	
	
#ifdef __cplusplus
}
#endif

#endif
