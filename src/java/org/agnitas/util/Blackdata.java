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
package org.agnitas.util;

/**
 * This class holds one email or pattern for blacklist
 * checking
 */
public class Blackdata {
    /** 
     * the email or the email pattern 
     */
    private	String		email;
    /** 
     * true, if the email is found on the global blacklist 
     */
    private boolean		global;
    /** 
     * true, if email contains any wildcard characters 
     */
    private boolean		iswildcard;
    
    /**
     * Constructor for the class
     *
     * @param nEmail the email or the pattern
     * @param nGlobal sets the source for this entry
     */
    public Blackdata (String nEmail, boolean nGlobal) {
        email = nEmail;
        global = nGlobal;
        iswildcard = (email.indexOf ('_') != -1) || (email.indexOf ('%') != -1);
    }
    
    /** compares a str against a wildcard pattern (recrusive)
     *
     * @param mask the pattern
     * @param mpos the current position in the mask
     * @param mlen the length of the mask
     * @param str the string to match against
     * @param spos the current position in the string
     * @param slen the length of the string
     */
    private boolean sqllike (String mask, int mpos, int mlen,
                 String str, int spos, int slen) {
        char	cur;

        while ((mpos < mlen) && (spos < slen)) {
            cur = mask.charAt (mpos++);
            if (cur == '_') {
                spos++;
            } else if (cur == '%') {
                while ((mpos < mlen) && (mask.charAt (mpos) == '%')) {
                    mpos++;
                }
                if (mpos == mlen) {
                    return true;
                }
                while (spos < slen) {
                    if (sqllike (mask, mpos, mlen, str, spos, slen)) {
                        return true;
                    } else {
                        ++spos;
                    }
                }
            } else {
                if ((cur == '\\') && (mpos < mlen)) {
                    cur = mask.charAt (mpos++);
                }
                if (cur != str.charAt (spos)) {
                    return false;
                }
                spos++;
            }
        }
        if ((spos == slen) && (mpos < mlen)) {
            while ((mpos < mlen) && (mask.charAt (mpos) == '%')) {
                ++mpos;
            }
        }
        return (mpos == mlen) && (spos == slen);
    }

    /** matches the given string against email
     *
     * @param check the email to check
     * @return true, if check matches email
     */
    public boolean matches (String check) {
        if (! iswildcard) {
            return email.equals (check);
        }
        return sqllike (email, 0, email.length (),
                check, 0, check.length ());
    }
    
    /** returns the source
     *
     * @return true, if entry is on global blacklist
     */
    public boolean isGlobal () {
        return global;
    }
}