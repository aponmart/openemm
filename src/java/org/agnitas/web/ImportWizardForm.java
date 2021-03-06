/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.Recipient;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * 
 * @author mhe
 */
public class ImportWizardForm extends StrutsFormBase {


	public static final int BLOCK_SIZE = 1000;

	public static final String MAILTYPE_KEY = "mailtype";

	public static final String GENDER_KEY = "gender";

	public static final String PARSE_ERRORS = "parseErrors";

	private static final long serialVersionUID = 7563170467003097523L;

	private CustomerImportStatus status = null;

	/**
	 * Holds value of property action.
	 */
	private int action;

	/**
	 * Holds value of property csvFile.
	 */
	private FormFile csvFile;

	/**
	 * Holds value of property csvAllColumns.
	 */
	private ArrayList csvAllColumns;

	/**
	 * Holds value of property mailingLists.
	 */
	private Vector mailingLists;

	/**
	 * Holds value of property usedColumns.
	 */
	private ArrayList usedColumns;

	/**
	 * Holds value of property parsedContent.
	 */
	private LinkedList parsedContent;

	/**
	 * Holds value of property uniqueValues.
	 */
	private HashSet uniqueValues;

	/**
	 * Holds value of property dbAllColumns.
	 */
	private Map dbAllColumns;

	/**
	 * Holds value of property mode.
	 */
	private int mode;

	/**
	 * Holds value of property dateFormat.
	 */
	private String dateFormat = "dd.MM.yyyy HH:mm";

	/**
	 * Constants
	 */
	public static final String DATE_ERROR = "date";

	public static final String EMAIL_ERROR = "email";

	public static final String EMAILDOUBLE_ERROR = "emailDouble";

	public static final String GENDER_ERROR = GENDER_KEY;

	public static final String MAILTYPE_ERROR = MAILTYPE_KEY;

	public static final String NUMERIC_ERROR = "numeric";

	public static final String STRUCTURE_ERROR = "structure";

	public static final String BLACKLIST_ERROR = "blacklist";

	public static final String DBINSERT_ERROR = "dbinsert";

	public static final int MODE_ADD = 1;

	public static final int MODE_ADD_UPDATE = 2;

	public static final int MODE_ONLY_UPDATE = 3;

	public static final int MODE_UNSUBSCRIBE = 4;

	public static final int MODE_BOUNCE = 5;

	public static final int MODE_BLACKLIST = 6;

	public static final int MODE_DELETE = 7;

	public static final int MODE_REMOVE_STATUS = 8;

	/*
	 * public static final int DOUBLECHECK_FULL = 0;
	 * 
	 * public static final int DOUBLECHECK_CSV = 1;
	 * 
	 * public static final int DOUBLECHECK_NONE = 2;
	 */

	/**
	 * Holds value of property linesOK.
	 */
	private int linesOK;
	
	/**
	 * number of read lines
	 */
	private int readlines;
	

	/**
	 * Holds value of property dbInsertStatus.
	 */
	private int dbInsertStatus;

	/**
	 * Holds value of property errorData.
	 */
	private HashMap errorData = new HashMap();

	/**
	 * Holds value of property parsedData.
	 */
	private StringBuffer parsedData;

	/**
	 * Holds value of property downloadName.
	 */
	private String downloadName;

	/**
	 * Holds value of property dbInsertStatusMessages.
	 */
	private LinkedList dbInsertStatusMessages;

	/**
	 * Holds value of property resultMailingListAdded.
	 */
	private Hashtable resultMailingListAdded;

	/**
	 * Holds value of property blacklist.
	 */
	protected HashSet blacklist;

	public static final int MODE_DONT_IGNORE_NULL_VALUES = 0;

	public static final int MODE_IGNORE_NULL_VALUES = 1;

	protected int csvMaxUsedColumn = 0;

	/**
	 * Holds value of property previewOffset.
	 */
	private int previewOffset;
	
	
	/**
	 * user may choose a default mailing-type in case of no column for mailing-type has been assigned
	 */

	private String manualAssignedMailingType =  Integer.toString(Recipient.MAILTYPE_HTML); 
	private String manualAssignedGender = Integer.toString(Recipient.GENDER_UNKNOWN); 
	
	private boolean mailingTypeMissing = false;
	private boolean genderMissing = false;
	
	/**
	 * Validate the properties that have been set from this HTTP request, and
	 * return an <code>ActionMessages</code> object that encapsulates any
	 * validation errors that have been found. If no errors are found, return
	 * <code>null</code> or an <code>ActionMessages</code> object with no
	 * recorded error messages.
	 * 
	 * @param mapping
	 *            The mapping used to select this instance
	 * @param request
	 *            The servlet request we are processing
	 * @return errors
	 */

	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ApplicationContext aContext = this.getWebApplicationContext();
		ActionErrors errors = new ActionErrors();
			
		AgnUtils.logger().info("validate: " + this.action);
		
