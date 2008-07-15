#!/usr/bin/env python
#	-*- python -*-
"""**********************************************************************************
*  The contents of this file are subject to the OpenEMM Public License Version 1.1
*  ("License"); You may not use this file except in compliance with the License.
*  You may obtain a copy of the License at http://www.agnitas.org/openemm.
*  Software distributed under the License is distributed on an "AS IS" basis,
*  WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
*  the specific language governing rights and limitations under the License.
* 
*  The Original Code is OpenEMM.
*  The Initial Developer of the Original Code is AGNITAS AG. Portions created by
*  AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
* 
*  All copies of the Covered Code must include on each user interface screen,
*  visible to all users at all times
*     (a) the OpenEMM logo in the upper left corner and
*     (b) the OpenEMM copyright notice at the very bottom center
*  See full license, exhibit B for requirements.
**********************************************************************************


"""
#
import	sys, os, getopt, time, signal, sre
import	agn
agn.require ('1.3.6')
agn.loglevel = agn.LV_INFO
#
delay = 30
#
class Update:
	def __init__ (self, path, name):
		self.path = path
		self.name = name
		self.base = os.path.basename (self.path)
		if self.path.find (os.sep) != -1:
			d = os.path.dirname (self.path)
		else:
			d = None
		n = self.base.rfind ('.')
		if n != -1:
			b = self.base[:n] + '.fail'
		else:
			b = self.base + '.fail'
		if d is None:
			self.fail = b
		else:
			self.fail = d + os.sep + b
		self.cur = 1
		self.pid = os.getpid ()
		self.opts = {}
	
	def options (self, nopts):
		for o in nopts:
			self.opts[o[0]] = o[1]
	
	def exists (self):
		return os.access (self.path, os.F_OK)

	def renameToTemp (self):
		tfname = self.path + '.%d.%d.%d' % (self.pid, time.time (), self.cur)
		self.cur += 1
		try:
			os.rename (self.path, tfname)
			agn.log (agn.LV_INFO, 'update', 'Renamed %s to %s' % (self.path, tfname))
		except OSError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to rename %s to %s: %s' % (self.path, tfname, `e.args`))
			tfname = None
		return tfname
	
	def __save (self, fname, line):
		rc = False
		try:
			fd = open (fname, 'a')
			fd.write (line + '\n')
			fd.close ()
			rc = True
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Failed to write to %s: %s' % (fname, `e.args`))
		return rc
	
	def saveToFail (self, line):
		return self.__save (self.fail, line)
	
	def __logfilename (self):
		now = time.localtime (time.time ())
		return agn.logpath + os.sep + '%04d%02d%02d-%s' % (now[0], now[1], now[2], self.base)
		
	def saveToLog (self, line):
		return self.__save (self.__logfilename (), line)

	def __removeFile (self, tfname):
		try:
			os.unlink (tfname)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to remove tempfile %s: %s' % (tfname, `e.args`))
		
	def moveToLog (self, tfname):
		dest = self.__logfilename ()
		try:
			rc = False
			fdi = open (tfname, 'r')
			try:
				fdo = open (dest, 'a')
				while 1:
					buf = fdi.read (65536)
					if len (buf) > 0:
						fdo.write (buf)
					else:
						break
				fdo.close ()
			except IOError, e:
				agn.log (agn.LV_ERROR, 'update', 'Unable to open output file %s: %s' % (tfname, `e.args`))
			fdi.close ()
			if rc:
				self.__removeFile (tfname)
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to open input file %s: %s' % (tfname, `e.args`))

	def updateStart (self, inst):
		raise agn.error ('Need to overwrite updateStart in your subclass')
	def updateEnd (self, inst):
		raise agn.error ('Need to overwrite updateEnd in your subclass')
	def updateLine (self, inst, line):
		raise agn.error ('Need to overwrite updateLine in your subclass')

	def update (self, inst):
		tfname = self.renameToTemp ()
		if tfname is None:
			return False
		try:
			fd = open (tfname, 'r')
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to open %s: %s' % (tfname, `e.args`))
			fd = None
		if fd is None:
			return False
		self.lineno = 0
		removeTemp = True
		rc = self.updateStart (inst)

		for line in [agn.chop (l) for l in fd.readlines ()]:
			self.lineno += 1
			if not self.updateLine (inst, line):
				if not self.saveToFail (line):
					removeTemp = False
				rc = False
			else:
				if not self.saveToLog (line):
					removeTemp = False
		if not self.updateEnd (inst):
			rc = False
		if removeTemp:
			self.__removeFile (tfname)
		return rc

