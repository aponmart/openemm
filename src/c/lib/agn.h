/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/
/** @file agn.h
 * Header file for the Agnitas C Library
 * All usable datatypes and functions are defined or declared
 * here. The definition of the functions can be found in each
 * separate file
 */
# ifndef	__LIB_AGN_H
# define	__LIB_AGN_H		1
# include	<stdio.h>
# include	<stdarg.h>
# include	<string.h>
# include	<unistd.h>
# include	<sys/types.h>
# include	<signal.h>
# include	<time.h>
# include	<limits.h>
# ifdef		linux
# include	<paths.h>
# else		/* linux */
# define	_PATH_DEVNULL		"/dev/null"
# define	_PATH_VARRUN		"/var/run/"
# define	_PATH_VARTMP		"/var/tmp/"
# endif		/* linux */

/*{{{	some predefined loglevels */
/** @def LV_NONE
 * Loglevel for undefined behaviour */
/** @def LV_FATAL
 * Loglevel for fatal error messages */
/** @def LV_ERROR
 * Loglevel for error messages */
/** @def LV_WARNING
 * Loglevel for warnings */
/** @def LV_NOTICE
 * Loglevel for for important notices, which are no warning or error */
/** @def LV_INFO
 * Loglevel for informal messages */
/** @def LV_VERBOSE
 * Loglevel for more verbose informations */
/** @def LV_DEBUG
 * Loglevel for debug output */
# define	LV_NONE			0
# define	LV_FATAL		1
# define	LV_ERROR		2
# define	LV_WARNING		3
# define	LV_NOTICE		4
# define	LV_INFO			5
# define	LV_VERBOSE		6
# define	LV_DEBUG		7
/*}}}*/
/*{{{	logmask handling */
/** @def LM_NONE
 * The empty log mask */
/** @def LM_BIT(bbb)
 * Sets the bitmask for a given loglevel */
/** @def LM_MATCH(mmm,ppp)
 * Checks wether a given loglevel mask matches a used loglevel mask */
# define	LM_NONE			((logmask_t) 0)
# define	LM_BIT(bbb)		((logmask_t) (1 << (bbb)))
# define	LM_MATCH(mmm,ppp)	(((! (mmm)) || ((ppp) & (mmm))) ? true : false)
/*}}}*/
/*{{{	logsuspend masks */
/** @def LS_LOGFILE
 * Mask to suspend output to logfile */
/** @def LS_FILEDESC
 * Mask to suspend output to file */
/** @def LS_SYSLOG
 * Mask to suspend output to syslog */
/** @def LS_COLLECT
 * Mask to suspend output to collection */
# define	LS_LOGFILE		(1 << 0)
# define	LS_FILEDESC		(1 << 1)
# define	LS_SYSLOG		(1 << 2)
# define	LS_COLLECT		(1 << 3)
/*}}}*/

/**
 * Represents an unsigned 8 bit value
 */
typedef unsigned char	byte_t;
/**
 * Unsigned number to hold a 32 bit hash value
 */
typedef unsigned long	hash_t;
/**
 * Symbolic names for boolean values
 */
typedef enum { /*{{{*/
	false,
	true
	/*}}}*/
}	bool_t;

/**
 * Keeps track of a dynamic growing/shrinking buffer
 */
typedef struct { /*{{{*/
	long	length;		/**< used length of buffer			*/
	long	size;		/**< allocated size of buffer			*/
	byte_t	*buffer;	/**< the buffer itself				*/
	long	spare;		/**< alloc this in bytes as spare memory	*/
	/*}}}*/
}	buffer_t;

/**
 * A linked list of variable/value pairs
 */
typedef struct var { /*{{{*/
	char	*var;		/**< The name of the variable			*/
	char	*val;		/**< Its value					*/
	struct var
		*next;		/**< Next element in list or NULL		*/
	/*}}}*/
}	var_t;

/**
 * A node in the hashmap
 */