		switch (this.action) {

		case ImportWizardAction.ACTION_START:
			initStatus(aContext);
			break;

		case ImportWizardAction.ACTION_CSV:
			if (request.getParameter("mode_back.x") != null) {
				this.action = ImportWizardAction.ACTION_START;
			} else {
				
				errors = parseFirstline(request);
				
			}
			break;
		
		case ImportWizardAction.ACTION_CHECK_FIELDS:
			mapColumns(request); // map columns		
			break;

		case ImportWizardAction.ACTION_PARSE:
			try {
				if (request.getParameter("mapping_back.x") != null) {
					this.action = ImportWizardAction.ACTION_MODE;
				} else {
					doParse(request);	
					setLinesOK(getLinesOKFromFile(request));
					if (getLinesOK() > Integer.parseInt(AgnUtils
							.getDefaultValue("import.maxrows"))) {
						errors.add("global", new ActionMessage(
								"error.import.too_many_records" , AgnUtils.getDefaultValue("import.maxrows")));
						this.action = ImportWizardAction.ACTION_MODE;
					 }
				}
			} catch (Exception e) {
				System.err.println("Exception caught: " + e);
				System.err.println(AgnUtils.getStackTrace(e));
			}
			break;

		case ImportWizardAction.ACTION_MODE:
			status.setErrors(new HashMap());
			break;

		case ImportWizardAction.ACTION_PRESCAN:
			if (request.getParameter("verify_back.x") != null) {
				this.action = ImportWizardAction.ACTION_CSV;
			} else {
				// default keyColumn to "EMAIL"
				if (status == null) {
					initStatus(aContext);
				}
				if (this.status.getKeycolumn() == null
						|| this.status.getKeycolumn().trim().equals("")) {
					this.status.setKeycolumn("email");
				}
			}
			break;

		case ImportWizardAction.ACTION_MLISTS:
			if (request.getParameter("prescan_back.x") != null) {
				this.action = ImportWizardAction.ACTION_PREVIEW_SCROLL;
			}
			break;

		case ImportWizardAction.ACTION_WRITE:
			if (request.getParameter("mlists_back.x") != null) {
				this.action = ImportWizardAction.ACTION_PRESCAN;
			} else {
				getMailinglistsFromRequest(request);
				if(this.mailingLists.size() <= 0) {
					errors.add("global", new ActionMessage("error.import.no_mailinglist"));
					this.action = ImportWizardAction.ACTION_MLISTS;
				}
			}
			break;

		case ImportWizardAction.ACTION_PREVIEW_SCROLL:
			if (this.parsedContent != null) {
				if (this.previewOffset >= parsedContent.size()) {
					this.previewOffset = parsedContent.size() - 6;
				}
			}
			if (this.previewOffset < 0) {
				this.previewOffset = 0;
			}
			break;
		}
		return errors;
	}

	public void doParse(HttpServletRequest request) {
		// start at the top of the csv file:
		this.previewOffset = 0;
		// change this to process the column name mapping from
		// previous action:
		this.parseContent(request);
	}

	private void initStatus(ApplicationContext aContext) {
		status = (CustomerImportStatus) aContext
				.getBean("CustomerImportStatus");
		status.setId(0);
	}

	/**
	 * Getter for property datasourceID.
	 * 
	 * @return Value of property datasourceID.
	 */
	public int getDatasourceID() {
		return status.getDatasourceID();
	}

	/**
	 * Sets an error.
	 */
	public void setError(String id, String desc) {
		status.addError(id);
		if (!errorData.containsKey(id)) {
			errorData.put(id, new StringBuffer());
		}
		((StringBuffer) errorData.get(id)).append(desc + "\n");
		status.addError("all");
	}

	/**
	 * Getter for property error.
	 * 
	 * @return Value of property error.
	 */
	public StringBuffer getError(String id) {
		return (StringBuffer) errorData.get(id);
	}

	/**
	 * Getter for property errorMap.
	 * 
	 * @return Value of property errorMap.
	 */
	public HashMap getErrorMap() {
		return errorData;
	}

	/**
	 * Getter for property status.
	 * 
	 * @return Value of property status.
	 */
	public CustomerImportStatus getStatus() {
		return status;
	}

	/**
	 * Setter for property charset.
	 * 
	 * @param status
	 *            New value of property status.
	 */
	public void setStatus(CustomerImportStatus status) {
		this.status = status;
	}

	/**
	 * Getter for property action.
	 * 
	 * @return Value of property action.
	 */
	public int getAction() {
		return this.action;
	}

	/**
	 * Setter for property action.
	 * 
	 * @param action
	 *            New value of property action.
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * Getter for property csvFile.
	 * 
	 * @return Value of property csvFile.
	 */
	public FormFile getCsvFile() {
		return this.csvFile;
	}

	/**
	 * Setter for property csvFile.
	 * 
	 * @param csvFile
	 *            New value of property csvFile.
	 */
	public void setCsvFile(FormFile csvFile) {
		this.csvFile = csvFile;
	}

	/**
	 * Getter for property csvAllColumns.
	 * 
	 * @return Value of property csvAllColumns.
	 */
	public ArrayList getCsvAllColumns() {
		return this.csvAllColumns;
	}

	/**
	 * Setter for property csvAllColumns.
	 * 
	 * @param csvAllColumns
	 *            New value of property csvAllColumns.
	 */
	public void setCsvAllColumns(ArrayList csvAllColumns) {
		this.csvAllColumns = csvAllColumns;
	}

	/**
	 * Getter for property mailingLists.
	 * 
	 * @return Value of property mailingLists.
	 * 
	 */
	public Vector getMailingLists() {
		return this.mailingLists;
	}

	/**
	 * Setter for property mailingLists.
	 * 
	 * @param mailingLists
	 *            New value of property mailingLists.
	 */
	public void setMailingLists(Vector mailingLists) {
		this.mailingLists = mailingLists;
	}

	/**
	 * Getter for property usedColumns.
	 * 
	 * @return Value of property usedColumns.
	 */
	public ArrayList getUsedColumns() {
		return this.usedColumns;
	}

	/**
	 * Setter for property usedColumns.
	 * 
	 * @param usedColumns
	 *            New value of property usedColumns.
	 */
	public void setUsedColumns(ArrayList usedColumns) {
		this.usedColumns = usedColumns;
	}

	/**
	 * Getter for property parsedContent.
	 * 
	 * @return Value of property parsedContent.
	 */
	public LinkedList getParsedContent() {
		return this.parsedContent;
	}

	/**
	 * Setter for property parsedContent.
	 * 
	 * @param parsedContent
	 *            New value of property parsedContent.
	 */
	public void setParsedContent(LinkedList parsedContent) {
		this.parsedContent = parsedContent;
	}

	/**
	 * Getter for property emailAdresses.
	 * 
	 * @return Value of property emailAdresses.
	 */
	public HashSet getUniqueValues() {
		return this.uniqueValues;
	}

	/**
	 * Setter for property emailAdresses.
	 * 
	 * @param uniqueValues
	 */
	public void setUniqueValues(HashSet uniqueValues) {
		this.uniqueValues = uniqueValues;
	}

	/**
	 * Getter for property dbAllColumns.
	 * 
	 * @return Value of property dbAllColumns.
	 */
	public Map getDbAllColumns() {
		return this.dbAllColumns;
	}

	/**
	 * Setter for property dbAllColumns.
	 * 
	 * @param dbAllColumns
	 *            New value of property dbAllColumns.
	 */
	public void setDbAllColumns(Hashtable dbAllColumns) {
		this.dbAllColumns = dbAllColumns;
	}

	/**
	 * Getter for property mode.
	 * 
	 * @return Value of property mode.
	 */
	public int getMode() {
		return this.mode;
	}

	/**
	 * Setter for property mode.
	 * 
	 * @param mode
	 *            New value of property mode.
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Reads columns from database.
	 */
	protected void readDBColumns(int companyID, DataSource ds) {
		String sqlGetTblStruct = "SELECT * FROM customer_" + companyID
				+ "_tbl WHERE 1=0";
		CsvColInfo aCol = null;
		String colType = null;

		dbAllColumns = new CaseInsensitiveMap();
		Connection con = DataSourceUtils.getConnection(ds);
		try {
			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sqlGetTblStruct);
			ResultSetMetaData meta = rset.getMetaData();

			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (!meta.getColumnName(i).equals("change_date")
						&& !meta.getColumnName(i).equals("creation_date")
						&& !meta.getColumnName(i).equals("datasource_id")) {
					if (meta.getColumnName(i).equals("customer_id")) {
						if (status == null) {
							initStatus(getWebApplicationContext());
						}
						if (!(this.mode == ImportWizardForm.MODE_ONLY_UPDATE && this.status
								.getKeycolumn().equals("customer_id"))) {
							continue;
						}
					}

					aCol = new CsvColInfo();
					aCol.setName(meta.getColumnName(i));
					aCol.setLength(meta.getColumnDisplaySize(i));
					aCol.setType(CsvColInfo.TYPE_UNKNOWN);
					aCol.setActive(false);
					colType = meta.getColumnTypeName(i);
					if (colType.startsWith("VARCHAR")) {
						aCol.setType(CsvColInfo.TYPE_CHAR);
					} else if (colType.startsWith("CHAR")) {
						aCol.setType(CsvColInfo.TYPE_CHAR);
					} else if (colType.startsWith("NUM")) {
						aCol.setType(CsvColInfo.TYPE_NUMERIC);
					} else if (colType.startsWith("INTEGER")) {
						aCol.setType(CsvColInfo.TYPE_NUMERIC);
					} else if (colType.startsWith("DOUBLE")) {
						aCol.setType(CsvColInfo.TYPE_NUMERIC);
					} else if (colType.startsWith("TIME")) {
						aCol.setType(CsvColInfo.TYPE_DATE);
					} else if (colType.startsWith("DATE")) {
						aCol.setType(CsvColInfo.TYPE_DATE);
					}
					this.dbAllColumns.put(meta.getColumnName(i), aCol);
				}
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			AgnUtils.logger().error("readDBColumns: " + e);
		}
		DataSourceUtils.releaseConnection(con, ds);
	}

	/**
	 * Loads blacklist.
	 */
	protected void loadBlacklist(int companyID, JdbcTemplate jdbc)
			throws Exception {
		SqlRowSet rset = null;
		String blackList = null;
		Object[] params = new Object[] { new Integer(companyID) };

		this.blacklist = new HashSet();
		try {
			blackList = "SELECT email FROM cust_ban_tbl WHERE company_id=? OR company_id=0";
			rset = jdbc.queryForRowSet(blackList, params);
			while (rset.next()) {
				this.blacklist.add(rset.getString(1).toLowerCase());
			}

		} catch (Exception e) {
			AgnUtils.logger().error("loadBlacklist: " + e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Creates a simple date format When mapping for a column is found get real
	 * csv column information Checks email / email adress / email adress on
	 * blacklist.
	 */
	public LinkedList parseLine(String input, Locale locale) {
		// EnhStringTokenizer aLine = null;
		CsvTokenizer aLine = null;
		int j = 0;
		String aValue = null;
		CsvColInfo aInfo = null;
		CsvColInfo aCsvInfo = null;
		LinkedList valueList = new LinkedList();
		int tmp = 0;

		if (this.dateFormat == null || this.dateFormat.trim().length() == 0) {
			this.dateFormat = new String("dd.MM.yyyy HH:mm");
		}

		SimpleDateFormat aDateFormat = new SimpleDateFormat(this.dateFormat);

		aLine = new CsvTokenizer(input, status.getSeparator(), status
				.getDelimiter());
		
		try {
			boolean addedGenderDummyValue = false;
			boolean addedMailtypeDummyValue = false;
			while ((aValue = aLine.nextToken()) != null) {
				aCsvInfo = (CsvColInfo) this.csvAllColumns.get(j);

				// only when mapping for this column is found:
				if (this.getColumnMapping().containsKey(aCsvInfo.getName())) {

					// get real CsvColInfo object:
					aInfo = (CsvColInfo) this.getColumnMapping().get(
							aCsvInfo.getName());

					aValue = aValue.trim();
					// do this before eventual duplicate check on Col Email
					if (aInfo.getName().equalsIgnoreCase("email")) {
						aValue = aValue.toLowerCase();
					}
					if (status.getDoubleCheck() != CustomerImportStatus.DOUBLECHECK_NONE
							&& this.status.getKeycolumn().equalsIgnoreCase(
									aInfo.getName())) {
						if (this.uniqueValues.add(aValue) == false) {
							setError(EMAILDOUBLE_ERROR, input + "\n");
							AgnUtils.logger()
									.error("Duplicate email: " + input);
							return null;
						}
					}
					if (aInfo.getName().equalsIgnoreCase("email")) {
						if (aValue.length() == 0) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error("Empty email: " + input);
							return null;
						}
						if (aValue.indexOf('@') == -1) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error("No @ in email: " + input);
							return null;
						}

						try {
							new InternetAddress(aValue);
						} catch (Exception e) {
							setError(EMAIL_ERROR, input + "\n");
							AgnUtils.logger().error(
									"InternetAddress error: " + input);
							return null;
						}
						// check blacklist
						if (AgnUtils.matchCollection(aValue, this.blacklist)) {
							setError(BLACKLIST_ERROR, input + "\n");
							AgnUtils.logger().error("Blacklisted: " + input);
							return null;
						}
					} else if (aInfo.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
						try {
							tmp = Integer.parseInt(aValue);
							if (tmp < 0 || tmp > 2) {
								throw new Exception("Invalid mailtype");
							}
						} catch (Exception e) {
							if (aInfo.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
								if (!aValue.equalsIgnoreCase("text")
										&& !aValue.equalsIgnoreCase("txt")
										&& !aValue.equalsIgnoreCase("html")
										&& !aValue.equalsIgnoreCase("offline")) {
									setError(MAILTYPE_ERROR, input + "\n");
									AgnUtils.logger().error(
											"Invalid mailtype: " + input);
									return null;
								}
							}
						}
					} else if (aInfo.getName().equalsIgnoreCase(GENDER_KEY)) {
						try {
							tmp = Integer.parseInt(aValue);
							if (tmp < 0 || tmp > 5) {
								throw new Exception("Invalid gender");
							}
						} catch (Exception e) {
							if (aInfo.getName().equalsIgnoreCase(GENDER_KEY)) {
								if (!aValue.equalsIgnoreCase("Herr")
										&& !aValue.equalsIgnoreCase("Herrn")
										&& !aValue.equalsIgnoreCase("m")
										&& !aValue.equalsIgnoreCase("Frau")
										&& !aValue.equalsIgnoreCase("w")) {
									setError(
											GENDER_ERROR,
											input
													+ ";"
													+ SafeString
															.getLocaleString(
																	"import.error.GenderFormat",
																	locale)
													+ aInfo.getName() + "\n");
									AgnUtils.logger().error(
											"Invalid gender: " + aValue);
									return null;
								}
							}
						}
					}
					if (aInfo != null && aInfo.isActive()) {
						if (aValue.length() == 0) { // is null value
							valueList.add(null);
						} else {
							switch (aInfo.getType()) {
							case CsvColInfo.TYPE_CHAR:
								valueList.add(SafeString.cutByteLength(aValue,
										aInfo.getLength()));
								break;

							case CsvColInfo.TYPE_NUMERIC:
								try {
									valueList.add(Double.valueOf(aValue));
								} catch (Exception e) {
									if (aInfo.getName().equalsIgnoreCase(
											GENDER_KEY) && !columnMapping.containsKey(GENDER_KEY+"_dummy")) {
										if (aValue.equalsIgnoreCase("Herr")
												|| aValue
														.equalsIgnoreCase("Herrn")
												|| aValue.equalsIgnoreCase("m")) {
											valueList.add(Double.valueOf(0));
										} else if (aValue
												.equalsIgnoreCase("Frau")
												|| aValue.equalsIgnoreCase("w")) {
											valueList.add(Double.valueOf(1));
										} 		
										else {
											valueList.add(Double.valueOf(2));
										}
									} else if (aInfo.getName().equalsIgnoreCase(
											MAILTYPE_KEY) &&  ! columnMapping.containsKey(MAILTYPE_KEY+"_dummy")) {
										if (aValue.equalsIgnoreCase("text")
												|| aValue
														.equalsIgnoreCase("txt")) {
											valueList.add(Double.valueOf(0));
										} else if (aValue
												.equalsIgnoreCase("html")) {
											valueList.add(Double.valueOf(1));
										} else if (aValue
												.equalsIgnoreCase("offline")) {
											valueList.add(Double.valueOf(2));
										} 
									} else {
										setError(
												NUMERIC_ERROR,
												input
														+ ";"
														+ SafeString
																.getLocaleString(
																		"import.error.NumberFormat",
																		locale)
														+ aInfo.getName()
														+ "\n");
										AgnUtils.logger().error(
												"Numberformat error: " + input);
										return null;
									}
								}
								break;

							case CsvColInfo.TYPE_DATE:
								try {
									valueList.add(aDateFormat.parse(aValue));
								} catch (Exception e) {
									setError(DATE_ERROR, input
											+ ";"
											+ SafeString.getLocaleString(
													"import.error.DateFormat",
													locale) + aInfo.getName()
											+ "\n");
									AgnUtils.logger().error(
											"Dateformat error: " + input);
									return null;
								}
							}
						}
					}
				}
				
				
				
				j++;
			}
			if (this.getColumnMapping().containsKey(GENDER_KEY+"_dummy" ) && ! addedGenderDummyValue ) {
				valueList.add(getManualAssignedGender());
				addedGenderDummyValue = true;
			}
			if (this.getColumnMapping().containsKey(MAILTYPE_KEY+"_dummy" ) && ! addedMailtypeDummyValue ) {
				valueList.add(getManualAssignedMailingType());
				addedMailtypeDummyValue = true;
			}
			
			
		} catch (Exception e) {
			setError(STRUCTURE_ERROR, input + "\n");
			AgnUtils.logger().error("parseLine: " + e);
			return null;
		
		}

		if (this.csvMaxUsedColumn != j) {
			setError(STRUCTURE_ERROR, input + "\n");
			AgnUtils.logger().error(
					"MaxusedColumn: " + this.csvMaxUsedColumn + ", " + j);
			return null;
		}

		return valueList;
	}

	/**
	 * Maps columns from database.
	 */
	protected void mapColumns(HttpServletRequest req) {
		int i = 1;
		CsvColInfo aCol = null;

		// initialize columnMapping hashtable:
		this.columnMapping = new Hashtable();

		for (i = 1; i < (csvAllColumns.size() + 1); i++) {
			String pName = new String("map_" + i);
			if (req.getParameter(pName) != null) {
				aCol = (CsvColInfo) csvAllColumns.get(i - 1);
				if (req.getParameter(pName).compareTo("NOOP") != 0) {
					CsvColInfo aInfo = (CsvColInfo) dbAllColumns.get(req
							.getParameter(pName));

					this.columnMapping.put(aCol.getName(), aInfo);

					aInfo.setActive(true);
					// write db column (set active now) back to dbAllColums:
					this.dbAllColumns.put(new String(req.getParameter(pName)),
							aInfo);

					// adjust & write back csvAllColumns hashtable entry:
					aCol.setActive(true);
					aCol.setLength(aInfo.getLength());
					aCol.setType(aInfo.getType());

					this.csvAllColumns.set(i - 1, aCol);
				}
			}
		}
		
		if ( ! columnIsMapped(GENDER_KEY) ) {
			CsvColInfo genderCol = new CsvColInfo();
			genderCol.setName(GENDER_KEY);
			genderCol.setType(CsvColInfo.TYPE_CHAR);
			columnMapping.put(GENDER_KEY+"_dummy", genderCol );
			setGenderMissing(true);
		}
		
		if ( ! columnIsMapped(MAILTYPE_KEY) ) {
			CsvColInfo mailtypeCol = new CsvColInfo();
			mailtypeCol.setName(MAILTYPE_KEY);
			mailtypeCol.setType(CsvColInfo.TYPE_CHAR);			
			columnMapping.put(MAILTYPE_KEY+"_dummy", mailtypeCol );
			setMailingTypeMissing(true);
		}
		
		// check if the mailtype/ gender is allready in columnmapping , if we find only a dummy -> add a dummy to csvAllColumns too 
		if( getColumnMapping().containsKey(GENDER_KEY+"_dummy")) {
			CsvColInfo mailtypeDummy = new CsvColInfo();
			mailtypeDummy.setName(GENDER_KEY+"_dummy");
			mailtypeDummy.setActive(true);
			mailtypeDummy.setType(CsvColInfo.TYPE_CHAR);
			csvAllColumns.add(mailtypeDummy);
		}
		
		
		if( getColumnMapping().containsKey(MAILTYPE_KEY+"_dummy")) {
			CsvColInfo mailtypeDummy = new CsvColInfo();
			mailtypeDummy.setName(MAILTYPE_KEY+"_dummy");
			mailtypeDummy.setActive(true);
			mailtypeDummy.setType(CsvColInfo.TYPE_CHAR);
			csvAllColumns.add(mailtypeDummy);
		}
		
		 
		
		
		
	}

	/**
	 * Tries to read csv file Reads database column structure reads first line
	 * splits line into tokens
	 */
	protected ActionErrors parseFirstline(HttpServletRequest req) {
		ApplicationContext aContext = this.getWebApplicationContext();
		DataSource ds = (DataSource) aContext.getBean("dataSource");
		String csvString = new String("");
		String firstline = null;
		ActionErrors errors = new ActionErrors();
		int colNum = 0;

		// try to read csv file:
		try {
			csvString = new String(this.getCsvFile().getFileData(), status
					.getCharset());
		} catch (Exception e) {
			AgnUtils.logger().error("parseFirstline: " + e);
			errors.add("global", new ActionMessage("error.import.charset"));
			return errors;
		}

		if (csvString.length() == 0) {
			errors.add("global", new ActionMessage("error.import.no_file"));
		}

		// read out DB column structure:
		this.readDBColumns(this.getCompanyID(req), ds);
		this.csvAllColumns = new ArrayList();
		LineNumberReader aReader = new LineNumberReader(new StringReader(
				csvString));

		
		try {
			// read first line:
			if ((firstline = aReader.readLine()) != null) {
				aReader.close(); // 
				
				// split line into tokens:
				CsvTokenizer st = new CsvTokenizer(firstline, status
						.getSeparator(), status.getDelimiter());
				String curr = "";
				CsvColInfo aCol = null;
				List<String> tempList = new ArrayList<String>();
				while ((curr = st.nextToken()) != null) {

					// curr = (String)st.nextElement();
					curr = curr.trim();
					curr = curr.toLowerCase();
					aCol = new CsvColInfo();
					aCol.setName(curr);
					aCol.setActive(false);
					aCol.setType(CsvColInfo.TYPE_UNKNOWN);

					// add column to csvAllColumns:
					if (!tempList.contains(aCol.getName())) {
						tempList.add(aCol.getName());
					} else {
						errors.add("global", new ActionMessage(
								"error.import.column"));
					}
					csvAllColumns.add(aCol);
					colNum++;
					this.csvMaxUsedColumn = colNum;
				}
			}
			
		} catch (Exception e) {
			AgnUtils.logger().error("parseFirstline: " + e);
		}

		return errors;
	}

	/**
	 * check in the columnMapping for the key column, and eventually for gender
	 * and mailtype read first csv line again; do not parse (allready parsed in
	 * parseFirstline) prepare download-files for errors and parsed data read
	 * the rest of the csv-file
	 */
	protected ActionErrors parseContent(HttpServletRequest req) {
		ApplicationContext aContext = this.getWebApplicationContext();
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) aContext
				.getBean("dataSource"));
		LinkedList aLineContent = null;
		String firstline = null;
		String csvString = new String("");
		ActionErrors errors = new ActionErrors();
		boolean hasGENDER = false;
		boolean hasMAILTYPE = false;
		boolean hasKeyColumn = false;

		this.uniqueValues = new HashSet();
		this.parsedContent = new LinkedList();
		this.linesOK = 0;
		// this.csvMaxUsedColumn=0;

		this.dbInsertStatus = 0;

		try {
			csvString = new String(this.getCsvFile().getFileData(), status
					.getCharset());
		} catch (Exception e) {
			AgnUtils.logger().error("parseContent: " + e);
			errors.add("global", new ActionMessage("error.import.charset"));
			return errors;
		}

		try {
			this.loadBlacklist(this.getCompanyID(req), jdbc);
		} catch (Exception e) {
			errors.add("global", new ActionMessage("import.blacklist.read"));
			return errors;
		}

		LineNumberReader aReader = new LineNumberReader(new StringReader(
				csvString));

		String myline = null;

		// check in the columnMapping for the key column,
		// and eventually for gender and mailtype:
		String aKey = "";
		CsvColInfo aCol = null;
			
	
			
		Enumeration aMapEnu = this.columnMapping.keys();
		
		while (aMapEnu.hasMoreElements()) {
			aKey = (String) aMapEnu.nextElement();
			aCol = (CsvColInfo) this.columnMapping.get(aKey);

									
			if (aCol.getName().equalsIgnoreCase(GENDER_KEY) ) {
				hasGENDER = true;
			}

			if (aCol.getName().equalsIgnoreCase(MAILTYPE_KEY)) {
				hasMAILTYPE = true;
			}

			if (aCol.getName().equalsIgnoreCase(this.status.getKeycolumn())) {
				hasKeyColumn = true;
			}
		}

		if (!hasKeyColumn) {
			errors.add("global", new ActionMessage(
					"error.import.no_keycolumn_mapping"));
		}

		if (this.getMode() == ImportWizardForm.MODE_ADD
				|| this.getMode() == ImportWizardForm.MODE_ADD_UPDATE) {
			if (!hasGENDER) {
				errors.add("global", new ActionMessage(
						"error.import.no_gender_mapping"));
			}
			if (!hasMAILTYPE) {
				errors.add("global", new ActionMessage(
						"error.import.no_mailtype_mapping"));
			}
		}

		try {

			// read first csv line again; do not parse (allready parsed in
			// parseFirstline):
			if ((myline = aReader.readLine()) != null) {
				firstline = myline;
			}

			// prepare download-files for errors and parsed data
			errorData.put(DATE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(EMAIL_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(EMAILDOUBLE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(GENDER_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(MAILTYPE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(NUMERIC_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(STRUCTURE_ERROR, new StringBuffer(firstline + '\n'));
			errorData.put(BLACKLIST_ERROR, new StringBuffer(firstline + '\n'));
			parsedData = new StringBuffer(firstline + '\n');

			// read the rest of the csv-file:
			// StringTokenizer file = new StringTokenizer(csvString, "\n");

			if (errors.isEmpty()) {
				readlines = 0;
				int maxrows = BLOCK_SIZE;
				this.linesOK = 0;
				while ((myline = aReader.readLine()) != null && this.linesOK < maxrows) { // Bug-Fix just read the first 1000 lines to avoid trouble with heap space 
					if (myline.trim().length() > 0) {
						aLineContent = parseLine(myline, (Locale) req
								.getSession().getAttribute(
										org.apache.struts.Globals.LOCALE_KEY));
						if (aLineContent != null) {
							parsedContent.add(aLineContent);
							this.parsedData.append(myline + "\n");
							this.linesOK++;
						}						
					}
					readlines++;
				}
				
				aReader.close();
			}
		} catch (Exception e) {
			AgnUtils.logger().error("parseContent: " + e);
		}
		return errors;
	}

	private boolean columnIsMapped( String key ) {
		Enumeration<CsvColInfo> elements = columnMapping.elements();
		while( elements.hasMoreElements()) {
			CsvColInfo colInfo = elements.nextElement();
			if(key.equalsIgnoreCase(colInfo.getName())) {
				return true;
			}			
		}
		return false;
	}

	/**
	 * Gets mailing lists from request.
	 */
	protected void getMailinglistsFromRequest(HttpServletRequest req) {
		String aParam = null;
		this.mailingLists = new Vector();
		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			aParam = (String) e.nextElement();
			if (aParam.startsWith("agn_mlid_")) {
				this.mailingLists.add(aParam.substring(9));
			}
		}
	}

	/**
	 * Getter for property linesOK.
	 * 
	 * @return Value of property linesOK.
	 */
	public int getLinesOK() {
		return this.linesOK;
	}

	/**
	 * Setter for property linesOK.
	 * 
	 * @param linesOK
	 *            New value of property linesOK.
	 */
	public void setLinesOK(int linesOK) {
		this.linesOK = linesOK;
	}

	/**
	 * Getter for property dbInsertStatus.
	 * 
	 * @return Value of property dbInsertStatus.
	 */
	public int getDbInsertStatus() {
		return this.dbInsertStatus;
	}

	/**
	 * Setter for property dbInsertStatus.
	 * 
	 * @param dbInsertStatus
	 *            New value of property dbInsertStatus.
	 */
	public void setDbInsertStatus(int dbInsertStatus) {
		this.dbInsertStatus = dbInsertStatus;
	}

	/**
	 * Getter for property parsedData.
	 * 
	 * @return Value of property parsedData.
	 */
	public StringBuffer getParsedData() {
		return this.parsedData;
	}

	/**
	 * Setter for property parsedData.
	 * 
	 * @param parsedData
	 *            New value of property parsedData.
	 */
	public void setParsedData(StringBuffer parsedData) {
		this.parsedData = parsedData;
	}

	/**
	 * Getter for property downloadName.
	 * 
	 * @return Value of property downloadName.
	 */
	public String getDownloadName() {
		return this.downloadName;
	}

	/**
	 * Setter for property downloadName.
	 * 
	 * @param downloadName
	 *            New value of property downloadName.
	 */
	public void setDownloadName(String downloadName) {
		this.downloadName = downloadName;
	}

	/**
	 * Getter for property dbInsertStatusMessages.
	 * 
	 * @return Value of property dbInsertStatusMessages.
	 */
	public LinkedList getDbInsertStatusMessages() {
		return this.dbInsertStatusMessages;
	}

	/**
	 * Setter for property dbInsertStatusMessages.
	 * 
	 * @param dbInsertStatusMessages
	 *            New value of property dbInsertStatusMessages.
	 */
	public void setDbInsertStatusMessages(LinkedList dbInsertStatusMessages) {
		this.dbInsertStatusMessages = dbInsertStatusMessages;
	}

	public void addDbInsertStatusMessage(String message) {
		if (this.dbInsertStatusMessages == null) {
			this.dbInsertStatusMessages = new LinkedList();
		}

		this.dbInsertStatusMessages.add(message);
	}

	/**
	 * Getter for property resultMailingListAdded.
	 * 
	 * @return Value of property resultMailingListAdded.
	 */
	public Hashtable getResultMailingListAdded() {
		return this.resultMailingListAdded;
	}

	/**
	 * Setter for property resultMailingListAdded.
	 * 
	 * @param resultMailingListAdded
	 *            New value of property resultMailingListAdded.
	 */
	public void setResultMailingListAdded(Hashtable resultMailingListAdded) {
		this.resultMailingListAdded = resultMailingListAdded;
	}

	/**
	 * Getter for property blacklist.
	 * 
	 * @return Value of property blacklist.
	 */
	public HashSet getBlacklist() {
		return this.blacklist;
	}

	/**
	 * Setter for property blacklist.
	 * 
	 * @param blacklist
	 *            New value of property blacklist.
	 */
	public void setBlacklist(HashSet blacklist) {
		this.blacklist = blacklist;
	}

	/**
	 * Getter for property previewOffset.
	 * 
	 * @return Value of property previewOffset.
	 */
	public int getPreviewOffset() {
		return this.previewOffset;
	}

	/**
	 * Setter for property previewOffset.
	 * 
	 * @param previewOffset
	 *            New value of property previewOffset.
	 */
	public void setPreviewOffset(int previewOffset) {
		this.previewOffset = previewOffset;
	}

	/**
	 * Getter for property dateFormat.
	 * 
	 * @return Value of property dateFormat.
	 */
	public String getDateFormat() {

		return this.dateFormat;
	}

	/**
	 * Setter for property dateFormat.
	 * 
	 * @param dateFormat
	 *            New value of property dateFormat.
	 */
	public void setDateFormat(String dateFormat) {

		this.dateFormat = dateFormat;
	}

	/**
	 * Holds value of property columnMapping.
	 */
	private Hashtable columnMapping;

	/**
	 * Getter for property columnMapping.
	 * 
	 * @return Value of property columnMapping.
	 */
	public Hashtable getColumnMapping() {

		return this.columnMapping;
	}

	/**
	 * Setter for property columnMapping.
	 * 
	 * @param columnMapping
	 *            New value of property columnMapping.
	 */
	public void setColumnMapping(Hashtable columnMapping) {

		this.columnMapping = columnMapping;
	}

	private boolean extendedEmailCheck = true;

	public boolean isExtendedEmailCheck() {
		return extendedEmailCheck;
	}

	public void setExtendedEmailCheck(boolean extendedEmailCheck) {
		this.extendedEmailCheck = extendedEmailCheck;
	}

	private String errorId = null;

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getManualAssignedMailingType() {
		return manualAssignedMailingType;
	}

	public void setManualAssignedMailingType(String manualAssignedMailingType) {
		this.manualAssignedMailingType = manualAssignedMailingType;
	}

	public String getManualAssignedGender() {
		return manualAssignedGender;
	}

	public void setManualAssignedGender(String manualAssignedGender) {
		this.manualAssignedGender = manualAssignedGender;
	}

	public boolean isMailingTypeMissing() {
		return mailingTypeMissing;
	}

	public void setMailingTypeMissing(boolean mailingTypeMissing) {
		this.mailingTypeMissing = mailingTypeMissing;
	}

	public boolean isGenderMissing() {
		return genderMissing;
	}

	public void setGenderMissing(boolean genderMissing) {
		this.genderMissing = genderMissing;
	}

	public int getReadlines() {
		return readlines;
	}	
	
	 /**
     * read all lines of the file
     * @param aForm
     * @param req
     * @return
     * @throws IOException
     */
    public int getLinesOKFromFile( HttpServletRequest req ) throws IOException {
    	String csvString =  new String(this.getCsvFile().getFileData(), this.getStatus().getCharset());
      	LineNumberReader aReader = new LineNumberReader(new StringReader(csvString));
        String myline = "";
		int linesOK = 0;
		this.getUniqueValues().clear();	
		aReader.readLine(); // skip header
		while ((myline = aReader.readLine()) != null ) { 
			if (myline.trim().length() > 0) {
				if(  this.parseLine(myline, (Locale) req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) != null) {
					linesOK++;
				}						
			}
		}
		aReader.close();
		return linesOK;
     }
	
}
