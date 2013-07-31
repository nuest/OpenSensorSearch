/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

/** @author Yakoub
 */
package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;

import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLInsertHarvestScriptDAO implements IInsertHarvestScriptDAO {
	/**
	 * the logger, used to log exceptions and additionally information
	 */
	private static Logger log = LoggerFactory
			.getLogger(PGSQLInsertHarvestScriptDAO.class);

	/**
	 * Connection pool for creating connections to the DB
	 */
	private PGConnectionPool cpool;
	
	public PGSQLInsertHarvestScriptDAO(){
		
	}
	
	public PGSQLInsertHarvestScriptDAO(PGConnectionPool cpool){
		this.cpool = cpool;
	}

	@Override
	public String insertScript(String path, String username, int version) {
		String insert;
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			String insertQuery = insertScriptString(path, username, version);
			System.out.println(insertQuery);
			log.info(insertQuery);
			stmt.execute(insertQuery);
			String id = null;
			ResultSet rs = stmt.executeQuery(searchByPath(path));
			if(rs.next()){
				id = rs.getString(PGDAOConstants.SCRIPTID);
			}
			return id;
		} catch (Exception e) {
			log.error("Cannot insert harvest Script",e);
			return null;
		}
	}
	
	private String insertScriptString(String path,String username,int version){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(PGDAOConstants.harvestScript);
		query.append("(");
		query.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
		query.append(",");
		query.append(PGDAOConstants.PATH_URL);
		query.append(",");
		query.append(PGDAOConstants.SCRIPT_VERSION);
		query.append(") values(");
		query.append("'");
		query.append(username);
		query.append("'");
		query.append(",");
		query.append("'");
		query.append(path);
		query.append("'");
		query.append(",");
		query.append("'");
		query.append(version);
		query.append("'");
		query.append(");");
		return query.toString();
	}
	private String searchByPath(String path){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.SCRIPTID);
		builder.append(" FROM ");
		builder.append(PGDAOConstants.harvestScript);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.PATH_URL);
		builder.append(" LIKE ");
		builder.append("'");
		builder.append(path);
		builder.append("'");
		return builder.toString();
	}

	@Override
	public String getScriptPath(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}