class UpdateInfo:
	def __init__ (self, info):
		self.info = info
		self.map = {}
		for elem in info.split ('\t'):
			parts = elem.split ('=', 1)
			if len (parts) == 2:
				self.map[parts[0]] = parts[1]
			elif len (parts) == 1:
				self.map['stat'] = elem
	
	def __getitem__ (self, var):
		if self.map.has_key (var):
			return self.map[var]
		return None
	
	def get (self, var, dflt):
		if self.map.has_key (var):
			return self.map[var]
		return dflt

class UpdateBounce (Update):

	bouncelog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'extbounce.log'

	def __init__ (self, path = bouncelog):
		Update.__init__ (self, path, 'bounce')
		self.company_map = {}
		self.dsnparse = sre.compile ('^([0-9])\\.([0-9])\\.([0-9])$')
	
	def __todetail (self, dsn, info):
		(detail, code, type, remark) = (0, 0, 2, 'bounce')
		match = self.dsnparse.match (dsn)
		if not match is None:
			grp = match.groups ()
			code = int (grp[0]) * 100 + int (grp[1]) * 10 + int (grp[2])
			infos = UpdateInfo (info)
			stat = infos.get ('stat', '').lower ()
			mailloop = infos['mailloop']
			
			#
			# special cases
			if infos.get ('relay', '').find ('yahoo.com') != -1:
				if code / 100 == 5 and stat.find ('service unavailable') != -1:
					detail = 511
			admin = infos['admin']
			if not admin is None:
				type = 4
				remark = admin
			
			if detail == 0:
				if (code in (511, 571) and (stat.find ('user unknown') != -1 or not mailloop is None)) or code == 513:
					detail = 511
				elif code == 512:
					detail = 512
				elif code in (420, 421, 422, 521, 522):
					detail = 420
				elif code in (430, 530, 535):
					detail = 430
				elif code / 100 == 4:
					detail = 400
				elif code in (500, 511, 550, 554):
					detail = 500
				elif code / 100 == 5:
					detail = 510
				else:
					agn.log (agn.LV_WARNING, 'updBounce', '%s resulting in %d does not match any rule' % (dsn, code))
		return (detail, code, type, remark)

	def __companyMap (self, inst, mailing):
		if not self.company_map.has_key (mailing):
			rec = inst.simpleQuery ('SELECT company_id FROM mailing_tbl WHERE mailing_id = %d' % mailing)
			if rec is None:
				agn.log (agn.LV_ERROR, 'updBounce', 'No company_id for mailing %d found' % mailing)
				return 0
			else:
				self.company_map[mailing] = rec[0]
				return rec[0]
		else:
			return self.company_map[mailing]

	def updateStart (self, inst):
		self.sbcount = 0
		self.hbcount = 0
		return True
	
	def updateEnd (self, inst):
		agn.log (agn.LV_INFO, 'udpBounce', 'Found %d hardbounces, %d softbounces in %d lines' % (self.hbcount, self.sbcount, self.lineno))
		return True
		
	def updateLine (self, inst, line):
		parts = line.split (';', 5)
		if len (parts) != 6:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got invalid line: ' + line)
			return False
		(dsn, licence, mailing, media, customer, info) = (parts[0], int (parts[1]), int (parts[2]), int (parts[3]), int (parts[4]), parts[5])
		if mailing <= 0 or customer <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got line with invalid mailing or customer: ' + line)
			return False
		(detail, dsnnr, bouncetype, bounceremark) = self.__todetail (dsn, info)
		if detail <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got line with invalid detail (%d): %s' % (detail, line))
			return False
		company = self.__companyMap (inst, mailing)
		if company <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Cannot map mailing %d to company for line: %s' % (mailing, line))
			return False
		map = { 'company': company,
			'customer': customer,
			'detail': detail,
			'mailing': mailing,
			'dsn': dsnnr
		}
		rc = True
		try:

			inst.update ("INSERT INTO bounce_tbl (company_id, customer_id, detail, mailing_id, dsn, change_date) VALUES (:company, :customer, :detail, :mailing, :dsn, now())", map)
		except agn.error, e:
			agn.log (agn.LV_ERROR, 'updBounce', 'Unable to add %s to database: %s' % (`map`, e.msg))
			rc = False
		if detail in (510, 511, 512):
			self.hbcount += 1
			map = { 'status': bouncetype,
				'remark': bounceremark,
				'mailing': mailing,
				'customer': customer,
				'media': media
			}
			try:

				inst.update ('UPDATE customer_%d_binding_tbl SET user_status = :status, change_date = now(), user_remark = :remark, exit_mailing_id = :mailing WHERE customer_id = :customer AND user_status = 1 AND mediatype = :media' % company, map)
			except agn.error, e:
				agn.log (agn.LV_ERROR, 'updBounce', 'Unable to unsubscribe %s for company %d from database: %s' % (`map`, company, e.msg))
				rc = False
		else:
			self.sbcount += 1
		return rc