typedef struct node { /*{{{*/
	char	*mkey;		/**< the key of this node			*/
	hash_t	hash;		/**< its hash value				*/
	char	*okey;		/**< original key of this node			*/
	char	*data;		/**< its value					*/
	struct node
		*next;		/**< sibling in same hash tree			*/
	/*}}}*/
}	node_t;

/**
 * Mapping a ka hash collection
 */
typedef struct { /*{{{*/
	bool_t	icase;		/**< ignore case on keys			*/
	int	hsize;		/**< size of the hashing array			*/
	node_t	**cont;		/**< the hashing array of nodes			*/
	/*}}}*/
}	map_t;

/**
 * Keeps track of signal handling
 */
typedef struct { /*{{{*/
	void		*base;		/**< all signals			*/
	sigset_t	mask;		/**< the mask for all signals		*/
	sigset_t	oldmask;	/**< for blocking			*/
	bool_t		isblocked;	/**< is currently blocked?		*/
	/*}}}*/
}	csig_t;

/**
 * Bitmask for handling logging masks
 */
typedef unsigned long	logmask_t;
/**
 * All informations required for logging
 */
typedef struct { /*{{{*/
	int		logfd;		/**< file desc. to copy output to	*/
	int		slprio;		/**< syslog priority			*/
	char		*logpath;	/**< path to logdirectory		*/
	char		hostname[64];	/**< the local hostname			*/
	char		*program;	/**< name of program			*/
	char		fname[PATH_MAX];	/**< filename to write to	*/
	int		level;		/**< current loglevel			*/
	logmask_t	use;		/**< the current used logmask		*/
	time_t		last;		/**< last time we wrote something	*/
	long		lastday;	/**< dito for the day			*/
	int		diff;		/**< TZ drift				*/
	FILE		*lfp;		/**< filepointer to output file		*/
	void		*idc;		/**< ID chain				*/
	bool_t		slactive;	/**< syslog is active			*/
	buffer_t	*obuf;		/**< output buffer			*/
	buffer_t	*collect;	/**< to collect all messages		*/
	
	void		*suspend;	/**< suspend list for logging		*/
	/*}}}*/
}	log_t;
extern buffer_t		*buffer_alloc (int nsize);
extern buffer_t		*buffer_free (buffer_t *b);
extern bool_t		buffer_size (buffer_t *b, int nsize);
extern bool_t		buffer_set (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_setb (buffer_t *b, byte_t data);
extern bool_t		buffer_setsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_sets (buffer_t *b, const char *str);
extern bool_t		buffer_setch (buffer_t *b, char ch);
extern bool_t		buffer_append (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_appendbuf (buffer_t *b, buffer_t *data);
extern bool_t		buffer_appendb (buffer_t *b, byte_t data);
extern bool_t		buffer_appendsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_appends (buffer_t *b, const char *str);
extern bool_t		buffer_appendch (buffer_t *b, char ch);
extern bool_t		buffer_appendnl (buffer_t *b);
extern bool_t		buffer_appendcrlf (buffer_t *b);
extern bool_t		buffer_stiff (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_stiffb (buffer_t *b, byte_t data);
extern bool_t		buffer_stiffsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_stiffs (buffer_t *b, const char *str);
extern bool_t		buffer_stiffch (buffer_t *b, char ch);
extern bool_t		buffer_stiffnl (buffer_t *b);
extern bool_t		buffer_stiffcrlf (buffer_t *b);
extern bool_t		buffer_vformat (buffer_t *b, const char *fmt, va_list par) __attribute__ ((format (printf, 2, 0)));
extern bool_t		buffer_format (buffer_t *b, const char *fmt, ...) __attribute__ ((format (printf, 2, 3)));
extern bool_t		buffer_strftime (buffer_t *b, const char *fmt, const struct tm *tt);
extern byte_t		*buffer_cut (buffer_t *b, long start, long length, long *rlength);
extern const char	*buffer_string (buffer_t *b);
extern int		buffer_iseol (const buffer_t *b, int pos);

extern var_t		*var_alloc (const char *var, const char *val);
extern var_t		*var_free (var_t *v);
extern var_t		*var_free_all (var_t *v);
extern bool_t		var_variable (var_t *v, const char *var);
extern bool_t		var_value (var_t *v, const char *val);
extern bool_t		var_match (var_t *v, const char *var);
extern bool_t		var_imatch (var_t *v, const char *var);
extern bool_t		var_partial_match (var_t *v, const char *var);
extern bool_t		var_partial_imatch (var_t *v, const char *var);

extern node_t		*node_alloc (const char *mkey, hash_t hash,
				     const char *okey, const char *data);
extern node_t		*node_free (node_t *n);
extern node_t		*node_free_all (node_t *n);
extern bool_t		node_setdata (node_t *n, const char *data);

extern map_t		*map_alloc (bool_t icase, int aproxsize);
extern map_t		*map_free (map_t *m);
extern bool_t		map_add (map_t *m, const char *key, const char *data);
extern bool_t		map_delete_node (map_t *m, node_t *n);
extern bool_t		map_delete (map_t *m, const char *key);
extern node_t		*map_find (map_t *m, const char *key);

extern csig_t		*csig_alloc (int signr, ...);
extern csig_t		*csig_free (csig_t *c);
extern void		csig_block (csig_t *c);
extern void		csig_unblock (csig_t *c);

extern const char	*log_level_name (int lvl);
extern log_t		*log_alloc (const char *logpath, const char *program, const char *levelname);
extern log_t		*log_free (log_t *l);
extern bool_t		log_level_set (log_t *l, const char *levelname);
extern void		log_set_mask (log_t *l, logmask_t mask);
extern void		log_clr_mask (log_t *l);
extern void		log_add_mask (log_t *l, logmask_t mask);
extern void		log_del_mask (log_t *l, logmask_t mask);
extern bool_t		log_path_set (log_t *l, const char *logpath);
extern bool_t		log_path_default (log_t *l);
extern void		log_tofd (log_t *l, int fd);
extern void		log_nofd (log_t *l);
extern void		log_tosyslog (log_t *l, const char *ident, int option, int facility, int priority);
extern void		log_nosyslog (log_t *l);
extern bool_t		log_collect (log_t *l);
extern void		log_uncollect (log_t *l);
extern bool_t		log_idset (log_t *l, const char *what);
extern void		log_idclr (log_t *l);
extern bool_t		log_idpush (log_t *l, const char *what, const char *separator);
extern void		log_idpop (log_t *l);
extern void		log_suspend_pop (log_t *l);
extern void		log_suspend_push (log_t *l, unsigned long mask, bool_t set);
extern bool_t		log_suspend (log_t *l, unsigned long what);
extern bool_t		log_vmout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, va_list par) __attribute__ ((format (printf, 5, 0)));
extern bool_t		log_mout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, ...) __attribute__ ((format (printf, 5, 6)));
extern bool_t		log_vidout (log_t *l, int level, logmask_t mask, const char *fmt, va_list par) __attribute__ ((format (printf, 4, 0)));
extern bool_t		log_idout (log_t *l, int level, logmask_t mask, const char *fmt, ...) __attribute__ ((format (printf, 4, 5)));
extern bool_t		log_slvout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, va_list par) __attribute__ ((format (printf, 6, 0)));
extern bool_t		log_slout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, ...) __attribute__ ((format (printf, 6, 7)));
extern bool_t		log_vout (log_t *l, int level, const char *fmt, va_list par) __attribute__ ((format (printf, 3, 0)));
extern bool_t		log_out (log_t *l, int level, const char *fmt, ...) __attribute__ ((format (printf, 3, 4)));
extern int		tzdiff (time_t tim);
extern bool_t		atob (const char *str);
extern bool_t		struse (char **buf, const char *str);
extern char		*get_fqdn (const char *name);
extern char		*get_local_fqdn (void);
extern char		*skip (char *str);