#
class UpdateAccount (Update):

	accountlog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'account.log'

	def __init__ (self, path = accountlog):
		Update.__init__ (self, path, 'account')
		self.tscheck = sre.compile ('^[0-9]{4}-[0-9]{2}-[0-9]{2}:[0-9]{2}:[0-9]{2}:[0-9]{2}$')

	def updateStart (self, inst):
		self.ignored = 0
		self.inserted = 0
		self.failed = 0
		return True
	
	def updateEnd (self, inst):
		agn.log (agn.LV_INFO, 'updAccount', 'Insert %d, failed %d, ignored %d records in %d lines' % (self.inserted, self.failed, self.ignored, self.lineno))
		return True
	
	def updateLine (self, inst, line):
		sql = 'INSERT INTO mailing_account_tbl ('
		values = 'VALUES ('

		timestamp = 'now()'
		sep = ''
		ignore = False
		for tok in line.split ():
			tup = tok.split ('=', 1)
			if len (tup) == 2:
				name = None
				(var, val) = tup
				quoteit = False

				if var == 'company':
					name = 'company_id'
				elif var == 'mailing':
					name = 'mailing_id'
				elif var == 'maildrop':
					name = 'maildrop_id'
				elif var == 'status_field':
					name = 'status_field'
					quoteit = True
				elif var in ('mailtype', 'subtype'):
					name = 'mailtype'
				elif var == 'count':
					name = 'no_of_mailings'
				elif var == 'bytes':
					name = 'no_of_bytes'
				elif var == 'block':
					name = 'blocknr'
				elif var == 'timestamp':
					if not self.tscheck.match (val) is None:

						timestamp = 'str_to_date(\'' + val + '\', \'%Y-%m-%d:%H:%i:%s\')'
				if quoteit:
					val = '\'%s\'' % val.replace ('\'', '\'\'')
				if not name is None:
					sql += '%s%s' % (sep, name)
					values += '%s%s' % (sep, val)
					sep = ', '

		sql += '%schange_date) %s, %s)' % (sep, values, timestamp)
		rc = True
		if not ignore:
			try:
				inst.update (sql, commit = True)
				self.inserted += 1
			except agn.error, e:
				agn.log (agn.LV_ERROR, 'updAccount', 'Failed to insert %s into database: %s' % (line, e.msg))
				rc = False
				self.failed += 1
		else:
			self.ignored += 1
		return rc
#
term = False
def handler (sig, stack):
	global	term
	
	term = True
signal.signal (signal.SIGINT, handler)
signal.signal (signal.SIGTERM, handler)
signal.signal (signal.SIGHUP, signal.SIG_IGN)
signal.signal (signal.SIGPIPE, signal.SIG_IGN)
#
opts = getopt.getopt (sys.argv[1:], 'o:')
updparm = {}
use = []
for o in opts[0]:
	if o[0] == '-o':
		parm = o[1].split (':', 1)
		if len (parm) == 2:
			v = parm[1].split ('=', 1)
			if len (v) == 1:
				v.append ('true')
			if updparm.has_key (parm[0]):
				updparm[parm[0]].append (v)
			else:
				updparm[parm[0]] = [v]
			if not parm[0] in use:
				use.append (parm[0])
for u in opts[1]:
	if not u in use:
		use.append (u)
updates = []
for u in use:
	if u == 'bounce':
		nu = UpdateBounce ()
	elif u == 'account':
		nu = UpdateAccount ()
	else:
		nu = None
		agn.log (agn.LV_ERROR, 'main', 'Invalid update: %s' % u)
	if not nu is None:
		if updparm.has_key (u):
			nu.options (updparm[u])
		updates.append (nu)
if len (updates) == 0:
	agn.die (agn.LV_ERROR, 'main', 'No update procedure found')
agn.lock ()
agn.log (agn.LV_INFO, 'main', 'Starting up')
while not term:
	agn.mark (agn.LV_INFO, 'loop', 180)
	db = None
	for upd in updates:
		if not term and upd.exists ():
			if db is None:
				db = agn.DBase ()
				if db is None:
					agn.log (agn.LV_ERROR, 'loop', 'Unable to connect to database')
			if not db is None:
				inst = db.newInstance ()
				if not inst is None:
					if not upd.update (inst):
						agn.log (agn.LV_ERROR, 'loop', 'Update for %s failed' % upd.name)
					inst.close ()
				else:
					agn.log (agn.LV_ERROR, 'loop', 'Unable to get database cursor')
	if not db is None:
		db.close ()
	#
	# Zzzzz....
	n = delay
	while n > 0 and not term:
		time.sleep (1)
		n -= 1
agn.log (agn.LV_INFO, 'main', 'Going down')
agn.unlock ()