# ifdef		__OPTIMIZE__
static inline bool_t
__buffer_stiff (buffer_t *b, const byte_t *data, int dlen) /*{{{*/
{
	if (b -> length + dlen < b -> size) {
		switch (dlen) {
		default:
			memcpy (b -> buffer + b -> length, data, dlen);
			b -> length += dlen;
			break;
		case 32:
			b -> buffer[b -> length++] = *data++;
		case 31:
			b -> buffer[b -> length++] = *data++;
		case 30:
			b -> buffer[b -> length++] = *data++;
		case 29:
			b -> buffer[b -> length++] = *data++;
		case 28:
			b -> buffer[b -> length++] = *data++;
		case 27:
			b -> buffer[b -> length++] = *data++;
		case 26:
			b -> buffer[b -> length++] = *data++;
		case 25:
			b -> buffer[b -> length++] = *data++;
		case 24:
			b -> buffer[b -> length++] = *data++;
		case 23:
			b -> buffer[b -> length++] = *data++;
		case 22:
			b -> buffer[b -> length++] = *data++;
		case 21:
			b -> buffer[b -> length++] = *data++;
		case 20:
			b -> buffer[b -> length++] = *data++;
		case 19:
			b -> buffer[b -> length++] = *data++;
		case 18:
			b -> buffer[b -> length++] = *data++;
		case 17:
			b -> buffer[b -> length++] = *data++;
		case 16:
			b -> buffer[b -> length++] = *data++;
		case 15:
			b -> buffer[b -> length++] = *data++;
		case 14:
			b -> buffer[b -> length++] = *data++;
		case 13:
			b -> buffer[b -> length++] = *data++;
		case 12:
			b -> buffer[b -> length++] = *data++;
		case 11:
			b -> buffer[b -> length++] = *data++;
		case 10:
			b -> buffer[b -> length++] = *data++;
		case 9:
			b -> buffer[b -> length++] = *data++;
		case 8:
			b -> buffer[b -> length++] = *data++;
		case 7:
			b -> buffer[b -> length++] = *data++;
		case 6:
			b -> buffer[b -> length++] = *data++;
		case 5:
			b -> buffer[b -> length++] = *data++;
		case 4:
			b -> buffer[b -> length++] = *data++;
		case 3:
			b -> buffer[b -> length++] = *data++;
		case 2:
			b -> buffer[b -> length++] = *data++;
		case 1:
			b -> buffer[b -> length++] = *data++;
		case 0:
			break;
		}
		return true;
	} else
		return buffer_stiff (b, data, dlen);
}/*}}}*/
static inline bool_t
__buffer_stiffb (buffer_t *b, byte_t data) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = data;
		return true;
	} else
		return buffer_stiffb (b, data);
}/*}}}*/
static inline bool_t
__buffer_stiffch (buffer_t *b, char ch) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = ch;
		return true;
	} else
		return buffer_stiffch (b, ch);
}/*}}}*/
static inline bool_t
__buffer_stiffnl (buffer_t *b) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = '\n';
		return true;
	} else
		return buffer_stiffch (b, '\n');
}/*}}}*/
static inline bool_t
__buffer_stiffcrlf (buffer_t *b) /*{{{*/
{
	if (b -> length + 2 < b -> size) {
		b -> buffer[b -> length++] = '\r';
		b -> buffer[b -> length++] = '\n';
		return true;
	} else
		return buffer_stiffcrlf (b);
}/*}}}*/
static inline int
__buffer_iseol (const buffer_t *b, int pos) /*{{{*/
{
	if (pos < b -> length) {
		if (b -> buffer[pos] == '\n')
			return 1;
		if ((b -> buffer[pos] == '\r') && (pos + 1 < b -> length) && (b -> buffer[pos + 1] == '\n'))
			return 2;
	}
	return 0;
}/*}}}*/
# define	buffer_stiff		__buffer_stiff
# define	buffer_stiffb		__buffer_stiffb
# define	buffer_stiffch		__buffer_stiffch
# define	buffer_stiffnl		__buffer_stiffnl
# define	buffer_stiffcrlf	__buffer_stiffcrlf
# define	buffer_iseol		__buffer_iseol
# endif		/* __OPTIMIZE__ */
# endif		/* __LIB_AGN_H */